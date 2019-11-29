package eg.edu.alexu.csd.oop.db.cs2.Commands;

import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateDatabase implements Command {
    @Override
    public int execute(String query) {
        Pattern regex = Pattern.compile("\\s+(\\w|\\\\)+\\s*;?\\s*$");
        Matcher match = regex.matcher(query);
        match.find();
        String databaseName = match.group().toLowerCase().replaceAll("\\s+", "").replaceAll(";", "");
        DatabaseManager.getInstance().setCurrentDatabase(databaseName);
        FilesHandler.createDatabase(databaseName);
        return 1;
    }
}
