# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# Bash function to convert one MARC file to XML files for each record.
# Depends on the environment variables defined in ld4p_configure.sh
# Depends on installation of the ld4p-tracer-bullets java library.
# Requires one input parameter - the path to a MARC21 binary file.
generate_marcxml_with_auth_uris () {

    MRC_FILE=$1

    stamp=$(date --iso-8601)
    filename=$(basename ${MRC_FILE} .mrc)
    LOG_FILE="${LD4P_LOGS}/${filename}_MarcToXML_${stamp}.log"
    ERR_FILE="${LD4P_LOGS}/${filename}_MarcToXML_${stamp}_errors.log"

    echo
    echo "Converting MARC file:  ${MRC_FILE}"
    echo "Output MARC-XML files: ${LD4P_MARCXML}/*.xml"
    echo "Logging conversion to: ${LOG_FILE}"

    options="-i ${MRC_FILE} -o ${LD4P_MARCXML} -l ${LOG_FILE}"
    [ ${LD4P_MARCXML_REPLACE} == true ] && options="${options} -r"

    # Process all records in the MRC_FILE using marc4j and SQL to
    # look up authority keys and retrieve any URI values from
    # 92X fields and put them in the subfield 0 so that the 
    # LOC converter (for Bibframe v1) can use them correctly.
    java -cp ${LD4P_JAR} org.stanford.MarcToXML ${options}

    SUCCESS=$?
    if [ ${SUCCESS} ]; then
        echo "Completed conversion."
        if [ ${LD4P_ARCHIVE_ENABLED} == true ]; then
            # Archive the MRC_FILE (preserve timestamps etc.)
            rsync -a --update "${MRC_FILE}" "${LD4P_ARCHIVE_MARC}/"
            rm "${MRC_FILE}"
        fi
    else
        echo "ERROR: Conversion failed for ${MRC_FILE}" | tee --append ${ERR_FILE}
    fi

    return ${SUCCESS}
}

