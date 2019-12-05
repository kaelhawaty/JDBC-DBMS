package eg.edu.alexu.csd.oop.db.cs2;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DBDriver implements java.sql.Driver{

    private String path;
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if(url == null)
            return false;
        String[] urlData = url.split(":");
        return urlData.length == 3 && urlData[0].equalsIgnoreCase("jdbc") && urlData[1].equalsIgnoreCase("xmldb") && urlData[2].equalsIgnoreCase("//localhost");
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url))
            throw new SQLException("The url is invalid/unsupported");
        this.path = info.get("path").toString();
        return new DBConnection(url, new File(path));
    }
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        DriverPropertyInfo[] propertyInfos = new DriverPropertyInfo[info.size()];
        int i = 0;
        for (Object object : info.keySet()){
            propertyInfos[i].name = (String) object;
            propertyInfos[i].value = (String) info.get(object);
            i++;
        }
        return propertyInfos;
    }

    @Override
    public int getMajorVersion() {
         throw new UnsupportedOperationException();
    }

    @Override
    public int getMinorVersion() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean jdbcCompliant() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException();
    }
}
