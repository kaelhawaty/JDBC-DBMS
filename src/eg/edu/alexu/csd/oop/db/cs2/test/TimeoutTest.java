package eg.edu.alexu.csd.oop.db.cs2.test;

import eg.edu.alexu.csd.oop.TestRunner;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class TimeoutTest {
    private Connection createDatabase(String databaseName, boolean drop) throws SQLException {
        Driver driver = (Driver)TestRunner.getImplementationInstanceForInterface(Driver.class);
        Properties info = new Properties();
        File dbDir = new File("sample" + System.getProperty("file.separator") + ((int)(Math.random() * 100000)));
        info.put("path", dbDir.getAbsoluteFile());
        Connection connection = driver.connect("jdbc:xmldb://localhost", info);
        Statement statement = connection.createStatement();
        statement.execute("DROP DATABASE " + databaseName);
        if(drop)
            statement.execute("CREATE DATABASE " + databaseName);
        statement.close();
        return connection;
    }
    @Test
    public void ExecuteTimeoutTest() throws SQLException {
        Connection connect = createDatabase("testDB", true);
        boolean flag = false;
        try {
            Statement statement = connect.createStatement();
            statement.setQueryTimeout(0);
            statement.execute("CREATE   TABLE   table_name1(column_name1 varchar , column_name2    int,  column_name3 varchar)");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            flag = true;
        }
        Assert.assertTrue("Exception wasn't thrown", flag);
    }


}
