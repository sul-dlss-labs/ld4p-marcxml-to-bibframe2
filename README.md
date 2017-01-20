# ld4p-tracer-bullet-scripts

Symphony scripts that use and run the [sul-dlss/ld4p-tracer-bullets](https://github.com/sul-dlss/ld4p-tracer-bullets)

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
  - data is available in `/s/SUL/Dataload/LD4P`
  - data is output to `/s/SUL/Dataload/LD4P/Marc/*.mrc`
  - errors are logged to `/s/SUL/Dataload/LD4P/log/errors`
  - processed catalog keys are moved to `/s/SUL/Dataload/LD4P/Archive/Ckeys`


## Conversion Scripts

Scripts that use the Java utilities from
[sul-dlss/ld4p-tracer-bullets](https://github.com/sul-dlss/ld4p-tracer-bullets)

- generate_marcxml_with_auth_uris.sh

- marcxml2bibframe.sh

