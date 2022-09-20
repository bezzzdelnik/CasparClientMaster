package avt.caspar.client.controllers;

import avt.caspar.client.model.TemplateProperty;

public class TableContextMenu {
    private TemplateEditDialogController tedc;

    public void setTedc(TemplateEditDialogController tedc) {
        this.tedc = tedc;
    }

    public void handleDeleteTemplateProperties() {
        tedc.template.getTemplateProperties().remove(tedc.propertyTableViewEdit.getSelectionModel().getFocusedIndex());
        tedc.propertyTableViewEdit.getItems().remove(tedc.propertyTableViewEdit.getSelectionModel().getFocusedIndex());
    }

    public void handleNewTemplateProperties() {
        TemplateProperty tempProperty = new TemplateProperty("", "");
        this.tedc.tempTemplateProperties = tedc.template.getTemplateProperties();
        tedc.tempTemplateProperties.add(tempProperty);
        tedc.propertyTableViewEdit.getItems().add(tempProperty);
    }
}
