#!/bin/bash

# Nombre del archivo para la salida
output_file="estado_red.txt"

echo "-------- INICIO DEL SCRIPT --------" > $output_file

# Función para obtener información de la red
get_network_info() {
    iteration=$1
    echo "-------- Iteración $iteration --------" >> $output_file
    echo "-------- Información de red --------" >> $output_file
    echo "#########################################################" >> $output_file
    echo "Fecha y hora: $(date)" >> $output_file
    echo "Estado de las interfaces de red:" >> $output_file
    ip addr show >> $output_file
    echo "#########################################################" >> $output_file
    echo "Tabla de enrutamiento:" >> $output_file
    ip route show >> $output_file
    echo "#########################################################" >> $output_file
    echo "Estadísticas de conexión:" >> $output_file
    netstat -s >> $output_file
    echo "#########################################################" >> $output_file
    echo "Conexiones activas:" >> $output_file
    netstat -tuln >> $output_file
    echo "#########################################################" >> $output_file
    echo "ping hacia google:" >> $output_file
    ping -c 5 8.8.8.8 >> $output_file
    echo "#########################################################" >> $output_file
    echo "Ruta hacia google:" >> $output_file
    traceroute 8.8.8.8 >> $output_file
    echo "------------------------------------" >> $output_file
}

# Función para manejar la señal SIGTERM y finalizar el script
exit_script() {
    echo "¡Señal recibida! Saliendo del script..."
    exit 0
}

# Capturar la señal SIGTERM y ejecutar la función exit_script
trap exit_script SIGTERM

# Ejecutar la función cada 10 segundos hasta un máximo de 10 veces
for ((i=1; i<=5; i++)); do
    get_network_info $i
    sleep 10
done

echo "-------- FIN DEL SCRIPT --------" >> $output_file

# Al final de netscript.sh
while true; do 
    sleep 60 
done
