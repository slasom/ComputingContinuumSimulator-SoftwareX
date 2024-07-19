'use strict';

var utils = require('../utils/writer.js');
var Upload = require('../service/UploadService.js');
const security = require('../security.js');
const fs = require('fs');

module.exports.uploadFiles = async function uploadFiles(req, res, next, body, project_name) {
  const dir = `./upload-data/${project_name}/`;

  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }

  const username = req.headers["user_name"];
  const api_key = req.headers["api_key"];

  // Ensure that both username and api_key are defined
  if (!username || !api_key) {
    return res.status(401).send("Username and API key are required");
  }

  try {
    // Authenticate and proceed if successful
    const authResult = await security.authenticateAPIKey(api_key, username);
    if (authResult) {
      console.log("Authenticated!");

      for (let i = 0; i < req.files.length; i++) {
        fs.writeFileSync(dir + req.files[i].originalname, req.files[i].buffer, (err) => {
          if (err) {
            console.error(err);
          } else {
            console.log("File saved!");
          }
        })
      }

      fs.readFile(dir + "json-config-new.json", "utf8", async (err, jsonString) => {
        if (err) {
          console.log("File read failed:", err);
          utils.writeJson(res, "Error: Request JSON could not be read", 400);
          return;
        }
        //const username = JSON.parse(jsonString)["user_name"]
        console.log(`USERNAME: ${username} || API KEY: ${req.headers["api_key"]}`)

        Upload.uploadFiles(body, project_name, username, api_key)
          .then(function (response) {
            utils.writeJson(res, response);
          })
          .catch(function (response) {
            utils.writeJson(res, response);
          });
      });
    } else {
      res.status(401).send("Authentication failed");
    }
  } catch (error) {
    console.error("Authentication error:", error);
    res.status(500).send("Internal server error during authentication");
  }


};