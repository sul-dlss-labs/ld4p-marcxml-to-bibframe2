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

-- The MARC record must have the authority key delimiter  output in the system record dump.
-- When encountering an authority key subfield delimiter ('?' or '=') it will add a subfield 0 to the MarcXML record
-- for each 92X field in order to leverage the functionality of the LOC marc2bibframe converter's ability to create
-- bf:hasAuthority elements for URI's present in that subfield (BF1.0).

-Look for the Marcxml file and send it to the LOC BF1 converter

All files are moved to the Archive/.. folder for future reference.

###Executing the pipeline scripts
```
/s/SUL/Bin/LD4P/TB1/bin/do_pipeline.ksh
```

##Compiling and Executing the pre- and post- processing Java packages

Import the project into a Java IDE and use the build tools provided, or from the command line:
```
$ mvn package
$ mvn exec:java
```
