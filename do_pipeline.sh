#!/bin/ksh
# The pipeline...
#
TBD=/s/SUL/Bin/LD4P/TB1/bin

# . ckey_sel_for_convert.sh

. $TBD/generate_marc.sh

. $TBD/ld4p_configure.sh
. $TBD/run_marc_bin2xml_conversion.sh
. $TBD/run_marc_xml2rdf_conversion.sh

