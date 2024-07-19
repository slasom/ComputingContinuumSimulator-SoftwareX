#! /bin/bash

############################## EXTRACT LOGS FROM ANDROID DEVICES WITH ADB ###############################

# This script extracts the logs from the virtualized Android devices.

# $1: String with device names, separated by commas.
# $2: 'applicationId' of the application that you want to extract logs from.

echo "Executing getLogs.sh"
echo "Get Logs..."

# Verificar si los parámetros son correctos
if [ -n "$1" ] && [ -n "$2" ];
then
    IFS=',' read -ra ADDR <<< "$1" # Separar el string por comas y leer en un array
    for device in "${ADDR[@]}"; do # Iterar sobre cada dispositivo
        echo "Obtaining Logs from $device"

        kathara exec -d ./lab $device -- adb connect localhost
        kathara exec -d ./lab $device -- adb -s localhost logcat $2:D -d > ./devices-logs/log-$device.txt 
        
        # Nota: Comentado código anterior que usaba IP
        # adb connect 172.17.0.$var
        # adb -s 172.17.0.$var logcat $2:D -d > ./devices-logs/log-android$i.txt 

        echo "Logs obtained correctly from $device"
    done

    echo "Logs extracted correctly!"
else
    echo "Enter the parameters, 1. Device names (comma-separated) 2. applicationId"
fi
