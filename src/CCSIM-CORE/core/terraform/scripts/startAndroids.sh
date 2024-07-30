#! /bin/bash

############################## STARTS ANDROID CONTAINERS ###############################

# This script starts the Docker containers with the virtualized Android devices.
# $1: Number of devices

echo "Executing startAndroids.sh"

echo "Starting devices..."

if [ $1 -gt 0 ]
then
	for i in $(seq 1 $1); do
		sudo docker start android-$i 
	done
else
	echo "Introduce the parameter, 1: Number of devices"
fi
