package org.stanford;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.junit.*;

import static java.nio.file.Files.createTempDirectory;
import static org.junit.Assert.*;
import static org.stanford.MarcToXML.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.junit.runner.RunWith;
import org.marc4j.*;
import org.marc4j.marc.Record;
import org.mockito.*;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import static org.junit.Assume.*;

/**
 *
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.management.*", "oracle.xdb.XMLType"})
@PrepareForTest({ MarcToXML.class, AuthDBConnection.class })
public class MarcToXMLTest {

    private static String logFile;
    private static Path outputPath;
    private static Options options = MarcToXML.options;

    // For additional test data, consider the marc4j data at
    // https://github.com/marc4j/marc4j/tree/master/test/resources
    private final String marcFileResource = "/one_record.mrc";
    private final String marcFilePath = getClass().getResource(marcFileResource).getFile();
    // MARC21 XML schema
    // https://www.loc.gov/standards/marcxml/schema/MARC21slim.xsd
    private final String marcSchemaResource = "/MARC21slim.xsd";

    private MarcStreamReader marcReader = null;
    private Record marcRecord = null;

    @Before
    public void setUp() throws IOException {
        outputPath = createTempDirectory("MarcToXMLTest_");
        logFile = File.createTempFile("MarcToXMLTest_", ".log", outputPath.toFile()).toString();
        setLogger(logFile);
        setXmlOutputPath(outputPath.toString());
        // Read a MARC binary file resource
        setMarcInputFile(marcFilePath);
        marcReader = new MarcStreamReader(new FileInputStream(marcFilePath));
        assertTrue(marcReader.hasNext());
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.deleteDirectory(outputPath.toFile());
    }

    @Test
    public void main() throws Exception {
        // TODO: this can simply confirm that a MARC file is read
        // TODO: and that the main method calls the .convertRecord method N-times.
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("LD4P_MARCXML")).thenReturn(null);
        String [] args = new String[] {"-i" + marcFilePath};
        try {
            MarcToXML.main(args);
        } catch (FileNotFoundException expected) {
            assertEquals(FileNotFoundException.class, expected.getClass());
        } catch (NullPointerException expected) {
            assertEquals(NullPointerException.class, expected.getClass());
        }
        assertNotNull(options.getMatchingOptions("h"));
    }

    @Test
    public void convertRecordTest() throws IOException, ParseException {
        outputPath = createTempDirectory("MarcToXMLTest_");
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("LD4P_MARCXML")).thenReturn(outputPath.toString());
        assertTrue(marcReader.hasNext());
        marcRecord = marcReader.next();
        String marcXmlFilePath = xmlOutputFilePath(marcRecord);
        convertMarcRecord(marcRecord);
        File file = new File(marcXmlFilePath);
        assertTrue(file.exists());
        assertTrue(marcXmlValid(marcXmlFilePath));
        // Reconvert and replace
        convertMarcRecord(marcRecord);
        CommandLineParser parser = new DefaultParser();
        MarcToXML.setOptions();
        String [] args = new String[] {"-r"};
        CommandLine cmd = parser.parse(options, args);
        Boolean xmlReplace = cmd.hasOption("r");
        Boolean doConversion = doConversion(file, xmlReplace);
        assertTrue(doConversion);
    }

    // TODO: read in the one_record.xml file
    // TODO: use XMLUnit to check the output file has the same content
    // TODO: see http://www.xmlunit.org/
    // TODO: use a test MARC record that requires AuthDB access to resolve URIs?

    @Test
    public void inputFileNameTest() {
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        String noFilePath = null;
        try {
            setMarcInputFile(noFilePath);
        } catch (Throwable expected) {
            assertEquals(NullPointerException.class, expected.getClass());
        }
        String errMsg = "ERROR: No MARC input file specified.\n";
        assertEquals(errMsg, errContent.toString());
    }

    @Test
    public void outputFileNameTest() {
        PowerMockito.mockStatic(System.class);
        PowerMockito.when(System.getenv("LD4P_MARCXML")).thenReturn(outputPath.toString());
        String noFilePath = null;
        setXmlOutputPath(noFilePath);
        assertEquals(xmlOutputPath, System.getenv("LD4P_MARCXML"));

        marcRecord = marcReader.next();
        String result = xmlOutputFilePath(marcRecord);
        assertTrue(result.contains(outputPath.toString()));
        String cn = marcRecord.getControlNumber();
        assertTrue(result.contains(cn));
        String fmt = ".xml";
        assertTrue(result.contains(fmt));
    }

    @Test
    public void setAuthConnectionTest() {
        try {
            assertTrue(MarcToXML.authDB == null);
            MarcToXML.setAuthConnection();
        } catch (Throwable expected) {
            assertNotEquals(IOException.class, expected.getClass());
            assertNotEquals(SQLException.class, expected.getClass());
        }

        try {
            MarcToXML.authDB = Mockito.mock(Connection.class);
            MarcToXML.setAuthConnection();
            assertFalse(MarcToXML.authDB == null);
        } catch (Throwable expected) {
            assertNotEquals(IOException.class, expected.getClass());
            assertNotEquals(SQLException.class, expected.getClass());
        }
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