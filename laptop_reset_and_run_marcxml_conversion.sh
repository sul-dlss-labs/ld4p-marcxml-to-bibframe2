# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# To replace exisiting MARC-XML files, run this script with
#LD4P_MARCXML_REPLACE=true

source ./laptop_configure.sh

echo
echo "Copying private MARC files from shared configs fixture data"
rsync -av --update ${LD4P_CONFIGS}/files/*.mrc ${LD4P_MARC}/ > /dev/null
echo
echo "Restoring/Updating any missing MARC files from archive"
rsync -av --update ${LD4P_ARCHIVE_MARC}/*.mrc ${LD4P_MARC}/ > /dev/null

# Find and convert any dumped marc record files; archive processed files.
echo
echo "Searching for MARC files: ${LD4P_MARC}"
for MRC_FILE in `find ${LD4P_MARC} -type f | sort`
do
    generate_marcxml_with_auth_uris ${MRC_FILE}
done

