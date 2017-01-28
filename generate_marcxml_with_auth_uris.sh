#!/bin/bash
# process all files in the Marc dir and use Marc4J and SQL to look up authority keys
# get the 92X URI and put it in the subfield 0 - jkg

# Check dependencies
if [ "$LD4P_MARC" == "" ]; then
    SCRIPT_PATH=$(dirname $0)
    source ${SCRIPT_PATH}/ld4p_configure.sh
fi

# Find and convert any dumped marc record files; archive processed files.
echo
echo "Searching for MARC files: ${LD4P_MARC}"
for MRC_FILE in `find ${LD4P_MARC} -type f | sort`
do
    filename=$(basename ${MRC_FILE} .mrc)
    LOG_FILE="${LD4P_LOGS}/${filename}_MarcToXML.log"
    SUCCESS=0
    echo
    echo "Converting MARC file:  ${MRC_FILE}"
    echo "Output MARC-XML files: ${LD4P_MARCXML}/*.xml"
    java -cp ${LD4P_JAR} org.stanford.MarcToXML ${MRC_FILE} ${LD4P_MARCXML} 2> ${LOG_FILE}
    SUCCESS=$?
    if [ ${SUCCESS} ]; then
	mv $MRC_FILE ${LD4P_DATA}/Archive/Marc/
    else
	echo
	echo "ERROR: Conversion failed for ${MRC_FILE}" | tee --append ${LD4P_LOGS}/errors
    fi
done

