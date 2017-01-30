package org.stanford;

import org.junit.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.marc4j.*;
import org.marc4j.marc.Record;

/**
 *
 */
public class MarcToXMLTest {

    // For additional test data, consider the marc4j data at
    // https://github.com/marc4j/marc4j/tree/master/test/resources

//    private String marcFileResource = "/sample_marc.mrc";
    private String marcFileResource = "/one_record.mrc";
    private MarcStreamReader marcReader = null;
    private Record marcRecord = null;
    private String xmlOutputDir = null;

    @Before
    public void setUp() throws IOException {
        String marcFilePath = getClass().getResource(marcFileResource).getFile();
        marcReader = new MarcStreamReader(new FileInputStream(marcFilePath));
        assertTrue(marcReader.hasNext());
        xmlOutputDir = Files.createTempDirectory("MarcToXMLTest").toString();
        MarcToXML.setXmlOutputPath(xmlOutputDir);
    }

    @After
    public void tearDown() throws IOException {
//        FileUtils.deleteDirectory(xmlOutputDir);
    }

    @Ignore("This does not yet test anything in MarcToXML")
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
        String outputFile = MarcToXML.marcRecordFileName(marcRecord);
        File file = new File(outputFile);
        assertTrue(file.exists());

        // TODO: read in the one_record.xml file
        // TODO: use XMLUnit to check the output file has the same content
        // TODO: see http://www.xmlunit.org/
    }

    @Test
    public void marcRecordFileNameTest() {
        marcRecord = marcReader.next();
        String result = MarcToXML.marcRecordFileName(marcRecord);
        assertTrue(result.contains(xmlOutputDir));
        String cn = marcRecord.getControlNumber();
        assertTrue(result.contains(cn));
        String fmt = ".xml";
        assertTrue(result.contains(fmt));
    }

//    public void debugInspections() {
//        marcRecord = marcReader.next();
//        List cFields = marcRecord.getControlFields();
//        List dFields = marcRecord.getDataFields();
//    }
}