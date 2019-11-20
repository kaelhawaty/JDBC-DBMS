package eg.edu.alexu.csd.oop.db.cs2.conditions;

import eg.edu.alexu.csd.oop.db.cs2.ConditionsFilter;
import eg.edu.alexu.csd.oop.db.cs2.structures.*;

import java.util.List;

public class Equal implements ConditionsFilter {

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
                    if ((Integer)record.getValue()==Integer.parseInt(value)){
                       List<Record> rowInfo = table.getRow(i);
                       meetTable.addRow(rowInfo);
                    }
                }else{
                    if(value.equals((String) record.getValue())) {
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
