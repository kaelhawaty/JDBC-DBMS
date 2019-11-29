package eg.edu.alexu.csd.oop.db.cs2.Factories;

import eg.edu.alexu.csd.oop.db.cs2.AbstractFactory;
import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.Commands.CreateDatabase;
import eg.edu.alexu.csd.oop.db.cs2.Commands.CreateTable;
import eg.edu.alexu.csd.oop.db.cs2.Commands.DropDatabase;
import eg.edu.alexu.csd.oop.db.cs2.Commands.DropTable;
import eg.edu.alexu.csd.oop.db.cs2.controller.QueriesParser;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StructureFactory implements AbstractFactory {
    private static StructureFactory instance;
    private StructureFactory(){
    }
    public synchronized static StructureFactory getInstance(){
        if(instance == null){
            instance = new StructureFactory();
        }
        return instance;
    }
    @Override
    public Command create(String query) throws SQLException {
        Command cmd;
        if(QueriesParser.checkCreateDatabase(query)){
            return new CreateDatabase();
        }else if(QueriesParser.checkDropDatabase(query)){
            return new DropDatabase();
        } else if (QueriesParser.checkCreateTable(query)) {
            return new CreateTable();
        }else if (QueriesParser.checkDropTable(query)){
            return new DropTable();
        }
        throw new SQLException("Syntax Error");
    }
}
