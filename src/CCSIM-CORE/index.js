#!/usr/bin/env node

"use strict";
require('log-timestamp'); // This is for logging operations

var path = require('path'); // This is for path operations
var http = require('http'); // This is for http server operations

var oas3Tools = require('oas3-tools'); // This is for swagger tools operations
const cluster = require('cluster'); // This is for cluster operations
var serverPort = 8080;  //At this moment, we are using localhost:8080

// swaggerRouter configuration
var options = {
  routing: {
      controllers: path.join(__dirname, './controllers')
  },
};

// Initialize the Swagger middleware
var expressAppConfig = oas3Tools.expressAppConfig(path.join(__dirname, 'api/openapi.yaml'), options);
// Get the express app instance
var app = expressAppConfig.getApp();

// Initialize the Swagger middleware
if(cluster.isMaster){
  http.createServer(app).listen(serverPort, function () {
    console.log('Your server is listening on port %d (http://localhost:%d)', serverPort, serverPort);
    console.log('Swagger-ui is available on http://localhost:%d/docs', serverPort);
  });
}