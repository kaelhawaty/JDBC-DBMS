package eg.edu.alexu.csd.oop.db.cs2;


import java.sql.SQLException;

public class QueriesParser {
    private Database db = DatabaseManager.getInstance();
    public void execute(String input){

        try {
            if (checkCreateDatabase(input) || checkDropDatabase(input) || checkCreateTable(input) || checkDropTable(input)) {
                db.executeStructureQuery(input);
            } else if (checkExecuteQuery(input)) {
                System.out.println("here selection");
            } else if (checkInsertInto(input) || checkDeleteFromTable(input)) {
                System.out.println(db.executeUpdateQuery(input));
            } else {
                System.out.println("Syntax Error");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static boolean checkCreateDatabase(String input){
        return input.toLowerCase().matches("^\\s*create\\s+database\\s+(\\w|\\\\)+\\s*;?\\s*$");
    }
    public static boolean checkDropDatabase(String input){
        return input.toLowerCase().matches("^\\s*drop\\s+database\\s+(\\w|\\\\)+\\s*;?\\s*$");
    }
    public static boolean checkCreateTable(String input){
        return input.toLowerCase().matches("^\\s*create\\s+table\\s+\\w+\\s*\\((\\s*\\w+\\s+(int|varchar)\\s*,\\s*)*(\\s*\\w+\\s+(int|varchar)\\s*)\\)\\s*;?\\s*$");
    }
    public static boolean checkDropTable(String input){
        return input.toLowerCase().matches("^\\s*drop\\s+table\\s+(\\w)+\\s*;?\\s*$");
    }
    public static boolean checkInsertInto(String input){
        return input.toLowerCase().matches("^\\s*insert\\s+into\\s+\\w+\\s+values\\s*\\((\\s*([0-9]+|\\'\\w+\\')\\s*,)*\\s*([0-9]+|\\'\\w+\\')\\s*\\)\\s*;?\\s*$")
                ||input.toLowerCase().matches("^\\s*insert\\s+into\\s+\\w+\\s*\\((\\s*\\w+\\s*,)*\\s*\\w+\\s*\\)\\s*values\\s*\\((\\s*([0-9]+|\\'\\w+\\')\\s*,)*\\s*([0-9]+|\\'\\w+\\')\\s*\\)\\s*;?\\s*$");
    }
    public static boolean checkDeleteFromTable(String input){
        return input.toLowerCase().matches("^\\s*delete\\s+from\\s+\\w+\\s*(\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|\\'\\w+\\'))?\\s*;?\\s*$");
    }
    public static boolean checkExecuteQuery(String input) {
        return input.toLowerCase().matches("^\\s*select\\s+(\\w+,\\s+)*(\\w+|\\*)\\s+from\\s+\\w+\\s*(\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|(\\'|\\\")\\w+(\\'|\\\")))?\\s*;?\\s*$");
    }
}
