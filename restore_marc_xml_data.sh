# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

echo "Restoring missing MARC-XML from archives in ${LD4P_ARCHIVE_MARCXML}/"
rsync -a --update ${LD4P_ARCHIVE_MARCXML}/ ${LD4P_MARCXML}/

