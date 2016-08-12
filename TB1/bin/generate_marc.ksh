#!/bin/ksh
# call catalogdump and output to Marc dir using the  (-z) - jkg

data=/s/SUL/Dataload/LD4P

#Uses "-z" flag to output authority keys into the marc file
for F in `find $data/Ckeys -type f`
do
  echo "catalogdumping $F"
  echo ""

  cat $F | catalogdump -om -kc -z 2>/dev/null > $data/Marc/stf.`date "+%Y%m%d%H%s"`.mrc 2>> $data/log/errors
  
  mv $F $data/Archive/Ckeys/.
done
