# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

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

stamp=$(date --iso-8601)
export M2B_LOG_FILE="${LD4P_LOGS}/Marc2bibframe_${stamp}.log"
echo "LOC converter logs to M2B_LOG_FILE: ${M2B_LOG_FILE}"

loc_marc2bibframe () {
    MRC_XML=$1

    filename=$(basename "${MRC_XML}" ".xml")
    MRC_RDF="${LD4P_MARCRDF}/${filename}.rdf"

    stamp=$(date --iso-8601=sec)

    run=false
    if [ "${LD4P_MARCRDF_REPLACE}" != "" ]; then
        # Replace any existing RDF
        run=true
    elsif [ "${MRC_XML}" -nt "${MRC_RDF}" ]; then
        # True if MRC_XML exists and MRC_RDF does not, or
        # True if MRC_XML has been changed more recently than MRC_RDF.
        run=true
    else
        echo "${stamp}  SKIPPED   MARC-RDF file: ${MRC_RDF}" >> ${M2B_LOG_FILE}
        return 0
    fi

    java -cp ${LD4P_JAR} \
        net.sf.saxon.Query ${LOC_M2B_XQUERY} \
            marcxmluri="file://${MRC_XML}" \
            baseuri=${LD4P_BASEURI} \
            serialization="rdfxml" \
            1> ${MRC_RDF} \
            2>> ${M2B_LOG_FILE}

    SUCCESS=$?
    if [ ${SUCCESS} ]; then
        echo "${stamp}  CONVERTED MARC-RDF file: ${MRC_RDF}" >> ${M2B_LOG_FILE}
        mv ${MRC_XML} ${LD4P_ARCHIVE_MARCXML}
    else
        echo "${stamp}  FAILURE   MARC-RDF file: ${MRC_RDF}" >> ${M2B_LOG_FILE}
    fi

    return ${SUCCESS}
}

