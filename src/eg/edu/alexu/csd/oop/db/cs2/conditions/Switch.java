package eg.edu.alexu.csd.oop.db.cs2.conditions;

import eg.edu.alexu.csd.oop.db.cs2.ConditionsFilter;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.sql.SQLException;
import java.util.HashMap;

public class Switch {
    private final HashMap<String, ConditionsFilter> commandMap = new HashMap<>();
    private ConditionsFilter equal = new Equal();
    private ConditionsFilter greater = new GreaterThan();
    private ConditionsFilter less = new LessThan();
    public Switch(){
        commandMap.put("=", equal);
        commandMap.put("<", less);
        commandMap.put(">", greater);
    }

    public Table meetCondition(String commandName, Table table, String columnName, Object value) throws SQLException {
        ConditionsFilter command = commandMap.get(commandName);
        if (command == null) {
            throw new IllegalStateException("no command registered for " + commandName);
        }
        return command.meetCondition(table, columnName, value);
    }
}
