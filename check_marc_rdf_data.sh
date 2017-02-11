#!/bin/bash

echo "Searching for empty MARC-RDF files ${LD4P_MARCRDF}/*.rdf"
for f in $(find $LD4P_MARCRDF -type f -name '*.rdf'); do
    [[ ! -s $f ]] && ls -l $f
done

