package avt.caspar.client.controllers;

import avt.caspar.client.MainApp;
import avt.caspar.client.model.Template;
import avt.caspar.client.model.TemplateProperty;
import avt.caspar.client.util.ExcelRead;
import avt.caspar.client.util.SheetsQuickstart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class TemplateUpdater {

    private final MainApp mainApp;
    private final TextField rrURL;
    private final TextField rrSheet;
    private final TextField rrRange;
    private final TextField rrChannelField;
    private final TextField rrLayerField;

    public TemplateUpdater(MainApp mainApp, TextField rrURL, TextField rrSheet, TextField rrRange,
                           TextField rrChannelField, TextField rrLayerField) {
        this.mainApp = mainApp;
        this.rrURL = rrURL;
        this.rrSheet = rrSheet;
        this.rrRange = rrRange;
        this.rrChannelField = rrChannelField;
        this.rrLayerField = rrLayerField;
    }

    public void updateRemoteTemplate() throws GeneralSecurityException, IOException {
        mainApp.getRemoteTemplateData().clear();
        SheetsQuickstart newSheet = new SheetsQuickstart();
        if (!rrURL.getText().split("/")[5].equals("") && rrURL.getText().split("/")[5] != null &&
                !rrSheet.getText().equals("") && rrSheet.getText() != null &&
                !rrRange.getText().equals("") && rrRange.getText() != null) {
            List<List<Object>> values = newSheet.getSheetData(rrURL.getText().split("/")[5],
                    rrSheet.getText() + "!" + rrRange.getText());
            for (List<Object> row : values) {
                Template remTemplate = new Template();
                ObservableList<TemplateProperty> remPropertiesList = FXCollections.observableArrayList();
                remTemplate.setTemplateChannel(Integer.parseInt(rrChannelField.getText()));
                remTemplate.setTemplateVideoLayer(Integer.parseInt(rrLayerField.getText()));
                remTemplate.setTemplateFlashLayer(1);
                remTemplate.setTemplateOrMedia("Template");
                if (row.size() > 0) remTemplate.setTemplateId(Integer.parseInt(row.get(0).toString()));
                if (row.size() > 1) remTemplate.setTemplatePath(row.get(1).toString());
                if (row.size() > 2) remTemplate.setTemplateName(row.get(2).toString());
                if (row.size() > 3) {
                    if (!row.get(3).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f0", row.get(3).toString()));
                    }
                }
                if (row.size() > 4) {
                    if (!row.get(4).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f1", row.get(4).toString()));
                    }
                }
                if (row.size() > 5) {
                    if (!row.get(5).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f2", row.get(5).toString()));
                    }
                }
                if (row.size() > 6) {
                    if (!row.get(6).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f3", row.get(6).toString()));
                    }
                }
                remTemplate.setTemplateProperties(remPropertiesList);
                this.mainApp.getRemoteTemplateData().add(remTemplate);
            }
        }
    }

    public void autoUpdateRemoteTemplate() throws GeneralSecurityException, IOException {
        ObservableList<Template> tempTemplateData = FXCollections.observableArrayList();
        SheetsQuickstart newSheet = new SheetsQuickstart();
        if (!rrURL.getText().split("/")[5].equals("") && rrURL.getText().split("/")[5] != null &&
                !rrSheet.getText().equals("") && rrSheet.getText() != null &&
                !rrRange.getText().equals("") && rrRange.getText() != null) {
            List<List<Object>> values = newSheet.getSheetData(rrURL.getText().split("/")[5],
                    rrSheet.getText() + "!" + rrRange.getText());
            for (List<Object> row : values) {
                Template remTemplate = new Template();
                ObservableList<TemplateProperty> remPropertiesList = FXCollections.observableArrayList();
                remTemplate.setTemplateChannel(Integer.parseInt(rrChannelField.getText()));
                remTemplate.setTemplateVideoLayer(Integer.parseInt(rrLayerField.getText()));
                remTemplate.setTemplateFlashLayer(1);
                remTemplate.setTemplateOrMedia("Template");
                if (row.size() > 0) remTemplate.setTemplateId(Integer.parseInt(row.get(0).toString()));
                if (row.size() > 1) remTemplate.setTemplatePath(row.get(1).toString());
                if (row.size() > 2) remTemplate.setTemplateName(row.get(2).toString());
                if (row.size() > 3) {
                    if (!row.get(3).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f0", row.get(3).toString()));
                    }
                }
                if (row.size() > 4) {
                    if (!row.get(4).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f1", row.get(4).toString()));
                    }
                }
                if (row.size() > 5) {
                    if (!row.get(5).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f2", row.get(5).toString()));
                    }
                }
                if (row.size() > 6) {
                    if (!row.get(6).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f3", row.get(6).toString()));
                    }
                }
                remTemplate.setTemplateProperties(remPropertiesList);
                tempTemplateData.add(remTemplate);
            }
        }
        boolean isPlayed = false;
        for (Template template: mainApp.getRemoteTemplateData()) {
            isPlayed = template.isTemplateOnAir() || template.isTemplatePVW();
            if (isPlayed) break;
        }
        System.out.println(isPlayed);
        if (tempTemplateData.size() == mainApp.getRemoteTemplateData().size()) {
            boolean equal = false;
            for (int i = 0; i < tempTemplateData.size(); i++) {
                equal = tempTemplateData.get(i).getTemplateName().equals(mainApp.getRemoteTemplateData().get(i).getTemplateName()) &&
                        tempTemplateData.get(i).getTemplateOrMedia().equals(mainApp.getRemoteTemplateData().get(i).getTemplateOrMedia()) &&
                        tempTemplateData.get(i).getTemplatePath().equals(mainApp.getRemoteTemplateData().get(i).getTemplatePath()) &&
                        tempTemplateData.get(i).getTemplateChannel() == mainApp.getRemoteTemplateData().get(i).getTemplateChannel() &&
                        tempTemplateData.get(i).getTemplateId() == mainApp.getRemoteTemplateData().get(i).getTemplateId() &&
                        tempTemplateData.get(i).getTemplateVideoLayer() == mainApp.getRemoteTemplateData().get(i).getTemplateVideoLayer() &&
                        tempTemplateData.get(i).getTemplateFlashLayer() == mainApp.getRemoteTemplateData().get(i).getTemplateFlashLayer() &&
                        tempTemplateData.get(i).getTemplateProperties().equals(mainApp.getRemoteTemplateData().get(i).getTemplateProperties());
            }
            if (!equal && !isPlayed) {
                mainApp.getRemoteTemplateData().clear();
                mainApp.getRemoteTemplateData().addAll(tempTemplateData);
                System.out.println("Updated");
            }
        } else {
            if (!isPlayed) {
                mainApp.getRemoteTemplateData().clear();
                mainApp.getRemoteTemplateData().addAll(tempTemplateData);
                System.out.println("Updated");
            }
        }
    }


    public void updateLocalExcelTemplate() throws GeneralSecurityException, IOException {
        mainApp.getRemoteTemplateData().clear();
        ExcelRead excelRead = new ExcelRead();
        if (!rrURL.getText().equals("") && rrURL.getText() != null &&
                !rrSheet.getText().equals("") && rrSheet.getText() != null &&
                !rrRange.getText().equals("") && rrRange.getText() != null) {
            ArrayList<ArrayList> values = excelRead.excelReader(rrURL.getText(),
                    rrSheet.getText(), rrRange.getText());
            for (ArrayList row : values) {
                Template remTemplate = new Template();
                ObservableList<TemplateProperty> remPropertiesList = FXCollections.observableArrayList();
                remTemplate.setTemplateChannel(Integer.parseInt(rrChannelField.getText()));
                remTemplate.setTemplateVideoLayer(Integer.parseInt(rrLayerField.getText()));
                remTemplate.setTemplateFlashLayer(1);
                remTemplate.setTemplateOrMedia("Template");
                if (row.size() > 0) remTemplate.setTemplateId(Integer.parseInt(row.get(0).toString()));
                if (row.size() > 1) remTemplate.setTemplatePath(row.get(1).toString());
                if (row.size() > 2) remTemplate.setTemplateName(row.get(2).toString());
                if (row.size() > 3) {
                    if (!row.get(3).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f0", row.get(3).toString()));
                    }
                }
                if (row.size() > 4) {
                    if (!row.get(4).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f1", row.get(4).toString()));
                    }
                }
                if (row.size() > 5) {
                    if (!row.get(5).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f2", row.get(5).toString()));
                    }
                }
                if (row.size() > 6) {
                    if (!row.get(6).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f3", row.get(6).toString()));
                    }
                }
                remTemplate.setTemplateProperties(remPropertiesList);
                this.mainApp.getRemoteTemplateData().add(remTemplate);
            }
        }
    }

    public void autoUpdateLocalExcelTemplate() throws GeneralSecurityException, IOException {
        ObservableList<Template> tempTemplateData = FXCollections.observableArrayList();
        ExcelRead excelRead = new ExcelRead();
        if (!rrURL.getText().equals("") && rrURL.getText() != null &&
                !rrSheet.getText().equals("") && rrSheet.getText() != null &&
                !rrRange.getText().equals("") && rrRange.getText() != null) {
            ArrayList<ArrayList> values = excelRead.excelReader(rrURL.getText(),
                    rrSheet.getText(), rrRange.getText());
            for (ArrayList row : values) {
                Template remTemplate = new Template();
                ObservableList<TemplateProperty> remPropertiesList = FXCollections.observableArrayList();
                remTemplate.setTemplateChannel(Integer.parseInt(rrChannelField.getText()));
                remTemplate.setTemplateVideoLayer(Integer.parseInt(rrLayerField.getText()));
                remTemplate.setTemplateFlashLayer(1);
                remTemplate.setTemplateOrMedia("Template");
                if (row.size() > 0) remTemplate.setTemplateId(Integer.parseInt(row.get(0).toString()));
                if (row.size() > 1) remTemplate.setTemplatePath(row.get(1).toString());
                if (row.size() > 2) remTemplate.setTemplateName(row.get(2).toString());
                if (row.size() > 3) {
                    if (!row.get(3).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f0", row.get(3).toString()));
                    }
                }
                if (row.size() > 4) {
                    if (!row.get(4).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f1", row.get(4).toString()));
                    }
                }
                if (row.size() > 5) {
                    if (!row.get(5).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f2", row.get(5).toString()));
                    }
                }
                if (row.size() > 6) {
                    if (!row.get(6).equals("")) {
                        remPropertiesList.add(new TemplateProperty("f3", row.get(6).toString()));
                    }
                }
                remTemplate.setTemplateProperties(remPropertiesList);
                tempTemplateData.add(remTemplate);
            }
        }
        boolean isPlayed = false;
        for (Template template: mainApp.getRemoteTemplateData()) {
            isPlayed = template.isTemplateOnAir() || template.isTemplatePVW();
            if (isPlayed) break;
        }
        System.out.println(isPlayed);
        if (tempTemplateData.size() == mainApp.getRemoteTemplateData().size()) {
            boolean equal = false;
            for (int i = 0; i < tempTemplateData.size(); i++) {
                equal = tempTemplateData.get(i).getTemplateName().equals(mainApp.getRemoteTemplateData().get(i).getTemplateName()) &&
                        tempTemplateData.get(i).getTemplateOrMedia().equals(mainApp.getRemoteTemplateData().get(i).getTemplateOrMedia()) &&
                        tempTemplateData.get(i).getTemplatePath().equals(mainApp.getRemoteTemplateData().get(i).getTemplatePath()) &&
                        tempTemplateData.get(i).getTemplateChannel() == mainApp.getRemoteTemplateData().get(i).getTemplateChannel() &&
                        tempTemplateData.get(i).getTemplateId() == mainApp.getRemoteTemplateData().get(i).getTemplateId() &&
                        tempTemplateData.get(i).getTemplateVideoLayer() == mainApp.getRemoteTemplateData().get(i).getTemplateVideoLayer() &&
                        tempTemplateData.get(i).getTemplateFlashLayer() == mainApp.getRemoteTemplateData().get(i).getTemplateFlashLayer() &&
                        tempTemplateData.get(i).getTemplateProperties().equals(mainApp.getRemoteTemplateData().get(i).getTemplateProperties());
            }
            if (!equal && !isPlayed) {
                mainApp.getRemoteTemplateData().clear();
                mainApp.getRemoteTemplateData().addAll(tempTemplateData);
                System.out.println("Updated");
            }
        } else {
            if (!isPlayed) {
                mainApp.getRemoteTemplateData().clear();
                mainApp.getRemoteTemplateData().addAll(tempTemplateData);
                System.out.println("Updated");
            }
        }
    }

}
