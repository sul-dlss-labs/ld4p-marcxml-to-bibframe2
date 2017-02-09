# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

echo
echo "Restoring/Updating any missing MARC-XML files from archive"
rsync -a --update ${LD4P_ARCHIVE_MARCXML}/ ${LD4P_MARCXML}/ > /dev/null

./run_marc_xml2rdf_conversion.sh
