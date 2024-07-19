'use strict';


const s3 = require('./UploadService').s3;
const fs = require('fs');
const archiver = require('archiver');
const path = require('path');

// Auxiliary function to download a single file from the S3 bucket
const s3downloadSingle = function (params, localFilePath) {
  
  return new Promise((resolve, reject) => {
    // Create the file write stream and get the S3 object stream
    const fileStream = fs.createWriteStream(localFilePath);
    const s3Stream = s3.getObject(params).createReadStream();

    s3Stream.on('error', function (err) {
      reject(err);
    });

    fileStream.on('error', function (err) {
      reject(err);
    });

    fileStream.on('close', function () {
      console.log(`Successfully downloaded data from bucket and saved to ${localFilePath}`);
      resolve();
    });

    s3Stream.pipe(fileStream);
  });
};

// Download all the files from the execution folder inside the S3 bucket
const s3download = function (params, localFolderPath) {
  
  return new Promise((resolve, reject) => {
    console.log("Inicio de descarga del S3");
    // List objects inside the bucket with the folder prefix
    s3.listObjectsV2(params, function (err, data) {
      if (err) {
        console.error("Error al listar objetos:", err);
        reject(err);
      } else {
        console.log("No hay error al listar objetos");
        console.log(JSON.stringify(data) + "\n");

        // Iterate over the object list and download each file
        const promises = data.Contents.map(async (obj) => {
          
          console.log("Descargando objeto:", obj.Key);
          // Get the file name from the object key
          const fileKey = obj.Key;
          // Create the local file path to save the file
          const localFilePath = `${localFolderPath}/${path.basename(fileKey)}`;
          
          // Call the function which downloads each file
          await s3downloadSingle({ Bucket: params.Bucket, Key: fileKey }, localFilePath)
            .then(() => {
              console.log("Descarga exitosa:", localFilePath);
            })
            .catch((downloadErr) => {
              console.error("Error durante la descarga:", downloadErr);
              throw downloadErr; // Propaga el error para que Promise.all lo capture
            });
        });

        // Wait for all the downloads to complete
        Promise.all(promises)
          .then(() => {
            console.log('Descarga completa');
            resolve();
          })
          .catch((err) => {
            console.error("Error durante la descarga de algunos objetos:", err);
            reject(err);
          });
      }
    });
  });
};



/**
 * Download all the files related to a Perses Execution.
 *
 * execution_name String Name of the execution to download from Perses
 * no response value expected for this operation
 **/
exports.downloadFilesFromExecution = function (project_name, execution_name, user_name) {
  
  return new Promise(async (resolve, reject) => {
    try {
      
      // Replace the ":" character in the execution name to avoid problems with the file system
      var replaced_execution_name = execution_name.replace(/:/g, "-");
      // Amazon S3 path for the execution data
      const EXEC_PATH = `${user_name}/${project_name}/${replaced_execution_name}/logs/`;
      // Local directory where the files will be downloaded
      const dir = `/home/ubuntu/projectmanager/download-data/${replaced_execution_name}`;

      const params = {
        Bucket: '', // YOUR BUCKET NAME HERE
        Prefix: EXEC_PATH
      };

      // Download all the files from the execution folder inside the S3 bucket
      await s3download(params, dir);

      console.log('Descarga completa de la carpeta');
      // Wait for the compression to complete and receive the zip file destination
      const zipFileDestination = await zipFilesToDownload(dir);
      console.log("Zip file: " + zipFileDestination);

      // REsolve the promise with the zip file destination
      resolve(zipFileDestination);

    } catch (err) {
      console.error('Error:', err);

      // Reject the promise with error
      reject(err);
    }
  });
};


// Compress all the files inside a directory into a ZIP file and returns the ZIP file destination
function zipFilesToDownload(dir) {
  
  return new Promise((resolve, reject) => {
    // Create the ZIP file destination path
    const zipFileDestination = '/home/ubuntu/projectmanager/download-data/perses-data.zip';

    // Create the ZIP file and the output stream
    const arch = archiver('zip', { zlib: { level: 9 } });
    const output = fs.createWriteStream(zipFileDestination);

    // When the compression is finished, returns the ZIP file destination
    output.on('close', () => {
      console.log('¡Compresión completada con éxito!');
      resolve(zipFileDestination); // Resuelve la promesa con la ruta del archivo ZIP
    });

    // Maneja errores durante la compresión
    arch.on('error', (err) => {
      console.error('Error durante la compresión:', err);
      reject(err); // Rechaza la promesa con el error
    });

    // Start the compression and add the directory to compress
    arch.pipe(output);
    arch.directory(dir, false);

    // End the compression
    arch.finalize();

  });
}
