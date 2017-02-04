# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

SCRIPT_PATH=$(dirname $0)

# This install script depends on prior configuration
if [ "$LD4P_CONFIGS" == "" ]; then
    source ${SCRIPT_PATH}/ld4p_configure.sh
fi

if [ ! -d ${LD4P_CONFIGS} ]; then
    # THIS IS A SUL PRIVATE REPOSITORY FOR SYSTEM CONFIGURATION DATA
    git clone git@github.com:sul-dlss/shared_configs.git ${LD4P_CONFIGS}
    pushd  ${LD4P_CONFIGS} > /dev/null
    git checkout ld4p-tracer-bullets
    popd > /dev/null
fi
