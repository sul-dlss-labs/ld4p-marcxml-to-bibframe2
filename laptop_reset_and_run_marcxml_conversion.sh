# vim: autoindent tabstop=4 shiftwidth=4 expandtab softtabstop=4 filetype=sh
#!/bin/bash

# To replace exisiting MARC-XML files, run this script with
#LD4P_MARCXML_REPLACE=true


export LD4P_SIRSI=/data/src/dlss/ld4l/ld4p_data
export LD4P_RDF=/data/src/dlss/ld4l/ld4p_rdf
source ./ld4p_configure.sh
source ./generate_marcxml_with_auth_uris.sh

export LD4P_CONFIGS=${LD4P_SIRSI}/configs

if [ ! -d ${LD4P_CONFIGS} ]; then
	git clone git@github.com:sul-dlss/shared_configs.git ${LD4P_CONFIGS}
	pushd  ${LD4P_CONFIGS}
	git checkout ld4p-tracer-bullets
	popd
fi

if [ ! -f ${LD4P_BIN}/ld4p_converter.jar ]; then
	SRC_JAR_FILE="../ld4p-tracer-bullets/conversiontracerbullet/target/conversion-tracer-bullet-jar-with-dependencies.jar"
	if [ ! -f ${SRC_JAR_FILE} ]; then
		echo "Build the  ld4p-tracer-bullets project first"
		echo "Expected to find: ${SRC_JAR_FILE}"
		exit 1
	fi
	cp ${SRC_JAR_FILE} ${LD4P_BIN}/ld4p_converter.jar
fi

echo
echo "Cleanup archived data and log files"
rm -f ${LD4P_DATA}/Archive/Marc/*.mrc
rm -f ${LD4P_DATA}/log/*.log

echo
echo "Copying private fixture data"
cp -vp ${LD4P_CONFIGS}/files/*.mrc ${LD4P_MARC}/


# Find and convert any dumped marc record files; archive processed files.
echo
echo "Searching for MARC files: ${LD4P_MARC}"
for MRC_FILE in `find ${LD4P_MARC} -type f | sort`
do
    generate_marcxml_with_auth_uris $MRC_FILE


    exit  # Just process one file while testing things out.
done

