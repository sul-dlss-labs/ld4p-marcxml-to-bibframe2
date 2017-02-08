# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# This install script depends on prior configuration
if [ "$LD4P_CONFIGS" == "" ]; then
    echo "LD4P_CONFIGS is undefined."
    echo "Please try again after 'source ld4p_configure.sh'"
    return 1
fi

if [ ! -d "${LD4P_CONFIGS}" ]; then
    # THIS IS A SUL PRIVATE REPOSITORY FOR SYSTEM CONFIGURATION DATA
    echo "Trying to clone private repository for SUL-DLSS shared_configs"
    git clone git@github.com:sul-dlss/shared_configs.git ${LD4P_CONFIGS}
    pushd  ${LD4P_CONFIGS} > /dev/null
    git checkout ld4p-tracer-bullets
    popd > /dev/null
fi
