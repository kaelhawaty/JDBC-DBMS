package eg.edu.alexu.csd.oop.db.cs2.conditions;

import eg.edu.alexu.csd.oop.db.cs2.ConditionsFilter;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Record;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.util.HashMap;
import java.util.List;

public class Equal implements ConditionsFilter {

    @Override
    public Table meetCondition(Table table, String columnName, String value) {
        List<Column> columns = table.getColumns();
        Table meetTable = new Table(table);
        for (Column column : columns){
            List<Record> records = column.getRecords();
            if (!column.getName().equals(columnName))
                continue;
            for (Record record : records){
                if(column.getType().equalsIgnoreCase("int")){
                    if ((Integer)record.getValue()==Integer.parseInt(value)){
                       HashMap rowInfo = table.getRow(column);
                       meetTable.addRow(rowInfo);
                    }
                }else{
                    if(value.equals((String) record.getValue())){
                        HashMap rowInfo = table.getRow(column);
                        meetTable.addRow(rowInfo);
                    }
                }
            }
        }
        return meetTable;
    }
}
