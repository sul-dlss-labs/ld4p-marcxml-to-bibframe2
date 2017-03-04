#!/bin/bash
#
# Requires one input parameter - the path to a marcxml file.
#
# Convert marcxml file indicated in param to a Bibframe RDF file.

BASEURI="http://ld4p-test.stanford.edu/"

BF2_XSL="loc_marc2bibframe2/xsl/marc2bibframe2.xsl"

marcxml_file=$1
marcrdf_file=$(echo "${marcxml_file}" | sed s/.xml$/.rdf/)

xsltproc --stringparam baseuri ${BASEURI} ${BF2_XSL} ${marcxml_file} \
  1> ${marcrdf_file}

success=$?
if [ ${success} ]; then
  msg="SUCCESS transformed ${marcxml_file} to ${marcrdf_file}"
else
  msg="FAILED to transform ${marcxml_file} to ${marcrdf_file}"
fi
echo "${msg}"

exit ${success}
