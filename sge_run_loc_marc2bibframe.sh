#!/bin/bash

# Name the job in Grid Engine
#$ -N LD4P_TB_XML2RDF

#tell grid engine to use current directory
#$ -cwd

# Set Email Address where notifications are to be sent
#$ -M dlweber@stanford.edu

# Tell Grid Engine to notify job owner if job 'b'egins, 'e'nds, 's'uspended is 'a'borted, or 'n'o mail
#$ -m a

# Tel Grid Engine to join normal output and error output into one file 
# -j n

## MAIN

source ./farmshare_config.sh

xml=$1
log=$(basename ${xml} | sed s/.xml/.log/)
export LD4P_MARCRDF_LOG="${LD4P_LOGS}/$log"

source ./loc_marc2bibframe.sh
loc_marc2bibframe ${xml}

