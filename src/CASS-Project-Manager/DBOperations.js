const axios = require('axios'); // This is for making HTTP requests to the database API

// At this moment, we are using localhost:8081 for avoiding conflicts with the perses API
const databaseAPIEndpoint = "http://localhost:8081/"; // This is the endpoint of the database API

// Returns a whole user from the database API
async function getUserFromDatabaseAPI(user_name) {
    try {
        const response = await axios.get(databaseAPIEndpoint + 'user/' + user_name);
        return response;
    } catch (error) {
        console.error(error);
        throw error;
    }
}

// Returns a project from the DB
async function getProjectFromDatabaseAPI(project_name, user_name, api_key) {
    try {
        const response = await axios.get(databaseAPIEndpoint + 'project/' + project_name, {
            headers: {
                'user_name': user_name,
                'api_key': api_key
            }
        });
        return response;
    } catch (error) {
        console.error('Error in getProjectFromDatabaseAPI:', error);
        throw error;
    }
}

// Returns an execution from the DB
async function getExecutionFromDatabaseAPI(execution_name, user_name, api_key) {
    try {
        const response = await axios.get(databaseAPIEndpoint + 'execution/' + execution_name, {
            headers: {
                'user_name': user_name,
                'api_key': api_key
            }
        });
        return response;
    } catch (error) {
        console.error('Error in getExecutionFromDatabaseAPI:', error);
        throw error;
    }
}

// Writes the project into the DB
async function writeProjectToDatabaseAPI(project_name, body, user_name, api_key) {
    try {
        const project = await getProjectFromDatabaseAPI(project_name, user_name, api_key);
        console.log(project.data[0]);

        if (project.data[0] !== null && project.data[0] !== undefined) {
            console.log("Project already exists, updating record");
            const response = await axios.put(databaseAPIEndpoint + 'project/' + project_name, body, {
                headers: {
                    'user_name': user_name,
                    'api_key': api_key
                }
            });
            console.log(response);
            return response;
        } else {
            const response = await axios.post(databaseAPIEndpoint + 'project/' + project_name, body, {
                headers: {
                    'user_name': user_name,
                    'api_key': api_key
                }
            });
            console.log(response);
            return response;
        }
    } catch (error) {
        console.error('Error in writeProjectToDatabaseAPI:', error); // Logging the error to the console 
        throw error;
    }
}

// Exporting functions and variables
module.exports = {
    getUserFromDatabaseAPI,
    writeProjectToDatabaseAPI,
    getProjectFromDatabaseAPI,
    getExecutionFromDatabaseAPI
};
