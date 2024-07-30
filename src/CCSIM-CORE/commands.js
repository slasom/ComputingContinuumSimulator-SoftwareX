#!/usr/bin/env node
/* eslint-disable no-async-promise-executor */

"use strict";

// External modules
const fs = require("fs-extra"); // Work with the filesystem
const path = require("path"); // Work with paths
const mustache = require("mustache"); // Render templates
const yaml = require("js-yaml"); // Parse YAML files
const child_process = require("child_process"); // Execute commands in subshell
const logger = require("./logger"); // Write local logs

// Internal modules
const DB = require("./DBOperations"); // Database operations
const executionUtils = require("./executions"); // Execution utils for costs, commands, etc.
const folderUtils = require("./folders"); // Utilities for working with folders
const testUtils = require("./tests"); // Utilities for checking and executing tests
const s3Operations = require("./s3Operations"); // Utilities for working with S3 buckets
const terraformUtils = require("./terraform"); // Operations to create and delete terrafom infrastructure
const logicUtils = require("./logic"); // Operations with program logic

console.warn = () => { }; // Avoid warnings showing in the console

const credentialsFileName = "credentialExample.yaml"; // File to read credentials
var configFileName = "json-config.json"; // File name from which to read configuration

// Read terraform variables template and logs filter template
const terraformTemplate = fs.readFileSync(
    path.join(__dirname, "./templates/variables.mustache"),
    "utf8"
);
const logTemplate = fs.readFileSync(
    path.join(__dirname, "./templates/filterLogs.mustache"),
    "utf8"
);

