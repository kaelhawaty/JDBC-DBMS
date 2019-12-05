package eg.edu.alexu.csd.oop.db.cs2;

import eg.edu.alexu.csd.oop.db.cs2.controller.DatabaseManager;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DBResultSetMetaData implements ResultSetMetaData{
    private ArrayList<String> columnNames;
    private Map<Integer, String> columnTypes ;
    private String tableName;
    public DBResultSetMetaData(String table_name, Map<String,Integer> col_names, Map<Integer, String> column_Types) throws SQLException {
         this.columnTypes = column_Types;
         this.tableName=table_name;
         for(Map.Entry<String,Integer> entry:col_names.entrySet()){
             columnNames.add(entry.getKey());
         }

    }
    private final int integertype = Types.INTEGER;
    private final int varchartype = Types.VARCHAR;
    private final int datetype = Types.DATE;
    private final int floattype = Types.FLOAT;

    public int convert_type(String type){
        if(type=="int"){
          return integertype;
        }
        else if(type =="varchar"){
            return varchartype ;
        }
        else if(type=="date"){
            return datetype;
        }
        else if(type=="float"){
            return floattype;
        }
        else return -1 ;
    }

    public DBResultSetMetaData() {
        columnNames = new ArrayList<>();
        columnTypes = new HashMap<>();
        tableName = null;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return columnNames.size();
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        if ( !(column >= 1 && column <= columnNames.size()))
        return null;

        return columnNames.get(--column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        if ( !(column >= 1 && column <= columnNames.size()))
            return null;

        return columnNames.get(--column);
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return tableName;
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        if ( !(column >= 1 && column <= columnNames.size())) {
            throw new SQLException();
        }

        return convert_type(columnTypes.get(--column));
    }
    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int isNullable(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getScale(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
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
}
