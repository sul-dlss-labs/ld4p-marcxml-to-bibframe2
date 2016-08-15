#!/bin/ksh
# Run some sels to get some ckeys for conversion to RDF
# 08/15/2016 dlr 

# start with latest returned Backstage file
# get records with "it" as title control
# put file into Ckeys directory (currently  /s/SUL/Dataload/LD4P/Ckeys)

# Currently only getting Casalini records
# and currently getting them AFTER Backstage processing, will be able to include URIs in $0
# All this will surely change later....

. /s/SUL/Config/sirsi.env

FILEDIR=/s/SUL/Dataload/Oracle/Package_report
OUTDIR=/s/SUL/Dataload/LD4P/Ckeys
#OUTDIR=/s/SUL/Bin/LD4P/TB1/bin
searchstring="BSTAGE_BIB*keys"

myfile0=`ls -t $FILEDIR/$searchstring | head -1`
myfile=`basename $myfile0`

#echo $myfile

cat $FILEDIR/$myfile | selcatalog -iC -oCF 2>/dev/null |\
  grep "|it" | cut -f1 -d"|" > $OUTDIR/$myfile.ckeys