// Creates folders, check files and prepares everything. See comments inside the function for more information.
exports.setupPerses = function (projectName, responseJSONFromDatabase) {
    return new Promise(async (resolve, reject) => {
        // Execution into database
        await executionUtils.createExecutionIntoDatabaseAPI(projectName);

        // Create folder for the project
        fs.mkdir(
            path.join(__dirname, "test", projectName),
            { recursive: true },
            async (err) => {
                // ERROR - Project folder could not be created
                if (err) {
                    console.log("\n##### Error: " + err);
                    console.log("Project folder could not be created...\n");
                    // Mark execution as finished in DB
                    DB.executionJSON["execution_state"] =
                        "ERROR: setup - project folder for the JSON and APKs could not be created";
                    await executionUtils.finishExecution(projectName);
                    throw err;
                }
            }
        );

        try {
            // Create and bind stds
            const stdout = fs.createWriteStream(
                path.join(__dirname, "test", projectName, "perses_stdout.txt")
            );
            const stderr = fs.createWriteStream(
                path.join(__dirname, "test", projectName, "perses_stderr.txt")
            );
            process.stdout.write = stdout.write.bind(stdout);
            process.stderr.write = stderr.write.bind(stderr);

            // Create the local file to store execution logs
            await logger.createLogFile(projectName);
            await logger.writeToLogFile(
                projectName,
                "----- Starting setup of project: " + projectName + " -----\n"
            );
            await logger.writeToLogFile(
                projectName,
                "\n----- START OF PERSES WORKFLOW -----\n"
            );
            await logger.writeToLogFile(
                projectName,
                "Project folder created successfully"
            );

            // S3 File Downloads: JSON config, APK and APK Test (this last one only if there exists UI tests)
            await s3Operations.downloadSetupFilesAndReturnJSONConfig(
                projectName,
                responseJSONFromDatabase
            );
            await logger.writeToLogFile(
                projectName,
                "Files downloaded successfully from the S3 bucket"
            );
            // Parse JSON config file
            var jsonData = JSON.parse(
                fs.readFileSync(
                    path.join(__dirname, "test", projectName, "json-config.json"),
                    "utf8"
                )
            );
            await logger.writeToLogFile(
                projectName,
                "JSON config file parsed successfully"
            );
        } catch (err) {
            console.log(
                "\n##### Error obtaining s3 config files: " + err + " #####\n"
            );

            DB.executionJSON["execution_state"] =
                "ERROR: setup - S3 files could not be obtained";
            await executionUtils.finishExecution(projectName);
            await folderUtils.removeProjectFolder(projectName); // Remove all the project files and folder if an error appears
            process.exit(1);
        }

        await logger.writeToLogFile(
            projectName,
            "\n  - Config File: '" + configFileName + "'"
        );
        await logger.writeToLogFile(
            projectName,
            "\n  - Credentials File: '" + credentialsFileName + "'"
        );
        await logger.writeToLogFile(
            projectName,
            "\n  - ProjectName: '" + projectName + "'\n"
        );

        // ERROR - Project already exists
        if (fs.existsSync(path.join(__dirname, "projects", projectName))) {
            await logger.writeToLogFile(
                projectName,
                "ERROR: setup - project already exists"
            );
        } else {
            await logger.writeToLogFile(projectName, "Reading local config files...");
            // Read config file parameters
            var parameters = yaml.load(
                fs.readFileSync(
                    path.join(__dirname, "test", "configExample.yaml"),
                    "utf8"
                )
            );
            // Read credentials file parameters
            var credentials = yaml.load(
                fs.readFileSync(
                    path.join(__dirname, "test", credentialsFileName),
                    "utf8"
                )
            );

            // Use the project name provided by the user from the API
            parameters.project_name = projectName;

            // Adds credentials to parameters
            for (let [key, value] of Object.entries(credentials)) {
                parameters[key] = value;
            }

            const directoryPath = path.join(__dirname, "test", projectName);
            parameters["directory_path"] = directoryPath

            try {
                // Leer todos los archivos en el directorio
                const files = fs.readdirSync(directoryPath);

                // Filtrar los archivos que terminan con "-androidTest.apk"
                const testApkFiles = files.filter(file => file.endsWith("-androidTest.apk"));

                // Crear una cadena con los paths completos, ahora usando "/home/ubuntu/files/", separados por ","
                var apk_test_path = testApkFiles.map(file => "/home/ubuntu/files/" + file).join(",");

                // Filtrar los archivos .apk regulares (que no terminan con "-androidTest.apk")
                const regularApkFiles = files.filter(file => file.endsWith(".apk") && !file.endsWith("-androidTest.apk"));

                // Crear una cadena con los paths completos de los archivos .apk regulares, ahora usando "/home/ubuntu/files/", separados por ","
                var apk_path = regularApkFiles.map(file => "/home/ubuntu/files/" + file).join(",");
            } catch (err) {
                await logger.writeToLogFile(
                    projectName,
                    "ERROR: Could not read downloaded files"
                );
            }

            var key_path = parameters["key_path"];

            // Use the APKs provided by the user from the API
            parameters["apk_path"] = apk_path;
            parameters["apk_test_path"] = apk_test_path;

            // Load everything from JSON
            var user_name = jsonData.user_name;
            parameters['user_name'] = user_name;

            var project_name = jsonData.project_name;
            parameters['project_name'] = project_name

            var tests = jsonData.tests;
            await logger.writeToLogFile(projectName, "TESTS: " + JSON.stringify(tests));
            await logicUtils.writeToFile(`test/${projectName}/tests.json`, JSON.stringify(tests));

            var architecture = await logicUtils.loadArchitecture(jsonData.architecture); 
            await logger.writeToLogFile(projectName, "ARCHITECTURE: " + JSON.stringify(architecture));
            await logicUtils.writeToFile(`test/${projectName}/architecture.dadosim`, JSON.stringify(architecture));

            var mobile_devices = logicUtils.getAndroidHosts(architecture);
            await logger.writeToLogFile(projectName, "MOBILE DEVICES: " + mobile_devices);
            parameters['mobile_devices'] = mobile_devices;

            var number_of_mobile_devices = mobile_devices.length
            await logger.writeToLogFile(projectName, "NUMBER OF MOBILE DEVICES: " + number_of_mobile_devices);
            parameters['number_mobiles'] = number_of_mobile_devices;

            var number_of_total_devices = architecture.hosts.length;
            await logger.writeToLogFile(projectName, "NUMBER OF TOTAL DEVICES: " + number_of_total_devices);
            parameters["number_devices"] = number_of_total_devices;

            var time_wait = logicUtils.calculateTimeWait(number_of_mobile_devices)
            await logger.writeToLogFile(projectName, "TIME WAIT FOR INSTALLATION: " + time_wait);
            parameters['time_wait'] = time_wait

            var idle_time = jsonData.idle_time
            await logger.writeToLogFile(projectName, "IDLE TIME: " + idle_time);
            parameters['idle_time'] = idle_time

            var mobile_app_installation = [];
            if ('mobile_app_installation' in jsonData) {
                mobile_app_installation = jsonData.mobile_app_installation;
            }
            await logger.writeToLogFile(projectName, "MOBILE APP INSTALLATION INSTRUCTIONS: " + JSON.stringify(mobile_app_installation));
            await logicUtils.writeToFile(`test/${projectName}/mobile_app_installation.json`, JSON.stringify(mobile_app_installation));

            // Check Key file
            if (!fs.existsSync(key_path)) {
                await logger.writeToLogFile(
                    projectName,
                    "\n######### Error: KEY FILE DOESN'T EXIST! #########\n"
                );
                DB.executionJSON["execution_state"] =
                    "ERROR: setup - Key file does not exist.";
                await executionUtils.finishExecution(projectName);
                throw new Error("Key file does not exist.");
            }

            // Check UI TESTS exists
            const uiTests = testUtils.checkUITests(tests);

            if (uiTests) {
                if (!apk_test_path) {
                    await logger.writeToLogFile(
                        projectName,
                        "\n####### Error: UI test exists but the variable apk_test_path is not defined #######\n"
                    );
                    DB.executionJSON["execution_state"] =
                        "ERROR: setup - UI Tests exist but apk_test_path is not defined.";
                    await executionUtils.finishExecution(projectName);
                    throw new Error("UI Tests exist but apk_test_path is not defined.");
                }
            }

            parameters["volume_size"] = Math.round(40 + number_of_total_devices * 6);
            await logger.writeToLogFile(projectName, "VOLUME SIZE: " + parameters["volume_size"]);

            // AMI ID
            if (parameters["ami_id"] == null)
                parameters["ami_id"] = "ami-0136ddddd07f0584f";

            // EC2 USERNAME
            parameters["ec2_username"] = ""; //YOUR EC2 USERNAME HERE

            // End Port
            parameters["port"] = 6000 + Number(parameters["number_devices"]);


            // Show all the final configuration parameters
            await logger.writeToLogFile(projectName, "\n--- PARAMETERS ---\n");
            await logger.writeToLogFile(projectName, JSON.stringify(parameters));
            await logger.writeToLogFile(projectName, "\n--- END OF PARAMETERS ---\n");

            // Render the templates
            // Here we load the contents at the variables.tf file for Terraform
            // Also the logs filter file for the devices logs
            await logger.writeToLogFile(projectName, "Rendering templates...");
            var output = mustache.render(terraformTemplate, parameters);
            var outputLog = mustache.render(logTemplate, parameters);

            // Create projects folder
            await logger.writeToLogFile(projectName, "Creating projects folder...");
            fs.mkdir(
                path.join(__dirname, "projects", projectName),
                { recursive: true },
                async (err) => {
                    if (err) {
                        await logger.writeToLogFile(
                            projectName,
                            "\n##### ERROR: setup - project folder could not be created #####\n"
                        );
                        DB.executionJSON["execution_state"] =
                            "ERROR: setup - project folder could not be created";
                        await executionUtils.finishExecution(projectName);
                        throw err;
                    } else {
                        // Generate terraform files
                        try {
                            // Generates variable files for Terraform
                            fs.writeFileSync(
                                path.join(__dirname, "projects", projectName, "variables.tf"),
                                output,
                                "utf8"
                            );
                            await logger.writeToLogFile(
                                projectName,
                                "Generated variables.tf file successfully"
                            );

                            // Generates the script to later filter the logs with the tags defined in the configuration file
                            fs.writeFileSync(
                                path.join(__dirname, "projects", projectName, "filterLogs.js"),
                                outputLog,
                                "utf8"
                            );
                            await logger.writeToLogFile(
                                projectName,
                                "Generated filterLogs.js file successfully"
                            );

                            // Generates the file for the tests configuration
                            fs.writeFileSync(
                                path.join(
                                    __dirname,
                                    "projects",
                                    projectName,
                                    "perses-tests.yaml"
                                ),
                                yaml.safeDump(tests),
                                "utf8"
                            );
                            await logger.writeToLogFile(
                                projectName,
                                "Generated perses-tests.yaml file successfully"
                            );
                        } catch (err) {
                            console.log(
                                "An error has apeared at trying to generate files..."
                            );
                            console.error("\n##### Error: " + err);
                            await logger.writeToLogFile(
                                projectName,
                                "\n##### Error generating setup files (variables.tf - filterLogs.js - perses-tests.yaml) #####\n"
                            );

                            DB.executionJSON["execution_state"] =
                                "ERROR: setup - files could not be generated";
                            await executionUtils.finishExecution(projectName);

                            // Delete the project folder with all the files if an error appears
                            await folderUtils.removeProjectFolder(projectName);
                        }

                        // Terraform init and plan using the copied files in the projet folder
                        await logger.writeToLogFile(
                            projectName,
                            "Initializing Terraform..."
                        );
                        await terraformUtils.initiateTerraform(
                            projectName,
                            reject
                        );
                        
                        await logger.writeToLogFile(
                            projectName,
                            "Generating logs folder inside the project folder..."
                        );
                        folderUtils.generateLogsFolder(projectName); // Generate the folder for the project logs
                    }
                }
            );
        }

        resolve();
    });
};

