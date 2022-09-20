package avt.caspar.client.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "root")
public class TemplateListWrapper {


    private List<Template> templates;
    private List<Template> remoteTemplates;
    private ObservableList<SheetDataProperty> sheetDataProperties = FXCollections.observableArrayList();

    @XmlElementWrapper(name="templates")
    @XmlElement(name = "template")
    public List<Template> getTemplates() {
        return templates;
    }
    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    @XmlElementWrapper(name="remoteTemplates")
    @XmlElement(name = "remoteTemplate")
    public List<Template> getRemoteTemplates() {
        return remoteTemplates;
    }
    public void setRemoteTemplates(List<Template> remoteTemplates) {
        this.remoteTemplates = remoteTemplates;
    }

    @XmlElementWrapper(name="sheetData")
    @XmlElement(name = "data")
    public ObservableList<SheetDataProperty> getSheetDataProperties() {
        return sheetDataProperties;
    }
    public void setSheetDataProperties(ObservableList<SheetDataProperty> sheetDataProperties) {
        this.sheetDataProperties = sheetDataProperties;
    }
}
