package eg.edu.alexu.csd.oop.db.cs2.Factories;

import eg.edu.alexu.csd.oop.db.cs2.AbstractFactory;
import eg.edu.alexu.csd.oop.db.cs2.Command;

import java.sql.SQLException;

public class SelectFactory implements AbstractFactory {
    private static SelectFactory instance;
    private SelectFactory(){
    }
    public synchronized static SelectFactory getInstance(){
        if(instance == null){
            instance = new SelectFactory();
        }
        return instance;
    }
    @Override
    public Command create(String query) throws SQLException {
        return null;
    }
}
