'use strict';

var utils = require('../utils/writer.js');
var Execution = require('../service/ExecutionService');
const security = require('../security.js');
const fs = require('fs');
require('stream');

module.exports.createProjectExecution = function createProjectExecution(req, res, next, body, execution_name) {
  Execution.createProjectExecution(body, execution_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};


module.exports.deleteExecutionInfoByExecutionName = function deleteExecutionInfoByExecutionName(req, res, next, execution_name) {
  Execution.deleteExecutionInfoByExecutionName(execution_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getAllExecutions = function getAllExecutions(req, res, next) {
  Execution.getAllExecutions()
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getAllExecutionsFromUser = async function getAllExecutionsFromUser(req, res, next, user_name) {

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

      Execution.getAllExecutionsFromUser(user_name)
        .then(function (response) {
          utils.writeJson(res, response);
        })
        .catch(function (response) {
          utils.writeJson(res, response);
        });

    } else {
      res.status(401).send("Authentication failed");
    }
  } catch (error) {
    console.error("Authentication error:", error);
    res.status(500).send("Internal server error during authentication");
  }

};

module.exports.getExecutionInfoByExecutionName = async function getExecutionInfoByExecutionName(req, res, next, execution_name) {
  
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

      Execution.getExecutionInfoByExecutionName(execution_name)
        .then(function (response) {
          utils.writeJson(res, response);
        })
        .catch(function (response) {
          utils.writeJson(res, response);
        });

    } else {
      res.status(401).send("Authentication failed");
    }
  } catch (error) {
    console.error("Authentication error:", error);
    res.status(500).send("Internal server error during authentication");
  }

};

module.exports.updateExecutionInfoByExecutionName = function updateExecutionInfoByExecutionName(req, res, next, body, execution_name) {
  Execution.updateExecutionInfoByExecutionName(body, execution_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

