package eg.edu.alexu.csd.oop.db.cs2.Commands;

import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;

public class DropTable implements Command {
    @Override
    public int execute(String query) {
        query = query.replaceAll("^\\s*drop\\s+table\\s+", "").replaceAll("\\s*;?\\s*$", "");
        if(!FilesHandler.isTableExist(query, DatabaseManager.getInstance().getCurrentDatabase()))
            return 0;
        FilesHandler.dropTable(query, DatabaseManager.getInstance().getCurrentDatabase());
        if (DatabaseManager.getInstance().getCurrentTable().getName().equals(query))
            DatabaseManager.getInstance().setCurrentTable(null);
        return 1;
    }
}
