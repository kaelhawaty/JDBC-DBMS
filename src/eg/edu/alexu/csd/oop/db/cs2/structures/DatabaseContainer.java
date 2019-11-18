package eg.edu.alexu.csd.oop.db.cs2.structures;

import java.util.ArrayList;
import java.util.List;

public class DatabaseContainer {
    String name;
    List<Table> tables;
    public DatabaseContainer(String n){
        this.name = n;
        tables = new ArrayList<>();
    }

}
