#!/bin/bash

SCRIPT_PATH=$( cd $(dirname $0) && pwd -P )
export LD4P_ROOT=$( cd "${SCRIPT_PATH}/.." && pwd -P )
export LD4P_CONFIG="${LD4P_ROOT}/config/config.sh"
source ${LD4P_CONFIG}

if [ ! -d "${LD4P_MARCXML}" ]; then
    echo "Failed to configure LD4P_MARCXML data directory: ${LD4P_MARCXML}"
    exit 1
fi

if [ ! -d "${LD4P_MARCRDF}" ]; then
    echo "Failed to configure LD4P_MARCRDF data directory: ${LD4P_MARCRDF}"
    exit 1
fi

if [ ! -d "${LD4P_LOGS}" ]; then
    echo "Failed to configure LD4P_LOGS directory: ${LD4P_LOGS}"
    exit 1
fi

LOG_DATE=$(date +%Y%m%dT%H%M%S)
export LD4P_MARCRDF_LOG="${LD4P_LOGS}/marcxml_to_bf2_${LOG_DATE}.log"
echo "Conversion logs to ${LD4P_MARCRDF_LOG}"
echo "Searching MARC-XML files: ${LD4P_MARCXML}/*.xml"
for marc_xml in $(find ${LD4P_MARCXML} -type f -name '*.xml')
do
    record=$(basename "${marc_xml}" ".xml")
    marc_rdf="${LD4P_MARCRDF}/${record}.rdf"

    log_stamp=$(date +%Y-%m-%dT%H:%M:%S%z)
    echo "${log_stamp}  CONVERT  ${marc_xml}" >> ${LD4P_MARCRDF_LOG}

    ${CONVERT_SCRIPT} ${marc_xml}  1> ${marc_rdf}  2>> ${LD4P_MARCRDF_LOG}

    SUCCESS=$?
    if [ ${SUCCESS} ]; then
        msg="CREATED  ${marc_rdf}"
        if [ "${LD4P_ARCHIVE_ENABLED}" == "true" ]; then
            # Archive the marc_xml file (preserve timestamps etc.)
            rsync -a --update "${marc_xml}" "${LD4P_ARCHIVE_MARCXML}/"
            rm ${marc_xml}
        fi
    else
        msg="FAILED  ${marc_rdf}"
    fi
    log_stamp=$(date +%Y-%m-%dT%H:%M:%S%z)
    echo "${log_stamp}  ${msg}" >> ${LD4P_MARCRDF_LOG}
done
echo "Completed MARC-XML files: ${LD4P_MARCXML}/*.xml"

