'use strict';

var utils = require('../utils/writer.js');
var Download = require('../service/DownloadService.js');
const security = require('../security.js');
const fs = require('fs');
require('stream');

module.exports.downloadFilesFromExecution = async function downloadFilesFromExecution (req, res, next, project_name, execution_name) {
  var replaced_execution_name = execution_name.replace(/:/g, "-");
  const dir = `/home/ubuntu/projectmanager/download-data/${replaced_execution_name}/`;

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

      const projectExecutionCheck = await security.checkProjectAndExecution(project_name, execution_name, username, api_key);
      if (projectExecutionCheck) {
        try {
          const response = await Download.downloadFilesFromExecution(project_name, execution_name, username);
          if (fs.existsSync(response)) {
            console.log(response);
            res.sendFile(response, function (err) {
              if (err) {
                res.status(err.status || 500).end();
              } else {
                console.log('File sent successfully');
                fs.unlinkSync(response);
                fs.rmdirSync(dir, { recursive: true, force: true });
              }
            });
          } else {
            res.status(404).send('File not found...');
          }
        } catch (error) {
          console.error("Error downloading files:", error);
          res.status(500).send("Error processing download");
        }
      } else {
        res.status(401).send("Project or execution check failed");
      }
    } else {
      res.status(401).send("Authentication failed");
    }
  } catch (error) {
    console.error("Authentication error:", error);
    res.status(500).send("Internal server error during authentication");
  }
};
