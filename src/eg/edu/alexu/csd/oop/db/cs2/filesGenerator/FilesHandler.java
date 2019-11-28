package eg.edu.alexu.csd.oop.db.cs2.filesGenerator;

import eg.edu.alexu.csd.oop.db.cs2.Parser;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.io.File;
import java.io.IOException;

public class FilesHandler {
    private File mainPath;
    private String fileSeparator = System.getProperty("file.separator");
    private Parser xml;
    public FilesHandler(){
        mainPath = new File("DatabasesWorkSpace");
        mainPath.mkdirs();
        xml = new XML();
    }
    public String getPathOf(String name){
        return mainPath.getAbsolutePath()+fileSeparator+name;
    }
    public String getPathOfTable(String tableName, String databaseName){
        return mainPath.getAbsolutePath()+fileSeparator+databaseName+fileSeparator+tableName;
    }
    public boolean isDatabaseExist(String name){
        if (name == null)
            return false;
        File f = new File(mainPath+fileSeparator+name);
        return f.exists();
    }
    public void createDatabase(String databaseName){
        File newDatabase = new File(mainPath+fileSeparator+databaseName);
        newDatabase.mkdirs();
    }
    private void deleteDirectory(File directory){
        File[] contents = directory.listFiles();
        for (File file : contents){
            if(file.isDirectory())
                deleteDirectory(file);
            else
                file.delete();
        }
        directory.delete();
    }
    public void dropDatabase(String name){
        deleteDirectory(new File(mainPath+fileSeparator+name));
    }
    public boolean isTableExist(String tableName, String databaseName){
        if (tableName == null || databaseName == null)
            return false;
        File xmlFile = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".xml");
        File dtdFile = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".dtd");
        if(xmlFile.exists() && dtdFile.exists()){
            if(DatabaseManager.getInstance().getCurrentDatabase().getTable(tableName) == null){
                DatabaseManager.getInstance().getCurrentDatabase().addTable(xml.loadTable(tableName, databaseName, this));
            }
            return true;
        }
        return false;
    }
    public void dropTable(String tableName, String databaseName){
        File xml = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".xml");
        File dtd = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".dtd");
        xml.delete();
        dtd.delete();
    }
    public void saveTable(Table table) throws IOException {
        xml.saveTable(table, DatabaseManager.getInstance().getCurrentDatabase().getName(), this);
    }

}
