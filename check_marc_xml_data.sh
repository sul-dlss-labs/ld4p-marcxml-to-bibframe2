#!/bin/bash

# Usage:
# source ./ld4p_configure.sh # or custom config file
# ./check_marc_xml_data.sh 2> log/marc_xml_checks.log

# This install script depends on prior configuration
if [ "$LD4P_MARCXML" == "" ]; then
    echo "LD4P_MARCXML path is undefined."
    echo "Please try again after 'source ld4p_configure.sh'"
    exit 1
fi

echo "Searching for empty MARC-XML files ${LD4P_MARCXML}/*.xml"
find ${LD4P_MARCXML} -type f -empty -name '*.xml' -exec ls -l {} \;

echo "Validating MARC-XML files ${LD4P_MARCXML}/*.xml"
find ${LD4P_MARCXML} -type f -name '*.xml' -exec xmllint --noout --schema ./lib/MARC21slim.xsd {} \;