/*
 * <<<< Launch Perses >>>>
 * This function starts the defined Terraform infrastructure created in the folder 'projectName'.
 */

exports.launchPerses = function (projectName) {
    return new Promise(async (resolve, reject) => {
        
        await logger.writeToLogFile(
            projectName,
            "\n----- STARTING LAUNCH PHASE -----\n"
        );

        // Update the execution state in the DB
        DB.executionJSON["execution_state"] = "RUNNING - LAUNCH";
        await DB.putExecutionToDatabaseAPI(
            DB.executionJSON["execution_name"],
            DB.executionJSON
        );

        // If the project exists, perform launch
        if (fs.existsSync(path.join(__dirname, "projects", projectName))) {
            console.log("Launching...");
            try {
                //Starts Terraform
                const terraform = child_process.spawnSync(
                    "terraform plan && terraform apply -auto-approve",
                    {
                        shell: true,
                        cwd: path.join(__dirname, "projects", projectName),
                        stdio: [process.stdin, process.stdout, process.stderr],
                    }
                );

                if (terraform.error) {
                    await logger.writeToLogFile(
                        projectName,
                        "\n##### Error: " + terraform.error + " #####\n"
                    );

                    DB.executionJSON["execution_state"] =
                        "ERROR: launch - terraform launch";
                    await executionUtils.finishExecution(projectName);
                    await folderUtils.removeProjectFolder(projectName);
                    reject(terraform.error);
                } else {
                    // Terraform launches correctly
                    if (terraform.status === 0) {
                        await logger.writeToLogFile(
                            projectName,
                            "\n----- Terraform Launch completed -----\n"
                        );
                        resolve();
                    } else {
                        console.error("\n###### Error in launch command ######\n");
                        await logger.writeToLogFile(
                            projectName,
                            "\n##### Error: Error in launch command #####\n"
                        );

                        DB.executionJSON["execution_state"] =
                            "ERROR: launch - terraform launch";
                        await executionUtils.finishExecution(projectName);
                        reject(new Error("Error in launch command"));
                    }
                }
            } catch (err) {
                console.error(err);
                await logger.writeToLogFile(
                    projectName,
                    "\n##### Error: " + err + " #####\n"
                );

                DB.executionJSON["execution_state"] =
                    "ERROR: launch - terraform launch";
                await executionUtils.finishExecution(projectName);

                await logger.writeToLogFile(
                    projectName,
                    "Destroying Perses infrastructure"
                );

                await exports.destroyPerses(projectName);
                reject(err);
            }
        } else {
            await logger.writeToLogFile(projectName, "The project does not exist");
        }
        resolve();
    });
};

