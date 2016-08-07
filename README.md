
#Compile the java classes:
```
cd TB1
javac -cp lib/marc4j.jar:lib/ojdbc14.jar:classes/ classes/*.java
```

#Execute the pipeline
```
/s/SUL/Bin/LD4P/TB1/bin/do_pipeline.ksh
```
The various scripts called will
1) Look through the Ckeys folder and dump the ckeys in the files to Marc in the MArc folder
2) Gather up all the Marc files and send that file to the Marc4J converter, a step is added to look up authority keys, get the 92X URI and put it in the subfield 0 where the LOC BF1 converter will handle it.
3) Look for the Marcxml file and send it to the LOC BF1 converter

All files are moved to the Archive/.. folder for future reference.

#TODO
reconcile_and_store
  breaks up RDF into various entities
  POST RDF to several Fedora stores
