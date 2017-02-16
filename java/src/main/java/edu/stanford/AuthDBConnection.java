package edu.stanford;

import oracle.jdbc.pool.OracleDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Stanford University Libraries, DLSS
 */
class AuthDBConnection {

    private static OracleDataSource ds = null;

    public static Connection open() throws SQLException, IOException {
        setDataSource();
        return ds.getConnection();
    }

    static void setDataSource() throws SQLException, IOException {
        AuthDBProperties props = new AuthDBProperties();
        ds = new OracleDataSource();
        ds.setURL(props.getURL());
        ds.setUser(props.getUserName());
        ds.setPassword(props.getUserPass());
        setDataSourceCache();
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

}
