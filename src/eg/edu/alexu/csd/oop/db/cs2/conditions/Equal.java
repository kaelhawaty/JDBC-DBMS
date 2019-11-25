package eg.edu.alexu.csd.oop.db.cs2.conditions;

import eg.edu.alexu.csd.oop.db.cs2.ConditionsFilter;
import eg.edu.alexu.csd.oop.db.cs2.structures.*;

import java.sql.SQLException;
import java.util.List;

public class Equal implements ConditionsFilter {

    @Override
    public Table meetCondition(Table table, String columnName, Object value) throws SQLException {
        List<Column> columns = table.getColumns();
        Table meetTable = new Table(table);
        for (Column column : columns){
            int i = 0;
            List<Record> records = column.getRecords();
            if (!column.getName().equals(columnName))
                continue;
            for (Record record : records){
                if(Factory.getInstance().compareObject(record.getValue(), value) == 0){
                    List<Record> rowInfo = table.getRow(i);
                    meetTable.addRow(rowInfo);
                }
                i++;
            }
        }
        return meetTable;
    }
}
