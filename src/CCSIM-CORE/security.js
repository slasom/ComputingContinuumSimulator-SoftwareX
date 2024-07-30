'use strict';
const DB = require('./DBOperations');
const crypto = require('crypto');

async function confirmUserExists(user_name){
    const user = await DB.getUserFromDatabaseAPI(user_name);
    if(user.data[0] !== null && user.data[0] !== undefined){
        return true;
    }
    return false;
}

async function authenticateAPIKey(api_key_provided, user_name){
    const user = await DB.getUserFromDatabaseAPI(user_name);
    const hash_api_key = crypto.createHash('sha256');

    hash_api_key.update(api_key_provided);
    const api_key_hashed = hash_api_key.digest('hex');

    if(user.data[0] !== null && user.data[0] !== undefined){
        if(api_key_hashed === user.data[0]["api_key"]){
            return true;
        }
    }
    return false;
}

async function confirmUserIsProjectOwner(user_name, project_name, api_key) {
    const project = await DB.getProjectFromDatabaseAPI(project_name, user_name, api_key);
    if(project.data[0] !== null && project.data[0] !== undefined){
        if (user_name === project.data[0]["user_name"]){
            return true;
        }
    }
    return false; 
}

// Exporting functions and variables
module.exports = {
    confirmUserExists,
    authenticateAPIKey,
    confirmUserIsProjectOwner
};
