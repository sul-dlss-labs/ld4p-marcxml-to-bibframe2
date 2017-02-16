#!/bin/bash

cmd=$(keeptoken 2> /dev/null | grep 'source')
eval $cmd

source ./farmshare_config.sh

echo "Searching for MARC-XML files: ${LD4P_MARCXML}/*.xml"
for XML_FILE in $(find ${LD4P_MARCXML} -type f -name '*.xml')
do
    echo "Submitting job to process: ${XML_FILE}"
    qsub sge_run_loc_marc2bibframe.sh ${XML_FILE}
done

