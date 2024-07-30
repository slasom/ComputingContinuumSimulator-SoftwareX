# Usar Ubuntu como imagen base
FROM ubuntu:20.04

# Evitar que las preguntas se hagan durante la instalación de paquetes
ARG DEBIAN_FRONTEND=noninteractive

# Actualizar los paquetes y instalar las dependencias necesarias
RUN apt-get update && apt-get install -y \
    curl \
    iproute2 \
    net-tools \
    inetutils-ping \
    traceroute \
    iputils-tracepath \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Instalar Node.js
RUN apt-get update && apt-get install -y ca-certificates curl gnupg \
    && mkdir -p /etc/apt/keyrings \
    && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
    && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_20.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list \
    && apt-get update \
    && apt-get install nodejs -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN npm install apipecker -g