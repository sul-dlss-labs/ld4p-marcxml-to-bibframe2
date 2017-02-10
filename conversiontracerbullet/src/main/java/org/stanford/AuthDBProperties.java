package org.stanford;

import oracle.jdbc.pool.OracleDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Stanford University Libraries, DLSS
 */
class AuthDBProperties {

    private static Logger log = LogManager.getLogger(AuthDBProperties.class.getName());

    private static final String DEFAULT_CONFIG_FILE = "/server.conf";

    private String server = null;
    private String service = null;
    private String userName = null;
    private String userPass = null;
    private String propertyFile = null;

    public AuthDBProperties() throws IOException {
        // initialize using the default property file resource
        this.propertyFile = propertyFileResource();
        initDataSourceProperties();
    }

    public AuthDBProperties(String propertyFile) throws IOException {
        this.propertyFile = propertyFile;
        initDataSourceProperties();
    }

    public String getURL() {
        return "jdbc:oracle:thin:@" + this.server + ":1521:" + this.service;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    private void initDataSourceProperties() throws IOException {
        try {
            Properties props = loadDataSourceProperties(propertyFile);
            this.server = props.getProperty("SERVER");
            this.service = props.getProperty("SERVICE_NAME");
            this.userName = props.getProperty("USER");
            this.userPass = props.getProperty("PASS");
        } catch (IOException e) {
            log.fatal("Failed to initialize AuthDBProperties", e);
            throw e;
        }
    }

    private static Properties loadDataSourceProperties(String propertyFile) throws IOException {
        FileInputStream iStream = new FileInputStream(propertyFile);
        Properties props = new Properties();
        props.load(iStream);
        iStream.close();
        log.debug( props.toString() );
        return props;
    }

    private static String propertyFileResource() {
        Class cls = AuthDBProperties.class;
        URL path = cls.getResource(DEFAULT_CONFIG_FILE);
        if (path == null)
            path = cls.getClassLoader().getResource(DEFAULT_CONFIG_FILE);
        if (path == null)
            log.fatal("Failed to find default server.conf file resource");
        return path.getFile();
    }

}
