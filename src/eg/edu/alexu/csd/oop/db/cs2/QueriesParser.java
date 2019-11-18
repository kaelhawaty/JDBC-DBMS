package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.DatabaseManager;

public class QueriesParser {
    Database db;
    public QueriesParser(){
        db = DatabaseManager.getInstance();
    }
    public static void execute(String input){
        if(checkExecuteStructureQuery(input)){
            System.out.println("here create table, drop table, drop table or drop database");
        }else if(checkExecuteQuery(input)){
            System.out.println("here selection");
        }else if(checkExecuteUpdateQuery(input)){
            System.out.println("here update/insert/delete");
        }else{
            System.out.println("Syntax Error");
        }
    }
    public static boolean checkCreateDatabase(String input){
        return input.toLowerCase().matches("^\\s*create\\s+database\\s+(\\w)+\\s*;?\\s*$");
    }
    public static boolean checkExecuteStructureQuery(String input){
        return input.toLowerCase().matches("^\\s*drop\\s+(database|table)\\s+(\\w)+\\s*;?\\s*$")
                || input.toLowerCase().matches("^\\s*create\\s+table\\s+\\w+\\s*\\((\\w+\\s+(int|varchar)\\s*,\\s*)*(\\w+\\s+(int|varchar)\\s*)\\)\\s*;?\\s*$")
                || checkCreateDatabase(input);
    }
    public static boolean checkExecuteQuery(String input) {
        return input.toLowerCase().matches("^\\s*select\\s+(\\w+,\\s+)*(\\w+|\\*)\\s+from\\s+\\w+\\s*(\\s+where\\s+\\w+\\s*[=<>]\\s*([0-9]+|(\\'|\\\")\\w+(\\'|\\\")))?\\s*;?\\s*$");
    }
    public static boolean checkExecuteUpdateQuery(String input){
        return false;
    }
}
