#!/bin/bash

# This install script depends on prior configuration
if [ "$LD4P_CONFIGS" == "" ]; then
    echo "LD4P_CONFIGS is undefined."
    echo "Please try again after 'source ld4p_configure.sh'"
    return 1
fi

if [ ! -f "${LD4P_CONFIGS}/README.md" ]; then
    # THIS IS A SUL PRIVATE REPOSITORY FOR SYSTEM CONFIGURATION DATA
    echo "Trying to clone private repository for SUL-DLSS shared_configs"
    git clone git@github.com:sul-dlss/shared_configs.git ${LD4P_CONFIGS}

    if [ ! -f "${LD4P_CONFIGS}/README.md" ]; then
       echo "WARNING: failed to clone private repository for SUL-DLSS shared_configs"
       echo "WARNING: Unless the private data files are essential, this could be OK."
       echo
       exit 1
    fi
fi

if [ ! -d "${LD4P_CONFIGS}/files" ]; then
    echo "Trying to checkout ld4p-tracer-bullets branch for shared_configs"
    pushd  ${LD4P_CONFIGS} > /dev/null
    git fetch -ap
    git pull
    git checkout ld4p-tracer-bullets
    popd > /dev/null

    if [ ! -d "${LD4P_CONFIGS}/files" ]; then
       echo "WARNING: failed to checkout ld4p-tracer-bullets branch for shared_configs"
       echo "WARNING: Unless the private data files are essential, this could be OK."
       echo
       exit 1
    fi
fi

