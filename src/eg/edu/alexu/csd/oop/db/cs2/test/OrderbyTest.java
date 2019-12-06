package eg.edu.alexu.csd.oop.db.cs2.test;

import org.junit.Test;

import eg.edu.alexu.csd.oop.TestRunner;
import org.junit.Assert;

import javax.xml.transform.Result;
import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.Properties;

public class OrderbyTest {
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
    public void SelectAllOrderBy() throws SQLException {
        Connection connect = createDatabase("testDB", true);
        Object[][] expected = new Object[][]{{"'3frto'", 21}, {"'Hazem'", 20}, {"'Kareem'", 20}, {"'Kareem'", 19}};
        try {
            Statement statement = connect.createStatement();
            statement.execute("CREATE   TABLE   ptats ( name varchar ,  age int)");
            int count1 = statement.executeUpdate("INSERT INTO ptats VALUES ('Kareem', 19)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count1);
            int count2 = statement.executeUpdate("INSERT INTO ptats VALUES ('Hazem', 20)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count2);
            int count3 = statement.executeUpdate("INSERT INTO ptats VALUES ('3frto', 21)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count3);
            int count4 = statement.executeUpdate("INSERT INTO ptats VALUES ('Kareem', 20)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count3);
            ResultSet ans = statement.executeQuery("Select * from ptats order by name asc, age desc");
            for(int i = 0; i < expected.length; i++){
                Assert.assertTrue("Wrong table selection", ans.next());
                for(int j = 0; j < expected[0].length; j++){
                    Object actual = ans.getObject(j+1);
                    Assert.assertEquals("Expected Object is wrong found: " + actual + " Expected: "+  expected[i][j], expected[i][j], actual );
                }
            }
            Assert.assertFalse("Wrong table selection", ans.next());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }
    @Test
    public void SelectAllConditionalOrderBy() throws SQLException {
        Connection connect = createDatabase("testDB", true);
        Object[][] expected = new Object[][]{{"'Tomato'", 2}, {"'Banana'", 2}};
        try {
            Statement statement = connect.createStatement();
            statement.execute("CREATE   TABLE   ptats ( fruit varchar ,  type int)");
            int count1 = statement.executeUpdate("INSERT INTO ptats VALUES ('Apple', 1)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count1);
            int count2 = statement.executeUpdate("INSERT INTO ptats VALUES ('Tomato', 2)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count2);
            int count3 = statement.executeUpdate("INSERT INTO ptats VALUES ('Banana', 2)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count3);
            int count4 = statement.executeUpdate("INSERT INTO ptats VALUES ('Orange', 1)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count4);
            ResultSet ans = statement.executeQuery("Select * from ptats where type=2 order by fruit desc");
            for(int i = 0; i < expected.length; i++){
                Assert.assertTrue("Wrong table selection", ans.next());
                for(int j = 0; j < expected[0].length; j++){
                    Object actual = ans.getObject(j+1);
                    Assert.assertEquals("Expected Object is wrong found: " + actual + " Expected: "+  expected[i][j], expected[i][j], actual );
                }
            }
            Assert.assertFalse("Wrong table selection", ans.next());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }
    @Test
    public void SelectColumnsOrderBy() throws SQLException {
        Connection connect = createDatabase("testDB", true);
        Object[][] expected = new Object[][]{
                {6, Float.valueOf("6.5353453")},
                {6, Float.valueOf("6.732455646543")},
                {5, Float.valueOf("-7.23132453")},
                {5, Float.valueOf("5.32453")},
        };
        try {
            Statement statement = connect.createStatement();
            statement.execute("CREATE   TABLE   ptats ( integer int ,  float float)");
            int count1 = statement.executeUpdate("INSERT INTO ptats VALUES (5, 5.32453 )");
            Assert.assertNotEquals("Insert returned zero rows", 0, count1);
            int count2 = statement.executeUpdate("INSERT INTO ptats VALUES (6 , 6.732455646543)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count2);
            int count3 = statement.executeUpdate("INSERT INTO ptats VALUES (5, -7.23132453)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count3);
            int count4 = statement.executeUpdate("INSERT INTO ptats VALUES (6, 6.5353453)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count4);
            ResultSet ans = statement.executeQuery("Select integer, float from ptats order by integer desc, float asc");
            for(int i = 0; i < expected.length; i++){
                Assert.assertTrue("Wrong table selection", ans.next());
                for(int j = 0; j < expected[0].length; j++){
                    Object actual = ans.getObject(j+1);
                    Assert.assertEquals("Expected Object is wrong found: " + actual + " Expected: "+  expected[i][j], expected[i][j], actual );
                }
            }
            Assert.assertFalse("Wrong table selection", ans.next());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }
    @Test
    public void SelectColumnsConditionalOrderBy() throws SQLException {
        Connection connect = createDatabase("testDB", true);
        Object[][] expected = new Object[][]{
                {"'Hazem'", new Date(1999-1900, 10, 10)},
                {"'Hazem'", new Date(1999-1900, 10, 11)},
                {"'Hazem'", new Date(2000-1900, 10, 10)},
                {"'Kareem'", new Date(1999-1900, 10, 10)},
        };
        try {
            Statement statement = connect.createStatement();
            statement.execute("CREATE   TABLE   ptats ( name varchar ,  birth date, IQ int)");
            int count1 = statement.executeUpdate("INSERT INTO ptats VALUES ('Hazem', 10/11/1999, -1)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count1);
            int count2 = statement.executeUpdate("INSERT INTO ptats VALUES ('Hazem', 10/11/2000, -2)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count2);
            int count3 = statement.executeUpdate("INSERT INTO ptats VALUES ('Hazem', 11/11/1999, -3)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count3);
            int count4 = statement.executeUpdate("INSERT INTO ptats VALUES ('Kareem', 10/11/1999, -900)");
            Assert.assertNotEquals("Insert returned zero rows", 0, count4);
            ResultSet ans = statement.executeQuery("Select name, birth from ptats where iq<0 order by name asc, birth asc");
            for(int i = 0; i < expected.length; i++){
                Assert.assertTrue("Wrong table selection", ans.next());
                for(int j = 0; j < expected[0].length; j++){
                    Object actual = ans.getObject(j+1);
                    Assert.assertEquals("Expected Object is wrong found: " + actual + " Expected: "+  expected[i][j], expected[i][j], actual );
                }
            }
            Assert.assertFalse("Wrong table selection", ans.next());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
    }

}
