#! /bin/bash


# Crear la red virtual kathara 
# $1: Number of devices connected to network

mkdir -p ./lab

if [ $1 -gt 0 ]
then
	# Generate network files
	touch ./lab/lab.conf
	touch ./lab/lab.dep
	touch ./lab/router.startup
	for i in $(seq 1 $1); do
		# Generate device config files
		touch ./lab/device$i.startup
	done
else
	echo "Enter the parameters, 1. Number of devices "
fi



