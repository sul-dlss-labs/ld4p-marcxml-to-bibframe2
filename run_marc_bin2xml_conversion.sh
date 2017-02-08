# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# To replace existing MARC-XML files, run this script with
#LD4P_MARCXML_REPLACE=true

# Source bash function to run converter
source ./generate_marcxml_with_auth_uris.sh

# Find and convert any dumped marc record files.
echo
echo "Searching for MARC files: ${LD4P_MARC}"
for MRC_FILE in `find ${LD4P_MARC} -type f | sort`
do
    generate_marcxml_with_auth_uris ${MRC_FILE}
done

