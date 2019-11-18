package eg.edu.alexu.csd.oop.db.cs2;

import java.io.File;

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
        File[] files = mainPath.listFiles();
        for(File file : files){
            if (file.getName().equals(name))
                return true;
        }
        return false;
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
        File [] files = mainPath.listFiles();
        for (File file : files){
            if(file.getName().equals(name)){
                deleteDirectory(file);
            }
        }
    }

}
