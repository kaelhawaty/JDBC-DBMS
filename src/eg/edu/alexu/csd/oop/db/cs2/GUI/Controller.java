package eg.edu.alexu.csd.oop.db.cs2.GUI;

import eg.edu.alexu.csd.oop.db.cs2.DBDriver;
import eg.edu.alexu.csd.oop.db.cs2.DBLogger;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class Controller {
    private Driver driver;
    private Connection connection;
    public Controller(String path){
        driver = new DBDriver();
        Properties info = new Properties();
        info.put("path", path);
        try {
            connection = driver.connect("jdbc:xmldb://localhost", info);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void setConnection(String path){
        Properties info = new Properties();
        info.put("path", path);
        try {
            connection = driver.connect("jdbc:xmldb://localhost", info);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void execute(Component []components, String query) throws SQLException {
        JScrollPane[] scrollPanes = new JScrollPane[3];
        JViewport[] viewports = new JViewport[3];
        for (int i = 0; i < 3; ++i) {
            scrollPanes[i] = (JScrollPane) components[i];
            viewports[i] = scrollPanes[i].getViewport();
        }
        JTextArea statusArea = (JTextArea) viewports[0].getView();
        JTextArea loggerArea = (JTextArea) viewports[1].getView();
        JTable table = (JTable) viewports[2].getView();
        Statement statement = connection.createStatement();
        String[] split = query.split("\\s+");
        boolean executeSuccess = false;
        int updated = -1;
        ResultSet resultSet = null;
        switch (split[0].toLowerCase()) {
            case "create":
            case "drop":
                executeSuccess = statement.execute(query);
                break;
            case "insert":
            case "update":
            case "delete":
                updated = statement.executeUpdate(query);
                break;
            case "select":
                resultSet = statement.executeQuery(query);
                break;
            default:
                JOptionPane.showMessageDialog(statusArea, "Enter a valid sql statement");
                DBLogger.getInstance().getLogger().info("Failed to execute query due to syntax error");
        }
        if (updated > -1) {
            statusArea.append(updated+ " line(s) have been updated successfully\n");
        }else if(resultSet != null){
            updateTableArea(table, resultSet);
            statusArea.append("Table updated successfully");
        }else{
            if(!executeSuccess)
                statusArea.append("Failed to " + split[0] + " "+ split[1] + " " + split[2] + "\n");
            else
                statusArea.append(split[1] + " " + split[2].split("\\(")[0] + " " + split[0] + "ed successfully\n");
        }
        updateLoggerArea(loggerArea);

    }


    public void updateLoggerArea(JTextArea loggerArea){
        FileReader reader;
        try {
            reader = new FileReader("log.txt");
            loggerArea.read(reader, "log.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateTableArea(JTable table, ResultSet resultSet){

    }
}
