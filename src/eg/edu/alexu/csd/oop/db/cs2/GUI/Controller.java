package eg.edu.alexu.csd.oop.db.cs2.GUI;

import eg.edu.alexu.csd.oop.db.cs2.DBDriver;
import eg.edu.alexu.csd.oop.db.cs2.DBLogger;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.List;

public class Controller {
    private Driver driver;
    private Connection connection;
    private Statement statement;
    private JTextArea loggerArea;
    private JTable table;
    private  JTextArea statusArea;
    public Controller(String path, Component []components){
        driver = new DBDriver();
        Properties info = new Properties();
        info.put("path", new File(path).getAbsoluteFile());
        JScrollPane[] scrollPanes = new JScrollPane[3];
        JViewport[] viewports = new JViewport[3];
        for (int i = 0; i < 3; ++i) {
            scrollPanes[i] = (JScrollPane) components[i];
            viewports[i] = scrollPanes[i].getViewport();
        }
        statusArea = (JTextArea) viewports[0].getView();
        loggerArea = (JTextArea) viewports[1].getView();
        table = (JTable) viewports[2].getView();
        try {
            connection = driver.connect("jdbc:xmldb://localhost", info);
        } catch (SQLException e) {
            statusArea.append("Couldn't connect to driver\n");
        }
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            statusArea.append("Couldn't create Statement\n");
        }

    }
    public void setConnection(String path){
        Properties info = new Properties();
        info.put("path", new File(path).getAbsoluteFile());
        try {
            connection = driver.connect("jdbc:xmldb://localhost", info);
            statement = connection.createStatement();
        } catch (SQLException e) {
            statusArea.append("Failed to set Connection\n");
        }
    }
    public void execute(String query) {
        String[] split = query.split("\\s+");
        boolean executeSuccess = false;
        int updated = -1;
        ResultSet resultSet = null;
        switch (split[0].toLowerCase()) {
            case "create":
            case "drop":
                try {
                    executeSuccess = statement.execute(query);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(statusArea, "Enter a valid sql statement");
                }
                break;
            case "insert":
            case "update":
            case "delete":
                try {
                    updated = statement.executeUpdate(query);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(statusArea, "Enter a valid sql statement");
                }
                break;
            case "select":
                try {
                    resultSet = statement.executeQuery(query);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(statusArea, "Enter a valid sql statement");
                }
                break;
            default:
                JOptionPane.showMessageDialog(statusArea, "Enter a valid sql statement");
                DBLogger.getInstance().getLogger().info("Failed to execute query due to syntax error");
        }
        if (updated > -1) {
            statusArea.append(updated+ " line(s) have been updated successfully\n");
        }else if(resultSet != null){
            updateTableArea(resultSet);
            statusArea.append("Table updated successfully\n");
        }else{
            if(!executeSuccess)
                statusArea.append("Failed to execute the command: " + query + "\n");
            else
                statusArea.append(split[1] + " " + split[2].split("\\(")[0] + " " + split[0] + "ed successfully\n");
        }
        updateLoggerArea();

    }


    public void updateLoggerArea(){
        FileReader reader;
        try {
            reader = new FileReader("log.txt");
            loggerArea.read(reader, "log.txt");
        } catch (FileNotFoundException e) {
            DBLogger.getInstance().getLogger().info("Couldn't update Logger file: FileNotFoundException msg: " +e.getMessage());
        } catch (IOException e) {
            DBLogger.getInstance().getLogger().info("Couldn't update Logger file: IOException msg: " +e.getMessage());
        }
    }
    public void updateTableArea(ResultSet resultSet){
        List<String> columnNames = new ArrayList<>();
        int numOfColumns = 1;
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            while (metaData.getColumnName(numOfColumns) != null){
                columnNames.add(metaData.getColumnName(numOfColumns++));
            }
        } catch (SQLException e) {
        }
        DefaultTableModel tableModel = new DefaultTableModel();
        for (int i = 0; i < numOfColumns-1; ++i){
            tableModel.addColumn(columnNames.get(i));
        }
        try {
            while(resultSet.next()){
                Object[] rowData = new Object[numOfColumns-1];
                for(int i = 1; i < numOfColumns; ++i)
                    rowData[i-1] = resultSet.getObject(i);
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
        }
        table.setModel(tableModel);

    }
}
