[![Build Status](https://travis-ci.org/sul-dlss/ld4p-marc21-to-xml.svg?branch=master)](https://travis-ci.org/sul-dlss/ld4p-marc21-to-xml)
[![Coverage Status](https://coveralls.io/repos/github/sul-dlss/ld4p-marc21-to-xml/badge.svg?branch=master)](https://coveralls.io/github/sul-dlss/ld4p-marc21-to-xml?branch=master)

# ld4p-marc21-to-xml
convert marc21 data into marcxml, with authority ids resolved to URIs via Symphony

## Development

### Dependencies

- Java 8
- Maven 3

### Compiling and Executing

See Dependencies below for one time setup.

To build and package the maven project (assuming maven is installed already):
```
mvn package
```

The resulting packaged JAR includes all dependencies.  The
packaged JAR can be copied to a convenient location and used on the CLASSPATH or the command line, e.g.
```
$ cp java/target/xform-marc21-to-xml-jar-with-dependencies.jar ~/lib/ld4p_conversion.jar
$ LD4P_JAR=~/lib/ld4p_conversion.jar
$ java -cp ${LD4P_JAR} edu.stanford.MarcToXML -h
usage: edu.stanford.MarcToXML
 -h,--help               help message
 -i,--inputFile <arg>    MARC input file (binary .mrc file expected;
                         required)
 -l,--logFile <arg>      Log file output (default: log/MarcToXML.log)
 -o,--outputPath <arg>   MARC XML output path (default:
                         ENV["LD4P_MARCXML"])
 -r,--replace            Replace existing XML files (default: false)
```

### Code Coverage Reports

To run the tests and view a coverage report from the command line:
```
mvn clean cobertura:cobertura
ls -l target/site/cobertura/
firefox target/site/cobertura/index.html
```

The [Travis CI](https://travis-ci.org/sul-dlss/ld4p-marc21-to-xml) builds run tests and submit
a coverage report to [Coveralls](https://coveralls.io/github/sul-dlss/ld4p-marc21-to-xml).
To update Coveralls from the command line, try:
```
mvn clean test cobertura:cobertura coveralls:report -DrepoToken=yourcoverallsprojectrepositorytoken
```
