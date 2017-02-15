#!/bin/bash

if [ "$1" == '-h' -o "$1" == '--help' ]; then
    cat <<HERE
Usage:
source ./ld4p_configure.sh # or custom config file
$0 > log/blazegraph_load.log 2>&1

 IMPORTANT:
    - this script depends on ./lib/blazegraph_load_api.sh
    - this script must be run on a system running blazegraph

 TODO: try to generalize this script so it can accept config
       params to setup the details in lib/blazegraph_load_api.sh 

       Until the script is generalized for any blazegraph target, the
       script assumes that the ./lib/blazegraph_load_api.sh script is
       properly configured for the target blazegraph instance and this
       script must be run on a system with blazegraph installed.

 TODO: investigate optimal ways to load the triples into blazegraph.

       At present, this script will iterate on the RDF files and submit them
       to blazegraph one-by-one.  There could be better ways to load all the files,
       depending on the performance of blazegraph.  Although it may not be the
       greatest performance, loading them one-by-one has worked.
HERE
    exit
fi


# This install script depends on prior configuration
if [ "$LD4P_MARCRDF" == "" ]; then
    echo "LD4P_MARCRDF path is undefined."
    echo "Please try again after 'source ld4p_configure.sh'"
    exit 1
fi


echo "Blazegraph loading MARC-RDF files ${LD4P_MARCRDF}/*.rdf into 'ld4p' graph."
find ${LD4P_MARCRDF} -type f -name '*.rdf' -exec ./blazegraph_load_api.sh {} ld4p \;

