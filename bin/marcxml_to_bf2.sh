#!/bin/bash
#
# Convert MARC-XML to Bibframe2 RDF

SCRIPT_PATH=$( cd $(dirname $0) && pwd -P )
export LD4P_ROOT=$( cd "${SCRIPT_PATH}/.." && pwd -P )
export LD4P_CONFIG="${LD4P_ROOT}/config/config.sh"
source ${LD4P_CONFIG}

if [ "$1" == "-h" -o "$1" == "--help" ]; then
    echo "Usage A:  $0 {MARC-XML-FILE}"
    echo "Usage B:  cat {MARC-XML-FILE} | $0"
    echo "The STDOUT should contain MARC-RDF data."
    exit
fi

if [ "$1" != "" ]; then
    marcxml_file=$1
    xsltproc --stringparam baseuri ${BASEURI} ${BF2_XSL} ${marcxml_file}
else
    # assume there is MARC-XML available on the STDIN stream
    xsltproc --stringparam baseuri ${BASEURI} ${BF2_XSL} -
fi
