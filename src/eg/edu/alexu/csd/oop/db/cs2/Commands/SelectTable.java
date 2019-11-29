package eg.edu.alexu.csd.oop.db.cs2.Commands;

import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.Factories.Factory;
import eg.edu.alexu.csd.oop.db.cs2.conditions.Switch;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Record;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.sql.SQLException;
import java.util.List;

public class SelectTable {
    private Switch aSwitch = new Switch();
    private Object[][] objects;
    public Object[][] execute(String query) throws SQLException {
        query = query.toLowerCase();
        query = query.replaceAll("(\\s*select\\s*)", "").replaceAll("\\s*;?\\s*$", "");;
        String[] split = query.split("(\\s+from\\s+)"); // split[0] = columns name.., split[1] = tablename where ...
        String columns[] = split[0].replaceAll(",", " ").split("\\s+");
        String[] split2 = (split[1].contains("where"))  ? split[1].split("(\\s+where\\s+)") : (new String[]{split[1], ""}); // split2[0] == tableName, split2[1] == condition;
        split2[0] = split2[0].replaceAll("\\s+", "").toLowerCase(); // clearing spaces;
        String[] condition = normalize(split2[1]).split("\\s+");
        if(!FilesHandler.isTableExist(split2[0], DatabaseManager.getInstance().getCurrentDatabase()))
            throw new SQLException("Table " + split2[0] + " doesn't exist in database" + DatabaseManager.getInstance().getCurrentDatabase());
        Table table = FilesHandler.getTable(split2[0], DatabaseManager.getInstance().getCurrentDatabase());
        if(!columns[0].equals("*")){
            for (String columnName : columns){
                if(!table.containColumn(columnName))
                    throw new SQLException("Column " + columnName + "doesn't exist in table" + table.getName());
            }
        }
        if(condition.length != 1) {
            if (!FilesHandler.getTable(split2[0], DatabaseManager.getInstance().getCurrentDatabase()).containColumn(condition[0]))
                throw new SQLException("Column " + condition[0] + "doesn't exist in table" + split2[0]);
            table = aSwitch.meetCondition(condition[1], table, condition[0], Factory.getInstance().getObject(condition[2]));
        }
        return selectTable((columns[0].equals("*")) ? table.getColumns() : table.getColumns(columns), (columns[0].equals("*")) ? true : false);
    }
    public String normalize(String s){
        StringBuilder sb = new StringBuilder();
        for(char c: s.toCharArray()){
            if(c == '=' || c == '<' || c == '>'){
                sb.append(" ");
                sb.append(c);
                sb.append(" ");
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
    private Object[][] selectTable(List<Column> columns, boolean flag){
        Object[][] objects;
        int i = 0, j = 0;
        if(flag)
            i = 1;
        objects = new Object[columns.get(0).getRecords().size()][columns.size()-i];
        for (Column column : columns) {
            if (flag){
                flag = false;
                continue;
            }
            List<Record> records = column.getRecords();
            i = 0;
            for (Record record : records) {
                objects[i][j] = (record == null) ? null : record.getValue();
                i++;
            }
            j++;
        }
        return objects;
    }
}
