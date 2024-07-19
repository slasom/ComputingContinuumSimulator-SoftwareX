#!/usr/bin/env node

"use strict";
const logger = require("./logger"); // This is for logging operations
const path = require("path"); // This is for path operations
const DB = require("./DBOperations"); // This is for database operations
const fs = require("fs-extra"); // This is for file system operations

const AWS = require("aws-sdk"); // You must previously use: npm install aws-sdk

// This is for creating the S3 object
const s3 = new AWS.S3({
    accessKeyId: "" /*  YOUR IAM USER KEY HERE */,
    secretAccessKey:
        "" /* YOUR IAM USER SECRET KEY HERE */,
    region: "" /* YOUR AWS REGION NAME HERE */,
    Bucket: "" /* YOUR AWS BUCKET NAME HERE */,
});

// This is for obtaining text files (txt, log...) from S3 bucket
const obtainFromS3 = (params) => {
    return new Promise((resolve, reject) => {
        s3.getObject(params, function (err, data) {
            if (err) {
                console.log(err, err.stack);
                reject(err);
            } else {
                //console.log(data.Body.toString()); // this is the file content
                console.log("--- Downloaded " + params.Key);
                resolve(data.Body.toString());
            }
        });
    });
};

// This is for obtaining binary files (APK) from S3 bucket
const obtainBinaryFromS3 = (params) => {
    return new Promise((resolve, reject) => {
        s3.getObject(params, function (err, data) {
            if (err) {
                console.log(err, err.stack);
                reject(err);
            } else {
                console.log("--- Downloaded " + params.Key);
                resolve(data.Body);
            }
        });
    });
};

// This is for uploading files to S3 bucket
const uploadToS3 = (params) => {
    return new Promise((resolve, reject) => {
        s3.putObject(params, function (err, data) {
            if (err) {
                console.log(err);
                reject(err);
            } else {
                resolve(data);
            }
        });
    });
};

// This is for removing files from S3 bucket
const removeFromS3 = (params) => {
    return new Promise((resolve, reject) => {
        s3.deleteObject(params, function (err, data) {
            if (err) {
                console.log(err);
                reject(err);
            } else {
                resolve(data);
            }
        });
    });
};

/*  
    The object that will be used to obtain the files from S3 bucket
    The Key will be modified in each request
    The Bucket is the same for all requests
    The Body is the content of the file and it is created when neccessary
*/
var s3Object = {
    Bucket: "", // YOUR AWS BUCKET NAME HERE
    Key: "",
};

// Uploads all the project logs to an S3 bucket using aux function
async function uploadLogsToS3(projectName) {
    await logger.writeToLogFile(projectName, "Uploading logs to S3...");
    var user_name = projectName.split("-")[0]; // Get the user name from the project name

    // Get the directory path where the logs are stored
    const directoryPath = path.join(__dirname, "projects", projectName, "logs");
    // Get the execution name from the database and replace the ":" with "-"
    const execution_name = DB.executionJSON["execution_name"].replace(/:/g, "-");

    // Create the route where the logs will be stored in the S3 bucket
    var route = `${user_name}/${projectName}/${execution_name}/logs`;

    // Call the function to start the upload from the root directory
    await uploadDirectoryToS3(directoryPath, route)
        .then(async () => {
            console.log("Logs successfully uploaded to S3.");
            DB.executionJSON["devices_logs"] = route;
            await DB.putExecutionToDatabaseAPI(
                DB.executionJSON["execution_name"],
                DB.executionJSON
            );
        })
        .catch((err) => {
            console.error("Error - Could not upload logs to S3.", err);
        });

    // Upload the local log file and the files generated redirecting the streams
    await uploadLocalLogsToS3(projectName, route);
}

/*
  This function uploads all the directory files to an S3 bucket
  If there are directories in the directory, it upload all the files recursively
*/
async function uploadDirectoryToS3(directoryPath, s3Prefix) {
    const files = await fs.promises.readdir(directoryPath);

    for (const file of files) {
        const filePath = path.join(directoryPath, file);
        const s3Key = s3Prefix + "/" + file;

        const stats = await fs.promises.stat(filePath);

        if (stats.isDirectory()) {
            // If the file is a directory, call the function recursively
            await uploadDirectoryToS3(filePath, s3Key);
        } else {
            // If it is not a directory, upload the file to S3
            const params = {
                Bucket: s3Object.Bucket,
                Key: s3Key,
                Body: fs.readFileSync(filePath),
            };

            try {
                const data = await uploadToS3(params);
                console.log("Archivo subido con éxito:", data.Location);
            } catch (err) {
                console.error("Error al subir el archivo", file + ":", err);
            }
        }
    }
}

