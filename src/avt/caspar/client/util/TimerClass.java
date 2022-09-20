package avt.caspar.client.util;

import avt.caspar.client.MainApp;
import avt.caspar.client.controllers.TemplateOverviewController;
import avt.caspar.client.model.TemplateProperty;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TimerClass {
    private int counter = 0;
    private Timer timer;
    private MainApp mainApp;
    private String template;
    private int updatePeriod;
    private EchoClient telnet;
    private int channel;
    private int layer;
    private TextArea reportArea;
    private String dataString = String.format("<componentData id=\\\"%s\\\"><data id=\\\"text\\\" value=\\\"%s\\\"/></componentData>",
            "f0", "");
    private TemplateOverviewController templateOverviewController;



    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setTemplateOverviewController(TemplateOverviewController templateOverviewController) {
        this.templateOverviewController = templateOverviewController;
    }

    public void startTimer() throws IOException {
        cgPlay();


        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                if (mainApp.getSheetDataProperties().size() ==0) {
                    System.out.println(counter);
                } else {
                    if (mainApp.getSheetDataProperties().size() <= counter) {
                        counter = 0;
                    }
                    setDataString();
                    counter++;
                    //increments the counter
                    cgUpdate();
                    templateOverviewController.getTableSheetData().getSelectionModel().select(counter);
                }
            }
        };

        timer = new Timer("MyTimer");//create a new Timer
        timer.scheduleAtFixedRate(timerTask, 0, updatePeriod);
    }

    public void setDataString() {
        String data = mainApp.getSheetDataProperties().get(counter).getSheetData();
        String data2 = mainApp.getSheetDataProperties().get(counter).getSheetData2();
        dataString = String.format("<componentData id=\\\"%s\\\"><data id=\\\"text\\\" value=\\\"%s\\\"/></componentData>" +
                        "<componentData id=\\\"%s\\\"><data id=\\\"text\\\" value=\\\"%s\\\"/></componentData>",
                "f0", data, "f1", data2);
    }

    public void stopTimer() {
        timer.cancel();
        counter = 0;
        cgStop();
    }

    public void cancelTimer() {
        timer.cancel();
        counter = 0;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setTemplate(String template) {
        this.template = template;
        //System.out.println(template);
    }

    public void setUpdatePeriod(int updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    public void setTelnet(EchoClient telnet) {
        this.telnet = telnet;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setReportArea(TextArea reportArea) {
        this.reportArea = reportArea;
    }

    public void cgPlay() {
        telnet.setCommand(String.format("CG %s-%s ADD %s \"%s\" 1 \"<templateData>%s</templateData>\"",
                channel, layer, 1, template, dataString));
        telnet.telnetSendCommand();
        try {
            reportArea.appendText("Play " + telnet.getInBuffer().readLine() + '\n');
        } catch (IOException e) {
            cancelTimer();
            templateOverviewController.stopConnectionAndUpdate();
            reportArea.appendText(e.getMessage());
            e.printStackTrace();
        }
    }

    public void cgUpdate(){
        telnet.setCommand(String.format("CG %s-%s UPDATE %s \"<templateData>%s</templateData>\"",
                channel, layer, 1, dataString));
        telnet.telnetSendCommand();
        try {
            reportArea.appendText("Update " + telnet.getInBuffer().readLine() + '\n');
        } catch (IOException e) {
            cancelTimer();
            templateOverviewController.stopConnectionAndUpdate();
            e.printStackTrace();
        }
    }

    public void cgNext() {
        telnet.setCommand(String.format("CG %s-%s NEXT %s",
                channel, layer, 1));
        telnet.telnetSendCommand();
        try {
            reportArea.appendText("Stop " + telnet.getInBuffer().readLine() + '\n');
        } catch (IOException e) {
            cancelTimer();
            templateOverviewController.stopConnectionAndUpdate();
            e.printStackTrace();
        }
    }

    public void cgStop() {
        telnet.setCommand(String.format("CG %s-%s STOP %s",
                channel, layer, 1));
        telnet.telnetSendCommand();
        try {
            reportArea.appendText("Stop " + telnet.getInBuffer().readLine() + '\n');
        } catch (IOException e) {
            cancelTimer();
            templateOverviewController.stopConnectionAndUpdate();
            e.printStackTrace();
        }
    }
}
