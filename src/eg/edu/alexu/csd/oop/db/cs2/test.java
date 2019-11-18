package eg.edu.alexu.csd.oop.db.cs2;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {

    public static void main(String argv[]){
        String s = "";
        QueriesParser qp = new QueriesParser();
        Scanner scan = new Scanner(System.in);
        while(true){
            s = scan.nextLine();
            qp.execute(s);
        }
    }
}
