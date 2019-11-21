package eg.edu.alexu.csd.oop.db.cs2.filesGenerator;

import java.io.File;
import java.io.IOException;

public class FilesHandler {
    private File mainPath;
    private String fileSeparator = System.getProperty("file.separator");
    public FilesHandler(){
        mainPath = new File("DatabasesWorkSpace");
        mainPath.mkdirs();
    }
    public String getPathOf(String name){
        return mainPath.getAbsolutePath()+fileSeparator+name;
    }
    public boolean isDatabaseExist(String name){
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
    public void createTable(String tableName, String databaseName){
        File xml = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".xml");
        File dtd = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".dtd");
        try {
            xml.createNewFile();
            dtd.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isTableExist(String tableName, String databaseName){
        File xml = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".xml");
        File dtd = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".dtd");
        return xml.exists() && dtd.exists();
    }
    public void dropTable(String tableName, String databaseName){
        File xml = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".xml");
        File dtd = new File(mainPath+fileSeparator+databaseName+fileSeparator+tableName+".dtd");
        xml.delete();
        dtd.delete();
    }
}
