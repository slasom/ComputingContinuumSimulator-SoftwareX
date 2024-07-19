#!/usr/bin/env node
/* eslint-disable no-unused-vars */

"use strict";

const DB = require("./DBOperations");
const child_process = require("child_process");
const logger = require("./logger");
const { performance } = require('perf_hooks');

// Establishes the end of execution timestamp on the DB and the associated cost
async function finishExecution(projectName) {
    await logger.writeToLogFile(projectName, "Finishing execution...");
    associateCostBasedOnTimestamps(projectName);
    await DB.putExecutionToDatabaseAPI(
        DB.executionJSON["execution_name"],
        DB.executionJSON
    );
    console.log(DB.executionJSON);
}

// Creates an execution info into the database
async function createExecutionIntoDatabaseAPI(projectName) {
    const currentDate = new Date();
    const timestamp =
        "" +
        currentDate.getDate() +
        "-" +
        currentDate.getMonth() +
        "-" +
        currentDate.getFullYear() +
        "-" +
        currentDate.getHours() +
        ":" +
        currentDate.getMinutes() +
        ":" +
        currentDate.getSeconds() +
        ":" +
        currentDate.getMilliseconds();
    DB.executionJSON = JSON.parse(JSON.stringify(DB.executionJSON));
    DB.executionJSON["project_name"] = projectName;
    DB.executionJSON["ms_start_execution"] = Math.ceil(performance.now());
    DB.executionJSON["execution_state"] = "RUNNING - SETUP";
    DB.executionJSON["execution_name"] =
        DB.executionJSON["project_name"] + "-" + timestamp;
    await DB.postExecutionToDatabaseAPI(
        DB.executionJSON["execution_name"],
        DB.executionJSON
    );
}

// Establishes the end of execution timestamp on the DB and the associated cost
async function associateCostBasedOnTimestamps(projectName) {
    
    await logger.writeToLogFile(
        projectName,
        "Associating cost based on timestamps..."
    );

    // Get the end of execution timestamp
    DB.executionJSON["ms_end_execution"] = Math.ceil(performance.now());
    // Convert milliseconds to hours
    const timeElapsedHours =
        (DB.executionJSON["ms_end_execution"] -
            DB.executionJSON["ms_start_execution"]) *
        0.000000278;
    // Time elapsed in hours of the big instance * 5 + Time elapsed in hours of the small instance* 0.025
    // This cost is converted to EUR
    const payment_cost = 0.95 * (timeElapsedHours * DB.C5_METAL_EC2_COST_PER_HOUR + timeElapsedHours * DB.T2_SMALL_EC2_COST_PER_HOUR);
    const string_cost = payment_cost.toFixed(2);
    await logger.writeToLogFile(projectName, "Associated cost: " + string_cost);
    const amount = Number(string_cost);
    DB.executionJSON["associated_cost"] = amount;
}

// Executes a command in the shell
async function executeCommand(command, cwd) {
    return new Promise((resolve, reject) => {
        const ch_process = child_process.spawnSync(command, {
            shell: true,
            cwd,
            stdio: [process.stdin, process.stdout, process.stderr],
        });

        if (ch_process.error) {
            console.error(ch_process.error);
            console.log("Error en executeCommand");
            // Cambiar de reject a resolve para continuar a pesar del error
            resolve({ error: ch_process.error });
        } else {
            console.log("executeCommand correcto");
            resolve({ status: ch_process.status });
        }
    });
}


// Exports
module.exports = {
    finishExecution,
    createExecutionIntoDatabaseAPI,
    associateCostBasedOnTimestamps,
    executeCommand,
};
