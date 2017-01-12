package main;

import oracle.jdbc.pool.OracleDataSource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 *  Created by Joshua Greben jgreben on 1/10/17.
 *  Stanford University Libraries, DLSS
 */
class AuthDBConnection {

    static Connection open() throws IOException {

        Properties props = PropGet.getProps("main/resources/server.conf");
        OracleDataSource ods;

        String USER = props.getProperty("USER");
        String PASS = props.getProperty("PASS");
        String SERVER = props.getProperty("SERVER");
        String SERVICE_NAME = props.getProperty("SERVICE_NAME");

        Connection connection = null;

        try {
            String url = "jdbc:oracle:thin:@" + SERVER + ":1521:" + SERVICE_NAME;

            ods = new OracleDataSource();
            ods.setURL(url);
            ods.setUser(USER);
            ods.setPassword(PASS);
            ods.setConnectionCachingEnabled(false);
            ods.setConnectionCacheName("CACHE");

            Properties cacheProps = new Properties();
            cacheProps.setProperty("MinLimit", "1");
            cacheProps.setProperty("InitialLimit", "1");
            cacheProps.setProperty("AbandonedConnectionTimeout", "100");
            cacheProps.setProperty("PropertyCheckInterval", "80");

            ods.setConnectionCacheProperties(cacheProps);
            connection = ods.getConnection();
        }
        catch(SQLException e) {
            System.err.println("SQLException:" + e.getMessage());
        }

        return connection;
    }
}
