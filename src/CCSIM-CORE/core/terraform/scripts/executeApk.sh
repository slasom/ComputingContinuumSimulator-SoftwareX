#!/bin/bash

############################## EXECUTE ANDROID APPLICATION WITH ADB ###############################

# $1: List of devices (comma-separated)
# $2: applicationId of the application that you want to run

echo "Executing executeApk.sh"

DEVICES=$1
APPLICATION_ID=$2

echo "Prepare to execute apk in devices..."

# Convertir la lista de dispositivos en un array
IFS=',' read -r -a device_array <<< "$DEVICES"

# Verificar si los parámetros necesarios están presentes
if [ -z "$DEVICES" ] || [ -z "$APPLICATION_ID" ]; then
    echo "Please provide the list of devices and the applicationId."
    echo "Usage: $0 device_list applicationId"
    exit 1
fi

sleep 10

# Ejecutar la aplicación en cada dispositivo
for device in "${device_array[@]}"; do
    echo "Starting apk on $device..."

    # Conectar al dispositivo
    kathara exec -d ./lab "$device" -- adb connect localhost

    # Ejecutar la aplicación
    kathara exec -d ./lab "$device" -- adb -s localhost shell monkey -p "$APPLICATION_ID" -c android.intent.category.LAUNCHER 1

    echo "Executed apk on $device"
done

echo "Finished executing on devices."
