package eg.edu.alexu.csd.oop.db.cs2.Commands;

import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.Factories.Factory;
import eg.edu.alexu.csd.oop.db.cs2.conditions.Switch;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.FileHandler;

public class DeleteFromTable implements Command {
    private Switch aSwitch = new Switch();
    @Override
    public int execute(String query) throws SQLException {
        query = query.toLowerCase();
        query = query.replaceAll("(^\\s*delete\\s+from\\s)", "").replaceAll("\\s*;?\\s*$", "");
        String[] split = (query.contains("where"))  ? query.split("(\\s+where\\s+)") : (new String[]{query, ""}); // 0: tablename 1: condition
        split[0] = split[0].replaceAll("\\s+", ""); // clearing spaces;
        String[] condition = normalize(split[1]).split("\\s+");
        if (!FilesHandler.isTableExist(split[0], DatabaseManager.getInstance().getCurrentDatabase()))
            throw new SQLException("Table " + split[0] + " doesn't exist in database" + DatabaseManager.getInstance().getCurrentDatabase());
        Table table = FilesHandler.getTable(split[0], DatabaseManager.getInstance().getCurrentDatabase());
        int ans;
        if(condition.length == 1){
            ans = FilesHandler.getTable(query, DatabaseManager.getInstance().getCurrentDatabase()).clear();
        }else{
            Table delete = aSwitch.meetCondition(condition[1], table, condition[0], Factory.getInstance().getObject(condition[2]));
            ans = table.deleteItems(delete);
        }
        FilesHandler.saveTable(table);
        return ans;
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
}
