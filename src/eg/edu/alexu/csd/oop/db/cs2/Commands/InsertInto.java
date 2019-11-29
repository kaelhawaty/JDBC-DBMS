package eg.edu.alexu.csd.oop.db.cs2.Commands;

import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.Factories.Factory;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class InsertInto implements Command {
    @Override
    public int execute(String query) throws SQLException {
        query = query.replaceAll("(?i)(^\\s*insert\\s+into\\s+)", "").replaceAll("\\s*;?\\s*$", "").replaceAll("[\\(\\),]", " ");;
        String[] split = query.split("(?i)(\\s+values\\s+)"); // 0: tablename cols 1: values
        String[] cols = split[0].split("\\s+"); // + tablename;
        String[] values = split[1].split("\\s+");
        HashMap<String, Object> hashMap = new HashMap<>();
        if(!(FilesHandler.isTableExist(cols[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase())))
            throw new SQLException("Table " + cols[0] + " doesn't exist in database" + DatabaseManager.getInstance().getCurrentDatabase());
        Table table = FilesHandler.getTable(cols[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase());
        if(cols.length == 1 && table.getColumns().size()-1 == values.length){
            String[] ans = new String[values.length+1];
            List<Column> columns = table.getColumns();
            ans[0] = cols[0];
            for(int i = 1; i < columns.size(); i++){
                ans[i] = columns.get(i).getName();
            }
            cols = ans;
        }
        if(cols.length-1 != values.length) throw new SQLException("Syntax Error");
        hashMap.put("ID" , FilesHandler.getTable(cols[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase()).getIDCounter());
        for(int i = 0; i < values.length; ++i){
            if(FilesHandler.getTable(cols[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase()).containColumn(cols[i+1].toLowerCase())) {
                hashMap.put(cols[i+1].toLowerCase(), Factory.getInstance().getObject(values[i]));
            }else
                throw new SQLException("Column " + cols[i+1] + "doesn't exist in table" + cols[0]);
        }
        table.addRow(hashMap);
        FilesHandler.saveTable(table);
        return 1;
    }
}
