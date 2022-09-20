package avt.caspar.client.controllers;

import avt.caspar.client.MainApp;
import avt.caspar.client.model.CasparControl;
import avt.caspar.client.model.SheetDataProperty;
import avt.caspar.client.model.Template;
import avt.caspar.client.model.TemplateProperty;
import avt.caspar.client.util.*;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TemplateOverviewController {
    private MarathonController marathonController;
    private final ObservableList<String> templatesList = FXCollections.observableArrayList();
    private final ObservableList<String> mediaList = FXCollections.observableArrayList();
    private final EchoClient telnet = new EchoClient();
    private final CasparControl cgControl = new CasparControl();
    private TableMultiSelections tableSheetDataMultiSel;
    @FXML
    private Label templateOrMediaLabel;
    @FXML
    private TableView<Template> titleTable;
    @FXML
    private TableColumn<Template, String> templateColumn;
    @FXML
    private TableColumn<Template, String> nameColumn;
    @FXML
    private TableColumn<Template, Integer> idColumn;
    @FXML
    private TableColumn<Template, Boolean> templateOnAir;
    @FXML
    private TableColumn<Template, Boolean> templatePVW;
    @FXML
    private TableView<Template> titleTable1;
    @FXML
    private TableColumn<Template, String> templateColumn1;
    @FXML
    private TableColumn<Template, String> nameColumn1;
    @FXML
    private TableColumn<Template, Integer> idColumn1;
    @FXML
    private TableColumn<Template, Boolean> rrOnAir;
    @FXML
    private TableColumn<Template, Boolean> rrPVW;
    @FXML
    private Label templatePathLabel;
    @FXML
    private Label templateNameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label templateChannelLabel;
    @FXML
    private Label templateVideoLayerLabel;
    @FXML
    private Label templateFlashLayerLabel;
    @FXML
    private TextField hostField;
    @FXML
    private TextField portField;
    @FXML
    private TextArea reportArea;
    @FXML
    private Button connectButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button nextButton;
    @FXML
    private TableView<TemplateProperty> propertyTableView;
    @FXML
    private TableColumn<TemplateProperty, String> propertyKey;
    @FXML
    private TableColumn<TemplateProperty, String> propertyValue;
    @FXML
    private TableColumn<SheetDataProperty, String> columnSheetID;
    @FXML
    private TableColumn<SheetDataProperty, String> columnSheetData;
    @FXML
    private TableColumn<SheetDataProperty, String> columnSheetData2;
    @FXML
    private TableView<SheetDataProperty> tableSheetData;
    @FXML
    private ComboBox<String> selectTemplateBox;
    @FXML
    private TextField sheetDataURLField;
    @FXML
    private TextField sheetNameField;
    @FXML
    private TextField sheetRangeField;
    @FXML
    private TextField updateIntervalField;
    @FXML
    private TextField channelField;
    @FXML
    private TextField layerField;
    @FXML
    private TextField rrURL;
    @FXML
    private TextField rrSheet;
    @FXML
    private TextField rrRange;
    @FXML
    private TextField rrUpdateIntervalField;
    @FXML
    private TextField rrChannelField;
    @FXML
    private TextField rrLayerField;
    @FXML
    private CheckBox autoUpdateCheckBox;
    @FXML private TextField pvwChannel;
    @FXML private Button updateDataButton1;
    @FXML private CheckBox autoUpdateNewsCheckBox;

    @FXML private Label urlPathLabel;
    @FXML private Label urlPathLabel1;
    @FXML private ComboBox choiceLR;
    @FXML private ComboBox choiceLR1;
    @FXML private Button selectFileButton;
    @FXML private Button selectFileButton1;
    @FXML private AnchorPane marathonPane;
    private final ObservableList<String> choiceLRList = FXCollections.observableArrayList("Google", "Local");

    private Template tempTemplate = new Template();
    private boolean isConnected = false;
    private String connected;
    private String templateString;
    private String mediaString;

    private String selectedTemplate;

    private Map<String, String> config;
    // Ссылка на главное приложение.
    private MainApp mainApp;
    private TimerClass newTimer;
    private boolean isTimerRunning = false;
    private Timer timer;
    private boolean isUpdated = false;
    private TemplateUpdater templateUpdater;


    public TemplateOverviewController() {
    }

    public TableView<SheetDataProperty> getTableSheetData() {
        return tableSheetData;
    }

    public void setTableSheetData(TableView<SheetDataProperty> tableSheetData) {
        this.tableSheetData = tableSheetData;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }


    //СОЕДИНЕНИЕ С КАСПАРОМ
    @FXML
    private void connectButton() {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws NullPointerException {
                        Platform.runLater(() -> {
                            try {
                                if (!isConnected()) {
                                    telnet.setHost(hostField.getText());
                                    telnet.setPort(Integer.parseInt(portField.getText()));
                                    telnet.telnetConnection();
                                    telnet.setCommand("VERSION SERVER");
                                    telnet.telnetSendCommand();
                                    reportArea.appendText(telnet.getInBuffer().readLine() + '\n');
                                    while (telnet.getInBuffer().ready()) {
                                        connected = telnet.getInBuffer().readLine() + '\n';
                                    }
                                    loadTemplate();
                                    reportArea.appendText("Connected " + connected + '\n');
                                    connectButton.setText("Disconnect");
                                    isConnected = true;

                                } else {
                                    closeConnection();
                                }
                            } catch (Exception e) {
                                if (e instanceof IOException) {
                                    try {
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.initOwner(mainApp.getPrimaryStage());
                                        alert.setTitle("Server is not available");
                                        alert.setHeaderText("Server is not available");
                                        alert.setContentText("Please check server.");
                                        alert.showAndWait();
                                    } catch (Exception ignored) {

                                    }

                                }
                            }
                        });
                        return null;
                    }
                };
            }
        };
        service.start();
    }

    private void closeConnection() {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws NullPointerException {
                        Platform.runLater(() -> {
                            try {
                                telnet.closeConnections();
                                telnet.setInBuffer(null);
                                telnet.setPingSocket(null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            reportArea.appendText("Disconnected" + '\n');
                            connectButton.setText("Connect");
                            isConnected = false;
                        });
                        return null;
                    }
                };
            }
        };
        service.start();
    }


    //СОЕДИНЕНИЕ С MySQL
    @FXML private TextField mySQLHostField, mySQLPortField, mySQLLoginField, mySQLDatabaseField;
    @FXML private PasswordField mySQLPasswordField;
    @FXML private Button connectMySQLButton;
    private boolean isConnectedMySQL = false;
    private static Connection connection;
    private static Statement stmt;
    private static ResultSet rs;

    public Connection getConnection() {
        return connection;
    }

    @FXML
    private void connectMySQLFunction() {

        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws NullPointerException {
                        Platform.runLater(() -> {
                            try {
                                if (!isConnectedMySQL) {
                                    //КОД ПОДКЛЮЧЕНИЯ
                                    String url = String.format("jdbc:mysql://%s:%s/%s",
                                            mySQLHostField.getText(), mySQLPortField.getText(), mySQLDatabaseField.getText());
                                    connection = (Connection) DriverManager.getConnection(url, mySQLLoginField.getText(), mySQLPasswordField.getText());

                                    connectMySQLButton.setText("Disconnect");
                                    isConnectedMySQL = true;

                                } else {
                                    closeMySQLConnection();
                                }
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        });
                        return null;
                    }
                };
            }
        };
        service.start();
    }

    private void closeMySQLConnection() {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws NullPointerException {
                        Platform.runLater(() -> {
                            //КОД ОТКЛЮЧЕНИЯ
                            try {
                                connection.close();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            connectMySQLButton.setText("Connect");

                        });
                        return null;
                    }
                };
            }
        };
        service.start();
    }




    @FXML
    private void loadChoiceBox() {
        selectTemplateBox.setItems(templatesList);
    }

    private void loadTemplate() {
        //templateArray.clear();
        templatesList.clear();
        new Thread(() -> {
            telnet.setCommand("TLS");
            telnet.telnetSendCommand();
            try {
                reportArea.appendText(telnet.getInBuffer().readLine() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    templateString = telnet.getInBuffer().readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!templateString.isEmpty()) {
                    templatesList.add(templateString);
                } else {
                    try {
                        Thread.sleep(500);
                        loadMedia();
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            loadChoiceBox();
            Thread.currentThread().interrupt();
        }, "loadTemplateThread").start();

    }

    private void loadMedia() throws IOException {
        //mediaArray.clear();
        mediaList.clear();
        new Thread(() -> {
            telnet.setCommand("CLS");
            telnet.telnetSendCommand();
            try {
                reportArea.appendText(telnet.getInBuffer().readLine() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    if (!telnet.getInBuffer().ready()) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaString = telnet.getInBuffer().readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!mediaString.isEmpty()) {
                    mediaList.add(mediaString);
                } else {
                    break;
                }
            }
            Thread.currentThread().interrupt();
        }, "loadMediaThread").start();
    }

    @FXML
    private void playTemplate() {
        if (isConnected()) {
            cgControl.setCasparControl(tempTemplate, reportArea, telnet, tempTemplate.getTemplateChannel());
            if (templateOrMediaLabel.getText().equals("Template")) {
                try {
                    cgControl.cgPlay();
                    tempTemplate.setTemplateOnAir(true);
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
            if (templateOrMediaLabel.getText().equals("Media")) {
                try {
                    cgControl.mediaPlay();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void updateTemplate() {
        if (isConnected()) {
            cgControl.setCasparControl(tempTemplate, reportArea, telnet, tempTemplate.getTemplateChannel());
            if (templateOrMediaLabel.getText().equals("Template")) {
                try {
                    cgControl.cgUpdate();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
            if (templateOrMediaLabel.getText().equals("Media")) {
                try {
                    cgControl.mediaPause();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void nextTemplate() {
        if (isConnected()) {
            cgControl.setCasparControl(tempTemplate, reportArea, telnet, tempTemplate.getTemplateChannel());
            if (templateOrMediaLabel.getText().equals("Template")) {
                try {
                    cgControl.cgNext();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
            if (templateOrMediaLabel.getText().equals("Media")) {
                try {
                    cgControl.mediaResume();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void stopTemplate() {
        if (isConnected()) {
            cgControl.setCasparControl(tempTemplate, reportArea, telnet, tempTemplate.getTemplateChannel());
            if (templateOrMediaLabel.getText().equals("Template")) {
                try {
                    cgControl.cgStop();
                    tempTemplate.setTemplateOnAir(false);
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
            if (templateOrMediaLabel.getText().equals("Media")) {
                try {
                    cgControl.mediaStop();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void playPVWTemplate() {
        if (isConnected()) {
            cgControl.setCasparControl(tempTemplate, reportArea, telnet, Integer.parseInt(pvwChannel.getText()));
            if (templateOrMediaLabel.getText().equals("Template")) {
                try {
                    cgControl.cgPlay();
                    tempTemplate.setTemplatePVW(true);
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
            if (templateOrMediaLabel.getText().equals("Media")) {
                try {
                    cgControl.mediaPlay();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void updatePVWTemplate() {
        if (isConnected()) {
            cgControl.setCasparControl(tempTemplate, reportArea, telnet, Integer.parseInt(pvwChannel.getText()));
            if (templateOrMediaLabel.getText().equals("Template")) {
                try {
                    cgControl.cgUpdate();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
            if (templateOrMediaLabel.getText().equals("Media")) {
                try {
                    cgControl.mediaPause();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void nextPVWTemplate() {
        if (isConnected()) {
            cgControl.setCasparControl(tempTemplate, reportArea, telnet, Integer.parseInt(pvwChannel.getText()));
            if (templateOrMediaLabel.getText().equals("Template")) {
                try {
                    cgControl.cgNext();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
            if (templateOrMediaLabel.getText().equals("Media")) {
                try {
                    cgControl.mediaResume();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void stopPVWTemplate() {
        if (isConnected()) {
            cgControl.setCasparControl(tempTemplate, reportArea, telnet, Integer.parseInt(pvwChannel.getText()));
            if (templateOrMediaLabel.getText().equals("Template")) {
                try {
                    cgControl.cgStop();
                    tempTemplate.setTemplatePVW(false);
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
            if (templateOrMediaLabel.getText().equals("Media")) {
                try {
                    cgControl.mediaStop();
                } catch (IOException e) {
                    closeConnection();
                    e.printStackTrace();
                }
            }
        }
    }

    public void initConfigFields() {
        hostField.setText(config.get("host"));
        portField.setText(config.get("port"));
        sheetDataURLField.setText(config.get("url"));
        sheetNameField.setText(config.get("sheet"));
        sheetRangeField.setText(config.get("range"));
        updateIntervalField.setText(config.get("updateInterval"));
        channelField.setText(config.get("channel"));
        layerField.setText(config.get("layer"));

        rrURL.setText(config.get("rrURL"));
        rrSheet.setText(config.get("rrSheet"));
        rrRange.setText(config.get("rrRange"));
        rrUpdateIntervalField.setText(config.get("rrUpdateInterval"));
        rrChannelField.setText(config.get("rrChannel"));
        rrLayerField.setText(config.get("rrLayer"));

        pvwChannel.setText(config.get("pvwChannel"));

        hostField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("host", newValue));
        portField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("port", newValue));
        sheetDataURLField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("url", newValue));
        sheetNameField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("sheet", newValue));
        sheetRangeField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("range", newValue));
        updateIntervalField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("updateInterval", newValue));
        channelField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("channel", newValue));
        layerField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("layer", newValue));
        rrURL.textProperty().addListener((observable, oldValue, newValue) -> config.replace("rrURL", newValue));
        rrSheet.textProperty().addListener((observable, oldValue, newValue) -> config.replace("rrSheet", newValue));
        rrRange.textProperty().addListener((observable, oldValue, newValue) -> config.replace("rrRange", newValue));
        rrUpdateIntervalField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("rrUpdateInterval", newValue));
        rrChannelField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("rrChannel", newValue));
        rrLayerField.textProperty().addListener((observable, oldValue, newValue) -> config.replace("rrLayer", newValue));

        pvwChannel.textProperty().addListener((observable, oldValue, newValue) -> config.replace("pvwChannel", newValue));
    }

    //    /**
//     * Инициализация класса-контроллера. Этот метод вызывается автоматически
//     * после того, как fxml-файл будет загружен.
//     */
    @FXML
    private void initialize() {
        loadMarathonTab();
        tableSheetData.setStyle("-fx-selection-bar: rgb(0, 80, 100);; -fx-selection-bar-non-focused: rgb(0, 80, 100);;");
        choiceLR1.setItems(choiceLRList);
        choiceLR.setItems(choiceLRList);
        selectFileButton.setDisable(true);
        choiceLR.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == "Google") {
                selectFileButton.setDisable(true);
                urlPathLabel.setText("URL");
            }
            else {
                selectFileButton.setDisable(false);
                urlPathLabel.setText("File");
            }
        });
        choiceLR1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == "Google") {
                selectFileButton1.setDisable(true);
                urlPathLabel1.setText("URL");
            }
            else {
                selectFileButton1.setDisable(false);
                urlPathLabel1.setText("File");
            }
        });

        titleTable.setOnKeyReleased(event -> {
            if (event.getCode().getName().equals("F2")) {
                playTemplate();
            }
            if (event.getCode().getName().equals("F1")) {
                stopTemplate();
            }
            if (event.getCode().getName().equals("F5")) {
                updateTemplate();
            }
            if (event.getCode().getName().equals("F6")) {
                nextTemplate();
            }

            if (event.getCode().getName().equals("F10")) {
                playPVWTemplate();
            }
            if (event.getCode().getName().equals("F9")) {
                stopPVWTemplate();
            }
            if (event.getCode().getName().equals("F11")) {
                updatePVWTemplate();
            }
            if (event.getCode().getName().equals("F12")) {
                nextPVWTemplate();
            }
        });

        layerField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                layerField.setText(newValue.replaceAll("[^\\d\\s]", "").replaceAll("\\s+", ""));
            }
        });
        channelField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                channelField.setText(newValue.replaceAll("[^\\d\\s]", "").replaceAll("\\s+", ""));
            }
        });
        updateIntervalField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                updateIntervalField.setText(newValue.replaceAll("[^\\d\\s]", "").replaceAll("\\s+", ""));
            }
        });
        pvwChannel.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pvwChannel.setText(newValue.replaceAll("[^\\d\\s]", "").replaceAll("\\s+", ""));
            }
        });
        // Инициализация таблицы адресатов с двумя столбцами.
        templateColumn.setCellValueFactory(
                cellData -> cellData.getValue().templatePathProperty());
        nameColumn.setCellValueFactory(
                cellData -> cellData.getValue().templateNameProperty());
        idColumn.setCellValueFactory(cellData -> cellData.getValue().templateIdProperty().asObject());
        templateOnAir.setCellValueFactory(cellData -> cellData.getValue().templateOnAirProperty());
        templateOnAir.setCellFactory(CheckBoxTableCell.forTableColumn(templateOnAir));
        templatePVW.setCellValueFactory(cellData -> cellData.getValue().templatePVWProperty());
        templatePVW.setCellFactory(CheckBoxTableCell.forTableColumn(templatePVW));

        propertyKey.setCellValueFactory(
                cellData -> cellData.getValue().templateKeyProperty());
        propertyValue.setCellValueFactory(cellData -> cellData.getValue().templateValueProperty());

        columnSheetData.setCellValueFactory(
                cellData -> cellData.getValue().sheetDataProperty());
        columnSheetData.setEditable(true);
        columnSheetData.setCellFactory(TextFieldTableCell.forTableColumn());
        columnSheetData2.setCellValueFactory(
                cellData -> cellData.getValue().sheetData2Property());
        columnSheetData2.setEditable(true);
        columnSheetData2.setCellFactory(TextFieldTableCell.forTableColumn());
        columnSheetID.setCellValueFactory(
                cellData -> cellData.getValue().sheetIDProperty());
        columnSheetID.setEditable(true);
        columnSheetID.setCellFactory(TextFieldTableCell.forTableColumn());


        selectTemplateBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> selectedTemplate = newValue);


        // Очистка дополнительной информации об адресате.
        showTemplateDetails(null);

        templateOrMediaLabel.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.equals("Template")) {
                        updateButton.setText("Update");
                        nextButton.setText("Next");
                    }
                    if (newValue.equals("Media")) {
                        updateButton.setText("Pause");
                        nextButton.setText("Resume");
                    }
                }
        );

        // Слушаем изменения выбора, и при изменении отображаем
        // дополнительную информацию об адресате.


        titleTable.setOnMouseClicked(event -> {
            tempTemplate = titleTable.getSelectionModel().getSelectedItem();
            showTemplateDetails(tempTemplate);
            if (isAlreadyOneClick) {
                handleEditTemplate();
                isAlreadyOneClick = false;
            } else {
                isAlreadyOneClick = true;
                Timer t = new Timer("doubleclickTimer", false);
                t.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        isAlreadyOneClick = false;
                    }
                }, 180);
            }
        });
        titleTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    tempTemplate = titleTable.getSelectionModel().getSelectedItem();
                    showTemplateDetails(tempTemplate);
                });


        TableMultiSelections titleTableMultiSel = new TableMultiSelections(titleTable, new DataFormat("application/x-java-serialized-object2"));
        titleTableMultiSel.setMultiSelection();

        tableSheetDataMultiSel = new TableMultiSelections(tableSheetData, new DataFormat("application/x-java-serialized-object1"));
        tableSheetDataMultiSel.setMultiSelection();

        initRemoteRundownTable();
        connectButton();
    }

    private void loadMarathonTab() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/Marathon.fxml"));
        AnchorPane mTab = null;
        try {
            mTab = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Помещаем сведения об адресатах в центр корневого макета.
        marathonPane.getChildren().add(mTab);
        marathonController = loader.getController();
        marathonController.setTemplateOverviewController(this);

    }
    private boolean isAlreadyOneClick;

    private void initRemoteRundownTable() {
        if (tableSheetData.getSelectionModel().getSelectedIndex() >= 0) {
            newTimer.setCounter(tableSheetData.getSelectionModel().getSelectedIndex());
            newTimer.setDataString();
            newTimer.cgPlay();
            isTimerRunning = true;
        }

        tableSheetData.setOnKeyReleased(event -> {
            if (event.getCode().getName().equals("Space")) {
                if (tableSheetData.getSelectionModel().getSelectedIndex() >= 0) {
                    setUpdatedTemplateCounter();
                }
            }
        });

        titleTable1.setOnKeyReleased(event -> {
            if (event.getCode().getName().equals("F2")) {
                playTemplate();
            }
            if (event.getCode().getName().equals("F1")) {
                stopTemplate();
            }
            if (event.getCode().getName().equals("F5")) {
                updateTemplate();
            }
            if (event.getCode().getName().equals("F6")) {
                nextTemplate();
            }

            if (event.getCode().getName().equals("F10")) {
                playPVWTemplate();
            }
            if (event.getCode().getName().equals("F9")) {
                stopPVWTemplate();
            }
            if (event.getCode().getName().equals("F11")) {
                updatePVWTemplate();
            }
            if (event.getCode().getName().equals("F12")) {
                nextPVWTemplate();
            }
        });

        templateColumn1.setCellValueFactory(
                cellData -> cellData.getValue().templatePathProperty());
        nameColumn1.setCellValueFactory(
                cellData -> cellData.getValue().templateNameProperty());
        idColumn1.setCellValueFactory(cellData -> cellData.getValue().templateIdProperty().asObject());
        rrOnAir.setCellValueFactory(cellData -> cellData.getValue().templateOnAirProperty());
        rrOnAir.setCellFactory(CheckBoxTableCell.forTableColumn(rrOnAir));
        rrPVW.setCellValueFactory(cellData -> cellData.getValue().templatePVWProperty());
        rrPVW.setCellFactory(CheckBoxTableCell.forTableColumn(rrPVW));

        titleTable1.setOnMouseClicked(event -> {
            tempTemplate = titleTable1.getSelectionModel().getSelectedItem();
            showTemplateDetails(tempTemplate);
        });

        titleTable1.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    tempTemplate = titleTable1.getSelectionModel().getSelectedItem();
                    showTemplateDetails(tempTemplate);
                });


        rrLayerField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                layerField.setText(newValue.replaceAll("[^\\d\\s]", "").replaceAll("\\s+", ""));
            }
        });
        rrChannelField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                channelField.setText(newValue.replaceAll("[^\\d\\s]", "").replaceAll("\\s+", ""));
            }
        });
        rrUpdateIntervalField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                updateIntervalField.setText(newValue.replaceAll("[^\\d\\s]", "").replaceAll("\\s+", ""));
            }
        });

    }

    //    /**
