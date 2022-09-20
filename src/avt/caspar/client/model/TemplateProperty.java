package avt.caspar.client.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlAttribute;

public class TemplateProperty {
    private final StringProperty templateKey;
    private final StringProperty templateValue;

    @Override
    public boolean equals(Object o) {
        TemplateProperty template = (TemplateProperty) o;
        return getTemplateValue().equals(template.getTemplateValue()) &&
                getTemplateKey().equals(template.getTemplateKey());
    }

    public TemplateProperty() {
        this(null, null);
    }


    public TemplateProperty(String templateKey, String templateValue) {
        this.templateKey = new SimpleStringProperty(templateKey);
        this.templateValue = new SimpleStringProperty(templateValue);
    }

    public String getTemplateKey() {
        return templateKey.get();
    }

    @XmlAttribute
    public void setTemplateKey(String templateKey) {
        this.templateKey.set(templateKey);
    }

    public StringProperty templateKeyProperty() {
        return templateKey;
    }

    public String getTemplateValue() {
        return templateValue.get();
    }

    @XmlAttribute
    public void setTemplateValue(String templateValue) {
        this.templateValue.set(templateValue);
    }

    public StringProperty templateValueProperty() {
        return templateValue;
    }
}
