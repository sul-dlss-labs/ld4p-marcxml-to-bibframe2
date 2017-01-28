package org.stanford;

import org.marc4j.*;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
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

    private static Connection authDB = null;

    public static void main (String [] args) throws NullPointerException, MarcException, IOException, SQLException {

        MarcWriter writer = new MarcXmlWriter(System.out, true);
        MarcFactory factory = MarcFactory.newInstance();

        try {
            InputStream input = new FileInputStream(args[0]);
            MarcReader reader = new MarcStreamReader(input);
            Record record;
            List fields;
            List subFieldList;
            Iterator dataFieldIterator;
            DataField dataField;

            while (reader.hasNext()) {
                record = reader.next();

                fields = record.getDataFields();
                dataFieldIterator = fields.iterator();

                while (dataFieldIterator.hasNext()) {
                    // TODO: does this need to be cast?
                    dataField = (DataField) dataFieldIterator.next();

                    subFieldList = dataField.getSubfields();
                    Object [] subFields = subFieldList.toArray(new Object[subFieldList.size()]);

                    for (int s = 0; s < subFields.length; s++) {
                        // TODO: does this need to be cast?
                        Subfield sf = (Subfield) subFields[s];
                        char code = sf.getCode();
                        String codeStr = String.valueOf(code);
                        String data = sf.getData();

                        if (codeStr.equals("=")) {
                            setAuthConnection();

                            String key = data.substring(2);
                            String authID = AuthIDfromDB.lookup(key, authDB);

                            //TODO consider just getting all the URI's from the authority record here
                            String[] tagNs = {"920", "921", "922"};
                            for (String n : tagNs) {
                                String uri = AuthURIfromDB.lookup(authID, n, authDB);
                                if (uri.length() > 0)
                                    dataField.addSubfield(factory.newSubfield('0', uri));
                            }
                            dataField.removeSubfield(sf);
                        }
                        if (codeStr.equals("?")) {
                            dataField.removeSubfield(sf);
                        }
                    }
                }
                writer.write(record);
                System.out.flush();

                break; // testing processing for one record

            }
        }
        catch (NullPointerException | MarcException | FileNotFoundException e) {
            reportErrors(e);
        }
        writer.close();
    }

//    private static MarcFactory factory() {
//        return MarcFactory.newInstance();
//    }
//
//    private static MarcWriter writer() {
//        return new MarcXmlWriter(System.out, true);
//    }
//
//    private static InputStream input(String marcfile) throws FileNotFoundException {
//        return new FileInputStream(marcfile);
//    }
//
//    private static MarcReader reader(InputStream input) {
//        return new MarcStreamReader(input);
//    }

    private static void setAuthConnection() throws IOException, SQLException {
        if ( authDB == null )
            authDB = AuthDBConnection.open();
    }

//    private static Record record(MarcReader reader) {
//        return reader.next();
//    }
//
//    private static List fields(Record record) {
//        return record.getDataFields();
//    }

    private static DataField dataField(Object field) {
        return (DataField) field;
    }

//    private static List subFieldList(Object field) {
//        return dataField(field).getSubfields();
//    }

//    @SuppressWarnings("unchecked")
//    private static Object[] subfields(Object field) {
//        return subFieldList(field).toArray(new Object[subFieldList(field).size()]);
//    }

    private static void reportErrors(Exception e) {
        String msg = e.getMessage();
        log.fatal(msg);
        System.err.println(msg);
        e.printStackTrace();
    }
}
