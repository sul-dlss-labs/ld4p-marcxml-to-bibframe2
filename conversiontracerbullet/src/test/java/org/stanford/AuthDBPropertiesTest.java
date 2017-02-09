package org.stanford;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Properties;

import static java.nio.file.Files.createTempDirectory;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class AuthDBPropertiesTest {

    /*
    Unit test config:      src/test/resources/server.conf
    Packaged code config:  src/main/resources/server.conf
     */

    private AuthDBProperties authProps;
    private Properties serverConf;
    private String propertyFile;
    private File serverConfFile;
    private File tmpDir;

    @Before
    public void setUp() throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        authProps = new AuthDBProperties();
        // Use private method to get the server.conf file path
        Field f = AuthDBProperties.class.getDeclaredField("propertyFile");
        f.setAccessible(true);
        propertyFile = (String) f.get(authProps);
        serverConfFile = new File(propertyFile);
        // Use private method to get the server.conf properties
        Method m = AuthDBProperties.class.getDeclaredMethod("loadDataSourceProperties", String.class);
        m.setAccessible(true);
        serverConf = (Properties) m.invoke(authProps, propertyFile);
        tmpDir = createTempDirectory("AuthDBPropertiesTest_").toFile();
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(tmpDir);
    }

    @Test
    public void testConstructor() {
        assertEquals(serverConf.getProperty("USER"), authProps.getUserName());
        assertEquals(serverConf.getProperty("PASS"), authProps.getUserPass());
        assertEquals(serverConf.getProperty("SERVER"), authProps.getServer());
        assertEquals(serverConf.getProperty("SERVICE_NAME"), authProps.getService());
    }

    @Test
    public void testConstructorWithString() throws IOException {
        FileUtils.copyFileToDirectory(serverConfFile, tmpDir);
        String customConfigFile = Paths.get(tmpDir.toString(), serverConfFile.getName()).toString();
        AuthDBProperties customProps = new AuthDBProperties(customConfigFile);
        assertEquals(serverConf.getProperty("USER"), customProps.getUserName());
        assertEquals(serverConf.getProperty("PASS"), customProps.getUserPass());
        assertEquals(serverConf.getProperty("SERVER"), customProps.getServer());
        assertEquals(serverConf.getProperty("SERVICE_NAME"), customProps.getService());
    }

    @Test
    public void testLoadDataSourceProperties() {
        assertNotNull(serverConf.getProperty("USER"));
        assertNotNull(serverConf.getProperty("PASS"));
        assertNotNull(serverConf.getProperty("SERVER"));
        assertNotNull(serverConf.getProperty("SERVICE_NAME"));
        assertNull(serverConf.getProperty("MISSING_PROPERTY"));
    }

    @Test
    public void testServer() throws Exception {
        // Same code is used to test setter/getter
        String server = "test.server.org";
        authProps.setServer(server);
        assertEquals(server, authProps.getServer());
    }

    @Test
    public void testService() throws Exception {
        // Same code is used to test setter/getter
        String service = "service_name";
        authProps.setService(service);
        assertEquals(service, authProps.getService());
    }

    @Test
    public void testUserName() throws Exception {
        // Same code is used to test setter/getter
        String userName = "user_name";
        authProps.setUserName(userName);
        assertEquals(userName, authProps.getUserName());
    }

    @Test
    public void testUserPass() throws Exception {
        // Same code is used to test setter/getter
        String userPass = "user_pass";
        authProps.setUserPass(userPass);
        assertEquals(userPass, authProps.getUserPass());
    }

    @Test
    public void testURL() throws Exception {
        String url = authProps.getURL();
        assertNotNull(url);
        assertThat(url, containsString("jdbc:oracle:thin:@"));
        assertThat(url, containsString(":1521:"));
        assertThat(url, containsString(authProps.getServer()));
        assertThat(url, containsString(authProps.getService()));
    }

    @Test (expected = FileNotFoundException.class)
    public void testFailureLoadingProperties() throws IOException {
        String customConfigFile = Paths.get(tmpDir.toString(), "missing.properties").toString();
        new AuthDBProperties(customConfigFile);
    }
}

