#!/bin/bash
#
# Requires one input parameter - the path to a marcxml file.
#
# Convert marcxml file indicated in param to a Bibframe RDF file.

# FIXME: perhaps this shouldn't be hardcoded in general, but ok for this test script
#OUTPUT_DIR = '../../../data/rdfxml_output'
#OUTPUT_DIR='data/test'
OUTPUT_DIR='../../../data/test'

# vars above this line need to change to process other data
#------------------------------------------------

BASEURI="http://ld4p-test.stanford.edu/"

bf1_converter="loc_marc2bibframe/xbin/saxon.xqy"

jar='java/target/xform-marcxml-to-bf1-jar-with-dependencies.jar'

marcxml_file=$1
mydir=`cd "$(dirname "$0")"; pwd`
marcxmluri="file://${mydir}/../${marcxml_file}"

filename=$(basename "${marcxml_file}" ".xml")
output_file="${OUTPUT_DIR}/${filename}.rdf"

log_date=$(date +%Y%m%dT%H%M%S)
log_file="log/Marc2bibframe_${log_date}.log"

echo "Converter logs to log_file: ${log_file}"

java -cp ${jar} net.sf.saxon.Query ${bf1_converter} \
  marcxmluri=${marcxmluri} \
  baseuri=${BASEURI} \
  serialization="rdfxml" \
  1> ${output_file} \
  2>> ${log_file}

success=$?
if [ ${success} ]; then
  msg="Completed conversion of ${marcxml_file}"
else
  log_stamp=$(date --iso-8601=sec)
  msg="${log_stamp} FAILED - rdfxml output file: ${output_file}"
fi
echo "${msg}" >> ${log_file}
echo

exit ${success}
