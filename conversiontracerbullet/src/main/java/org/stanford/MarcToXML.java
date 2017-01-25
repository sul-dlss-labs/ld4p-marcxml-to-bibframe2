package org.stanford;

import org.marc4j.*;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Uses the Marc4J library to transform the MARC record to MarcXML.
 * The MARC record must have the authority key delimiter output in the system record dump.
 * When encountering an authority key subfield delimiter ('?' or '=') it will add a subfield 0 to the MarcXML record
 * for each 92X field in order to leverage the functionality of the LOC marc2bibframe converter's ability to create
 * bf:hasAuthority elements for URI's present in that subfield (BF1.0).
 */
class MarcToXML {

    private static Logger log = LogManager.getLogger(MarcToXML.class.getName());

    public static void main (String [] args) throws NullPointerException, MarcException, IOException {
        //log.info("\nCONVERTING MARC TO XML\n%n");

        try {
            String marcfile = args[0];
            while (reader(input(marcfile)).hasNext()) {

                for (Object field : fields(record(reader(input(marcfile))))) {

                    for (Object subfield : subfields(field)) {
                        Subfield sf = (Subfield) subfield;
                        char code = sf.getCode();
                        String codeStr = String.valueOf(code);
                        String data = sf.getData();

                        if (codeStr.equals("=")) {  // authority key (-z flag) from catalogdump

                            try {
                                String key = data.substring(2);
                                String authID = AuthIDfromDB.lookup(key, conn());

                                //TODO consider just getting all the URI's from the authority record here
                                String[] tagNs = {"920", "921", "922"};
                                for (String n : tagNs) {
                                    String uri = AuthURIfromDB.lookup(authID, n, conn());
                                    if (uri.length() > 0) {
                                        dataField(field).addSubfield(factory().newSubfield('0', uri));
                                    }
                                }
                                dataField(field).removeSubfield(sf);
                            } catch (SQLException e) {
                                reportErrors(e);
                            }
                        }
                        if (codeStr.equals("?")) {
                            dataField(field).removeSubfield(sf);
                        }
                    }
                }
                writer().write(record(reader(input(marcfile))));
            }
        } catch (FileNotFoundException e) {
            reportErrors(e);
        }

        //log.info("DONE WITH MARCXML CONVERSION\n");
        writer().close();
    }

    private static MarcFactory factory() {
        return MarcFactory.newInstance();
    }

    private static MarcWriter writer() {
        return new MarcXmlWriter(System.out, true);
    }

    private static InputStream input(String marcfile) throws FileNotFoundException {
        return new FileInputStream(marcfile);
    }

    private static MarcReader reader(InputStream input) {
        return new MarcStreamReader(input);
    }

    private static Connection conn() throws IOException, SQLException {
        return AuthDBConnection.open();
    }

    private static Record record(MarcReader reader) {
        return reader.next();
    }

    private static List fields(Record record) {
        return record.getDataFields();
    }

    private static DataField dataField(Object field) {
        return (DataField) field;
    }

    private static List subFieldList(Object field) {
        return dataField(field).getSubfields();
    }

    @SuppressWarnings("unchecked")
    private static Object[] subfields(Object field) {
        return subFieldList(field).toArray(new Object[subFieldList(field).size()]);
    }

    private static void reportErrors(Exception e) {
        String msg = e.getMessage();
        log.fatal(msg);
        System.err.println(msg);
        e.printStackTrace();
    }
}
