package avt.caspar.client.controllers;

import avt.caspar.client.MainApp;
import avt.caspar.client.model.Leader;
import avt.caspar.client.model.Leaders;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MarathonController {

    private Connection connection;
    private Statement stmt;
    private ResultSet rs;
    private TemplateOverviewController templateOverviewController;
    private int id = 0;

    public void setTemplateOverviewController(TemplateOverviewController templateOverviewController) {
        this.templateOverviewController = templateOverviewController;
    }

    private Leaders leaders;




    @FXML private void testSQLQuery() {
        getLeadersFromDB("M", "finish1a", "ФИНИШ");
    }

    private void getLeadersFromDB(String gender, String distance, String distanceTitle) {
        id = 0;
        leaders = new Leaders(new SimpleStringProperty(gender), new SimpleStringProperty(distanceTitle),
                new SimpleStringProperty("Title"), new SimpleStringProperty("TimeTitle"));
        try {
            connection = templateOverviewController.getConnection();
            stmt = (Statement) connection.createStatement();
            rs = stmt.executeQuery("CALL `marathon`.`get_marks`(\"" + distance + "\", \"" + gender + "\", 0);");

            while (rs.next() && leaders.getLeadersList().size() < 6) {
                id++;
                leaders.getLeadersList().add(new Leader(new SimpleStringProperty(String.valueOf(id)),
                        new SimpleStringProperty(rs.getString("p_name")),
                        new SimpleStringProperty(rs.getString("flag")),
                        new SimpleStringProperty(rs.getString("ts")),
                        new SimpleStringProperty(rs.getString("gdc")),
                        new SimpleBooleanProperty(true)));
            }
            System.out.println(leaders.getTitle() + " " + leaders.getTimeTitle());
            System.out.println(leaders.getDistance() + " " + leaders.getGender());
            for (Leader l: leaders.getLeadersList()) {
                System.out.println(l.getId() + " " + l.getFlag() + " " + l.getName() + " " +
                        l.getTime() + " " + l.getHandicap());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
