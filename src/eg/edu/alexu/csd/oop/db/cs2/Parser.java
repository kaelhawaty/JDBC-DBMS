package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.io.IOException;

public interface Parser {
    public  void saveTable(Table table, String dataBaseName, FilesHandler filesHandler) throws IOException;
    public Table loadTable(String TableName, String dataBaseName, FilesHandler filesHandler);
}
