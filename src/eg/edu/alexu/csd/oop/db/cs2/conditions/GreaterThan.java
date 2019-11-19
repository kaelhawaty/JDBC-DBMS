package eg.edu.alexu.csd.oop.db.cs2.conditions;

import eg.edu.alexu.csd.oop.db.cs2.ConditionsFilter;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Record;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.util.List;

public class GreaterThan implements ConditionsFilter {

    @Override
    public Table meetCondition(Table table, String columnName, String compareTo) {
        List<Column> columns = table.getColumns();
        Table meetTable = new Table("meet"+table.getName());
        for (Column column : columns){
            List<Record> records = column.getRecords();
            Column current = new Column(column.getName(), column.getType());
            if (!column.getName().equals(columnName))
                continue;
            for (Record record : records){
                if(column.getType().equalsIgnoreCase("int")){
                    if ((Integer)record.getValue() > Integer.parseInt(compareTo)){
                        current.addRecord(new Record<Integer>((Integer)record.getValue()));
                    }
                }else{
                    if(compareTo.compareTo((String) record.getValue()) < 0){
                        current.addRecord(new Record<String>((String) record.getValue()));
                    }
                }
            }
            meetTable.addColumn(current);
        }
        return meetTable;
    }
}
