'use strict';

var utils = require('../utils/writer.js');
var Project = require('../service/ProjectService');
const security = require('../security.js');
const fs = require('fs');
require('stream');

module.exports.createProject = function createProject(req, res, next, body, project_name) {
  Project.createProject(body, project_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getAllProjects = function getAllProjects(req, res, next) {
  Project.getAllProjects()
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};

module.exports.getProjectByProjectName = async function getProjectByProjectName(req, res, next, project_name) {
  
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

      Project.getProjectByProjectName(project_name)
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

module.exports.getProjectsByUsername = async function getProjectsByUsername(req, res, next, user_name) {

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

      Project.getProjectsByUsername(user_name)
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

module.exports.updateProjectByName = function updateProjectByName(req, res, next, body, project_name) {
  Project.updateProjectByName(body, project_name)
    .then(function (response) {
      utils.writeJson(res, response);
    })
    .catch(function (response) {
      utils.writeJson(res, response);
    });
};
