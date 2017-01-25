#!/bin/bash
# process all files in the Marc dir and use Marc4J and SQL to look up authority keys
# get the 92X URI and put it in the subfield 0 - jkg

# Check dependencies
if [ "$LD4P_MARC" == "" ]; then
    SCRIPT_PATH=$(dirname $0)
    source ${SCRIPT_PATH}/ld4p_configure.sh
fi

stamp=`date "+%Y%m%d%H%s"`
MRC_FILE="${LD4P_DATA}/marc.${stamp}"
XML_FILE="${LD4P_MARCXML}/stf.${stamp}.xml"

# Gather all the dumped marc records and make 1 temp file, then clean up.
echo "Searching for MARC files: ${LD4P_MARC}"
for F_MRC in `find ${LD4P_MARC} -type f`
do
  cat $F_MRC >> ${MRC_FILE}
  mv $F_MRC ${LD4P_DATA}/Archive/Marc/
done

# $MRC_FILE is assumed to be one large file of marc records
if [ -f ${MRC_FILE} ]; then
    java -cp ${LD4P_JAR} org.stanford.MarcToXML ${MRC_FILE} > ${XML_FILE} 2>> ${LD4P_LOGS}/errors
    conversion_success=$?
    rm ${MRC_FILE}  # always cleanup the concatenation of all the marc files
    if [ ${conversion_success} != 0 ]; then
        echo "ERROR: Conversion failed" && cat ${LD4P_LOGS}/errors && exit 1
    fi
else
    echo "WARNING: MARC files are missing" | tee --append ${LD4P_LOGS}/errors
fi
