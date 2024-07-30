'use strict';

const { getUserByUsername } = require('./UserService');

var executeSqlQuery = require('../index').executeSqlQuery;

/**
 * Create a new project
 * Create a new project
 *
 * body Project Create a new project
 * project_name String Name of project to create
 * returns Project
 **/
exports.createProject = async function (body, project_name) { 
  
  if(body.user_name !== null && body.user_name !== undefined){

    const usr = await getUserByUsername(body.user_name);

    if(usr[0]["user_name"] !== null && usr[0]["user_name"] !== undefined){

      return new Promise(function (resolve, reject) {

          var sqlQuery = "INSERT INTO projects (project_name, user_name, json_config, apk_file, apk_test_file, creation_date) VALUES (?, ?, ?, ?, ?, ?)";
          var params = [body.project_name, body.user_name, body.json_config, body.apk_file, body.apk_test_file, body.creation_date];
          
          executeSqlQuery(sqlQuery, params)
            .then((result) => {
              resolve(result);
            })
            .catch((err) => {
              reject(err);
            });
      });
    } 
  }
};


/**
 * Returns an array of all the projects
 * Returns an array of all the projects
 *
 * returns List
 **/
exports.getAllProjects = function() { 
  return new Promise(function(resolve, reject) {
    var sqlQuery = `SELECT * FROM projects;`;
    executeSqlQuery(sqlQuery)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });
  });
}


/**
 * Find a project by its name
 * Returns all the project data
 *
 * project_name String Name of project to return its info
 * returns Project
 **/
exports.getProjectByProjectName = function(project_name) { 
  return new Promise(function(resolve, reject) {

    var sqlQuery = "SELECT * FROM projects WHERE project_name =  (?)";
    var params = [project_name];
    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });
  });
}


/**
 * Returns an array of all projects registered in Perses from a certain user
 * Returns an array of all projects registered in Perses from a certain user
 *
 * user_name String Username of the user
 * returns Map
 **/
exports.getProjectsByUsername = function(user_name) {
  return new Promise(function(resolve, reject) { 
    

    var sqlQuery = "SELECT * FROM projects WHERE user_name = (?)";
    var params = [user_name];
    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });
  });
}


/**
 * Updates an existing project
 * Updates an existing project
 *
 * project_name String Name of project to udpate
 * returns Project
 **/
exports.updateProjectByName = function(body, project_name) { 
  return new Promise(function(resolve, reject) {
    
    var sqlQuery = "UPDATE projects SET json_config=(?), apk_file=(?), apk_test_file=(?), creation_date=(?) WHERE project_name=(?)";
    var params = [body.json_config, body.apk_file, body.apk_test_file, body.creation_date, project_name];
    
    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });
  });
}
