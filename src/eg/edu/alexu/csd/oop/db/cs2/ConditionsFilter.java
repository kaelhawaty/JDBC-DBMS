package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

public interface ConditionsFilter {
    public Table meetCondition(Table table, String columnName, String value);
}
