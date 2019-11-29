package eg.edu.alexu.csd.oop.db.cs2;

import java.sql.SQLException;

public interface Command {
    public int execute(String query) throws SQLException;
}
