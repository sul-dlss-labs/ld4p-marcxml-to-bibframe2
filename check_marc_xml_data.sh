#!/bin/bash

echo "Searching for empty MARC-XML files ${LD4P_MARCXML}/*.xml"
for f in $(find $LD4P_MARCXML -type f -name '*.xml'); do
    [[ ! -s $f ]] && ls -l $f
done

