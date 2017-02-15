package org.stanford;

import org.apache.commons.io.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
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
    private String serverConfResourceName = "/server.conf";
    private File serverConfFile;
    private File tmpDir;

    @Before
    public void setUp() throws IOException {
        tmpDir = createTempDirectory("AuthDBPropertiesTest_").toFile();
        authProps = new AuthDBProperties();
    }

    public void setServerConfFile() {
        Class cls = AuthDBProperties.class;
        URL path = cls.getResource(serverConfResourceName);
        serverConfFile = new File(path.getFile());
    }

    public void setServerConf() throws IOException {
        Class cls = AuthDBProperties.class;
        InputStream iStream = cls.getResourceAsStream(serverConfResourceName);
        serverConf = new Properties();
        serverConf.load(iStream);
        iStream.close();
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(tmpDir);
    }

    @Test
    public void testConstructor() throws IOException {
        setServerConf();
        assertEquals(serverConf.getProperty("USER"), authProps.getUserName());
        assertEquals(serverConf.getProperty("PASS"), authProps.getUserPass());
        assertEquals(serverConf.getProperty("SERVER"), authProps.getServer());
        assertEquals(serverConf.getProperty("SERVICE_NAME"), authProps.getService());
    }

    @Test
    public void testConstructorWithString() throws IOException {
        setServerConf();
        setServerConfFile();
        FileUtils.copyFileToDirectory(serverConfFile, tmpDir);
        String customConfigFile = Paths.get(tmpDir.toString(), serverConfFile.getName()).toString();
        AuthDBProperties customProps = new AuthDBProperties(customConfigFile);
        assertEquals(serverConf.getProperty("USER"), customProps.getUserName());
        assertEquals(serverConf.getProperty("PASS"), customProps.getUserPass());
        assertEquals(serverConf.getProperty("SERVER"), customProps.getServer());
        assertEquals(serverConf.getProperty("SERVICE_NAME"), customProps.getService());
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