/*
  This function uploads to the S3 bucket the local log file and the 
  files generated by redirecting the stdout and stderr streams
*/
async function uploadLocalLogsToS3(projectName, route) {
    var filePath = path.join(__dirname, "test", projectName, "perses_stdout.txt");

    var params = {
        Bucket: s3Object.Bucket,
        Key: route,
        Body: fs.readFileSync(filePath),
    };

    try {
        params.Key = route + "/perses_stdout.txt";
        const data = await uploadToS3(params);
        console.log("Archivo subido con éxito:", data.Location);
        await logger.writeToLogFile(projectName, "stdout logs uploaded to S3...");
    } catch (err) {
        console.error("Error al subir el archivo perses_stdout.txt:", err);
        await logger.writeToLogFile(projectName, "Error uploading logs to S3...");
    }

    params.Body = fs.readFileSync(
        path.join(__dirname, "test", projectName, "perses_stderr.txt")
    );
    try {
        params.Key = route + "/perses_stderr.txt";
        const data = await uploadToS3(params);
        console.log("Archivo subido con éxito:", data.Location);
        await logger.writeToLogFile(projectName, "stderr logs uploaded to S3...");
    } catch (err) {
        console.error("Error al subir el archivo perses_stderr.txt:", err);
        await logger.writeToLogFile(projectName, "Error uploading logs to S3...");
    }

    params.Body = fs.readFileSync(
        path.join(__dirname, "test", projectName, "localLogs.txt")
    );
    try {
        params.Key = route + "/localLogs.txt";
        const data = await uploadToS3(params);
        console.log("Archivo subido con éxito:", data.Location);
        await logger.writeToLogFile(projectName, "localLogs uploaded to S3...");
    } catch (err) {
        console.error("Error al subir el archivo localLogs.txt:", err);
        await logger.writeToLogFile(projectName, "Error uploading logs to S3...");
    }
}

// Downloads JSON config file, APK file and APK test file from an S3 bucket
async function downloadSetupFilesAndReturnJSONConfig(
    projectName,
    responseJSONFromDatabase
) {
    // Get the JSON file from the S3 bucket with the project configuration
    s3Object.Key = responseJSONFromDatabase["json_config"];
    const configFile = await obtainFromS3(s3Object);

    // Write the config file in the project folder inside the test folder
    fs.writeFileSync(
        path.join(__dirname, "test", projectName, "json-config.json"),
        configFile,
        "utf8",
        function (err) {
            if (err) throw err;
            console.log("JSON config saved!");
        }
    );

    const apkFiles = responseJSONFromDatabase["apk_file"].split(', ');
    const apkTestFiles = responseJSONFromDatabase["apk_test_file"].split(', ');

    // Procesar cada fichero APK
    for (const apkFile of apkFiles) {
        s3Object.Key = apkFile;
        const apkData = await obtainBinaryFromS3(s3Object);

        // Escribir el fichero APK en la carpeta del proyecto
        const apkFilePath = path.join(__dirname, "test", projectName, path.basename(apkFile));
        fs.writeFileSync(apkFilePath, apkData, "utf8", function(err) {
            if (err) throw err;
            console.log(`APK ${apkFile} saved!`);
        });
    }

    // Procesar cada fichero APK de test
    for (const apkTestFile of apkTestFiles) {
        s3Object.Key = apkTestFile;
        const apkTestData = await obtainBinaryFromS3(s3Object);

        // Escribir el fichero APK de test en la carpeta del proyecto
        const apkTestFilePath = path.join(__dirname, "test", projectName, path.basename(apkTestFile));
        fs.writeFileSync(apkTestFilePath, apkTestData, "utf8", function(err) {
            if (err) throw err;
            console.log(`APK test ${apkTestFile} saved!`);
        });
    }
    
}

// Export the functions and the object
module.exports = {
    obtainFromS3,
    obtainBinaryFromS3,
    uploadToS3,
    removeFromS3,
    uploadLogsToS3,
    uploadDirectoryToS3,
    uploadLocalLogsToS3,
    downloadSetupFilesAndReturnJSONConfig,
    s3Object,
};
