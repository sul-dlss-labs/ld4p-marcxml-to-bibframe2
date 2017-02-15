package org.stanford;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

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

/**
 * Uses the Marc4J library to transform the MARC record to MarcXML.
 * The MARC record must have the authority key delimiter output in the system record dump.
 * When encountering an authority key subfield delimiter ('?' or '=') it will add a subfield 0 to the MarcXML record
 * for each 92X field in order to leverage the functionality of the LOC marc2bibframe converter's ability to create
 * bf:hasAuthority elements for URI's present in that subfield (BF1.0).
 */
class MarcToXML {

    public static Connection authDB = null;

    // Apache Commons-CLI Options
    // https://commons.apache.org/proper/commons-cli/introduction.html
    public static CommandLine cmd = null;
    public static Options options = new Options();

    private static void printHelp() {
        if (! cmd.hasOption('h'))
            return;
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(MarcToXML.class.getName(), options);
        System.exit(0);
    }

    private static String marcInputFile = null;

    public static void setMarcInputFile(String file) {
        if (file != null) {
            MarcToXML.marcInputFile = file;
        } else {
            System.err.println("ERROR: No MARC input file specified.");
            printHelp();
        }
    }

    public static Boolean xmlReplace = false;

    public static String xmlOutputPath = null;

    public static void setXmlOutputPath(String path) {
        if (path != null)
            MarcToXML.xmlOutputPath = path;
        else
            MarcToXML.xmlOutputPath = System.getenv("LD4P_MARCXML");
    }

    private static Logger log = null;

    private static String logFileDefault = "log/MarcToXML.log";

    public static void setLogger(String logFile) {
        // See src/main/resources/log4j2.xml for configuration details.
        // This method uses a programmatic approach to add a file logger.
        if (logFile == null)
            logFile = logFileDefault;
        addLogFileAppender(logFile);
        log = LogManager.getLogger();
//        log.trace("Here is some TRACE");
//        log.debug("Here is some DEBUG");
//        log.info("Here is some INFO");
//        log.warn("Here is some WARN");
//        log.error("Here is some ERROR");
//        log.fatal("Here is some FATAL");
    }

    private static void addLogFileAppender(String filename) {
        String loggerName = MarcToXML.class.getName();
        String fileAppenderName = "LOGFile";
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        PatternLayout layout = PatternLayout.newBuilder()
                .withConfiguration(config)
                .withPattern("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n")
                .build();
        FileAppender appender = FileAppender.newBuilder()
                .withFileName(filename)
                .withName(fileAppenderName)
                .withLayout(layout)
                .build();
        appender.start();
        config.addAppender(appender);

        AppenderRef ref = AppenderRef.createAppenderRef(fileAppenderName, null, null);
        AppenderRef[] appenderRefs = new AppenderRef[] {ref};

        Boolean loggerAdd = false;
        Filter loggerFilter = null;
        Level loggerLevel = Level.INFO;
        Property[] loggerProperties = null;
        LoggerConfig loggerConfig = LoggerConfig.createLogger(
                loggerAdd,
                loggerLevel,
                loggerName,
                "true",
                appenderRefs,
                loggerProperties,
                config,
                loggerFilter);

        loggerConfig.addAppender(appender, loggerLevel, loggerFilter);
        config.removeLogger(loggerName);
        config.addLogger(loggerName, loggerConfig);
        context.updateLoggers();
    }

    public static void main (String [] args) throws IOException, ParseException {

        options.addOption("h", "help", false, "help message");
        options.addOption("i", "inputFile", true, "MARC input file (binary .mrc file expected; required)");
        options.addOption("o", "outputPath", true, "MARC XML output path (default: ENV[\"LD4P_MARCXML\"])");
        options.addOption("l", "logFile", true, "Log file output (default: " + logFileDefault + ")");
        options.addOption("r", "replace", false, "Replace existing XML files (default: false)");

        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);
        printHelp();
        setMarcInputFile( cmd.getOptionValue("i") );
        setXmlOutputPath( cmd.getOptionValue("o") );
        // TODO: check what happens when long options are used instead of short options?
        // TODO: might need to check for the presence of each of them to get the value?
        setLogger( cmd.getOptionValue("l") );
        xmlReplace = cmd.hasOption("r");

        FileInputStream marcInputFileStream = new FileInputStream(marcInputFile);
        MarcReader marcReader = new MarcStreamReader(marcInputFileStream);
        while (marcReader.hasNext()) {
            convertMarcRecord(marcReader.next());
        }
    }

    public static void convertMarcRecord (Record record) {
        try {
            String xmlFilePath = xmlOutputFilePath(record);
            File xmlFile = new File(xmlFilePath);
            if (doConversion(xmlFile, xmlReplace)) {
                MarcWriter writer = marcRecordWriter(xmlFilePath);

                marcResolveAuthorities(record);
                writer.write(record);
                writer.close();
                log.info("Output MARC-XML file: " + xmlFilePath);
            } else {
                log.info("Skipped MARC-XML file: " + xmlFilePath);
            }
        }
        catch (IOException | SQLException | NullPointerException | MarcException e) {
            reportErrors(e);
        }
    }

    // TODO: move this method to a subclass of Record
    public static void marcResolveAuthorities(Record record) throws IOException, SQLException {
        List subFieldList;
        DataField dataField;
        MarcFactory factory = MarcFactory.newInstance();

        List fields = record.getDataFields();
        Iterator dataFieldIterator = fields.iterator();

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
                    addAuthURIandRemoveSubfields(data, dataField, sf, factory);
                }
                if (codeStr.equals("?")) {
                    dataField.removeSubfield(sf);
                }
            }
        }
    }

    // TODO: move this method to a subclass of Record
    public static String xmlOutputFilePath(Record record) {
        String cn = record.getControlNumber();
        String outFileName = cn.replaceAll(" ", "_").toLowerCase() + ".xml";
        Path outFilePath = Paths.get(xmlOutputPath, outFileName);
        return outFilePath.toString();
    }

    public static Boolean doConversion (File xmlFile, Boolean xmlReplace) {
        if (!xmlFile.exists() || xmlReplace) {
             return true;
         }
         else {
             return false;
         }
    }

    public static void addAuthURIandRemoveSubfields(String data, DataField dataField,
                                                    Subfield sf, MarcFactory factory) throws IOException, SQLException {
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

    public static void setAuthConnection() throws IOException, SQLException {
        if ( authDB == null )
            authDB = AuthDBConnection.open();
    }

    private static MarcWriter marcRecordWriter(String filePath) throws FileNotFoundException {
        OutputStream outFileStream = new FileOutputStream(filePath);;
        return new MarcXmlWriter(outFileStream, true);
    }

    private static void reportErrors(Exception e) {
        String msg = e.getMessage();
        log.fatal(msg);
        System.err.println(msg);
        //e.printStackTrace();
    }
}
