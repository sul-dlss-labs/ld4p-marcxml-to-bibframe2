package edu.stanford;

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
        // TODO
    }

}

