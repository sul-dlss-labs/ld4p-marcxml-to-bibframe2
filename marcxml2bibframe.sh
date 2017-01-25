#!/bin/bash
# Run the converter

# Check dependencies
if [ "$LD4P_MARCXML" == "" ]; then
    SCRIPT_PATH=$(dirname $0)
    source ${SCRIPT_PATH}/ld4p_configure.sh
fi

# Currently assumes there is one large MARC XML file in $LD4P_MARCXML path.
echo "Searching for MARC XML files: ${LD4P_MARCXML}"
for F_XML in `find ${LD4P_MARCXML} -type f`
do
    F_RDF="${LD4P_MARCRDF}/stf.`date "+%Y%m%d%H%s"`.rdf"
    echo "Converting: ${F_XML} -> ${F_RDF}"
    # See ld4p_configure.sh for details of ld4p_marc2bibframe
    ld4p_marc2bibframe ${F_XML} ${F_RDF}
    if [ $? == 0 ]; then
      echo "INFO: Marc2Bibframe success for ${F_RDF}" >> ${LD4P_LOGS}/info
      mv -p ${F_XML} ${LD4P_DATA}/Archive/MarcXML/
    fi
done

