/* eslint-disable no-async-promise-executor */
const axios = require('axios'); // This is for making HTTP requests to the database API

// At this moment, we are using localhost:8081 for avoiding conflicts with the perses API
const databaseAPIEndpoint = "http://localhost:8081/"; // This is the endpoint of the database API

// Object that will be used to create an execution of a project in the database API
var executionJSON = {
    "devices_logs": "undefined",
    "execution_logs": "undefined",
    "ms_end_execution": 0,
    "associated_cost": 0,
    "execution_name": "undefined",
    "execution_finished": 0,
    "execution_state": "NOT STARTED",
    "project_name": "undefined",
    "ms_start_execution": 0
};

// An approximation of the cost of a C5 Metal EC2 instance per hour (in USD)
const C5_METAL_EC2_COST_PER_HOUR = 5;
// An approximation of the cost of a T2 Small EC2 instance per hour (in USD)
const T2_SMALL_EC2_COST_PER_HOUR = 0.025;

// Returns a whole project from the database API
async function getProjectFromDatabaseAPI(project_name, user_name, api_key) {
    try {
        //console.log("***** Haciendo peticion en DBO *****");
        /*console.log('Headers:', {
            'user_name': user_name,
            'api_key': api_key
        });*/

        const response = await axios.get(databaseAPIEndpoint + 'project/' + project_name, {
            headers: {
                'user_name': user_name,
                'api_key': api_key
            }
        });
        //console.log("***** RESPUESTA: *****");
        //console.log(response);
        return response;
    } catch (error) {
        console.error('Error in getProjectFromDatabaseAPI:', error);
        throw error;
    }
}


// Returns a whole user from the database API
function getUserFromDatabaseAPI(user_name) {
    return new Promise(async function (resolve, reject) {
        axios.get(databaseAPIEndpoint + 'user/' + user_name)
            .then(response => {
                resolve(response);
            })
            .catch(error => {
                console.error(error);
                reject(error);
            });
    })
}

// This function posts an execution of a project to the database API
function postExecutionToDatabaseAPI(execution_name, body) {
    return new Promise(async function (resolve, reject) {
        axios.post(databaseAPIEndpoint + 'execution/' + execution_name, body)
            .then(response => {
                console.log(response);
                resolve(response);
            })
            .catch(error => {
                console.error(error);
                reject(error);
            });
    })
}

// This function puts an execution of a project to the database API to update an existing execution of a project
function putExecutionToDatabaseAPI(execution_name, body) {
    return new Promise(async function (resolve, reject) {
        axios.put(databaseAPIEndpoint + 'execution/' + execution_name, body)
            .then(response => {
                console.log(response);
                resolve(response);
            })
            .catch(error => {
                console.error(error);
                reject(error);
            });
    })
}


// Exporting functions and variables
module.exports = {
    getProjectFromDatabaseAPI,
    postExecutionToDatabaseAPI,
    putExecutionToDatabaseAPI,
    getUserFromDatabaseAPI,
    executionJSON,
    C5_METAL_EC2_COST_PER_HOUR,
    T2_SMALL_EC2_COST_PER_HOUR
};
