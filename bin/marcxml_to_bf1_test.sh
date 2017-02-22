#!/bin/bash
#
# Requires one input parameter - the path to a marcxml file.
#
# Convert marcxml file indicated in param to a Bibframe RDF file.

#OUTPUT_DIR = '../../../data/rdfxml_output'
OUTPUT_DIR='data/test'
#OUTPUT_DIR='../../../data/test'

# ensure we have LoC marc2bibframe converter
bf1_converter_path="loc_marc2bibframe"
bf1_converter="${bf1_converter_path}/xbin/saxon.xqy"
if [ ! -f "${bf1_converter}" ]; then
  if [ -d "${bf1_converter_path}" ]; then
    git submodule update --init --recursive
  fi
  if [ ! -f "${bf1_converter}" ]; then
    echo "ERROR: Can't find converter: ${bf1_converter}" 1>&2
    exit 1
  fi
fi

baseuri="http://linked-data-test.stanford.edu/library/"

jar_dir='java/target'
jar="${jar_dir}/xform-marcxml-to-bf1-jar-with-dependencies.jar"

log_date=$(date +%Y%m%dT%H%M%S)
log_file="log/Marc2bibframe_${log_date}.log"
echo "Converter logs to log_file: ${log_file}"

marcxml_file=$1
mydir=`cd "$(dirname "$0")"; pwd`

filename=$(basename "${marcxml_file}" ".xml")
rdf_output_file="${OUTPUT_DIR}/${filename}.rdf"

java -cp ${jar} \
  net.sf.saxon.Query ${bf1_converter} \
  marcxmluri="file://${mydir}/../${marcxml_file}" \
  baseuri=${baseuri} \
  serialization="rdfxml" \
  1> ${rdf_output_file} \
  2>> ${log_file}

success=$?
if [ ${success} ]; then
  msg="Completed conversion of ${marcxml_file}"
else
  log_stamp=$(date --iso-8601=sec)
  msg="${log_stamp} FAILED - rdf output file: ${rdf_output_file}"
fi
echo "${msg}" >> ${log_file}
echo

exit ${success}
