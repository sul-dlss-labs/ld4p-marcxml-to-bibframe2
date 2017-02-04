# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

SCRIPT_PATH=$(dirname $0)

# Check dependencies
if [ "$LOC_M2B_XQUERY" == "" ]; then
    source ${SCRIPT_PATH}/ld4p_configure.sh
fi

# Bash function to convert one MARC-XML file to a Bibframe RDF file.
# Depends on the environment variables defined in ld4p_configure.sh
# Depends on installation of the loc marc2bibframe project.
# Usage:  loc_marc2bibframe {MRC_XML}
#
# Output RDF files are placed in
# "${LD4P_MARCRDF}/{MRC_XML_filename}.rdf"
#
# To replace exisiting MARC-RDF files, set
#LD4P_MARCRDF_REPLACE=true loc_marc2bibframe {MRC_XML}
#
loc_marc2bibframe () {
    MRC_XML=$1

    filename=$(basename "${MRC_XML}" ".xml")
    MRC_RDF="${LD4P_MARCRDF}/${filename}.rdf"

    stamp=$(date --iso-8601=sec)

    if [[ "${LD4P_MARCRDF_REPLACE}" == "" && -s "${MRC_RDF}" ]]; then
        echo "${stamp}  SKIPPED   MARC-RDF file: ${MRC_RDF}" >> ${LOG_FILE}
        return 0
    fi

    java -cp ${LD4P_JAR} \
        net.sf.saxon.Query ${LOC_M2B_XQUERY} \
            marcxmluri="file://${MRC_XML}" \
            baseuri=${LD4P_BASEURI} \
            serialization="rdfxml" \
            1> ${MRC_RDF} \
            2>> ${LOG_FILE}

    SUCCESS=$?
    if [ ${SUCCESS} ]; then
        echo "${stamp}  CONVERTED MARC-RDF file: ${MRC_RDF}" >> ${LOG_FILE}
        mv ${MRC_XML} ${LD4P_ARCHIVE_MARCXML}
    else
        echo "${stamp}  FAILURE   MARC-RDF file: ${MRC_RDF}" >> ${LOG_FILE}
    fi

    return ${SUCCESS}
}

