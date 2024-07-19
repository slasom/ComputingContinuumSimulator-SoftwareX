#! /bin/bash


# Despliega y crea la arquitectura al completo.
# $1: Architecture to be built

echo "Executing archBuilder.sh"

echo "Creating custom Pascal architecture..."
chmod 777 ./scripts/build_images.sh
sudo ./scripts/build_images.sh
python3 ./pascal/pascal_cli.py -i ./files/architecture.dadosim -r ./pascal/msimage.json --internet -o lab/
sudo cp -a apk/. lab/shared/