package eg.edu.alexu.csd.oop.db.cs2.conditions;

import eg.edu.alexu.csd.oop.db.cs2.ConditionsFilter;
import eg.edu.alexu.csd.oop.db.cs2.structures.*;

import java.util.List;

public class LessThan implements ConditionsFilter {

    @Override
    public Table meetCondition(Table table, String columnName, String value) {
        List<Column> columns = table.getColumns();
        Table meetTable = new Table(table);
        for (Column column : columns){
            int i = 0;
            List<Record> records = column.getRecords();
            if (!column.getName().equals(columnName))
                continue;
            for (Record record : records){
                if(column.getType().equalsIgnoreCase("int")){
                    if (record != null && (Integer)record.getValue() < Integer.parseInt(value)){
                        List<Record> rowInfo = table.getRow(i);
                        meetTable.addRow(rowInfo);
                    }
                }else{

                    if(record != null && value.compareToIgnoreCase((String) record.getValue()) > 0){
                        List<Record> rowInfo = table.getRow(i);
                        meetTable.addRow(rowInfo);
                    }
                }
                i++;
            }
        }
        return meetTable;
    }
}