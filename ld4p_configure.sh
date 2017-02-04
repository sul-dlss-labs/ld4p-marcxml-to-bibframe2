#!/bin/bash
# Configure the LD4P-tracer-bullet scripts
#
# This configuration script is designed to be used like so:
# source ./ld4p_configure.sh
#
# If any custom LD4P_* paths are required, they can be set in the
# system ENV or on the command line, like so:
# LD4P_SIRSI=/ld4p_data LD4P_RDF=/ld4p_rdf source /path/to/ld4p_configure.sh

SCRIPT_PATH=$(dirname $0)

export LD4P_BASEURI="http://linked-data-test.stanford.edu/library/"

# If the system already defines an LD4P_SIRSI path, it will be used.
# If a custom LD4P_SIRSI path is required, it can be set in the
# system ENV or on the command line, like so:
# LD4P_SIRSI=/ld4p_data source /path/to/ld4p_configure.sh
if [ "$LD4P_SIRSI" == "" ]; then
    export LD4P_SIRSI=/symphony
fi
if [ ! -d "$LD4P_SIRSI" ]; then
    echo "ERROR: The LD4P scripts require an LD4P_SIRSI path: ${LD4P_SIRSI}" 1>&2
    kill -INT $$
else
    echo "LD4P_SIRSI path: $LD4P_SIRSI"
fi

# If the system already defines an LD4P_RDF path, it will be used.
# If a custom LD4P_RDF path is required, it can be set in the
# system ENV or on the command line, like so:
# LD4P_RDF=/ld4p_data source /path/to/ld4p_configure.sh
if [ "$LD4P_RDF" == "" ]; then
    export LD4P_RDF=/rdf
fi
if [ ! -d "$LD4P_RDF" ]; then
    echo "ERROR: The LD4P scripts require an LD4P_RDF path: ${LD4P_RDF}" 1>&2
    kill -INT $$
else
    echo "LD4P_RDF path:   $LD4P_RDF"
fi

export LD4P_BIN="${LD4P_SIRSI}/bin"
mkdir -p ${LD4P_BIN} || kill -INT $$

export LD4P_DATA="${LD4P_SIRSI}/Dataload/LD4P"
export LD4P_MARC="${LD4P_DATA}/Marc"
export LD4P_MARCXML="${LD4P_DATA}/MarcXML"
export LD4P_MARCRDF="${LD4P_RDF}/MarcRDF"
export LD4P_LOGS="${LD4P_DATA}/log"
export LD4P_CONFIGS="${LD4P_SIRSI}/configs"

export LD4P_ARCHIVE_MARC="${LD4P_DATA}/Archive/Marc"
export LD4P_ARCHIVE_MARCXML="${LD4P_DATA}/Archive/MarcXML"
export LD4P_ARCHIVE_MARCRDF="${LD4P_DATA}/Archive/MarcRDF"

mkdir -p ${LD4P_DATA} || kill -INT $$
mkdir -p ${LD4P_MARC} || kill -INT $$
mkdir -p ${LD4P_MARCXML} || kill -INT $$
mkdir -p ${LD4P_MARCRDF} || kill -INT $$
mkdir -p ${LD4P_ARCHIVE_MARC} || kill -INT $$
mkdir -p ${LD4P_ARCHIVE_MARCXML} || kill -INT $$
mkdir -p ${LD4P_ARCHIVE_MARCRDF} || kill -INT $$
mkdir -p ${LD4P_LOGS} || kill -INT $$
mkdir -p ${LD4P_CONFIGS} || kill -INT $$

# Check and install required libraries
source ${SCRIPT_PATH}/ld4p_install_libraries.sh

# Source bash functions to run converters
source ${SCRIPT_PATH}/generate_marcxml_with_auth_uris.sh
source ${SCRIPT_PATH}/loc_marc2bibframe.sh

