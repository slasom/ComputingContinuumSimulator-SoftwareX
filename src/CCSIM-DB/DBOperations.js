const axios = require('axios'); // This is for making HTTP requests to the database API

const databaseAPIEndpoint = "http://localhost:8081/"; // This is the endpoint of the database API

// Returns a whole user from the database API
function getUserFromDatabaseAPI(user_name) {
    return new Promise(async function (resolve, reject) {
        axios.get(databaseAPIEndpoint + 'user/' + user_name)
            .then(response => {
                console.log("Database API Response:", response.data);
                resolve(response.data); // Asegurarse de que se resuelve con response.data
            })
            .catch(error => {
                console.error("Error from Database API:", error);
                reject(error);
            });
    });
}

// Exporting functions and variables
module.exports = {
    getUserFromDatabaseAPI
};
