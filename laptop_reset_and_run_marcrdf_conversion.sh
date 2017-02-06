# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# To replace exisiting MARC-RDF files, run this script with
#LD4P_MARCRDF_REPLACE=true

source ./laptop_configure.sh

echo
echo "Restoring/Updating any missing MARC-XML files from archive"
rsync -a --update ${LD4P_ARCHIVE_MARCXML}/ ${LD4P_MARCXML}/ > /dev/null

stamp=$(date --iso-8601)
LOG_FILE="${LD4P_LOGS}/Marc2bibframe_${stamp}.log"

echo
echo "Searching for MARC-XML files: ${LD4P_MARCXML}"
echo "Logging to: ${LOG_FILE}"
for XML_FILE in `find ${LD4P_MARCXML} -type f | sort`
do
    loc_marc2bibframe ${XML_FILE}
done

