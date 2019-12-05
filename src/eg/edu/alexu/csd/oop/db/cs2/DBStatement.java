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
        DBLogger.getInstance().getLogger().info("Statement has been created successfully");
        this.databaseManager = databaseManager;
        this.connection = connection;
        commands = new LinkedList<>();
        isClosed = false;
        timeout = 100;
    }
    @Override
    public void addBatch(String sql) throws SQLException {
            if(isClosed){
                DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
                throw new SQLException("This statement is already closed");
            }
            commands.add(sql);
            DBLogger.getInstance().getLogger().info("SQL command has been added to the batch successfully");
    }
    @Override
    public void clearBatch() throws SQLException {
        if(isClosed){
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
        commands.clear();
        DBLogger.getInstance().getLogger().info("Batch has been cleared successfully");
    }
    @Override
    public void close() throws SQLException {
        if(isClosed){
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
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
        DBLogger.getInstance().getLogger().info("Statement has been closed successfully");

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
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
        if(checkStructureQuery(sql)){
            try {
                boolean flag = databaseManager.executeStructureQuery(sql);
                if(flag)
                    DBLogger.getInstance().getLogger().info("SQL command has been executed successfully: " + sql);
                else
                    DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql);
                return flag;
            } catch (SQLException e) {
                DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql);
                DBLogger.getInstance().getLogger().info("Exception has been thrown: " + e.getMessage());
                throw e;
            }catch(IOException e){
                DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql);
                DBLogger.getInstance().getLogger().info("Exception has been thrown: " + e.getMessage());
                throw new SQLException(e.getMessage());
            }
        }else if(QueriesParser.checkExecuteQuery(sql)){
            Object[][] table;
            try {
                table = databaseManager.executeQuery(sql);
            }catch(SQLException e){
                DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql);
                DBLogger.getInstance().getLogger().info("Exception has been thrown: " + e.getMessage());
                throw e;
            }
            Table schema = new Table(databaseManager.getCurrentTable());
            List<Column> list = schema.getColumns();
            HashMap<String, Integer> colToIndex = new HashMap<>();
            HashMap<Integer, String> indexToType = new HashMap<>();
            for(int i = 1; i < list.size(); i++){
                colToIndex.put(list.get(i).getName(), i-1);
                indexToType.put(i-1, list.get(i).getType());
            }
            resultSet = new DBResultset(schema.getName(), colToIndex, indexToType, table, this);
            DBLogger.getInstance().getLogger().info("SQL command has been executed successfully: "+ sql);
            return table.length != 0;

        }else if(checkUpdateQuery(sql)){
            try {
                currentResult = databaseManager.executeUpdateQuery(sql);
            }catch(SQLException e){
                DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql);
                DBLogger.getInstance().getLogger().info("Exception has been thrown: " + e.getMessage());
                throw e;
            }
            DBLogger.getInstance().getLogger().info("SQL command has been executed successfully: "+ sql);
            DBLogger.getInstance().getLogger().info("Number of rows changed: " + currentResult);
            return true;
        }
        DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql + " Reason: Wrong Syntax");
        throw new SQLException("Syntax Error");
    }
    @Override
    public int[] executeBatch() throws SQLException {
        if(isClosed){
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
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
                    boolean flag = databaseManager.executeStructureQuery(sql);
                    if(flag)
                        DBLogger.getInstance().getLogger().info("SQL command has been executed successfully: " + sql);
                    else
                        DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql);
                    updateCounts[i] = SUCCESS_NO_INFO;
                } catch (IOException | SQLException ex) {
                    updateCounts[i] = EXECUTE_FAILED;
                    e = new BatchUpdateException(ex.getMessage(), updateCounts);
                    DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql + ex.getMessage());
                }
            }else if(QueriesParser.checkExecuteQuery(sql)){
                try {
                    resultSet = executeQuery(sql);
                    DBLogger.getInstance().getLogger().info("SQL command has been executed successfully: "+ sql);
                } catch (SQLException ex) {
                    updateCounts[i] = EXECUTE_FAILED;
                    e = new BatchUpdateException(ex.getMessage(), updateCounts);
                    DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql + ex.getMessage());
                }
            }else if(checkUpdateQuery(sql)){
                try{
                    currentResult = executeUpdate(sql);
                    updateCounts[i] = currentResult;
                    DBLogger.getInstance().getLogger().info("SQL command has been executed successfully: "+ sql);
                    DBLogger.getInstance().getLogger().info("Number of rows changed: " + currentResult);
                }catch (SQLException ex){
                    updateCounts[i] = EXECUTE_FAILED;
                    e = new BatchUpdateException(ex.getMessage(), updateCounts);
                    DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql + ex.getMessage());
                }
            }else{
                updateCounts[i] = EXECUTE_FAILED;
                e = new BatchUpdateException("Syntax Error", updateCounts);
                DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql + e.getMessage());
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
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
        if(QueriesParser.checkExecuteQuery(sql)){
            Object[][] table;
            try {
                table = databaseManager.executeQuery(sql);
            }catch(SQLException e){
                DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql);
                DBLogger.getInstance().getLogger().info("Exception has been thrown: " + e.getMessage());
                throw e;
            }
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
            DBLogger.getInstance().getLogger().info("SQL command has been executed successfully: "+ sql);
            return resultSet;
        }
        DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql + " Reason: Wrong Syntax");
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
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
        if(QueriesParser.checkInsertInto(sql) || QueriesParser.checkDeleteFromTable(sql) || QueriesParser.checkUpdate(sql)){
            try {
                currentResult = databaseManager.executeUpdateQuery(sql);
            }catch(SQLException e){
                DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql);
                DBLogger.getInstance().getLogger().info("Exception has been thrown: " + e.getMessage());
                throw e;
            }
            DBLogger.getInstance().getLogger().info("SQL command has been executed successfully: "+ sql);
            DBLogger.getInstance().getLogger().info("Number of rows changed: " + currentResult);
            return currentResult;
        }
        DBLogger.getInstance().getLogger().info("Failed to execute SQL command: " + sql + " Reason: Wrong Syntax");
        throw new SQLException("Syntax Error");
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(isClosed){
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
        return connection;
    }
    @Override
    public int getQueryTimeout() throws SQLException {
        if(isClosed){
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
        return timeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        if(isClosed){
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
        timeout = seconds;
    }
    @Override
    public ResultSet getResultSet() throws SQLException {
        if(isClosed){
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
        return resultSet;
    }
    @Override
    public int getUpdateCount() throws SQLException {
        if(isClosed){
            DBLogger.getInstance().getLogger().info("Failed to executed close: Statement is already closed");
            throw new SQLException("This statement is already closed");
        }
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