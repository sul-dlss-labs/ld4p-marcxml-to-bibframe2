#!/bin/ksh
# Run the LOC M2B converter here with STDOUT to RDF dir - jkg
#
data=/s/SUL/Dataload/LD4P
m2b=/s/SUL/Bin/Marc2Bibframe/marc2bibframe
jvm_args="-Xms3G -Xmx6G"

for F in `find $data/Marcxml -type f`
do
  # convert to bibframe
  /usr/bin/java $jvm_args -classpath $m2b/lib/saxon9he.jar net.sf.saxon.Query $m2b/xbin/saxon.xqy \
                marcxmluri="$data/Marcxml/$F" baseuri="http://linked-data-test.stanford.edu/library/" \
                serialization="json" true 1>$data/RDF/stf.`date "+%Y%m%d%H%s"`.rdf 2>> ../log/errors

  mv $F $data/Marcxml/Archive/.
done
