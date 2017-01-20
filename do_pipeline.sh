#!/bin/ksh
# The pipeline...
#
TBD=/s/SUL/Bin/LD4P/TB1/bin

# . ckey_sel_for_convert.ksh

. $TBD/generate_marc.ksh

. $TBD/generate_marcxml_with_auth_uris.ksh

. $TBD/marcxml2bibframe.ksh
