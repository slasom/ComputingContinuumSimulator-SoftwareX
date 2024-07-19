#!/bin/bash

############################## INSTALL APK WITH ADB ###############################

# $1: APK to install
# $2: Devices in which to install it (comma-separated)

echo "Executing installApk.sh"

APK=$1
DEVICES=$2

# Convertir la lista de dispositivos en un array
IFS=',' read -r -a device_array <<< "$DEVICES"

# Verificar si se ha proporcionado un APK
if [ -z "$APK" ]; then
    echo "No APK file specified."
    exit 1
fi

# Instalar el APK en cada dispositivo
for device in "${device_array[@]}"; do
    echo "Installing APK $APK on $device..."

    # Conectar al dispositivo
    kathara exec -d /home/ubuntu/lab "$device" -- adb connect localhost

    # Determinar si es un APK de test y elegir la opción adecuada
    if [[ $APK == *"-androidTest.apk" ]]; then
        echo "Installing test APK"
        kathara exec -d /home/ubuntu/lab "$device" -- adb -s localhost install -t "$APK"
    else
        kathara exec -d /home/ubuntu/lab "$device" -- adb -s localhost install -r "$APK"
    fi
done
