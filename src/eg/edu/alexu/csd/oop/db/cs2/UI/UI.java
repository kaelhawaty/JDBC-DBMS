package eg.edu.alexu.csd.oop.db.cs2.UI;
import eg.edu.alexu.csd.oop.db.cs2.controller.QueriesParser;
import java.util.Scanner;

public class UI {

    public static void main(String argv[]){
        String s;
        QueriesParser qp = new QueriesParser();
        Scanner scan = new Scanner(System.in);
        while(true){
            s = scan.nextLine();
            qp.execute(s);
        }
    }
}
