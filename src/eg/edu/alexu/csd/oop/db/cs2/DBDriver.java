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
    public DBDriver(){
        DBLogger.getInstance().getLogger().info("DBDriver has been created successfully!");
    }
    @Override
    public boolean acceptsURL(String url) throws SQLException {
        if(url == null) {
            DBLogger.getInstance().getLogger().info("URL has been rejected: " + url);
            return false;
        }
        String[] urlData = url.split(":");
        boolean result = urlData.length == 3 && urlData[0].equalsIgnoreCase("jdbc") && urlData[1].equalsIgnoreCase("xmldb") && urlData[2].equalsIgnoreCase("//localhost");
        if(result){
            DBLogger.getInstance().getLogger().info("URL Accepted: "+url);
            return true;
        }
        DBLogger.getInstance().getLogger().info("URL has been rejected: "+url);
        return false;
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url))
            throw new SQLException("The url is invalid/unsupported!");
        if(!info.containsKey("path")){
            DBLogger.getInstance().getLogger().info("Creation of connection has failed: Missing \"Path\" in info");
            throw new SQLException("There is no \"path\" key in info!");
        }
        this.path = info.get("path").toString();
        return new DBConnection(url, new File(path));
    }
    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        DriverPropertyInfo[] propertyInfos = new DriverPropertyInfo[1];
        propertyInfos[0].name = "path";
        propertyInfos[0].required = true;
        propertyInfos[0].description = "File object representing main path file for the Database";
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
