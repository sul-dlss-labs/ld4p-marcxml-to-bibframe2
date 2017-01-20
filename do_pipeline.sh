#!/bin/ksh
# The pipeline...
#
TBD=/s/SUL/Bin/LD4P/TB1/bin

# . ckey_sel_for_convert.sh

. $TBD/generate_marc.sh

. $TBD/generate_marcxml_with_auth_uris.sh

. $TBD/marcxml2bibframe.sh
