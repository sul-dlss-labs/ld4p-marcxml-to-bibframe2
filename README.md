[![Build Status](https://travis-ci.org/sul-dlss/ld4p-tracer-bullets.svg?branch=develop)](https://travis-ci.org/sul-dlss/ld4p-tracer-bullets)
[![Coverage Status](https://coveralls.io/repos/github/sul-dlss/ld4p-tracer-bullets/badge.svg?branch=develop)](https://coveralls.io/github/sul-dlss/ld4p-tracer-bullets?branch=develop)
#LD4P Tracer Bullets

## Background
https://consul.stanford.edu/display/LD4P/Linked+Data+for+Production

Code repository to create software that will reify the goals of the initial LD4P Tracer Bullet pathways:

###Pathway 1:
- Completely map and evaluate Stanford’s vendor-supplied cataloging workflow
- Acquisitions staff will be able to read converted Bibframe records
- Install the MARC to BIBFRAME converter and implement a draft end-to-end process
- Casalini vendor will improveme supplied MARC data to enhance the conversion to Bibframe
- Test the conversion process within the Stanford environment

###Pathway 2:
- Completely map and evaluate Stanford’s original cataloging workflow
- Review Bibframe and discuss updates to the ontology
- Evaluate & choose controlled vocabularies & identifiers to be used
- Install or create an editor to create Bibframe RDF, and selected RDF profiles and integrate into end-to-end process
- Provide means for creating local identifiers for terms without universal identifiers
- Cataloging staff will be able to use the editor/profiles
- Evaluate initial impression of profiles and recommend necessary changes

## Technical Implementation

###Shell scripts and generation of MARC records to be converted
- Look through the Ckeys folder and dump the ckeys in the files to Marc in the Marc folder

- Gather up all the Marc files and send a concatenated file to a Marc to MarxXml converter.

- Uses the Marc4J library to transform the MARC record to MarcXML.

    - The MARC record must have the authority key delimiter  output in the system record dump.
    - When encountering an authority key subfield delimiter ('?' or '=') it will add a subfield 0 to the MarcXML record 
    - for each 92X field in order to leverage the functionality of the LOC marc2bibframe converter's ability to create
    - bf:hasAuthority elements for URI's present in that subfield (BF1.0).

- Look for the Marcxml file and send it to the LOC BF1 converter

All files are moved to the Archive/.. folder for future reference.

###Executing the pipeline scripts

The scripts that create the MARC files for conversion are located in the
[sul-dlss/ld4p-tracer-bullet-scripts](https://github.com/sul-dlss/ld4p-tracer-bullet-scripts)
repository.  The instructions in that repository can be followed to run those scripts.


## Development

### Dependencies

- Java 8
- Maven 3

The Oracle JDBC maven artifacts require a license, follow the instructions at:
- http://docs.oracle.com/middleware/1213/core/MAVEN/config_maven_repo.htm
- https://blogs.oracle.com/dev2dev/entry/oracle_maven_repository_instructions_for

Once the Oracle sign-up/sign-in and licence agreement is accepted, add the sign-in
credentials to maven settings.  Follow maven instructions to encrypt the passwords, see
- https://maven.apache.org/guides/mini/guide-encryption.html
  - encrypt a master password:

          $ mvn --encrypt-master-password
          Master password: TYPE_YOUR_PASSWD_HERE
          {L+bX9REL8CAH/EkcFM4NPLUxjaEZ6nQ79feSk+xDxhE=}

  - add this master password to `~/.m2/settings-security.xml` in a block like:

          <settingsSecurity>
              <master>{L+bX9REL8CAH/EkcFM4NPLUxjaEZ6nQ79feSk+xDxhE=}</master>
          </settingsSecurity>

  - encrypt server password:

          $ mvn --encrypt-password
          Password: TYPE_YOUR_PASSWD_HERE
          {JhJfPXeAJm0HU9VwsWngQS5qGreK29EQ3fdm/7Q7A7c=}

  - add this encrypted password to `~/.m2/settings.xml` using this template:

          <servers>
            <server>
              <id>maven.oracle.com</id>
              <username>your_oracle_username</username>
              <password>{JhJfPXeAJm0HU9VwsWngQS5qGreK29EQ3fdm/7Q7A7c=}</password>
              <configuration>
                <basicAuthScope>
                  <host>ANY</host>
                  <port>ANY</port>
                  <realm>OAM 11g</realm>
                </basicAuthScope>
                <httpConfiguration>
                  <all>
                    <params>
                      <property>
                        <name>http.protocol.allow-circular-redirects</name>
                        <value>%b,true</value>
                      </property>
                    </params>
                  </all>
                </httpConfiguration>
              </configuration>
            </server>
          </servers>

- For additional information about maven settings, see
    - https://maven.apache.org/settings.html
    - https://books.sonatype.com/nexus-book/reference/_adding_credentials_to_your_maven_settings.html



### Compiling and Executing Conversions

There are convenient wrapper scripts available from the
[sul-dlss/ld4p-tracer-bullet-scripts](https://github.com/sul-dlss/ld4p-tracer-bullet-scripts)
project.  To use those scripts, the artifacts of this project need to be
downloaded or built from source.  To build this project, the
[Apache Maven](https://maven.apache.org/) build
tools must be available.

To build and package the maven project (assuming maven is installed already):
```
git clone https://github.com/sul-dlss/ld4p-tracer-bullets.git
cd ld4p-tracer-bullets
mvn package
```

The packaged JAR includes all dependencies, so it should work outside of this project.  The
packaged JAR can be copied to a convenient location and used on the CLASSPATH or the command line, e.g.
```
$ cp conversiontracerbullet/target/conversion-tracer-bullet-jar-with-dependencies.jar ~/lib/ld4p_conversion.jar
$ LD4P_JAR=~/lib/ld4p_conversion.jar
$ java -cp ${LD4P_JAR} org.stanford.MarcToXML -h
usage: org.stanford.MarcToXML
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

The [Travis CI](https://travis-ci.org/sul-dlss/ld4p-tracer-bullets) builds run tests and submit
a coverage report to [Coveralls](https://coveralls.io/github/sul-dlss/ld4p-tracer-bullets).
To update Coveralls from the command line, try:
```
mvn clean test cobertura:cobertura coveralls:report -DrepoToken=yourcoverallsprojectrepositorytoken
```
