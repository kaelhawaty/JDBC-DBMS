package eg.edu.alexu.csd.oop.db.cs2.test;
import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import eg.edu.alexu.csd.oop.TestRunner;


public class IntegrationTest {
    public static Class<?> getSpecifications(){
        return Driver.class;
    }

    @Test
    public void test() {
        Assert.assertNotNull("Failed to create Driver implemenation",  (Driver)TestRunner.getImplementationInstanceForInterface(Driver.class));
    }

}