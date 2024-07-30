/* eslint-disable no-async-promise-executor */
"use strict";

const logger = require("./logger");
const exutils = require("./executions");
const DB = require("./DBOperations");
const fs = require("fs-extra");
const path = require("path");

/*
 * <<<< checkUITests >>>>
 * This function checks for UI tests.
 */

function checkUITests(tests) {
    return tests.some(test => test.type === 'espresso');
}


/*
 * <<<< checkPerformanceTests >>>>
 * This function checks for performance tests.
 */

function checkPerformanceTests(tests) {
    return tests.some(test => test.type === 'apipecker');
}

/*<<<< Execute Performance Tests >>>>
 * The function executes APIPecker with the different
 * tests that are defined in the file tests.yaml*/

async function executePerformanceTests(projectName, test) {
    return new Promise(async (resolve) => {
        await logger.writeToLogFile(
            projectName,
            "\n##### Launching Performance Tests #####"
        );

        await logger.writeToLogFile(projectName, "STARTING PERFORMANCE TEST: " + test.id);

        try {
            await exutils.executeCommand(
                "npx apipecker " +
                test.config.concurrent_users +
                " " +
                test.config.iterations +
                " " +
                test.config.delay +
                ' "' +
                test.config.url +
                '" -v  2>&1 | tee ./logs/results' +
                test.id.replace(/\s/g, "") +
                ".txt",
                path.join(__dirname, "projects", projectName)
            );

        } catch (err) {
            
            await logger.writeToLogFile(projectName, "\n##### Error executing performance tests: " + err + " #####\n");

            DB.executionJSON["execution_state"] =
                "ERROR: executePerformanceTests - cannot execute performance tests";
            exutils.associateCostBasedOnTimestamps(projectName);
            await DB.putExecutionToDatabaseAPI(
                DB.executionJSON["execution_name"],
                DB.executionJSON
            );
            process.exit(1);
        }
        resolve();
    });
}

/*<<<< Execute UI Tests >>>>
 * Function to execute the user interface tests with Espresso.
 * The function reads the connection file to connect to the machine where the
 * virtualized mobile devices are hosted and runs a script to launch the tests
 */
function executeUITests(projectName, test, mobile_app_installation) {
    return new Promise(async (resolve) => {
        console.log("Launching UI Tests...");
        await logger.writeToLogFile(projectName, "Launching UI tests...");

        try {
            var connection = fs.readFileSync(
                path.join(__dirname, "projects", projectName, "connection.txt"),
                "utf8"
            );
            connection = connection.split(",");
            var key = connection[0];
            var username = connection[1];
            var ip = connection[2];

            let matchedDevices = [];

            mobile_app_installation.forEach(app => {
                if (app.application_id === test.config.application_id) {
                    matchedDevices = app.installation_destination;
                }
            });

            await logger.writeToLogFile(
                projectName,
                "MATCHED DEVICES: " + matchedDevices
            );

            await exutils.executeCommand(
                "ssh -o StrictHostKeyChecking=no -i " +
                key +
                " " +
                username +
                "@" +
                ip +
                " bash ./scripts/executeUITests.sh " +
                matchedDevices +
                " " +
                test.config.application_id,
                path.join(__dirname, "projects", projectName)
            );

        } catch (err) {
            console.error(err);
            await logger.writeToLogFile(
                projectName,
                "\n##### Error executing UI tests: " + err + " #####\n"
            );

            DB.executionJSON["execution_state"] =
                "ERROR: executeUITests - cannot execute UI tests";
            exutils.associateCostBasedOnTimestamps(projectName);
            await DB.putExecutionToDatabaseAPI(
                DB.executionJSON["execution_name"],
                DB.executionJSON
            );
        }
        resolve();
    });
}

module.exports = {
    checkUITests,
    checkPerformanceTests,
    executePerformanceTests,
    executeUITests,
};
