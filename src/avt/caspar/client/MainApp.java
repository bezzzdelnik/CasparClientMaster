package avt.caspar.client;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import avt.caspar.client.model.SheetDataProperty;
import avt.caspar.client.model.Template;
import avt.caspar.client.model.TemplateListWrapper;
import avt.caspar.client.model.TemplateProperty;
import avt.caspar.client.controllers.TemplateEditDialogController;
import avt.caspar.client.controllers.TemplateOverviewController;
import avt.caspar.client.controllers.RootLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class MainApp extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private final ObservableList<Template> templateData = FXCollections.observableArrayList();
    private final ObservableList<Template> remoteTemplateData = FXCollections.observableArrayList();
    private final ObservableList<TemplateProperty> templateProperties = FXCollections.observableArrayList();
    private final ObservableList<SheetDataProperty> sheetDataProperties = FXCollections.observableArrayList();
    private ObservableList<String> templatesList = FXCollections.observableArrayList();
    private ObservableList<String> mediaList = FXCollections.observableArrayList();
    private static final String CONFIG_FILE_PATH = "config.cfg";
    private final Map<String, String> config = new HashMap(){{
        put("port", "5250");
        put("host", "localhost");
        put("url", "");
        put("sheet", "");
        put("range", "A1:B");
        put("updateInterval", "5000");
        put("channel", "1");
        put("layer", "10");

        put("rrURL", "");
        put("rrSheet", "");
        put("rrRange", "A2:G");
        put("rrUpdateInterval", "1000");
        put("rrChannel", "1");
        put("rrLayer", "10");
        put("pvwChannel", "2");
    }};
    public Map<String, String > getConfig() {
        return config;
    }

    /**
     * Конструктор
     */
    public MainApp() {
        // В качестве образца добавляем некоторые данные
//        templateData.add(new Template("Hans", "Muster"));
//        templateData.add(new Template("Ruth", "Mueller"));
//        templateData.add(new Template("Heinz", "Kurz"));
//        templateData.add(new Template("Cornelia", "Meier"));
//        templateData.add(new Template("Werner", "Meyer"));
//        templateData.add(new Template("Lydia", "Kunz"));
//        templateData.add(new Template("Anna", "Best"));
//        templateData.add(new Template("Stefan", "Meier"));
//        templateData.add(new Template("Martin", "Mueller"));
    }

    /**
     * Возвращает данные в виде наблюдаемого списка адресатов.
     * @return
     */
    public ObservableList<Template> getTemplateData() {
        return templateData;
    }

    public ObservableList<Template> getRemoteTemplateData() {
        return remoteTemplateData;
    }

    public void setTemplatesList(ObservableList<String> templatesList, ObservableList<String> mediaList){
        this.templatesList = templatesList;
        this.mediaList = mediaList;
    }

    public ObservableList<SheetDataProperty> getSheetDataProperties() {
        return sheetDataProperties;
    }


    /**
     * Открывает диалоговое окно для изменения деталей указанного адресата.
     * Если пользователь кликнул OK, то изменения сохраняются в предоставленном
     * объекте адресата и возвращается значение true.
     *
     * @param template - объект адресата, который надо изменить
     * @return true, если пользователь кликнул OK, в противном случае false.
     */
    public boolean showTemplateEditDialog(Template template) {
        try {
            // Загружаем fxml-файл и создаём новую сцену
            // для всплывающего диалогового окна.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TemplateEditDialog.fxml"));
            AnchorPane page = loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Template");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(new Image("avt/caspar/client/icons/icons.png")) ;
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            TemplateEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTemplate(template);
            controller.setTemplatesOrMediaList(templatesList, mediaList);
            if (template.getTemplateOrMedia().equals("Template")) {
                controller.setTemplatesList(templatesList);
            }
            if (template.getTemplateOrMedia().equals("Media")) {
                controller.setTemplatesList(mediaList);
            }
            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CasparCG Client");

        initRootLayout();

        showTemplateOverview();
    }

//    /**
//     * Инициализирует корневой макет и пытается загрузить последний открытый
//     * файл с адресатами.
//     */
    public void initRootLayout() {
        try {
            // Загружаем корневой макет из fxml файла.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Отображаем сцену, содержащую корневой макет.
            Scene scene = new Scene(rootLayout, 1100, 780);
            primaryStage.setScene(scene);
            primaryStage.minHeightProperty().set(780);
            primaryStage.minWidthProperty().set(1100);
            primaryStage.maxHeightProperty().set(1080);
            primaryStage.maxWidthProperty().set(1920);
            primaryStage.getIcons().add(new Image("avt/caspar/client/icons/icons.png")) ;
            // Даём контроллеру доступ к главному прилодению.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    saveConfig();
                    System.exit(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Пытается загрузить последний открытый файл с адресатами.
        File file = getTemplateFilePath();
        if (file != null) {
            loadTemplateDataFromFile(file);
        }
        //Загружаем конфиг
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_FILE_PATH)));
            if (in != null) {
                while (in.ready()) {
                    String inString = in.readLine();
                    config.replace(inString.split(":", 2)[0],inString.split(":", 2)[1]);
                }
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * Показывает в корневом макете сведения об адресатах.
//     */
    public void showTemplateOverview() {
        try {
            // Загружаем сведения об адресатах.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/TemplateOverview.fxml"));
            AnchorPane personOverview = loader.load();

            // Помещаем сведения об адресатах в центр корневого макета.
            rootLayout.setCenter(personOverview);

            // Даём контроллеру доступ к главному приложению.
            TemplateOverviewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setConfig(config);
            controller.initConfigFields();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    /**
//     * Возвращает главную сцену.
//     * @return
//     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

//    /**
//     * Возвращает preference файла адресатов, то есть, последний открытый файл.
//     * Этот preference считывается из реестра, специфичного для конкретной
//     * операционной системы. Если preference не был найден, то возвращается null.
//     *
//     * @return
//     */
    public File getTemplateFilePath() {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

//    /**
//     * Задаёт путь текущему загруженному файлу. Этот путь сохраняется
//     * в реестре, специфичном для конкретной операционной системы.
//     *
//     * @param file - файл или null, чтобы удалить путь
//     */
    public void setTemplateFilePath(File file) {
        Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
        if (file != null) {
            prefs.put("filePath", file.getPath());

            // Обновление заглавия сцены.
            primaryStage.setTitle("CasparCG Client - " + file.getName());
        } else {
            prefs.remove("filePath");

            // Обновление заглавия сцены.
            primaryStage.setTitle("CasparCG Client");
        }
    }

//    /**
//     * Загружает информацию об адресатах из указанного файла.
//     * Текущая информация об адресатах будет заменена.
//     *
//     * @param file
//     */
    public void loadTemplateDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(TemplateListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            // Чтение XML из файла и демаршализация.
            TemplateListWrapper wrapper = (TemplateListWrapper) um.unmarshal(file);

            templateData.clear();
            templateData.addAll(wrapper.getTemplates());

            remoteTemplateData.clear();
            remoteTemplateData.addAll(wrapper.getRemoteTemplates());

            sheetDataProperties.clear();
            sheetDataProperties.addAll(wrapper.getSheetDataProperties());

            // Сохраняем путь к файлу в реестре.
            setTemplateFilePath(file);

        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());
            alert.showAndWait();
        }
    }

//    /**
//     * Сохраняет текущую информацию об адресатах в указанном файле.
//     *
//     * @param file
//     */
public void saveConfig() {
    //Сохраняем конфиг
    try {
        String configString = "";
        BufferedWriter out = new BufferedWriter(new FileWriter(CONFIG_FILE_PATH));
        for (Map.Entry<String, String> entry : config.entrySet()) {
            //out.write(entry.getKey() + ":" + entry.getValue() + "\n");
            configString += (entry.getKey() + ":" + entry.getValue() + "\n");
        }
        out.write(configString);
        out.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    public void saveTemplateDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext
                    .newInstance(TemplateListWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Обёртываем наши данные об адресатах.
            TemplateListWrapper wrapper = new TemplateListWrapper();
            wrapper.setSheetDataProperties(sheetDataProperties);
            wrapper.setTemplates(templateData);
            wrapper.setRemoteTemplates(remoteTemplateData);
            // Маршаллируем и сохраняем XML в файл.
            m.marshal(wrapper, file);

            // Сохраняем путь к файлу в реестре.
            setTemplateFilePath(file);
        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }
}