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

The scripts that create the MARC files for conversion are located in the `ld4p-tracer-bullet-scripts` repository (https://github.com/sul-dlss/ld4p-tracer-bullet-scripts).
Clone that repository and run the wrapper script:

```
/s/SUL/Bin/LD4P/TB1/bin/do_pipeline.ksh
```
Then run `mvn exec:java`

##Dependencies

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


##Compiling and Executing the pre- and post-marc processing Java packages (and submitting code coverage data)

Import the project into a Java IDE and use the provided iDE build tools. Don't forget to set the Maven property 
`repoToken` to be the coveralls project repository token, using `-DrepoToken=yourcoverallsprojectrepositorytoken` in the 
configuration section for the plugin in your IDE.

If you want to build the project and run the program from the command line:
```
mvn package
mvn exec:java
```

To run the tests and submit coverage report from the command line:
```
mvn clean test cobertura:cobertura coveralls:report -DrepoToken=yourcoverallsprojectrepositorytoken
```
