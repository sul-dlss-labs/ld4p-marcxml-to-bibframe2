#!/bin/ksh
# process all files in the Marc_wURIs dir and send to /s/SUL/Dataload/LD4P/Marcxml - jkg
#
data=/s/SUL/Dataload/LD4P

for F in `ls $data/Marc` do
  /usr/bin/java -classpath ./lib:./lib/marc4j.jar:./lib/ojdbc14.jar ./classes/MarcToXMLsf0 $F \
                > $data/Marcxml/stf.`date "+%Y%m%d%H%s"`.xml 2>> ./log/errors

  mv $F $data/Marc/Archive
done
