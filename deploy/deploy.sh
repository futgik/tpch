#!/bin/bash

#check if environment already exist.
#environment consist of test scripts, generate .sql queries scripts and dbgen to generate data table.
function environmentNotExist(){
	FARRAY=(dbgen/dbgen dbgen/dists.dss tests/alter_user.sh tests/count.sh tests/load_hana.sh tests/power_test.sh tests/prepare_queries.sh tests/q1.sh tests/q2.sh tests/q3.sh tests/q4.sh tests/q5.sh tests/q6.sh tests/q7.sh tests/q8.sh tests/q9.sh tests/q10.sh tests/q11.sh tests/q12.sh tests/q13.sh tests/q14.sh tests/q15.sh tests/q16.sh tests/q17.sh tests/q18.sh tests/q19.sh tests/q20.sh tests/q21.sh tests/q22.sh tests/refresh.sh tests/refresh_gen.sh tests/rf1.sh tests/rf2.sh tests/streams_matrix.csv)

	CHECK=0
	for f in ${FARRAY[*]} ; do
		if ! [ -f $(pwd)/$f ] ; then 
			CHECK=1;
			break; 
		fi
	done;
	return $CHECK
}

# send files and set rights	
function sendFiles(){
	ArchiveName="deploy.zip"
	if ! test -f $ArchiveName ; then return 1; fi
	unzip -q -o $ArchiveName
	sudo chmod 755 tpch_2_17_0 tests/*.sh
	sudo chmod 644 tests/*.sql tests/*.csv
	sudo chmod 777 tests
	return 0
}

# compile dbgen
function compileDBgen(){
	cd tpch_2_17_0/dbgen/
	touch makefile.suite 
	make -f makefile.suite -s
	return 0
}

function changeDBGenDestination(){
	cd $1
	mkdir -p dbgen
	sudo chmod 777 dbgen
	cp tpch_2_17_0/dbgen/dbgen dbgen/
	cp tpch_2_17_0/dbgen/dists.dss dbgen/
	return 0
}

Dir=$(dirname $(readlink -f "$0"))
cd $Dir
if environmentNotExist;
then
	echo "0"
	exit
else
	if sendFiles == 0 && compileDBgen == 0 && changeDBGenDestination $Dir == 0;
	then
		echo "0"
		exit
	fi
fi
echo "1"
