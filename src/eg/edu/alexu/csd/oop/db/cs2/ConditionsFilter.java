package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.sql.SQLException;

public interface ConditionsFilter {
    public Table meetCondition(Table table, String columnName, Object value) throws SQLException;
}
