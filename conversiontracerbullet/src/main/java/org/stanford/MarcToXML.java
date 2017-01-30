package org.stanford;

import org.marc4j.*;
import org.marc4j.marc.DataField;
import org.marc4j.marc.MarcFactory;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    private static String marcInputFile = null;
    private static FileInputStream marcInputFileStream = null;
    private static MarcReader marcReader = null;

    private static String xmlOutputPath = null;

    public static String getXmlOutputPath() {
        return xmlOutputPath;
    }

    public static void setXmlOutputPath(String path) {
        if (path != null)
            MarcToXML.xmlOutputPath = path;
        else
            MarcToXML.xmlOutputPath = System.getenv("LD4P_MARCXML");
    }

    public static void main (String [] args) throws IOException {

        marcInputFile = args[0];
        setXmlOutputPath(args[1]);

        marcInputFileStream = new FileInputStream(marcInputFile);
        marcReader = new MarcStreamReader(marcInputFileStream);
        while (marcReader.hasNext()) {
            convertMarcRecord(marcReader.next());

            // TODO: remove this break after testing
            break; // testing processing for one record
        }
    }

    public static void convertMarcRecord (Record record) {

        List fields;
        List subFieldList;
        Iterator dataFieldIterator;
        DataField dataField;
        MarcFactory factory = MarcFactory.newInstance();

        try {
            // Ensure the record can be written to an output file
            // before doing all the work.
            MarcWriter writer = marcRecordWriter(record);

            fields = record.getDataFields();
            dataFieldIterator = fields.iterator();

            while (dataFieldIterator.hasNext()) {
                dataField = (DataField) dataFieldIterator.next();

                subFieldList = dataField.getSubfields();
                Object [] subFields = subFieldList.toArray(new Object[subFieldList.size()]);

                for (int s = 0; s < subFields.length; s++) {
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
            writer.close();
        }
        catch (IOException | SQLException | NullPointerException | MarcException e) {
            reportErrors(e);
        }
    }

    public static String marcRecordFileName(Record record) {
        String cn = record.getControlNumber();
        String outFileName = cn.replaceAll(" ", "_").toLowerCase() + ".xml";
        Path outFilePath = Paths.get(xmlOutputPath, outFileName);
        return outFilePath.toString();
    }

    private static MarcWriter marcRecordWriter(Record record) throws FileNotFoundException {
        OutputStream outFileStream = new FileOutputStream(marcRecordFileName(record));
        return new MarcXmlWriter(outFileStream, true);
    }

    private static void setAuthConnection() throws IOException, SQLException {
        if ( authDB == null )
            authDB = AuthDBConnection.open();
    }

    private static void reportErrors(Exception e) {
        String msg = e.getMessage();
        log.fatal(msg);
        System.err.println(msg);
        e.printStackTrace();
    }
}
