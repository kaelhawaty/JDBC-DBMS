package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;
import eg.edu.alexu.csd.oop.db.cs2.controller.QueriesParser;
import eg.edu.alexu.csd.oop.db.cs2.structures.Column;
import eg.edu.alexu.csd.oop.db.cs2.structures.Table;

import java.io.IOException;
import java.lang.annotation.Target;
import java.sql.*;
import java.util.*;

public class DBStatement implements java.sql.Statement{
    private DatabaseManager databaseManager;
    private Connection connection;
    private Queue<String> commands;
    private Queue<Object> results;
    private boolean isClosed;
    private ResultSet resultSet;
    private int currentResult;
    private int timeout;
    public DBStatement(DatabaseManager databaseManager, Connection connection){
        this.databaseManager = databaseManager;
        this.connection = connection;
        commands = new LinkedList<>();
        isClosed = false;
        timeout = 100;
    }
    @Override
    public void addBatch(String sql) throws SQLException {
            if(isClosed){
                throw new SQLException("This statement is already closed");
            }
            commands.add(sql);
    }
    @Override
    public void clearBatch() throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        commands.clear();
    }
    @Override
    public void close() throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        isClosed = true;
        commands = null;
        databaseManager = null;
        if(resultSet != null && !resultSet.isClosed()){
            resultSet.close();
        }
        resultSet = null;
        connection = null;

    }
    private boolean checkStructureQuery(String sql){
        return QueriesParser.checkCreateDatabase(sql) || QueriesParser.checkDropDatabase(sql) || QueriesParser.checkCreateTable(sql) || QueriesParser.checkDropTable(sql);
    }
    private boolean checkUpdateQuery(String sql){
        return QueriesParser.checkInsertInto(sql) || QueriesParser.checkDeleteFromTable(sql) || QueriesParser.checkUpdate(sql);
    }
    @Override
    public boolean execute(String sql) throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        if(checkStructureQuery(sql)){
            try {
                return databaseManager.executeStructureQuery(sql);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(QueriesParser.checkExecuteQuery(sql)){
            Object[][] table = databaseManager.executeQuery(sql);
            Table schema = new Table(databaseManager.getCurrentTable());
            List<Column> list = schema.getColumns();
            HashMap<String, Integer> colToIndex = new HashMap<>();
            HashMap<Integer, String> indexToType = new HashMap<>();
            for(int i = 1; i < list.size(); i++){
                colToIndex.put(list.get(i).getName(), i-1);
                indexToType.put(i-1, list.get(i).getType());
            }
            resultSet = new DBResultset(schema.getName(), colToIndex, indexToType, table, this);
            return table.length != 0;

        }else if(checkUpdateQuery(sql)){
            currentResult = databaseManager.executeUpdateQuery(sql);
            return true;
        }
        throw new SQLException("Syntax Error");
    }
    @Override
    public int[] executeBatch() throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        BatchUpdateException e = null;
        int size = commands.size();
        int[] updateCounts = new int[size];
        for(int i = 0; i < size; i++){
            String sql = commands.peek();
            commands.poll();
            if(checkStructureQuery(sql)){
                try {
                      databaseManager.executeStructureQuery(sql);
                     updateCounts[i] = SUCCESS_NO_INFO;
                } catch (IOException | SQLException ex) {
                    updateCounts[i] = EXECUTE_FAILED;
                    e = new BatchUpdateException(ex.getMessage(), updateCounts);
                }
            }else if(QueriesParser.checkExecuteQuery(sql)){
                try {
                    resultSet = executeQuery(sql);
                } catch (SQLException ex) {
                    updateCounts[i] = EXECUTE_FAILED;
                    e = new BatchUpdateException(ex.getMessage(), updateCounts);
                }
            }else if(checkUpdateQuery(sql)){
                try{
                    currentResult = executeUpdate(sql);
                    updateCounts[i] = currentResult;
                }catch (SQLException ex){
                    updateCounts[i] = EXECUTE_FAILED;
                    e = new BatchUpdateException(ex.getMessage(), updateCounts);
                }
            }else{
                updateCounts[i] = EXECUTE_FAILED;
                e = new BatchUpdateException("Syntax Error", updateCounts);
            }
        }
        if(e != null){
            throw e;
        }
        return updateCounts;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        if(QueriesParser.checkExecuteQuery(sql)){
            Object[][] table = databaseManager.executeQuery(sql);
            Table schema = new Table(databaseManager.getCurrentTable());
            List<Column> list = schema.getColumns();
            Set<String> s = parseQuery(sql, list);
            Map<String, Integer> colToIndex = new HashMap<>();
            Map<Integer, String> indexToType = new HashMap<>();
            int cnt = 0;
            for(int i = 1; i < list.size(); i++){
                if (s.contains(list.get(i).getName())) {
                    colToIndex.put(list.get(i).getName(), cnt);
                    indexToType.put(cnt, list.get(i).getType());
                    cnt++;
                }
            }
            resultSet = new DBResultset(schema.getName(), colToIndex, indexToType, table, this);
            return resultSet;
        }

        throw new SQLException("Syntax Error");
    }
    public Set<String> parseQuery(String query, List<Column> list){
        Set<String> set = new HashSet<>();
        query = query.toLowerCase();
        query = query.replaceAll("\\s*select\\s*", "");
        String[] split = query.split("\\s*from\\s*");
        split[0] = split[0].replaceAll("\\s+" , "");
        String[] cols = split[0].split(",");
        if(cols[0].equals("*")){
            for(int i = 1; i < list.size(); i++){
                set.add(list.get(i).getName());
            }
        }else{
            for(String s: cols){
                set.add(s);
            }
        }
        return set;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        if(QueriesParser.checkInsertInto(sql) || QueriesParser.checkDeleteFromTable(sql) || QueriesParser.checkUpdate(sql)){
            currentResult = databaseManager.executeUpdateQuery(sql);
            return currentResult;
        }
        throw new SQLException("Syntax Error");
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        return connection;
    }
    @Override
    public int getQueryTimeout() throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        return timeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        if(isClosed){
            throw new SQLException("This statement is already closed");
        }
        timeout = seconds;
    }
    @Override
    public ResultSet getResultSet() throws SQLException {
        return resultSet;
    }
    @Override
    public int getUpdateCount() throws SQLException {
        return currentResult;
    }
    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }
    @Override
    public int getMaxFieldSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getMaxRows() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchDirection() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFetchSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getResultSetType() throws SQLException {
        throw new UnsupportedOperationException();
    }

}