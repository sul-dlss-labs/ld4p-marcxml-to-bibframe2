#!/bin/bash

# To replace existing MARC-RDF files, run this script with
#LD4P_MARCRDF_REPLACE=true

# Source bash function to run converter
source ./loc_marc2bibframe.sh

echo
echo "Searching for MARC-XML files: ${LD4P_MARCXML}"
for XML_FILE in `find ${LD4P_MARCXML} -type f | sort`
do
    loc_marc2bibframe ${XML_FILE}
done
