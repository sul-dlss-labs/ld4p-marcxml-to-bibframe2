package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Joshua Greben jgreben on 1/10/17.
 * Stanford University Libraries, DLSS
 */
class AuthURIfromDB {

    static String lookup(String authID, String tagNum, Connection connection) {
        String result = "";

        String sql = "SELECT AUTHORVED.tag FROM AUTHORVED LEFT JOIN AUTHORITY ON AUTHORVED.offset = AUTHORITY.ved_offset" +
                " where AUTHORITY.authority_id='" + authID + "' and AUTHORVED.tag_number='" + tagNum + "'";
        try {
            Statement s;
            ResultSet rs;

            s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = s.executeQuery(sql);

            while (rs.next()) {
                result = rs.getString(1);
            }
            rs.close();
            s.close();
        }
        catch(SQLException e) {
            System.err.println("Lookup URI SQLException:" + e.getMessage());
        }

        return result;
    }
}
