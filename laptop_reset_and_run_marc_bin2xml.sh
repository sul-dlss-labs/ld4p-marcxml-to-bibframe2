# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# To replace existing MARC-XML files, run this script with
#LD4P_MARCXML_REPLACE=true

source ./laptop_configure.sh

echo
echo "Copying private MARC files from shared configs fixture data"
rsync -av --update ${LD4P_CONFIGS}/files/*.mrc ${LD4P_MARC}/ > /dev/null
echo
echo "Restoring/Updating any missing MARC files from archive"
rsync -av --update ${LD4P_ARCHIVE_MARC}/*.mrc ${LD4P_MARC}/ > /dev/null

./run_marc_bin2xml_conversion.sh
