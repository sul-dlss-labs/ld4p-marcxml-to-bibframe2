#!/bin/ksh
# process all files in the Marc dir and use Marc4J and SQL to look up authority keys
# get the 92X URI and put it in the subfield 0. Finally send to /s/SUL/Dataload/LD4P/Marcxml - jkg
#
data=/s/SUL/Dataload/LD4P
TBdir=/s/SUL/Bin/LD4P/TB1
stamp=`date "+%Y%m%d%H%s"`

# Gather all the dumped marc records and make 1 temp file, then clean up.
for F in `find $data/Marc -type f`
do
  cat $F >> $data/marc.$stamp

  mv $F $data/Archive/Marc/.
done

# At present marc.$stamp is assumed to be one large file of marc records
#
/usr/bin/java -classpath $TBdir/lib/marc4j.jar:$TBdir/lib/ojdbc14.jar:$TBdir/classes/. MarcToXMLsf0 \
              $data/marc.$stamp > $data/Marcxml/stf.$stamp.xml 2>> $data/log/errors

[[ $? == 0 ]] && rm $data/marc.$stamp
