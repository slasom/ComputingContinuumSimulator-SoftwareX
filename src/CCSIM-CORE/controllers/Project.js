/* eslint-disable no-unused-vars */
'use strict';

var utils = require('../utils/writer.js');
var Project = require('../service/ProjectService');
const security = require('../security');

module.exports.launchProject = async function launchProject (req, res, next, project_name) {
  console.log("USERNAME: " + req.headers["user_name"]);

  if(await security.confirmUserExists(req.headers["user_name"])){
    console.log("User exists");

    if(await security.authenticateAPIKey(req.headers["api_key"], req.headers["user_name"])){
      console.log("API key is valid");

      if(await security.confirmUserIsProjectOwner(req.headers["user_name"], project_name, req.headers["api_key"])) {
        console.log("User is project owner");

        Project.launchProject(project_name, req.headers["user_name"], req.headers["api_key"])
        .then(function (response) {
          utils.writeJson(res, "Request accepted! " + response);
        })
        .catch(function (response) {
          utils.writeJson(res, "Request denied: " + response);
        });

      } else {
        utils.writeJson(res, "Request denied: You are not the owner of that project or it does not exist.", 403);
      }

    } else {
      utils.writeJson(res, "Request denied: Wrong API key.", 401);
    }

  } else {
    utils.writeJson(res, "Request denied: User does not exist.", 404);
  }

};
