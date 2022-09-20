package avt.caspar.client.model;

import avt.caspar.client.util.EchoClient;
import javafx.collections.ObservableList;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class CasparControl {
    private EchoClient telnet;
    private Template template = null;
    private TextArea reportArea;
    private Integer channel;

    public void setCasparControl(Template template, TextArea reportArea, EchoClient telnet, int channel) {
        this.template = template;
        this.reportArea = reportArea;
        this.telnet = telnet;
        this.channel = channel;
    }

    private String createDataString(ObservableList<TemplateProperty> templatesList) {
        String tempDataString = "";
        for (TemplateProperty property: templatesList) {
            tempDataString += String.format("<componentData id=\\\"%s\\\"><data id=\\\"text\\\" value=\\\"%s\\\"/></componentData>",
                    property.getTemplateKey(), property.getTemplateValue());
        }
        return tempDataString;
    }

    public void cgPlay() throws IOException {
        telnet.setCommand(String.format("CG %s-%s ADD %s \"%s\" 1 \"<templateData>%s</templateData>\"",
                channel, template.getTemplateVideoLayer(),
                template.getTemplateFlashLayer(), template.getTemplatePath(), createDataString(template.getTemplateProperties())));
        telnet.telnetSendCommand();
        reportArea.appendText("Play " + telnet.getInBuffer().readLine() + '\n');
    }

    public void cgUpdate() throws IOException{
        telnet.setCommand(String.format("CG %s-%s UPDATE %s \"<templateData>%s</templateData>\"",
                template.getTemplateChannel(), template.getTemplateVideoLayer(),
                template.getTemplateFlashLayer(), createDataString(template.getTemplateProperties())));
        telnet.telnetSendCommand();
        reportArea.appendText("Update " + telnet.getInBuffer().readLine() + '\n');
    }

    public void cgNext() throws IOException{
        telnet.setCommand(String.format("CG %s-%s NEXT %s",
                channel, template.getTemplateVideoLayer(),
                template.getTemplateFlashLayer()));
        telnet.telnetSendCommand();
        reportArea.appendText("Next " + telnet.getInBuffer().readLine() + '\n');
    }

    public void cgStop() throws IOException{
        telnet.setCommand(String.format("CG %s-%s STOP %s",
                channel, template.getTemplateVideoLayer(),
                template.getTemplateFlashLayer()));
        telnet.telnetSendCommand();
        reportArea.appendText("Stop " + telnet.getInBuffer().readLine() + '\n');
    }

    public void mediaPlay() throws IOException {
        telnet.setCommand(String.format("PLAY %s-%s %s CUT 0 Linear RIGHT",
                channel, template.getTemplateVideoLayer(),
                template.getTemplatePath()));
        telnet.telnetSendCommand();
        reportArea.appendText("Play " + telnet.getInBuffer().readLine() + '\n');
    }

    public void mediaStop() throws IOException{
        telnet.setCommand(String.format("STOP %s-%s",
                channel, template.getTemplateVideoLayer()));
        telnet.telnetSendCommand();
        reportArea.appendText("Stop " + telnet.getInBuffer().readLine() + '\n');
    }

    public void mediaPause() throws IOException{
        telnet.setCommand(String.format("PAUSE %s-%s",
                channel, template.getTemplateVideoLayer()));
        telnet.telnetSendCommand();
        reportArea.appendText("Pause " + telnet.getInBuffer().readLine() + '\n');
    }

    public void mediaResume() throws IOException{
        telnet.setCommand(String.format("RESUME %s-%s",
                channel, template.getTemplateVideoLayer()));
        telnet.telnetSendCommand();
        reportArea.appendText("Resume " + telnet.getInBuffer().readLine() + '\n');
    }

}
