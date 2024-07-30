"use strict"

const path = require('path');
const AWS = require('aws-sdk');
const DB = require('../DBOperations');
const fs = require('fs');

var jsonApiBody = {};

// S3 constants definition
const s3 = new AWS.S3({
  accessKeyId: "",  /* required  # Put your iam user key */
  secretAccessKey: "", /* required   # Put your iam user secret key */
  region: "", /* required   # Put your region name */
  Bucket: ""     /* required      # Put your bucket name */
});

// Uploads a file to Amazon S3
const uploadToS3 = (params) => {
  return new Promise((resolve, reject) => {
    s3.putObject(params, function (err, data) {
      if (err) {
        console.log(err)
      } else {
        resolve(data);
      }
    });
  })
}

// Returns data from a file
function returnData(path) {
  return new Promise((resolve, reject) => {
    fs.readFile(path, (err, data) => {
      if (err) {
        reject(err);
      } else {
        resolve(data);
      }
    });
  });
}

/**
 * Uploads the necessary files for Perses from GHA.
 * Uploads the necessary files for Perses from GHA.
 *
 * body UploadRequest Upload Request
 * project_name String Name of the project to execute
 * returns String
 **/
function uploadFiles(body, project_name, user_name, api_key) {
  return new Promise(async function (resolve, reject) {

    // Amazon S3 paths for each file
    const APK_PATH = `${user_name}/${project_name}/data`;
    const APK_TEST_PATH = `${user_name}/${project_name}/data`;
    const JSON_PATH = `${user_name}/${project_name}/data/json-config-new.json`;

    const dir = `./upload-data/${project_name}/`;

    // JSON posted to the DB when creating a new project
    const currentDate = new Date();

    let apkFiles = []; // To store regular .apk files
    let androidTestApkFiles = []; // To store -androidTest.apk files

    // READ APKs
    fs.readdir(dir, async (err, files) => {
      if (err) {
        console.error("Error reading the directory: ", err);
        return;
      }

      files.forEach(file => {
        if (path.extname(file) === '.apk') {
          if (file.endsWith('-androidTest.apk')) {
            androidTestApkFiles.push(file);
          } else {
            apkFiles.push(file);
          }
        }
      });

      console.log("Regular .apk files found:", apkFiles);
      console.log("-androidTest.apk files found:", androidTestApkFiles);

      // APK FILES
      for (const file of apkFiles) {
        console.log("Uploading APK:", file);
        try {
          const data = await returnData(dir + file);
          const input = {
            "Bucket": "", // YOUR BUCKET NAME HERE
            "Key": `${APK_PATH}/${file}`,
            "Body": data
          };
          await uploadToS3(input);
        } catch (err) {
          console.error('Error while reading file:', err);
        }
      }

      // APK TEST FILES
      for (const file of androidTestApkFiles) {
        console.log("Uploading APK Test:", file);
        try {
          const data = await returnData(dir + file);
          const input = {
            "Bucket": "", // YOUR BUCKET NAME HERE
            "Key": `${APK_TEST_PATH}/${file}`,
            "Body": data
          };
          await uploadToS3(input);
        } catch (err) {
          console.error('Error while reading file:', err);
        }
      }

      // JSON FILE //
      // (No need to read binary data, can be uploaded directly using createReadStream)
      var input = {
        "Bucket": "", // YOUR BUCKET NAME HERE
        "Key": JSON_PATH,
        "Body": fs.createReadStream(dir + "json-config-new.json")
      };
      console.log("Uploading JSON...");
      await uploadToS3(input);

      let apkFilesString = apkFiles.map(file => `${APK_PATH}/${file}`).join(', ');
      let androidTestApkFilesString = androidTestApkFiles.map(file => `${APK_TEST_PATH}/${file}`).join(', ');

      jsonApiBody = {
        "apk_file": apkFilesString,
        "apk_test_file": androidTestApkFilesString,
        "json_config": JSON_PATH,
        "user_name": user_name,
        "creation_date": "" + currentDate.getDate() + "-" + (Number(currentDate.getMonth()) + 1) + "-" + currentDate.getFullYear() + "-" + currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds() + ":" + currentDate.getMilliseconds(),
        "project_name": project_name
      }

      // POSTs the new project into the DB
      await DB.writeProjectToDatabaseAPI(project_name, jsonApiBody, user_name, api_key);

      fs.rmdirSync(dir, { recursive: true, force: true });

      var examples = {};
      examples['application/json'] = "Upload successful!";
      if (Object.keys(examples).length > 0) {
        resolve(examples[Object.keys(examples)[0]]);
      } else {
        resolve();
      }
    });
  });
}


module.exports = {
  s3,
  uploadFiles
}