package org.stanford;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Joshua Greben jgreben on 1/10/17.
 * Stanford University Libraries, DLSS
 */
class AuthIDfromDB {

    static String lookup(String key, Connection connection) {

        String result = "";
        String sql;

        try {
            sql = "select authority_id from authority where authority_key = '" + key + "'";
            Statement s;
            ResultSet rs;

            s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = s.executeQuery(sql);

            while (rs.next()) {
                result = rs.getString(1).trim();
            }
            rs.close();
            s.close();
        }
        catch(SQLException e) {
            System.err.println("Lookup AuthID SQLException:" + e.getMessage());
        }

        return result;
    }
}
