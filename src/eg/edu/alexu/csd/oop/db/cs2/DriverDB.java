package eg.edu.alexu.csd.oop.db.cs2;

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class DriverDB implements java.sql.Driver{

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
        return new connectionDB(url, info.get("path").toString().toString());
    }
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
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
