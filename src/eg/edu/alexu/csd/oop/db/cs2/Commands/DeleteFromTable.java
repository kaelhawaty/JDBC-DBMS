package eg.edu.alexu.csd.oop.db.cs2.Commands;

import eg.edu.alexu.csd.oop.db.cs2.Command;

import java.sql.SQLException;

public class DeleteFromTable implements Command {
    @Override
    public int execute(String query) throws SQLException {
        query = query.toLowerCase();
        query = query.replaceAll("^\\s*delete\\s+from\\s", "").replaceAll("\\s*;?\\s*$", "");
        return 0;
    }
}
