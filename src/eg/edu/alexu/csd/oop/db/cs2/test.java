package eg.edu.alexu.csd.oop.db.cs2;

import java.util.Scanner;


public class test {

    public static void main(String argv[]){
        String s = "";
        QueriesParser qp = new QueriesParser();
        Scanner scan = new Scanner(System.in);
        qp.execute("create database db1");
        qp.execute("create table test(age varchar, size int)");
        qp.execute("insert into test(size) values (5)");
        qp.execute("insert into test(age, size) values ('Hazumy',10)");
        qp.execute("insert into test(age, size) values ('Yomna',3)");
        qp.execute("select * from test where age='Hazumy'");
        while (true){
            s = scan.nextLine();
            qp.execute(s);
        }
    }
}
