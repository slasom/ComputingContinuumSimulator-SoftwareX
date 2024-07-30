FROM ubuntu:latest

# Instala las dependencias
RUN apt-get update && apt-get install -y \
    iproute2 \
    net-tools \
    inetutils-ping \
    traceroute \
    iputils-tracepath

# Copia el script al contenedor
COPY netscript.sh /netscript.sh

# Otorga permisos de ejecución al script
RUN chmod +x /netscript.sh

# Configura el contenedor para ejecutar el script
CMD sleep 15 && /netscript.sh
