#!/bin/bash

echo "Searching for empty MARC-XML files ${LD4P_MARCXML}/*.xml"
find $LD4P_MARCXML -type f -empty -name '*.xml' -exec ls -l {} \;

