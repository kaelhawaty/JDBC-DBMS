package eg.edu.alexu.csd.oop.db.cs2.Factories;

import eg.edu.alexu.csd.oop.db.cs2.AbstractFactory;
import eg.edu.alexu.csd.oop.db.cs2.Command;
import eg.edu.alexu.csd.oop.db.cs2.Commands.DeleteFromTable;
import eg.edu.alexu.csd.oop.db.cs2.Commands.InsertInto;
import eg.edu.alexu.csd.oop.db.cs2.Commands.UpdateTable;
import eg.edu.alexu.csd.oop.db.cs2.controller.QueriesParser;
import eg.edu.alexu.csd.oop.db.cs2.filesGenerator.FilesHandler;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateFactory implements AbstractFactory {
    private static UpdateFactory instance;
    private UpdateFactory(){
    }
    public synchronized static UpdateFactory getInstance(){
        if(instance == null){
            instance = new UpdateFactory();
        }
        return instance;
    }
    @Override
    public Command create(String query) throws SQLException {
        if (QueriesParser.checkInsertInto(query)){
            return new InsertInto();
        }else if (QueriesParser.checkDeleteFromTable(query)){
            return new DeleteFromTable();
        }else if(QueriesParser.checkUpdate(query)){
            return new UpdateTable();
        }
        throw new SQLException("Syntax Error");

    }
}
