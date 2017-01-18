#!/bin/ksh
# Run the LOC M2B converter here with STDOUT to RDF dir - jkg
#
data=/s/SUL/Dataload/LD4P
m2b=/s/SUL/Bin/Marc2Bibframe/marc2bibframe

for F in `find $data/Marcxml -type f`
do
  # convert to bibframe
  # java -cp /path/to/saxon9he.jar net.sf.saxon.Query saxon.xqy \
  # marcxmluri=/path/to/marc/xml/file baseuri=http://my-base-uri/ serialization=rdfxml

  /usr/bin/java -classpath lib/saxon9he.jar net.sf.saxon.Query $m2b/xbin/saxon.xqy \
                marcxmluri=$F baseuri="http://linked-data-test.stanford.edu/library/" \
                serialization="rdfxml" 1>$data/RDF/stf.`date "+%Y%m%d%H%s"`.rdf 2>> $data/log/errors

  echo "NO M2B CONVERSION ERRORS in $F.." >> $data/log/errors

  mv $F $data/Archive/Marcxml/.
done
