package org.stanford;

import org.apache.commons.io.FileUtils;
import org.junit.*;

import static java.nio.file.Files.createTempDirectory;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.marc4j.*;
import org.marc4j.marc.Record;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

/**
 *
 */
public class MarcToXMLTest {

    private static String logFile;
    private static Path outputPath;

    // For additional test data, consider the marc4j data at
    // https://github.com/marc4j/marc4j/tree/master/test/resources
    //private String marcFileResource = "/sample_marc.mrc";
    private final String marcFileResource = "/one_record.mrc";
    // MARC21 XML schema
    // https://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd
    private final String marcSchemaResource = "/MARC21slim.xsd";

    private MarcStreamReader marcReader = null;
    private Record marcRecord = null;

    @Before
    public void setUp() throws IOException {
        outputPath = createTempDirectory("MarcToXMLTest_");
        logFile = File.createTempFile("MarcToXMLTest_", ".log", outputPath.toFile()).toString();
        MarcToXML.setLogger(logFile);
        MarcToXML.setXmlOutputPath(outputPath.toString());
        // Read a MARC binary file resource
        String marcFilePath = getClass().getResource(marcFileResource).getFile();
        marcReader = new MarcStreamReader(new FileInputStream(marcFilePath));
        assertTrue(marcReader.hasNext());
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(outputPath.toFile());
    }

    @Ignore("This does not yet test anything in MarcToXML\n")
    @Test
    public void main() throws Exception {
        // TODO: this can simply confirm that a MARC file is read
        // TODO: and that the main method calls the .convertRecord method N-times.
    }

    @Test
    public void convertRecordTest() throws FileNotFoundException {
        assertTrue(marcReader.hasNext());
        marcRecord = marcReader.next();
        MarcToXML.convertMarcRecord(marcRecord);
        String marcXmlFilePath = MarcToXML.xmlOutputFilePath(marcRecord);
        File file = new File(marcXmlFilePath);
        assertTrue(file.exists());
        assertTrue(marcXmlValid(marcXmlFilePath));
    }

    // TODO: read in the one_record.xml file
    // TODO: use XMLUnit to check the output file has the same content
    // TODO: see http://www.xmlunit.org/

    // TODO: use a test MARC record that requires AuthDB access to resolve URIs?

    @Test
    public void marcRecordFileNameTest() {
        marcRecord = marcReader.next();
        String result = MarcToXML.xmlOutputFilePath(marcRecord);
        assertTrue(result.contains(outputPath.toString()));
        String cn = marcRecord.getControlNumber();
        assertTrue(result.contains(cn));
        String fmt = ".xml";
        assertTrue(result.contains(fmt));
    }

    private boolean marcXmlValid(String marcXmlFilePath) {
        try {
            File xmlFile = new File(marcXmlFilePath);
            Source xmlSource = new StreamSource(xmlFile);
            Validator validator = marcXmlValidator();
            validator.validate(xmlSource);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Validator marcXmlValidator = null;

    private Validator marcXmlValidator() throws SAXException {
        if (marcXmlValidator == null) {
            String marcSchemaFilePath = getClass().getResource(marcSchemaResource).getFile();
            File schemaFile = new File(marcSchemaFilePath);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(schemaFile);
            marcXmlValidator = schema.newValidator();
        }
        return marcXmlValidator;
    }

    private void debugInspections() {
        marcRecord = marcReader.next();
        List cFields = marcRecord.getControlFields();
        List dFields = marcRecord.getDataFields();
        System.err.println(cFields.toString());
        System.err.println(dFields.toString());
    }

}