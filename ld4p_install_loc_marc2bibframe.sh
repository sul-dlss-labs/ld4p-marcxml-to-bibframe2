# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# This install script depends on prior configuration
if [ "$LD4P_CONFIGS" == "" ]; then
    echo "LD4P_CONFIGS is undefined."
    echo "Please try again after 'source ld4p_configure.sh'"
    exit 1
fi

# The LOC converter is from https://github.com/lcnetdev/marc2bibframe.git
# Confirm the LOC converter source package is available
SCRIPT_PATH=$(dirname $0)
export LOC_M2B_PATH="${SCRIPT_PATH}/loc_marc2bibframe"
export LOC_M2B_XQUERY="${LOC_M2B_PATH}/xbin/saxon.xqy"
if [ ! -f "${LOC_M2B_PATH}" ]; then
    pushd ${SCRIPT_PATH} > /dev/null
    git submodule update --init --recursive
    popd > /dev/null
fi
# Confirm the source package is available
if [ ! -f "${LOC_M2B_XQUERY}" ]; then
   echo "ERROR: The LD4P scripts require an LOC converter source: ${LOC_M2B_XQUERY}" 1>&2
   echo "The LOC converter is from https://github.com/lcnetdev/marc2bibframe.git" 1>&2
   exit 1
fi

