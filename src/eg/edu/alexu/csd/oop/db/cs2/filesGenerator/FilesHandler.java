package eg.edu.alexu.csd.oop.db.cs2.filesGenerator;

import eg.edu.alexu.csd.oop.db.cs2.Database;
import eg.edu.alexu.csd.oop.db.cs2.Parser;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import javax.xml.crypto.Data;
import java.io.File;
import java.sql.SQLException;
import java.util.logging.FileHandler;

public class FilesHandler {
    private static File mainPath  = new File("DatabasesWorkSpace");
    private static String fileSeparator = System.getProperty("file.separator");
    private static Parser xml = xml = new XML();
    private FilesHandler(){

    }
    public static String getPathOf(String name){
        return mainPath.getAbsolutePath()+fileSeparator+name;
    }
    public static String getPathOfTable(String tableName, String databaseName){
        return mainPath.getAbsolutePath()+fileSeparator+databaseName+fileSeparator+tableName;
    }
    public static boolean isDatabaseExist(String name){
        if (name == null)
            return false;
        File f = new File(mainPath+fileSeparator+name);
        return f.exists();
    }
    public static void createDatabase(String databaseName){
        File newDatabase = new File(mainPath+fileSeparator+databaseName);
        newDatabase.mkdirs();
    }
    private static void deleteDirectory(File directory){
        File[] contents = directory.listFiles();
        for (File file : contents){
            if(file.isDirectory())
                deleteDirectory(file);
            else
                file.delete();
        }
        directory.delete();
    }
    public static void dropDatabase(String name){
        deleteDirectory(new File(mainPath+fileSeparator+name));
    }
    public static boolean isTableExist(String tableName, String databaseName){
        if (tableName == null || databaseName == null)
            return false;
        File xmlFile = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".xml");
        File dtdFile = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".dtd");
        return xmlFile.exists() && dtdFile.exists();
    }
    public static Table getTable(String tableName, String databaseName) throws SQLException {
        Table table = DatabaseManager.getInstance().getCurrentTable();
        if(table.getName().equals(tableName)){
            return table;
        }
        table = xml.loadTable(tableName, databaseName);
        if(table == null)
            throw new SQLException("Couldn't load table!");
        DatabaseManager.getInstance().setCurrentTable(table);
        return table;
    }
    public static void dropTable(String tableName, String databaseName){
        File xml = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".xml");
        File dtd = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".dtd");
        xml.delete();
        dtd.delete();
    }
    public static void saveTable(Table table){
        xml.saveTable(table, DatabaseManager.getInstance().getCurrentDatabase());
    }

}
