'use strict';
const DB = require('./DBOperations');
const crypto = require('crypto');

async function authenticateAPIKey(api_key_provided, user_name){
    const user = await DB.getUserFromDatabaseAPI(user_name);
    const hash_api_key = crypto.createHash('sha256');

    hash_api_key.update(api_key_provided);
    const api_key_hashed = hash_api_key.digest('hex');

    // Checks:
    // - User exists
    // - Api key of user is valid
    if(user.data[0] !== null && user.data[0] !== undefined){
        if(api_key_hashed === user.data[0]["api_key"]){
            return true;
        }
    }
    return false;
}

async function checkProjectAndExecution(project_name, execution_name, user_name, api_key){
    var project = await DB.getProjectFromDatabaseAPI(project_name, user_name, api_key);
    var execution = await DB.getExecutionFromDatabaseAPI(execution_name, user_name, api_key);

    project = project.data[0];
    execution = execution.data[0];

    // Checks:
    // - Project exists
    // - Execution exists
    // - Execution belongs to project and project belongs to user
    if(project !== null && project !== undefined){
        if(execution !== null && execution !== undefined){
            if(execution["project_name"] == project_name && project["user_name"] == user_name){
                return true;
            }
        }
    }
    return false;
}

// Exporting functions and variables
module.exports = {
    authenticateAPIKey,
    checkProjectAndExecution
};