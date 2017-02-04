# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

SCRIPT_PATH=$(dirname $0)

# This install script depends on prior configuration
if [ "$LD4P_BIN" == "" ]; then
    source ${SCRIPT_PATH}/ld4p_configure.sh
fi

# Java libraries should be installed or deployed from https://github.com/sul-dlss/ld4p-tracer-bullets
# Check the deployment recipes in that project for details.
export LD4P_JAR="${LD4P_BIN}/ld4p_converter.jar"
if [ ! -f "${LD4P_JAR}" ]; then
    TB_PATH="./ld4p-tracer-bullets"
    TB_JAR_FILE="${TB_PATH}/conversiontracerbullet/target/conversion-tracer-bullet-jar-with-dependencies.jar"
    if [ ! -f ${TB_JAR_FILE} ]; then
        pushd ${TB_PATH} > /dev/null
        echo "Building ld4p-tracer-bullets project"
        # Building this library depends on private configuration files
        # that must be installed by the ld4p_install_shared_configs
        rsync -a ${LD4P_CONFIGS}/conversiontracerbullet/ conversiontracerbullet/
        mvn package > /dev/null
        popd > /dev/null
    fi
    # Confirm the package is available
    if [ ! -f ${TB_JAR_FILE} ]; then
        echo "Expected to find: ${TB_JAR_FILE}"
        exit 1
    fi
    cp ${TB_JAR_FILE} ${LD4P_JAR}
fi
# Confirm the installation
if [ ! -f "${LD4P_JAR}" ]; then
   echo "ERROR: The LD4P scripts require a java library: ${LD4P_JAR}" 1>&2
   echo "See https://github.com/sul-dlss/ld4p-tracer-bullets for details" 1>&2
   exit 1
fi

