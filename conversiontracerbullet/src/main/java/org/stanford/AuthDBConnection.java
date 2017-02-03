package org.stanford;

import oracle.jdbc.pool.OracleDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Stanford University Libraries, DLSS
 */
class AuthDBConnection {

    private static Logger log = LogManager.getLogger(AuthDBConnection.class.getName());

    private static OracleDataSource ds = null;
    private static String server = null;
    private static String service = null;
    private static String userName = null;
    private static String userPass = null;

    public static Connection open() throws SQLException, IOException {
        setDataSource();
        return ds.getConnection();
    }

    public static String getServer() {
        return server;
    }

    public static void setServer(String server) {
        AuthDBConnection.server = server;
    }

    public static String getService() {
        return service;
    }

    public static void setService(String service) {
        AuthDBConnection.service = service;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        AuthDBConnection.userName = userName;
    }

    public static String getUserPass() {
        return userPass;
    }

    public static void setUserPass(String userPass) {
        AuthDBConnection.userPass = userPass;
    }

    static void setDataSource() throws SQLException, IOException {
        setDataSourceProperties();
        ds = new OracleDataSource();
        ds.setURL("jdbc:oracle:thin:@" + server + ":1521:" + service);
        ds.setUser(userName);
        ds.setPassword(userPass);
        setDataSourceCache();
    }

    static void setDataSourceProperties() {
        Properties props = getDataSourceProperties();
        if (server == null)
            server = props.getProperty("SERVER");
        if (service == null)
            service = props.getProperty("SERVICE_NAME");
        if (userName == null)
            userName = props.getProperty("USER");
        if (userPass == null)
            userPass = props.getProperty("PASS");
    }

    static Properties getDataSourceProperties() {
        Class cls = AuthDBConnection.class;
        InputStream in = null;
        Properties props = new Properties();
        try {
            URL path;
            path = cls.getResource("/server.conf");
            if (path == null) {
                log.debug("Failed to cls.getResource(\"/server.conf\")");
                log.debug("Trying to cls.getClassLoader().getResource(\"/server.conf\")");
                path = cls.getClassLoader().getResource("/server.conf");
                if (path == null)
                    log.debug("Failed to cls.getClassLoader().getResource(\"/server.conf\")");
            }
            if (path != null)
                log.debug(path.getFile());
            in = cls.getResourceAsStream("/server.conf");
            props.load(in);
            log.debug( props.toString() );
        } catch (IOException e) {
            reportErrors(e);
        } finally {
            try {
                if( in != null )
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return props;
    }

    static void setDataSourceCache() throws SQLException {
        Properties cacheProps = new Properties();
        cacheProps.setProperty("MinLimit", "1");
        cacheProps.setProperty("InitialLimit", "1");
        cacheProps.setProperty("AbandonedConnectionTimeout", "100");
        cacheProps.setProperty("PropertyCheckInterval", "80");
        ds.setConnectionCachingEnabled(false);
        ds.setConnectionCacheName("CACHE");
        ds.setConnectionCacheProperties(cacheProps);
    }

    private static void reportErrors(Exception e) {
        String msg = e.getMessage();
        log.error(msg);
        System.err.println(msg);
        e.printStackTrace();
    }
}
