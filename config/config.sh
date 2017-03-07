#!/bin/bash

export BASEURI="http://ld4p-test.stanford.edu/"

# An LD4P_ROOT path must be defined by any scripts calling this configuration.
if [ "$LD4P_ROOT" == "" ]; then
    echo "ERROR: The LD4P configuration requires an LD4P_ROOT path: ${LD4P_ROOT}" 1>&2
    kill -INT $$
fi

export LD4P_CONFIG="${LD4P_ROOT}/config/config.sh"
export LD4P_LOGS="${LD4P_ROOT}/log"
export LD4P_BIN="${LD4P_ROOT}/bin"

export LD4P_DATA="${LD4P_ROOT}/data"
export LD4P_MARCXML="${LD4P_DATA}/MarcXML"
export LD4P_MARCRDF="${LD4P_DATA}/MarcRDF"

export LD4P_ARCHIVE_ENABLED=false
export LD4P_MARCXML_ARCHIVE="${LD4P_DATA}/MarcXML_Archive"

export BF2_XSL="${LD4P_ROOT}/loc_marc2bibframe2/xsl/marc2bibframe2.xsl"
if [ ! -f ${BF2_XSL} ]; then
  echo "Failed to configure LOC BF2 converter: ${BF2_XSL}"
  return 1
fi

CONVERT_SCRIPT="${LD4P_BIN}/marcxml_to_bf2.sh"
if [ ! -f "${CONVERT_SCRIPT}" ]; then
    echo "Failed to locate convert script: ${CONVERT_SCRIPT}"
    exit 1
fi

