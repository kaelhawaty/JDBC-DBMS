package eg.edu.alexu.csd.oop.db.cs2.conditions;

import eg.edu.alexu.csd.oop.db.cs2.ConditionsFilter;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.util.HashMap;

public class Switch {
    private final HashMap<String, ConditionsFilter> commandMap = new HashMap<>();
    public void register(String commandName, ConditionsFilter command) {
        commandMap.put(commandName, command);
    }

    public Table meetCondition(String commandName, Table table, String columnName, String value) {
        ConditionsFilter command = commandMap.get(commandName);
        if (command == null) {
            throw new IllegalStateException("no command registered for " + commandName);
        }
        return command.meetCondition(table, columnName, value);
    }
}
