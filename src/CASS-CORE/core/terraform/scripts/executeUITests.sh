#! /bin/bash

############################## EXECUTE ANDROID APPLICATION WITH ADB ###############################

# This script starts the application on virtualized Android devices.

# $1: List of devices 
# $2: applicationId of the application that you want to run

echo "Executing executeUITests.sh"

echo "Execute UI Tests..."

# Convertir la lista de dispositivos en un array
IFS=',' read -r -a device_array <<< "$1"

for device in "${device_array[@]}"; do
    echo "Starting UI test on $device"
    
    # Conectar al dispositivo
    kathara exec -d ./lab "$device" -- adb connect localhost
    
    # Comprobar el tipo de Instrumentation Runner
    OUTPUT=$(kathara exec -d ./lab "$device" -- adb -s localhost shell pm list instrumentation 2>&1)
    
    if [[ $OUTPUT == *"androidx"* ]]; then
        nohup kathara exec -d ./lab "$device" -- adb -s localhost shell am instrument -w -r -e debug false "$2".test/androidx.test.runner.AndroidJUnitRunner > "./devices-logs/espresso/$device-UI-tests_result.txt" &
    else
        nohup kathara exec -d ./lab "$device" -- adb -s localhost shell am instrument -w -r -e debug false "$2".test/android.support.test.AndroidJUnitRunner > "./devices-logs/espresso/$device-UI-tests_result.txt" &
    fi
done

# Revisar los archivos de test
sleep 3
node ./scripts/checkUIResults.js

echo "Finish UI tests."
