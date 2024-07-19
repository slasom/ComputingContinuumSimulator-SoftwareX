#!/bin/bash

############################## APK MANAGER ###############################

# This script manages the installation of APKs in mobile devices from the architecture.

# $1: Number of mobile devices
# $2: Device names
# $3: APK Paths
# $4: APK Test Paths
# $5: Time to wait until APKs are installed

echo "Executing apkManager.sh, arguments provided:"
for arg in "$@"
do
    echo "$arg"
done

# Turn string list into array
IFS=',' read -r -a array_apks <<< "$3"
IFS=',' read -r -a array_apks_test <<< "$4"


# Mover los archivos APK y actualizar las rutas en los arrays
move_and_update_paths() {
    local -n arr=$1
    for i in "${!arr[@]}"; do
        mv "${arr[i]}" "/home/ubuntu/lab/shared/"
        arr[i]="/shared/${arr[i]##*/}"
    done
}

# Llamando a la función para ambos arrays
move_and_update_paths array_apks
move_and_update_paths array_apks_test

# Leer las instrucciones de instalación del archivo JSON
installation_instructions=$(cat ./files/mobile_app_installation.json | jq -c '.[]')

call_install_script() {
    local apk_path=$1
    local devices=$2

    # Llamada a installApk.sh
    /home/ubuntu/scripts/installApk.sh "$apk_path" "$devices"
}

sleep $5

# Iterar sobre todas las instrucciones del archivo JSON
echo "$installation_instructions" | while read -r instruction; do
    app_name=$(echo "$instruction" | jq -r '.app_name')
    application_id=$(echo "$instruction" | jq -r '.application_id')
    installation_destination=$(echo "$instruction" | jq -r '.installation_destination | join(",")')

    # Buscar la ruta del APK correspondiente al nombre de la aplicación
    for apk_path in "${array_apks[@]}" "${array_apks_test[@]}"; do
        echo "APK PATH: $apk_path"
        echo "APP NAME: $app_name"
        if [[ "${apk_path##*/}" == "$app_name.apk" || "${apk_path##*/}" == "$app_name-androidTest.apk" ]]; then
            if [ "$installation_destination" == "all" ]; then
                device_list="$2"
            else
                device_list="$installation_destination"
            fi

            # Llamar al script de instalación
            call_install_script "$apk_path" "$device_list"
        fi
    done
done

echo "Waiting 30 seconds before executing APKs..."
sleep 30

# Ejecutar las aplicaciones
echo "$installation_instructions" | while read -r instruction; do
    application_id=$(echo "$instruction" | jq -r '.application_id')
    installation_destination=$(echo "$instruction" | jq -r '.installation_destination | join(",")')

    if [ "$installation_destination" == "all" ]; then
        device_list="$2"
    else
        device_list="$installation_destination"
    fi

    # Llamar a executeApk.sh para ejecutar la aplicación
    if [ -n "$application_id" ]; then
        /home/ubuntu/scripts/executeApk.sh "$device_list" "$application_id"
    fi
done

echo "All APKs have been installed and executed."

time_to_wait_minutes="$6"

time_to_wait_seconds=$((time_to_wait_minutes * 60))

# Esperar el tiempo especificado
echo "Waiting for $time_to_wait_minutes minutes ($time_to_wait_seconds seconds) before finishing."
sleep $time_to_wait_seconds

echo "Finished idle wait"


