#!/bin/bash

# This install script depends on prior configuration
if [ "$LD4P_CONFIGS" == "" ]; then
    echo "LD4P_CONFIGS is undefined."
    echo "Please try again after 'source ld4p_configure.sh'"
    exit 1
fi

# Java libraries should be installed or deployed from https://github.com/sul-dlss/ld4p-tracer-bullets
# Check the deployment recipes in that project for details.
SCRIPT_PATH=$(dirname $0)
export LD4P_JAR="${LD4P_BIN}/ld4p_converter.jar"
if [ ! -f "${LD4P_JAR}" ]; then
    TB_JAR_FILE="./lib/ld4p_converter.jar"
    # Confirm the package is available
    if [ ! -f "${TB_JAR_FILE}" ]; then
        echo "ERROR: Expected to find: ${TB_JAR_FILE}"
        echo
        exit 1
    fi
    cp ${TB_JAR_FILE} ${LD4P_JAR}
fi
# Confirm the installation
if [ ! -f "${LD4P_JAR}" ]; then
   echo "ERROR: The LD4P scripts require a java library: ${LD4P_JAR}" 1>&2
   echo "See https://github.com/sul-dlss/ld4p-tracer-bullets for details" 1>&2
   echo "Build that project and place the packaged artifact into this project." 1>&2
   exit 1
fi

