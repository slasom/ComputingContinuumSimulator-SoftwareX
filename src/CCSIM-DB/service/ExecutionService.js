'use strict';

var executeSqlQuery = require('../index').executeSqlQuery;

/**
 * Creates a project execution info
 * Creates a project execution info
 *
 * body Execution Creates a project execution info
 * execution_name String Name of the execution
 * returns Execution
 **/
exports.createProjectExecution = function(body,execution_name) { 
  return new Promise(function(resolve, reject) {


    var sqlQuery = "INSERT INTO executions (execution_name, project_name, ms_start_execution, ms_end_execution, devices_logs, associated_cost, execution_finished, execution_state) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    var params = [body.execution_name, body.project_name, body.ms_start_execution, body.ms_end_execution, body.devices_logs, body.associated_cost, body.execution_finished, body.execution_state];

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
 * Deletes a project execution info by the execution name
 * Deletes a project execution info by the execution name
 *
 * execution_name String Name of the execution to delete
 * returns Execution
 **/
exports.deleteExecutionInfoByExecutionName = function(execution_name) {
  return new Promise(function(resolve, reject) { 
    

    var sqlQuery = "DELETE FROM executions WHERE execution_name=(?)";
    var params = [execution_name];
    //console.log(sqlQuery);
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
 * Gets all the executions from the existing projects
 * Gets all the executions from the existing projects
 *
 * returns List
 **/
exports.getAllExecutions = function() {
  return new Promise(function(resolve, reject) {
    var sqlQuery = `SELECT * FROM executions;`;
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
 * Gets all the executions of projects from a certain user
 * Gets all the executions of projects from a certain user
 *
 * user_name String Username of the user
 * returns Execution
 **/
exports.getAllExecutionsFromUser = function(user_name) { 
  return new Promise(function(resolve, reject) {

    var sqlQuery = "SELECT * FROM executions WHERE project_name IN (SELECT project_name FROM projects WHERE user_name=(?));";
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
 * Gets a project execution info
 * Gets a project execution info
 *
 * execution_name String Name of the execution
 * returns Execution
 **/
exports.getExecutionInfoByExecutionName = function(execution_name) {
  return new Promise(function(resolve, reject) {

    var sqlQuery = "SELECT * FROM executions WHERE execution_name=(?)";
    var params = [execution_name];
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
 * Updates a project execution info by the execution name
 * Creates a project execution info by the execution name
 *
 * body Execution Updates a project execution info by the execution name
 * execution_name String Name of the execution to update
 * returns Execution
 **/
exports.updateExecutionInfoByExecutionName = function(body,execution_name) {
  return new Promise(function(resolve, reject) {

    var sqlQuery = "UPDATE executions SET execution_name=(?), project_name=(?), ms_start_execution=(?), ms_end_execution=(?), devices_logs=(?), associated_cost=(?), execution_finished=(?), execution_state=(?) WHERE execution_name=(?);";
    var params = [body.execution_name, body.project_name, body.ms_start_execution, body.ms_end_execution, body.devices_logs, body.associated_cost, body.execution_finished, body.execution_state, execution_name];
    executeSqlQuery(sqlQuery, params)
      .then((result) => {
        resolve(result);
      })
      .catch((err) => {
        reject(err);
      });
  });
}

