#!/bin/ksh
# The pipeline...
#

# . ckey_sel_for_convert.ksh

. generate_marc.ksh

. generate_marcxml_with_auth_uris.ksh

.  marcxml2bibframe.ksh
