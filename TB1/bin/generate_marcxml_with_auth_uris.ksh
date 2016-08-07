#!/bin/ksh
# process all files in the Marc_wURIs dir and send to /s/SUL/Dataload/LD4P/Marcxml - jkg
#
data=/s/SUL/Dataload/LD4P
TBdir=/s/SUL/Bin/LD4P/TB1
stamp=`date "+%Y%m%d%H%s"`

# Gather all the marc records and Make 1 file
for F in `find $data/Marc -type f`
do
  cat $F >> $data/marc.$stamp

  mv $F $data/Archive/Marc/.
done
  
/usr/bin/java -classpath $TBdir/lib/marc4j.jar:$TBdir/lib/ojdbc14.jar:$TBdir/classes/. MarcToXMLsf0 \
              $data/marc.$stamp > $data/Marcxml/stf.$stamp.xml 2>> $data/log/errors 

rm $data/marc.$stamp
