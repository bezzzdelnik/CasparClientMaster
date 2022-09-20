package avt.caspar.client.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.*;

/**
 * Класс-модель для адресата (Person).
 *
 * @author Marco Jakob
 */
public class Template {
    private final StringProperty templateName;
    private final IntegerProperty templateId;
    private final StringProperty templatePath;
    private final IntegerProperty templateChannel;
    private final IntegerProperty templateVideoLayer;
    private final IntegerProperty templateFlashLayer;
    private final StringProperty templateOrMedia;
    private ObservableList<TemplateProperty> templateProperties;
    private final BooleanProperty templateOnAir;
    private final BooleanProperty templatePVW;


    /**
     * Конструктор по умолчанию.
     */
    public Template() {
        this(null, null);
    }

    /**
     * Конструктор с некоторыми начальными данными.
     *
     * @param templateName
     * @param templatePath
     */
    public Template(String templateName, String templatePath) {
        this.templateId = new SimpleIntegerProperty(0);
        this.templatePath = new SimpleStringProperty(templatePath);
        this.templateChannel = new SimpleIntegerProperty(1);
        this.templateVideoLayer = new SimpleIntegerProperty(20);
        this.templateFlashLayer = new SimpleIntegerProperty(10);
        this.templateProperties =  FXCollections.observableArrayList();
        this.templateName = new SimpleStringProperty(templateName);
        this.templateOrMedia = new SimpleStringProperty("Template");
        this.templateOnAir = new SimpleBooleanProperty(false);
        this.templatePVW = new SimpleBooleanProperty(false);
    }

    public boolean isTemplatePVW() {
        return templatePVW.get();
    }

    public BooleanProperty templatePVWProperty() {
        return templatePVW;
    }

    @XmlTransient
    public void setTemplatePVW(boolean templatePVW) {
        this.templatePVW.set(templatePVW);
    }

    public boolean isTemplateOnAir() {
        return templateOnAir.get();
    }

    public BooleanProperty templateOnAirProperty() {
        return templateOnAir;
    }

    @XmlTransient
    public void setTemplateOnAir(boolean templateOnAir) {
        this.templateOnAir.set(templateOnAir);
    }

    public String getTemplateOrMedia() {
        return templateOrMedia.get();
    }

    @XmlAttribute
    public void setTemplateOrMedia(String templateOrMedia) {
        this.templateOrMedia.set(templateOrMedia);
    }

    public StringProperty templateOrMediaProperty() {
        return templateOrMedia;
    }

    public String getTemplatePath() {
        return templatePath.get();
    }

    @XmlAttribute
    public void setTemplatePath(String templatePath) {
        this.templatePath.set(templatePath);
    }

    public StringProperty templatePathProperty() {
        return templatePath;
    }

    public String getTemplateName() {
        return templateName.get();
    }

    @XmlAttribute
    public void setTemplateName(String templateName) {
        this.templateName.set(templateName);
    }

    public StringProperty templateNameProperty() {
        return templateName;
    }

    public int getTemplateId() {
        return templateId.get();
    }

    @XmlAttribute
    public void setTemplateId(int templateId) {
        this.templateId.set(templateId);
    }

    public IntegerProperty templateIdProperty() {
        return templateId;
    }

    public int getTemplateChannel() {
        return templateChannel.get();
    }

    @XmlAttribute
    public void setTemplateChannel(int templateChannel) {
        this.templateChannel.set(templateChannel);
    }

    public IntegerProperty templateChannelProperty() {
        return templateChannel;
    }

    public int getTemplateVideoLayer() {
        return templateVideoLayer.get();
    }

    @XmlAttribute
    public void setTemplateVideoLayer(int templateVideoLayer) {
        this.templateVideoLayer.set(templateVideoLayer);
    }

    public IntegerProperty templateVideoLayerProperty() {
        return templateVideoLayer;
    }

    public int getTemplateFlashLayer() {
        return templateFlashLayer.get();
    }

    @XmlAttribute
    public void setTemplateFlashLayer(int templateFlashLayer) {
        this.templateFlashLayer.set(templateFlashLayer);
    }

    public IntegerProperty templateFlashProperty() {
        return templateFlashLayer;
    }

    //@XmlElementWrapper(name="properties")
    @XmlElement(name = "property")
    public ObservableList<TemplateProperty> getTemplateProperties() {
        return templateProperties;
    }
    public void setTemplateProperties(ObservableList<TemplateProperty> templateProperties) {
        this.templateProperties = templateProperties;
    }

}