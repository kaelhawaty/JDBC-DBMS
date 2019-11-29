package eg.edu.alexu.csd.oop.db.cs2.Commands;

import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.Factories.Factory;
import eg.edu.alexu.csd.oop.db.cs2.conditions.Switch;
import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateTable  implements Command {
    private Switch aSwitch = new Switch();
    @Override
    public int execute(String query) throws SQLException {
        query = query.replaceAll("(?i)^\\s*update\\s+", "").replaceAll("\\s*;?\\s*$", "");
        Pattern regex = Pattern.compile("where\\s+\\w+\\s*[=<>]\\s*([0-9]+|\\'\\w+\\')$");
        Matcher matcher = regex.matcher(query.toLowerCase());
        String[] split = query.replaceAll("(?i)(\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|\\'\\w+\\'))$", "").replaceAll("\\,", " ").replaceAll("=", " ").split("\\s+");
        if(!FilesHandler.isTableExist(split[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase()))
            throw new SQLException("Table " + split[0] + " doesn't exist in database" + DatabaseManager.getInstance().getCurrentDatabase());
        for (int i = 2; i < split.length; i+=2){
            if (!FilesHandler.getTable(split[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase()).containColumn(split[i].toLowerCase()))
                throw new SQLException("Column " + split[i] + "doesn't exist in table" + split[0]);
        }
        Object[] vals = new Object[split.length-2];
        for(int i = 2; i < split.length; i+= 2){
            vals[i-2] = split[i];
            vals[i-1] = Factory.getInstance().getObject(split[i+1]);
        }
        if(matcher.find()) {
            String[] condition = normalize(matcher.group()).split("\\s+");
            Table table = aSwitch.meetCondition(condition[2], FilesHandler.getTable(split[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase()), condition[1], Factory.getInstance().getObject(condition[3]));
            table.updateTable(vals);
            FilesHandler.getTable(split[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase()).updateTable(table);
            FilesHandler.saveTable(DatabaseManager.getInstance().getCurrentTable());
            return table.getIDCounter();
        }
        FilesHandler.getTable(split[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase()).updateTable(vals);
        FilesHandler.saveTable(DatabaseManager.getInstance().getCurrentTable());
        return FilesHandler.getTable(split[0].toLowerCase(), DatabaseManager.getInstance().getCurrentDatabase()).getIDCounter();
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