/*
 * <<<< Run Tests >>>>
 * This function starts the tests generated with API PECKER.
 */

exports.runTests = function (projectName) {
    return new Promise(async (resolve) => {
        
        await logger.writeToLogFile(
            projectName,
            "\n----- STARTING TESTS PHASE -----\n"
        );

        // Update the execution state in the DB
        DB.executionJSON["execution_state"] = "RUNNING - TESTS";
        await DB.putExecutionToDatabaseAPI(
            DB.executionJSON["execution_name"],
            DB.executionJSON
        );

        if (fs.existsSync(path.join(__dirname, "projects", projectName))) {
            // CHECK TESTS
            await logger.writeToLogFile(projectName, "Checking tests...");
            var jsonData = JSON.parse(
                fs.readFileSync(
                    path.join(__dirname, "test", projectName, "json-config.json"),
                    "utf8"
                )
            );
            var tests = jsonData.tests
            var mobile_app_installation = jsonData.mobile_app_installation
            
            // Initially uiTests is false and later check if the JSON has defined uiTests 
            var uiTests = false;
            uiTests = testUtils.checkUITests(tests);

            // Initially performanceTests is false and later check if the JSON has defined performanceTests
            var performanceTests = false;
            performanceTests = testUtils.checkPerformanceTests(tests);

            // Check if there are UI test to launch
            if (uiTests) {
                await logger.writeToLogFile(projectName, "\n---  Ejecutando los uiTests ---");
                tests.forEach(async test => {
                    if (test.type === "espresso") {
                        console.log(`Ejecutando ${test.id} de tipo espresso`);
                        await testUtils.executeUITests(projectName, test, mobile_app_installation);
                    }
                });
            }
            // Check if there are performance tests to launch
            if (performanceTests) {
                await logger.writeToLogFile(projectName, "\n---  Ejecutando los performanceTests ---");
                tests.forEach(async test => {
                    if (test.type === "apipecker") {
                        console.log(`Ejecutando ${test.id} de tipo apipecker`);
                        await testUtils.executePerformanceTests(projectName, test);
                    }
                });
            }
        } else {
            await logger.writeToLogFile(projectName, "The project does not exist");
        }

        resolve();
    });
};

