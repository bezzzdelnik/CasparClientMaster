package avt.caspar.client.controllers;

import java.io.*;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import avt.caspar.client.MainApp;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Контроллер для корневого макета. Корневой макет предоставляет базовый
 * макет приложения, содержащий строку меню и место, где будут размещены
 * остальные элементы JavaFX.
 *
 * @author Marco Jakob
 */
public class RootLayoutController {

    // Ссылка на главное приложение
    private MainApp mainApp;
    private static final String CONFIG_FILE_PATH = "config.cfg";

    /**
     * Вызывается главным приложением, чтобы оставить ссылку на самого себя.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Создаёт пустую адресную книгу.
     */
    @FXML
    private void handleNew() {
        mainApp.getTemplateData().clear();
        mainApp.setTemplateFilePath(null);
    }

    /**
     * Открывает FileChooser, чтобы пользователь имел возможность
     * выбрать адресную книгу для загрузки.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Показываем диалог загрузки файла
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadTemplateDataFromFile(file);
        }
    }

    /**
     * Сохраняет файл в файл адресатов, который в настоящее время открыт.
     * Если файл не открыт, то отображается диалог "save as".
     */
    @FXML
    private void handleSave() throws IOException {
        mainApp.saveConfig();
        File personFile = mainApp.getTemplateFilePath();
        if (personFile != null) {
            mainApp.saveTemplateDataToFile(personFile);
        } else {
            handleSaveAs();
        }
    }

    /**
     * Открывает FileChooser, чтобы пользователь имел возможность
     * выбрать файл, куда будут сохранены данные
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Задаём фильтр расширений
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Показываем диалог сохранения файла
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.saveTemplateDataToFile(file);
        }
    }

    /**
     * Открывает диалоговое окно about.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("CasparCG Client");
        alert.setHeaderText("CasparCG Client v2.0");

        alert.showAndWait();
    }

    @FXML
    private void helpAlert () {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("CasparCG Client - Hot keys");
        alert.setHeaderText("PGM \r\nF1 - Stop \r\nF2 - Play \r\nF5 - Next/Resume \r\nF6 - Update/Pause \r\n" +
                        "\r\n" +
                        "Preview \r\nF9 - Stop \r\nF10 - Play \r\nF11 - Next/Resume \r\nF12 - Update/Pause \r\n");

        alert.showAndWait();
    }

    /**
     * Закрывает приложение.
     */
    @FXML
    private void handleExit() {
        mainApp.saveConfig();
        System.exit(0);
    }




}