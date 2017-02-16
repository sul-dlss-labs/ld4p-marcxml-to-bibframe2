# ld4p-tracer-bullet-scripts

Symphony scripts to select and export catalog records. 
 - Symphony scripts select catalog keys for MARC records to be
   converted. These records are dumped into .mrc files in an NFS
   directory, which has a shared mount point on both the Symphony
   system and an ld4p-conversion system.

Conversion scripts that use and run conversion utilities, including:
 - [sul-dlss/ld4p-tracer-bullets](https://github.com/sul-dlss/ld4p-tracer-bullets)
   - A packaged archive from this project should be in `./lib/ld4p_converter.jar`
 - [LOC marc2bibframe](https://github.com/lcnetdev/marc2bibframe.git)
   - This LOC repository is a submodule of this project.


There could be a cron job that runs and generates MARC record dumps as
new records for conversion become available. Another cron job on an
`ld4p-conversion` system can scan an NFS directory to run new records
through a conversion pipeline.


### General Dependencies

- Java 8
- Maven 3
- Converter utilities installed on the Java classpath
    - The `ld4p_install_*.sh` scripts should take care of dependencies
    - [LOC marc2bibframe](https://github.com/lcnetdev/marc2bibframe.git)
- File system configuration details
    - See how `ld4p_configure.sh` sets various input/output paths

The conversion scripts currently work with file systems.  There is also an authority
record lookup option that requires access to a Symphony/Oracle database.  Some of these
configuration details are in a SUL-DLSS private configuration repository.


## Getting Started: Installation and Configuration

```sh
git clone https://github.com/sul-dlss/ld4p-tracer-bullet-scripts.git
cd ld4p-tracer-bullet-scripts
# populate code from external projects (only required once)
git submodule update --init --recursive
```

Review and modify `ld4p_configure.sh` as required to configure
system paths for data files (see details in that script).
Then install the dependencies (only required once):
```
cp ld4p_configure.sh custom_configure.sh
# edit custom_configure.sh
source ./custom_configure.sh
./ld4p_install_libraries.sh
```

#### Updating Dependencies

Some dependencies for these scripts are git submodules in this project.
If there are updates to these external projects, they can be
managed using `git submodule` features. If any submodules are updated,
this project should be redeployed. For example, to update all
submodules to the latest master for the remote, e.g.:
```
git submodule foreach git pull origin master
```

#### Configuration

The details are in `ld4p_configure.sh`
    - that file is more authoritative than this README

In brief, there are two data paths that must be set and the
rest of the configuration can be done automatically (using
assumptions derived from an existing system).
    - A Symphony root path
    - An RDF root path

For example, the effective defaults are:
    ```
    export LD4P_SIRSI=/symphony
    export LD4P_RDF=/rdf
    source ./ld4p_configure.sh
    ```

## Symphony Scripts

### Dependencies

- A SIRSI configuration (e.g. `/s/SUL/Config/sirsi.env`)
- Symphony scripts available on the $PATH:
  - `selcatalog`
  - `catalogdump`
- `ld4p_configure.sh` sets various input/output paths

### Scripts

- `ckey_sel_for_convert.sh`
  - selects catalog keys for MARC records to convert
  - runs Symphony `selcatalog` to output catalog keys

- `generate_marc.sh`
    - requires catalog keys from `ckey_sel_for_convert.sh`
    - runs Symphony `catalogdump` to output MARC data
        - uses options to output required fields
    - `ld4p_configure.sh` sets various input/output paths, e.g. 
        - data input path configuration (e.g. `/s/SUL/Dataload/LD4P`)
        - data output path configuration (e.g. `/s/SUL/Dataload/LD4P/Marc/*.mrc`)
        - error log path configuration (e.g. `/s/SUL/Dataload/LD4P/log/errors`)
        - archived catalog keys path (e.g. `/s/SUL/Dataload/LD4P/Archive/Ckeys`)

## Conversion Scripts

- `run_marc_bin2xml_conversion.sh`
  - sources function defined by `generate_marcxml_with_auth_uris.sh`
  - depends on java utility: `MarcToXML`
    - process MARC files and use Marc4J and SQL to look up authority keys
    - get the 92X URI and put it in subfield 0
  - I/O paths define by `ld4p_configure.sh`:
    - data input:      `$LD4P_MARC`
    - data output:     `$LD4P_MARCXML`
    - error logs:      `$LD4P_LOG`
    - processed files: `$LD4P_ARCHIVE_MARC`

- `run_marc_xml2rdf_conversion.sh`
  - convert MARC-XML to RDF (Bibframe)
  - sources function defined by `loc_marc2bibframe.sh`
    - LOC converter uses XSLT XQuery
    - requires Saxon on the java classpath
    - A "BASE_URI" is define by `ld4p_configure.sh`
  - I/O paths define by `ld4p_configure.sh`:
    - data input:      `$LD4P_MARCXML`
    - data output:     `$LD4P_MARCRDF`
    - error logs:      `$LD4P_LOG`
    - processed files: `$LD4P_ARCHIVE_MARCXML`
    
#### Development Conversions

Follow the getting started notes above and once the
`ld4p_install_libraries.sh` works, it should be possible to
run the conversions on a development laptop.  To work on
custom configurations and shell scripts, prefix the script
file with `laptop` and git will ignore them.

In brief, create two file system paths for MARC and RDF data and
add those paths to a `laptop_configure.sh` script, and
the rest of the configuration should be automatic.  Read the
`ld4p_configure.sh` details to understand what happens.  Then
copy that configuration file into a custom configuration file
and modify anything as required. For example,
```
cp ld4l_configure.sh laptop_configure.sh
```
Then add the laptop paths to the top of `laptop_configure.sh`:
```
#laptop_configure.sh
#!/bin/bash
export LD4P_SIRSI=/ld4p/marc
export LD4P_RDF=/ld4p/rdf
# the rest of this file is from ld4p_configure.sh
# modify anything from ld4p_configure.sh as required
```

Using that `laptop_configure.sh` script, an example of running
conversions on a development laptop:
```
source ./laptop_configure.sh
run_marc_bin2xml_conversion.sh
run_marc_xml2rdf_conversion.sh
```

