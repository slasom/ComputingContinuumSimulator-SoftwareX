#!/usr/bin/env node
/* eslint-disable no-async-promise-executor */

"use strict";

const fs = require("fs-extra"); // This is for file system operations
const path = require("path"); // This is for path operations
const DB = require("./DBOperations"); // This is for database operations
const logger = require("./logger"); // This is for logging operations
const executionUtils = require("./executions"); // This is for execution operations

// Generate the folder for the project logs
function generateLogsFolder(projectName) {
    fs.mkdir(
        path.join(__dirname, "projects", projectName, "logs"),
        { recursive: true },
        async (err) => {
            if (err) {
                console.log(
                    "\n##### Error creating the logs folder: " + err + " #####\n"
                );

                DB.executionJSON["execution_state"] =
                    "ERROR: setup - cannot create the logs folder";
                await executionUtils.finishExecution(projectName);
                await removeProjectFolder(projectName);
                throw err;
            }
        }
    );
}

/*
 * <<<< removeProjectFolder >>>>
 * This function removes the actual project folder if an error appears.
 */
async function removeProjectFolder(projectName) {
    return new Promise(async (resolve, reject) => {
        if (fs.existsSync(path.join(__dirname, "projects", projectName)))
            fs.rmdirSync(
                path.join(__dirname, "projects", projectName),
                { recursive: true },
                async (err) => {
                    if (err) {
                        console.log(
                            "\n ###### The following error appeared trying to delete the project: ######\n" +
                            err
                        );
                        await logger.writeToLogFile(
                            projectName,
                            "\n##### Error at deleting project folder #####\n" + err
                        );

                        DB.executionJSON["execution_state"] =
                            "ERROR: removeProjectFolder - cannot delete project folder";
                        executionUtils.associateCostBasedOnTimestamps(projectName);
                        await DB.putExecutionToDatabaseAPI(
                            DB.executionJSON["execution_name"],
                            DB.executionJSON
                        );
                        reject(err);
                        throw err;
                    } else {
                        console.log("The project folder has been deleted successfully");
                        await logger.writeToLogFile(
                            projectName,
                            "Project folder deleted successfully"
                        );
                    }
                }
            );

        if (fs.existsSync(path.join(__dirname, "test", projectName)))
            fs.rmdirSync(
                path.join(__dirname, "test", projectName),
                { recursive: true },
                async (err) => {
                    if (err) {
                        console.log(
                            "\n ###### The following error appeared trying to delete the project data: ######\n" +
                            err
                        );
                        await logger.writeToLogFile(
                            projectName,
                            "\n##### Error at deleting project folder #####\n" + err
                        );

                        DB.executionJSON["execution_state"] =
                            "ERROR: removeProjectFolder - cannot delete data folder";
                        await executionUtils.finishExecution(projectName);
                        reject(err);
                        throw err;
                    } else {
                        console.log("The data folder has been deleted successfully");
                    }
                }
            );

        resolve();
    });
}

// This function removes the project folder and finishes the execution
async function removeProjectAndFinish(projectName, removeProjectWhenFinished, interrupt) {
    
    if (removeProjectWhenFinished) {
        await logger.writeToLogFile(projectName, "Removing project folder...");
        await removeProjectFolder(projectName);
    }

    DB.executionJSON.execution_finished = 1;
    console.log(
        "\n\n@@@@@ VALOR DE execution_finished = " +
        DB.executionJSON.execution_finished +
        " @@@@@\n\n"
    );
    await DB.putExecutionToDatabaseAPI(
        DB.executionJSON.execution_name,
        DB.executionJSON
    );

    if (interrupt) {
        DB.executionJSON.execution_state = "INTERRUPTED";
    } else {
        DB.executionJSON.execution_state = "FINISHED";
    }

    console.log(
        "\n\n@@@@@ VALOR DE execution_state = " +
        DB.executionJSON.execution_state +
        " @@@@@\n\n"
    );
    await DB.putExecutionToDatabaseAPI(
        DB.executionJSON.execution_name,
        DB.executionJSON
    );
    await executionUtils.finishExecution(projectName);
}

// Export the functions
module.exports = {
    generateLogsFolder,
    removeProjectFolder,
    removeProjectAndFinish,
};
