# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# To replace existing MARC-RDF files, run this script with
#LD4P_MARCRDF_REPLACE=true

source ./laptop_configure.sh

echo
echo "Restoring/Updating any missing MARC-XML files from archive"
rsync -a --update ${LD4P_ARCHIVE_MARCXML}/ ${LD4P_MARCXML}/ > /dev/null

./run_marc_xml2rdf_conversion.sh
