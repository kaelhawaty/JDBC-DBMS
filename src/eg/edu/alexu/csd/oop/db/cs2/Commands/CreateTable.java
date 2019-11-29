package eg.edu.alexu.csd.oop.db.cs2.Commands;

import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.Database;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

public class CreateTable implements Command {

    @Override
    public int execute(String query) {
        query = query.replaceAll("^\\s*create\\s+table\\s+", "").replaceAll("[\\(\\),;]", " ");
        String[] tableInfo = query.split("\\s+");
        if (FilesHandler.isTableExist(tableInfo[0], DatabaseManager.getInstance().getCurrentDatabase()))
            return 0;
        DatabaseManager.getInstance().setCurrentTable(new Table(tableInfo));
        FilesHandler.saveTable(DatabaseManager.getInstance().getCurrentTable());
        return 1;
    }
}
