package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.conditions.Equal;
import eg.edu.alexu.csd.oop.db.cs2.conditions.GreaterThan;
import eg.edu.alexu.csd.oop.db.cs2.conditions.LessThan;
import eg.edu.alexu.csd.oop.db.cs2.structures.DatabaseContainer;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.util.List;
import java.util.Scanner;


public class test {

    public static void main(String argv[]){
        String s = "";
        QueriesParser qp = new QueriesParser();
        Scanner scan = new Scanner(System.in);
        qp.execute("create database db1");
        qp.execute("create table test(age varchar, size int)");
        qp.execute("insert into test(age, size) values ('AboBakr', 5)");
        qp.execute("insert into test(age, size) values ('Hazumy',10)");
        qp.execute("insert into test(age, size) values ('Yomna',3)");
        qp.execute("delete from test where age > 'aa'");
        while (true){
            s = scan.nextLine();
            qp.execute(s);
        }
    }
}
