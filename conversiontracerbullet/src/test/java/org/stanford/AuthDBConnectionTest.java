package org.stanford;

import oracle.jdbc.pool.OracleDataSource;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class AuthDBConnectionTest {

    /*
    Unit test config:      src/test/resources/server.conf
    Packaged code config:  src/main/resources/server.conf
     */

    @Test
    public void testServer() throws Exception {
        // Same code is used to test setter/getter
        String server = "test.server.org";
        AuthDBConnection.setServer(server);
        assertEquals(server, AuthDBConnection.getServer());
    }

    @Test
    public void testService() throws Exception {
        // Same code is used to test setter/getter
        String service = "service_name";
        AuthDBConnection.setService(service);
        assertEquals(service, AuthDBConnection.getService());
    }

    @Test
    public void testUserName() throws Exception {
        // Same code is used to test setter/getter
        String userName = "user_name";
        AuthDBConnection.setUserName(userName);
        assertEquals(userName, AuthDBConnection.getUserName());
    }

    @Test
    public void testUserPass() throws Exception {
        // Same code is used to test setter/getter
        String userPass = "user_pass";
        AuthDBConnection.setUserPass(userPass);
        assertEquals(userPass, AuthDBConnection.getUserPass());
    }

    @Test
    public void setDataSource() throws Exception {
        // Note about Field.get(Object obj):
        // If the underlying field is a static field, the obj argument is ignored; it may be null.
        Field field = AuthDBConnection.class.getDeclaredField("ds");
        field.setAccessible(true);
        assertNull(field.get(null));
        AuthDBConnection.setDataSource();
        assertNotNull(field.get(null));
        assertThat(field.get(null), instanceOf(OracleDataSource.class));
    }

    @Test
    public void setDataSourceCache() throws Exception {

    }

    @Test
    public void getDataSourceProperties() throws Exception {
        Properties serverConf = AuthDBConnection.getDataSourceProperties();
        assertNotNull(serverConf.getProperty("USER"));
        assertNotNull(serverConf.getProperty("PASS"));
        assertNotNull(serverConf.getProperty("SERVER"));
        assertNotNull(serverConf.getProperty("SERVICE_NAME"));
        assertNull(serverConf.getProperty("MISSING_PROPERTY"));
    }

    @Test
    public void setDataSourceProperties() throws Exception {
        Properties serverConf = AuthDBConnection.getDataSourceProperties();
        AuthDBConnection.setDataSourceProperties();
        assertEquals(serverConf.getProperty("USER"), AuthDBConnection.getUserName());
        assertEquals(serverConf.getProperty("PASS"), AuthDBConnection.getUserPass());
        assertEquals(serverConf.getProperty("SERVER"), AuthDBConnection.getServer());
        assertEquals(serverConf.getProperty("SERVICE_NAME"), AuthDBConnection.getService());
    }

    @Test
    public void resetDataSourceProperties() throws Exception {
        // test resetting any of the data source properties
        Properties serverConf = AuthDBConnection.getDataSourceProperties();
        AuthDBConnection.setDataSourceProperties();
        assertEquals(serverConf.getProperty("USER"), AuthDBConnection.getUserName());
        String userName = "another_user";
        AuthDBConnection.setUserName(userName);
        assertEquals(userName, AuthDBConnection.getUserName());
    }
}

