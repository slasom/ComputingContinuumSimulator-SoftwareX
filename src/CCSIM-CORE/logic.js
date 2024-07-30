#!/usr/bin/env node
"use strict";

const fs = require('fs');
const path = require('path');

function getAndroidHosts(data) {
    const androidHosts = data.hosts.filter(host => host.deployed.includes('android'));
    return androidHosts.map(host => host.id);
}

async function writeToFile(fileName, data) {
    try {
        await fs.promises.writeFile(path.join(__dirname, fileName), data);
        console.log(`Data written to file ${fileName}`);
    } catch (error) {
        console.error('Error writing to file:', error);
    }
}

async function loadArchitecture(architecture) {
    let configData;
    let dir = "./architectures/"

    if (typeof architecture === 'string') {
        // Si 'architecture' es una cadena, carga el archivo JSON correspondiente
        let fileName;
        if (architecture === 'mobile_default') {
            fileName = dir + 'mobile_default.json';
        } else if (architecture === 'simple_network_default') {
            fileName = dir + 'simple_network_default.json';
        }

        if (fileName) {
            const filePath = path.join(__dirname, fileName); // Asegúrate de que la ruta sea correcta
            configData = JSON.parse(fs.readFileSync(filePath, 'utf8'));
        } else {
            throw new Error('JSON not recognised or not found');
        }
    } else if (typeof architecture === 'object' && architecture !== null) {
        // Si 'architecture' es un objeto (diccionario), úsalo directamente
        configData = architecture;
    } else {
        throw new Error('Architecture value not valid');
    }

    return configData;
}

function createAppInstallationDictionary(jsonData) {
    let installationDict = {};

    // Verificar si jsonData.mobile_app_instalation existe y es un array
        jsonData.mobile_app_installation.forEach(app => {
            // Usar application_id como clave y los dispositivos como valor en formato string
            if (app.application_id) {
                installationDict[app.application_id] = app.installation_destination.join(',');
            }
        });

    return installationDict;
}

function calculateTimeWait(numDevices) {
    // Asumiendo que 60 dispositivos requieren 25 minutos como el valor más alto
    const maxDevices = 60;
    const maxMinutes = 25;

    // Calcular la proporción
    let minutes = Math.round((numDevices / maxDevices) * maxMinutes);

    // Asegurarse de que al menos se devuelva el tiempo mínimo
    minutes = Math.max(minutes, 3);

    return `${minutes}m`;
}


module.exports = {
    getAndroidHosts,
    loadArchitecture,
    calculateTimeWait,
    writeToFile,
    createAppInstallationDictionary
}