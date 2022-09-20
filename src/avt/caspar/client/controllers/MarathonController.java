package avt.caspar.client.controllers;

import avt.caspar.client.MainApp;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import javafx.fxml.FXML;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MarathonController {

    private Connection connection;
    private Statement stmt;
    private ResultSet rs;
    private TemplateOverviewController templateOverviewController;

    public void setTemplateOverviewController(TemplateOverviewController templateOverviewController) {
        this.templateOverviewController = templateOverviewController;
    }


    @FXML private void testSQLQuery() {
        try {
            connection = templateOverviewController.getConnection();
            stmt = (Statement) connection.createStatement();
            rs = stmt.executeQuery("CALL `marathon`.`get_marks`(\"5KA\", \"M\", 0);");
            while (rs.next()) {
                System.out.println(rs.getString("p_name"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
