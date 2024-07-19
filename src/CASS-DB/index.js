"use strict";

var path = require('path');
var http = require('http');
var mysql = require('mysql');

var oas3Tools = require('oas3-tools');
var serverPort = 8081;

// DB connection definition 
var con = mysql.createConnection({
    host: "", //INSERT YOUR HOST HERE
    user: "", //INSERT YOUR USER HERE
    password: "", //INSERT YOUR PASSWORD HERE
    database: "" //INSERT YOUR DATABASE HERE
});

// DB connection establishment
con.connect(function(err) {
    if (err) {
        throw err;
    }
    console.log("Database connection established");
});

// Execute a query into the DB
function executeSqlQuery(sqlQuery, params) {
    return new Promise(function (resolve, reject) {
    con.query(sqlQuery, params, function (err, result) {
    if (err) {
        // En lugar de lanzar una excepción, rechazamos la promesa con el error.
        reject(err);
    } else {
        console.log(result);
        resolve(result);
    }
    });
});
}

// Export the function that executes queries
module.exports = {executeSqlQuery};


// swaggerRouter configuration
var options = {
    routing: {
        controllers: path.join(__dirname, './controllers')
    },
};

var expressAppConfig = oas3Tools.expressAppConfig(path.join(__dirname, 'api/openapi.yaml'), options);
var app = expressAppConfig.getApp();

// Initialize the Swagger middleware
http.createServer(app).listen(serverPort, function () {
    console.log('Your server is listening on port %d (http://localhost:%d)', serverPort, serverPort);
    console.log('Swagger-ui is available on http://localhost:%d/docs', serverPort);
});