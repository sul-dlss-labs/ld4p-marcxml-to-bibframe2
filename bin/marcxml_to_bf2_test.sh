#!/bin/bash

SCRIPT_PATH=$( cd $(dirname $0) && pwd -P )
export LD4P_ROOT=$( cd "${SCRIPT_PATH}/.." && pwd -P )
export LD4P_CONFIG="${LD4P_ROOT}/config/config.sh"
source ${LD4P_CONFIG}

MARC_XML="${LD4P_MARCXML}/one_record.xml"
MARC_RDF="${LD4P_MARCRDF}/one_record.rdf"
if [ ! -f ${MARC_XML} ]; then
    echo "Failed to locate MARC-XML file: ${MARC_XML}"
    exit 1
fi

${CONVERT_SCRIPT} ${MARC_XML} > ${MARC_RDF}

success=$?
if [ ${success} ]; then
  msg="SUCCESS transformed ${MARC_XML} to ${MARC_RDF}"
else
  msg="FAILED to transform ${MARC_XML} to ${MARC_RDF}"
fi
echo "${msg}"

exit ${success}
