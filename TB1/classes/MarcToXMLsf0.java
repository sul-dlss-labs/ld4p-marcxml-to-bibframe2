import java.io.*;
import java.util.List;
import java.util.Iterator;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Subfield;
import org.marc4j.MarcException;
import org.marc4j.marc.MarcFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Properties;

import java.sql.*;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.pool.OracleConnectionCacheManager;

public class MarcToXMLsf0 {

  public static void Main (String args[]) {

    System.err.println("\nCONVERTING MARC TO XML\n");

    MarcWriter writer = new MarcXmlWriter(System.out, true);
    MarcFactory factory = MarcFactory.newInstance();

    Properties props = PropGet.getProps("server.conf");
    Connection conn  = null;

    try
    {
      InputStream input = new FileInputStream(args[0]);
      MarcReader reader = new MarcStreamReader(input);

      while (reader.hasNext()) {
        Record record = reader.next();
        
        List fields = record.getDataFields();
        Iterator dataFieldIterator = fields.iterator();

        while (dataFieldIterator.hasNext()) {
          DataField dataField = (DataField) dataFieldIterator.next();

          List subFieldList = dataField.getSubfields();
          Object [] subfields = subFieldList.toArray(new Object[subFieldList.size()]);

          for (int s = 0; s < subfields.length; s++) {
            Subfield sf = (Subfield) subfields[s];
            char code = sf.getCode();
            String codeStr = String.valueOf(code);
            String data = sf.getData();

            if (codeStr.equals("=")) {
              conn = OpenAuthDBConnection(props);

              String key = data.substring(2);
              String authID = LookupAuthIDfromDB(key, conn);

              String uri = "";
              String [] tagNs = {"920","921","922"};
              for (String n : tagNs) {
                uri = LookupAuthURIfromDB(authID, n, conn);
                if (uri.length() > 0) {
                  dataField.addSubfield(factory.newSubfield('0',uri));
                }
              }
              dataField.removeSubfield(sf);
            }
            if (codeStr.equals("?")) {
              dataField.removeSubfield(sf);
            }

          }
        }

        writer.write(record);
      }
    }
    catch (NullPointerException e) {
      System.err.println(e.getMessage());
    }
    catch (MarcException e) {
      System.err.println(e.getMessage());
    }
    catch (FileNotFoundException e) {
      System.err.println(e.getMessage());
    }

    writer.close();
    System.err.println("DONE WITH MARCXML CONVERSION\n");
  }

  public static String LookupAuthIDfromDB(String key, Connection connection) {

    String result = "";
    String sql = "";

    try
    {
      sql = "select authority_id from authority where authority_key = '" + key + "'";
      Statement s = null;
      ResultSet rs = null;

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

  public static String LookupAuthURIfromDB (String authID, String tagNum, Connection connection) {
    String result = "";

    String sql = "SELECT AUTHORVED.tag FROM AUTHORVED LEFT JOIN AUTHORITY ON AUTHORVED.offset = AUTHORITY.ved_offset" +
      " where AUTHORITY.authority_id='" + authID + "' and AUTHORVED.tag_number='" + tagNum + "'";
    try
    {
      Statement s = null;
      ResultSet rs = null;

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

  public static Connection OpenAuthDBConnection(Properties props) {

    OracleDataSource ods = null;
    String url = "";

    String USER = props.getProperty("USER");
    String PASS = props.getProperty("PASS");
    String SERVER = props.getProperty("SERVER");
    String SERVICE_NAME = props.getProperty("SERVICE_NAME");

    Connection connection = null;

    try
    {
      url = "jdbc:oracle:thin:@" + SERVER + ":1521:" + SERVICE_NAME;

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
};
