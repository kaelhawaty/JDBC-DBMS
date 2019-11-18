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

}