//     * Вызывается главным приложением, которое даёт на себя ссылку.
//     *
//     * @param mainApp
//     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Добавление в таблицу данных из наблюдаемого списка
        titleTable.setItems(mainApp.getTemplateData());
        titleTable1.setItems(mainApp.getRemoteTemplateData());
        tableSheetData.setItems(mainApp.getSheetDataProperties());
        templateUpdater = new TemplateUpdater(mainApp, rrURL, rrSheet, rrRange, rrChannelField, rrLayerField);
    }

    //    /**
//     * Заполняет все текстовые поля, отображая подробности об адресате.
//     * Если указанный адресат = null, то все текстовые поля очищаются.
//     *
//     * @param template — адресат типа Person или null
//     */
    protected void showTemplateDetails(Template template) {
        if (template != null) {
            // Заполняем метки информацией из объекта template.
            templatePathLabel.setText(template.getTemplatePath());
            templateChannelLabel.setText(Integer.toString(template.getTemplateChannel()));
            templateVideoLayerLabel.setText(Integer.toString(template.getTemplateVideoLayer()));
            templateFlashLayerLabel.setText(Integer.toString(template.getTemplateFlashLayer()));
            templateNameLabel.setText(template.getTemplateName());
            templateOrMediaLabel.setText(template.getTemplateOrMedia());
            idLabel.setText(Integer.toString(template.getTemplateId()));
            propertyTableView.setItems(template.getTemplateProperties());

        } else {
            // Если Template = null, то убираем весь текст.
            templateChannelLabel.setText("");
            templateVideoLayerLabel.setText("");
            templateFlashLayerLabel.setText("");
            templatePathLabel.setText("");
            templateNameLabel.setText("");
            templateOrMediaLabel.setText("");
            idLabel.setText("");
            propertyTableView.getItems().clear();
        }
    }

    //    /**
