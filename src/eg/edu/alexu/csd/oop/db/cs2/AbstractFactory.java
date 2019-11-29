package eg.edu.alexu.csd.oop.db.cs2;

import java.sql.SQLException;

public interface AbstractFactory {
    public Command create(String query) throws SQLException;
}