/*
 * <<<< Get Logs  >>>>
 * This function downloads the logs generated on the virtualized Android devices to filter them later.
 */

exports.getLogs = function (projectName) {
    return new Promise(async (resolve, reject) => {
        
        await logger.writeToLogFile(
            projectName,
            "\n----- STARTING GET LOGS PHASE -----\n"
        );

        // Update the execution state in the DB
        DB.executionJSON["execution_state"] = "RUNNING - LOGS";
        await DB.putExecutionToDatabaseAPI(
            DB.executionJSON["execution_name"],
            DB.executionJSON
        );

        if (fs.existsSync(path.join(__dirname, "projects", projectName))) {
            console.log(
                "\n##### Getting logs of the project: '" + projectName + "' #####"
            );
            try {
                await logger.writeToLogFile(
                    projectName,
                    "Obtaining parameters from the connection file..."
                );
                // Required parameters for obtaining the logs from the virtualized devices
                var connection = fs.readFileSync(
                    path.join(__dirname, "projects", projectName, "connection.txt"),
                    "utf8"
                );
                var jsonData = JSON.parse(
                    fs.readFileSync(
                        path.join(__dirname, "test", projectName, "json-config.json"),
                        "utf8"
                    )
                );
                connection = connection.split(",");
                var key = connection[0];
                var username = connection[1];
                var ip = connection[2];
                var app_installation_dict = logicUtils.createAppInstallationDictionary(jsonData);

                await logger.writeToLogFile(
                    projectName,
                    "APP INSTALL DICT: " + app_installation_dict
                );

                for (let applicationId in app_installation_dict) {
                    await logger.writeToLogFile(
                        projectName,
                        "Entra en el bucle"
                    );
                    let devices = app_installation_dict[applicationId];
            
                    let command = `ssh -o StrictHostKeyChecking=no -i ${key} ${username}@${ip} bash ./scripts/getLogs.sh ${devices} ${applicationId}`;

                    await logger.writeToLogFile(
                        projectName,
                        "Comando: " + command
                    );
            
                    const getLogs = child_process.spawnSync(command, {
                        shell: true,
                        cwd: path.join(__dirname, "projects", projectName),
                        stdio: [process.stdin, process.stdout, process.stderr],
                    });
            
                    if (getLogs.error) {
                        console.error(getLogs.error);
                        await logger.writeToLogFile(
                            projectName,
                            "\n##### Error: " + getLogs.error + " #####\n"
                        );
    
                        DB.executionJSON["execution_state"] =
                            "ERROR: getLogs - cannot get logs with getLogs.sh using ssh";
                        await executionUtils.finishExecution(projectName);
                        reject(getLogs.error);
    
                    } else {
                        await logger.writeToLogFile(
                            projectName,
                            "ssh to virtualized devices completed successfully"
                        );
                        // Filter and download logs
                        command =
                            "scp -i " +
                            key +
                            " -r " +
                            username +
                            "@" +
                            ip +
                            ":devices-logs/ logs/ && node filterLogs.js " +
                            devices;
    
                        const downloadLogs = child_process.spawnSync(command, {
                            shell: true,
                            cwd: path.join(__dirname, "projects", projectName),
                            stdio: [process.stdin, process.stdout, process.stderr],
                        });
    
                        if (downloadLogs.error) {
                            console.error(downloadLogs.error);
                            await logger.writeToLogFile(
                                projectName,
                                "\n##### Error: " + downloadLogs.error + " #####\n"
                            );
    
                            DB.executionJSON["execution_state"] =
                                "ERROR: getLogs - cannot download logs with scp and filter with filterLogs.js";
                            await executionUtils.finishExecution(projectName);
                            reject(downloadLogs.error);
    
                        } else {
                            await logger.writeToLogFile(
                                projectName,
                                "Logs downloaded and filtered succesfully"
                            );
                        }
                    }
                    
                }

            } catch (err) {
                console.error(err);
                await logger.writeToLogFile(
                    projectName,
                    "\n##### Error: " + err + " #####\n"
                );

                DB.executionJSON["execution_state"] =
                    "ERROR: getLogs - unexpected error getting logs";
                await executionUtils.finishExecution(projectName);
                reject(err);
            }
        } else {
            await logger.writeToLogFile(projectName, "The project does not exist");
        }

        resolve();
    });
};

