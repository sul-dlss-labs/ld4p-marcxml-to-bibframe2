#!/bin/ksh
# call catalogdump and output to Marc dir using the  (-z) - jkg

data=/s/SUL/Dataload/LD4P

#Uses "-z" flag to output authority keys into the marc file
for F in `ls $data/Ckeys`
do
  find . -name $F -exec cat {} \; | catalogdump -om -kc -z 2>/dev/null > $data/Marc/stf.`date "+%Y%m%d%H%s"`.mrc
  mv $F $data/Ckeys/Archive
done
