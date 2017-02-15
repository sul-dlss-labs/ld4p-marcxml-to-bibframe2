#!/bin/bash

# Usage:
# source ./ld4p_configure.sh # or custom config file
# ./check_marc_rdf_data.sh > log/marc_rdf_checks.log

# This install script depends on prior configuration
if [ "$LD4P_MARCRDF" == "" ]; then
    echo "LD4P_MARCRDF path is undefined."
    echo "Please try again after 'source ld4p_configure.sh'"
    exit 1
fi

echo "Searching for empty MARC-XML files ${LD4P_MARCRDF}/*.rdf"
find ${LD4P_MARCRDF} -type f -empty -name '*.rdf' -exec ls -l {} \;

