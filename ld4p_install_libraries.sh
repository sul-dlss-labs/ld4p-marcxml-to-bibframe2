# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

SCRIPT_PATH=$(dirname $0)

# This install script depends on prior configuration
if [ "$LD4P_BIN" == "" ]; then
    source ${SCRIPT_PATH}/ld4p_configure.sh
fi

source ${SCRIPT_PATH}/ld4p_install_shared_configs.sh
source ${SCRIPT_PATH}/ld4p_install_tracer_bullets.sh
source ${SCRIPT_PATH}/ld4p_install_loc_marc2bibframe.sh

