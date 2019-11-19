package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.conditions.Equal;
import eg.edu.alexu.csd.oop.db.cs2.structures.DatabaseContainer;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.util.List;
import java.util.Scanner;


public class test {

    public static void main(String argv[]){
        String s = "";
        QueriesParser qp = new QueriesParser();
        Scanner scan = new Scanner(System.in);
        qp.execute("create database db1 ");
        qp.execute("create table tab2 (name varchar, age int, size int)");
        qp.execute("insert into tab2 (name, age, size) values ('hazem', 12, 15)");
        qp.execute("insert into tab2 (name, age, size) values ('hazem', 12, 15)");
        qp.execute("insert into tab2 (name, age, size) values ('hazem', 12, 15)");
        DatabaseContainer dbc = DatabaseManager.getInstance().get();
        List<Table> tables = dbc.getTables();
        for (Table table : tables){
            if (table.getName().equalsIgnoreCase("tab2")){
                ConditionsFilter cf = new Equal();
                Table tb = cf.meetCondition(table, "age", "12");
            }
        }
    }
}
