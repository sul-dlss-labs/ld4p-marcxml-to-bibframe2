#!/bin/bash

echo "Restoring private MARC files from shared configs files"
rsync -a --update ${LD4P_CONFIGS}/files/ ${LD4P_MARC}/

echo "Restoring missing MARC files from archives in ${LD4P_ARCHIVE_MARC}/*.mrc"
rsync -a --update ${LD4P_ARCHIVE_MARC}/ ${LD4P_MARC}/

