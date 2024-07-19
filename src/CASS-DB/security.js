'use strict';
const DB = require('./DBOperations.js');
const crypto = require('crypto');

async function authenticateAPIKey(api_key_provided, user_name) {
    const user = await DB.getUserFromDatabaseAPI(user_name);
    console.log("User data from DB:", user);  // Verificar qué se imprime aquí
    if (!user || !user[0]) {  // Asegurarse de que user y user[0] existen
        return false;
    }
    const hash_api_key = crypto.createHash('sha256');

    hash_api_key.update(api_key_provided);
    const api_key_hashed = hash_api_key.digest('hex');

    // Checks:
    // - User exists
    // - Api key of user is valid
    if (user[0] && api_key_hashed === user[0]["api_key"]) {
        return true;
    }
    return false;
}

// Exporting functions and variables
module.exports = {
    authenticateAPIKey
};