//     * Вызывается, когда пользователь кликает по кнопке удаления.
//     */
    @FXML
    private void handleDeleteTemplate() {
        int selectedIndex = titleTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            //titleTable.getItems().remove(selectedIndex);
            titleTable.getItems().removeAll(titleTable.getSelectionModel().getSelectedItems());
        } else {
            // Ничего не выбрано.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Template Selected");
            alert.setContentText("Please select a template in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void updateSheetData() throws GeneralSecurityException, IOException {
        if (choiceLR.getSelectionModel().getSelectedItem() == "Google") {
            SheetsQuickstart newSheet = new SheetsQuickstart();
            if (!sheetDataURLField.getText().split("/")[5].equals("") && sheetDataURLField.getText().split("/")[5] != null &&
                    !sheetNameField.getText().equals("") && sheetNameField.getText() != null &&
                    !sheetRangeField.getText().equals("") && sheetRangeField.getText() != null) {
                List<List<Object>> values = newSheet.getSheetData(sheetDataURLField.getText().split("/")[5],
                        sheetNameField.getText() + "!" + sheetRangeField.getText());
                mainApp.getSheetDataProperties().clear();
                for (List<Object> row : values) {
                    if (row.size() > 0) {
                        SheetDataProperty newProperty = new SheetDataProperty(row.get(0).toString(), row.get(1).toString(), row.get(2).toString());
                        mainApp.getSheetDataProperties().add(newProperty);
                    }
                }
            }
        } if (choiceLR.getSelectionModel().getSelectedItem() == "Local") {
            ExcelRead excelRead = new ExcelRead();
            if (!sheetDataURLField.getText().equals("") && sheetDataURLField.getText() != null &&
                    !sheetNameField.getText().equals("") && sheetNameField.getText() != null &&
                    !sheetRangeField.getText().equals("") && sheetRangeField.getText() != null) {
                ArrayList<ArrayList> values = excelRead.excelReader(sheetDataURLField.getText(),
                        sheetNameField.getText(), sheetRangeField.getText());
                mainApp.getSheetDataProperties().clear();
                for (ArrayList row : values) {
                    SheetDataProperty newProperty = new SheetDataProperty(row.get(0).toString(), row.get(1).toString(), row.get(2).toString());
                    mainApp.getSheetDataProperties().add(newProperty);
                }
            }
        }
    }

    @FXML
    private void updateRemoteTemplateData() {
        if (autoUpdateCheckBox.isSelected()) {
            if (!isUpdated) {
                isUpdated = true;
                updateDataButton1.setText("Stop update");
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            if (choiceLR1.getSelectionModel().getSelectedItem() == "Google") {
                                templateUpdater.autoUpdateRemoteTemplate();
                            }
                            if (choiceLR1.getSelectionModel().getSelectedItem() == "Local") {
                                templateUpdater.autoUpdateLocalExcelTemplate();
                            }
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                timer = new Timer("UpdateRemoteRundown");//create a new Timer
                timer.scheduleAtFixedRate(timerTask, 0, Long.parseLong(rrUpdateIntervalField.getText()));
            } else {
                stopAutoUpdateRemoteTemplate();
            }
        } else {
            try {
                if (choiceLR1.getSelectionModel().getSelectedItem() == "Google") {
                    templateUpdater.updateRemoteTemplate();
                }
                if (choiceLR1.getSelectionModel().getSelectedItem() == "Local") {
                    templateUpdater.updateLocalExcelTemplate();
                }
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopAutoUpdateRemoteTemplate() {
        timer.cancel();
        isUpdated = false;
        updateDataButton1.setText("Update Data");
    }

    @FXML private void copyToLocalRundown() {
        mainApp.getTemplateData().add(titleTable1.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void handleNewTickerString() {
        SheetDataProperty tempSheetData = new SheetDataProperty("", "", "");
        mainApp.getSheetDataProperties().add(tempSheetData);
    }

    @FXML
    private void handleDeleteTickerString() {
        SheetDataProperty selectedSheetData = tableSheetData.getSelectionModel().getSelectedItem();
        mainApp.getSheetDataProperties().remove(selectedSheetData);
    }

    @FXML
    private void handleNewTemplate() {
        mainApp.setTemplatesList(templatesList, mediaList);
        Template tempTemplate = new Template();
        boolean okClicked = mainApp.showTemplateEditDialog(tempTemplate);
        if (okClicked) {
            mainApp.getTemplateData().add(tempTemplate);
        }
    }

    @FXML
    private void handleDuplicateTemplate() {
        Template selectedTemplate = titleTable.getSelectionModel().getSelectedItem();
        Template tempTemplate = new Template();
        ObservableList<TemplateProperty> tempTemplateProperties = FXCollections.observableArrayList();
        for (TemplateProperty property : selectedTemplate.getTemplateProperties()) {
            tempTemplateProperties.add(new TemplateProperty(property.getTemplateKey(), property.getTemplateValue()));
        }

        tempTemplate.setTemplateChannel(selectedTemplate.getTemplateChannel());
        tempTemplate.setTemplateVideoLayer(selectedTemplate.getTemplateVideoLayer());
        tempTemplate.setTemplateFlashLayer(selectedTemplate.getTemplateFlashLayer());
        tempTemplate.setTemplatePath(selectedTemplate.getTemplatePath());
        tempTemplate.setTemplateName(selectedTemplate.getTemplateName());
        tempTemplate.setTemplateOrMedia(selectedTemplate.getTemplateOrMedia());

        tempTemplate.setTemplateId(selectedTemplate.getTemplateId());
        tempTemplate.setTemplateProperties(tempTemplateProperties);

        mainApp.getTemplateData().add(tempTemplate);
    }

    //    /**
//     * Вызывается, когда пользователь кликает по кнопка Edit...
//     * Открывает диалоговое окно для изменения выбранного адресата.
//     */
    @FXML
    protected void handleEditTemplate() {
        mainApp.setTemplatesList(templatesList, mediaList);
        Template selectedTemplate = titleTable.getSelectionModel().getSelectedItem();
        if (selectedTemplate != null) {
            boolean okClicked = mainApp.showTemplateEditDialog(selectedTemplate);
            if (okClicked) {
                showTemplateDetails(selectedTemplate);
            }

        } else {
            // Ничего не выбрано.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Template Selected");
            alert.setContentText("Please select a template in the table.");

            alert.showAndWait();
        }
    }

    @FXML
    private void startUpdatedTemplate() {
        if (!isTimerRunning && mainApp.getSheetDataProperties().size() > 0
                && selectedTemplate != null && selectedTemplate != "" && isConnected) {
            tableSheetDataMultiSel.setSingleSelection();
            newTimer = new TimerClass();
            newTimer.setMainApp(mainApp);
            newTimer.setTemplate(selectedTemplate);
            newTimer.setUpdatePeriod(Integer.parseInt(updateIntervalField.getText()));
            newTimer.setTelnet(telnet);
            newTimer.setChannel(Integer.parseInt(channelField.getText()));
            newTimer.setLayer(Integer.parseInt(layerField.getText()));
            newTimer.setReportArea(reportArea);
            newTimer.setTemplateOverviewController(this);
            autoUpdateNewsCheckBox.setDisable(true);
            tableSheetData.setEditable(false);
            try {
                if (autoUpdateNewsCheckBox.isSelected()) {
                    newTimer.startTimer();
                    isTimerRunning = true;
                } else {
                    if (tableSheetData.getSelectionModel().getSelectedIndex() >= 0) {
                        newTimer.setCounter(tableSheetData.getSelectionModel().getSelectedIndex());
                        newTimer.setDataString();
                        newTimer.cgPlay();
                        isTimerRunning = true;
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void stopUpdatedTemplate() {
        autoUpdateNewsCheckBox.setDisable(false);
        tableSheetData.setEditable(true);
        if (isTimerRunning) {
            if (autoUpdateNewsCheckBox.isSelected()) {
                newTimer.stopTimer();
                tableSheetDataMultiSel.setMultiSelection();
            } else {
                newTimer.cgStop();
            }
            isTimerRunning = false;
        }
    }

    @FXML
    private void setUpdatedTemplateCounter() {
        if (isTimerRunning) {
            newTimer.setCounter(tableSheetData.getSelectionModel().getSelectedIndex());
            if (autoUpdateNewsCheckBox.isSelected()) {
            } else {
                newTimer.setDataString();
                newTimer.cgUpdate();
            }
        }
    }

    public void stopConnectionAndUpdate() {
        isTimerRunning = false;
        closeConnection();
    }

    @FXML
    private void clearLog() {
        reportArea.clear();
    }

    @FXML private void openFile() {
        sheetDataURLField.setText(choiceFile());
    }

    @FXML private void openRRFile() {
        rrURL.setText(choiceFile());
    }

    private String choiceFile() {
        String filePath = null;

        FileChooser fileChooser = new FileChooser();

        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "EXCEL files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        // Показываем диалог загрузки файла
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            filePath = file.getPath();
        }

        return filePath;
    }
}