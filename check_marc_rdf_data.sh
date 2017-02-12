#!/bin/bash

echo "Searching for empty MARC-XML files ${LD4P_MARCRDF}/*.rdf"
find $LD4P_MARCRDF -type f -empty -name '*.rdf' -exec ls -l {} \;

