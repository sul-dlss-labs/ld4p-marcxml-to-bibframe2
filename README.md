# ld4p-tracer-bullet-scripts

Symphony scripts to select and export catalog records.  Conversion
scripts that use and run the [sul-dlss/ld4p-tracer-bullets](https://github.com/sul-dlss/ld4p-tracer-bullets)

These scripts will select the catalog keys for MARC records to be converted.
These records are dumped into .mrc files in an NFS directory, which has a shared
mount point on both the Symphony system and an ld4p-conversion system.

There could be a cron job that runs and generates MARC record dumps as
new records for conversion become available. Another cron job on an
`ld4p-conversion` system can scan an NFS directory to run new records
through a conversion pipeline.


## Symphony Scripts

### Dependencies

- A SIRSI configuration in `/s/SUL/Config/sirsi.env`
- Symphony scripts available on the $PATH:
  - `selcatalog`
  - `catalogdump`
- data is available in `/s/SUL/Dataload/LD4P`

### Scripts

- `ckey_sel_for_convert.sh`
  - selects catalog keys for MARC records to convert
  - runs Symphony `selcatalog` to output catalog keys

- `generate_marc.sh`
  - requires catalog keys from `ckey_sel_for_convert.sh`
  - runs Symphony `catalogdump` to output MARC data
    - uses options to output required fields
  - data input:  `/s/SUL/Dataload/LD4P`
  - data output: `/s/SUL/Dataload/LD4P/Marc/*.mrc`
  - errors logs: `/s/SUL/Dataload/LD4P/log/errors`
  - moves processed catalog keys to `/s/SUL/Dataload/LD4P/Archive/Ckeys`


## Conversion Scripts

### Dependencies

- Java 8
- Converter utilities installed on the Java classpath
  - see [sul-dlss/ld4p-tracer-bullets](https://github.com/sul-dlss/ld4p-tracer-bullets)
- data available in `/s/SUL/Dataload/LD4P`

### Scripts

- `generate_marcxml_with_auth_uris.sh`
  - calls java utility: `MarcToXMLsf0`
    - process MARC files and use Marc4J and SQL to look up authority keys
    - get the 92X URI and put it in subfield 0
  - data input:  `/s/SUL/Dataload/LD4P`
  - data output: `/s/SUL/Dataload/LD4P/Marcxml`
  - error logs:  `/s/SUL/Dataload/LD4P/log/errors`
  - moves processed MARC files to `/s/SUL/Dataload/LD4P/Archive/Marc/`

- `marcxml2bibframe.sh`
  - run a converter to convert MARC-XML to RDF
  - data input:  `/s/SUL/Dataload/LD4P/Marcxml`
  - data output: `/s/SUL/Dataload/LD4P/RDF`
  - error logs:  `/s/SUL/Dataload/LD4P/log/errors`
  - moves processed MARC-XML files to `/s/SUL/Dataload/LD4P/Archive/Marcxml/`
  - LOC converter uses XSLT XQuery
    - requires Saxon on the java classpath
    - available from `/s/SUL/Bin/Marc2Bibframe/marc2bibframe`
    - args:
      - `marcxmluri=/path/to/marc/xml/file`
      - `baseuri=http://my-base-uri/`
      - `serialization=rdfxml`