/*
 * <<<< Destroy Perses >>>>
 * This function destroy the defined Terraform infrastructure created in the folder 'projectName'.
 */

exports.destroyPerses = async function (projectName) {
    return new Promise(async (resolve, reject) => {
        
        try {
            await logger.writeToLogFile(
                projectName,
                "----- STARTING DESTROY PHASE -----"
            );

            // Update the execution state in the DB
            DB.executionJSON.execution_state = "RUNNING - DESTROY";
            await DB.putExecutionToDatabaseAPI(
                DB.executionJSON.execution_name,
                DB.executionJSON
            );

            if (fs.existsSync(path.join(__dirname, "projects", projectName))) {
                await terraformUtils.destroyTerraformInfrastructure(projectName);
                //Take the route before sending data to DB
                await s3Operations.uploadLogsToS3(projectName);
                await executionUtils.finishExecution(projectName);
            } else {
                await logger.writeToLogFile(projectName, "The project does not exist");
            }
        } catch (error) {
            console.error(error);

            DB.executionJSON.execution_state =
                "ERROR: destroyPerses - unexpected error destroying Perses infrastructure";
            await executionUtils.associateCostBasedOnTimestamps(projectName);
            await DB.putExecutionToDatabaseAPI(
                DB.executionJSON.execution_name,
                DB.executionJSON
            );
            reject(error);
            process.exit(1);
        }
        resolve();
    });
};
