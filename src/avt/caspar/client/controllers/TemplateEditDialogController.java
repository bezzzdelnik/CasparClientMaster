package avt.caspar.client.controllers;

import avt.caspar.client.MainApp;
import avt.caspar.client.model.TemplateProperty;
import avt.caspar.client.util.TextAreaTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import avt.caspar.client.model.Template;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Окно для изменения информации об адресате.
 *
 * @author Marco Jakob
 */
public class TemplateEditDialogController {
    @FXML
    private Label templatePathLabel;
    @FXML
    private TextField templateValue1Field;
    private final ObservableList<String> st = FXCollections.observableArrayList("Template", "Media");
    @FXML ChoiceBox choiceTemplateOrMedia;
    @FXML
    private TextField idField;
    @FXML
    private Spinner<Integer> templateChannelSpinner;
    @FXML
    private Spinner<Integer> templateVideoLayerSpinner;
    @FXML
    private Spinner<Integer> templateFlashLayerSpinner;
    @FXML
    protected ListView templateListView;
    @FXML
    protected TableView<TemplateProperty> propertyTableViewEdit;
    @FXML
    private TableColumn<TemplateProperty, String> propertyKeyEdit;
    @FXML
    private TableColumn<TemplateProperty, String> propertyValueEdit;
    @FXML MenuItem setTemplateNameMenu;

    public TableContextMenu tableContextMenu = new TableContextMenu();

    protected ObservableList<TemplateProperty> tempTemplateProperties = FXCollections.observableArrayList();
    private ObservableList<String> templatesList;
    private ObservableList<String> mediaList;

    public void setTemplatesList(ObservableList<String> templatesList){
        templateListView.setItems(templatesList);
    }

    private String currentItemSelected;
    private Stage dialogStage;
    protected Template template;
    private boolean okClicked = false;
    private String templateName = "";
    //private final String filePath = "C:\\CasparCG\\CasparCG 2.3.0\\Server\\template";
    int selectedIndex = -1;
    /**
     * Инициализирует класс-контроллер. Этот метод вызывается автоматически
     * после того, как fxml-файл будет загружен.
     */
    @FXML
    private void initialize() throws IOException {
        setTemplateNameMenu.setDisable(true);

        propertyTableViewEdit.setOnMouseClicked(event -> {
            selectedIndex = propertyTableViewEdit.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                templateName = propertyTableViewEdit.getSelectionModel().selectedItemProperty().getValue().getTemplateValue();
                setTemplateNameMenu.setDisable(false);
            } else setTemplateNameMenu.setDisable(true);
        });

        tableContextMenu.setTedc(this);
        propertyKeyEdit.setCellValueFactory(
                cellData -> cellData.getValue().templateKeyProperty());
        propertyValueEdit.setCellValueFactory(cellData -> cellData.getValue().templateValueProperty());

        propertyKeyEdit.setEditable(true);
        propertyKeyEdit.setCellFactory(TextAreaTableCell.forTableColumn());
        propertyValueEdit.setEditable(true);
        propertyValueEdit.setCellFactory(TextAreaTableCell.forTableColumn());
        choiceTemplateOrMedia.setItems(st);

        choiceTemplateOrMedia.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue.equals("Template")){
                        templateListView.setItems(templatesList);
                        templateVideoLayerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20));
                    }
                    if (newValue.equals("Media")){
                        templateListView.setItems(mediaList);
                        templateVideoLayerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10));
                    }
                });


        templateListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent click) {

                if (click.getClickCount() == 2) {
                    //Use ListView's getSelected Item
                    currentItemSelected = (String) templateListView.getSelectionModel().getSelectedItem();
                    //use this to do whatever you want to. Open Link etc.
                    templatePathLabel.setText(currentItemSelected);
                }
            }
        });

    }

    @FXML private void handleDeleteTemplateProperties() {
        tableContextMenu.handleDeleteTemplateProperties();
    }

    @FXML private void handleNewTemplateProperties() {
        tableContextMenu.handleNewTemplateProperties();
    }

    @FXML private void setTemplateName() {
        //TemplateProperty tempTemplateProperty = propertyTableViewEdit.getSelectionModel().getSelectedItem();
        templateValue1Field.setText(templateName);
    }

    /**
     * Устанавливает сцену для этого окна.
     *
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    /**
     * Задаёт адресата, информацию о котором будем менять.
     *
     * @param template
     */



    public void setTemplate(Template template) {
        this.template = template;
        this.tempTemplateProperties = template.getTemplateProperties();
        templatePathLabel.setText(template.getTemplatePath());
        templateValue1Field.setText(template.getTemplateName());
        propertyTableViewEdit.getItems().addAll(template.getTemplateProperties());
        choiceTemplateOrMedia.setValue(template.getTemplateOrMedia());

        templateChannelSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, template.getTemplateChannel()));
        templateVideoLayerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, template.getTemplateVideoLayer()));
        templateFlashLayerSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, template.getTemplateFlashLayer()));

        idField.setText(Integer.toString(template.getTemplateId()));
    }

    /**
     * Returns true, если пользователь кликнул OK, в другом случае false.
     *
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Вызывается, когда пользователь кликнул по кнопке OK.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            template.setTemplateChannel(templateChannelSpinner.getValue());
            template.setTemplateVideoLayer(templateVideoLayerSpinner.getValue());
            template.setTemplateFlashLayer(templateFlashLayerSpinner.getValue());
            template.setTemplatePath(templatePathLabel.getText());
            template.setTemplateName(templateValue1Field.getText());
            template.setTemplateId(Integer.parseInt(idField.getText()));

            template.setTemplateProperties(tempTemplateProperties);
            template.setTemplateOrMedia(choiceTemplateOrMedia.getSelectionModel().getSelectedItem().toString());

            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Вызывается, когда пользователь кликнул по кнопке Cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Проверяет пользовательский ввод в текстовых полях.
     *
     * @return true, если пользовательский ввод корректен
     */
    private boolean isInputValid() {
        String errorMessage = "";

/*        if (templateValue1Field.getText() == null || templateValue1Field.getText().length() == 0) {
            errorMessage += "No valid first name!\n";
        }
        if (templateValue2Field.getText() == null || templateValue2Field.getText().length() == 0) {
            errorMessage += "No valid last name!\n";
        }
        if (templateValue3Field.getText() == null || templateValue3Field.getText().length() == 0) {
            errorMessage += "No valid street!\n";
        }

        if (idField.getText() == null || idField.getText().length() == 0) {
            errorMessage += "No valid postal code!\n";
        } else {
            // пытаемся преобразовать почтовый код в int.
            try {
                Integer.parseInt(idField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid id (must be an integer)!\n";
            }
        }

        if (templateValue5Field.getText() == null || templateValue5Field.getText().length() == 0) {
            errorMessage += "No valid city!\n";
        }*/


        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показываем сообщение об ошибке.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

    public void setTemplatesOrMediaList(ObservableList<String> templatesList, ObservableList<String> mediaList) {
        this.templatesList = templatesList;
        this.mediaList = mediaList;
    }
}