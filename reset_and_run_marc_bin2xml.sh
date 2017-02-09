# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

echo
echo "Copying private MARC files from shared configs fixture data"
rsync -a --update ${LD4P_CONFIGS}/files/*.mrc ${LD4P_MARC}/ > /dev/null
echo
echo "Restoring/Updating any missing MARC files from archive"
rsync -a --update ${LD4P_ARCHIVE_MARC}/*.mrc ${LD4P_MARC}/ > /dev/null

./run_marc_bin2xml_conversion.sh
