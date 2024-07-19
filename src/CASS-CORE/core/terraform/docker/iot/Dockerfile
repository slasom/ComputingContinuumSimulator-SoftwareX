# Usa una imagen base de Alpine Linux
FROM alpine:latest

# Instala las herramientas necesarias
RUN apk add --no-cache \
    curl \
    iproute2 \
    net-tools \
    busybox-extras \
    traceroute \
    iputils

# Comando por defecto cuando se ejecute el contenedor
CMD ["sh"]
