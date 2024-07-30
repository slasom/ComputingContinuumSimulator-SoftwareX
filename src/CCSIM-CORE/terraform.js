#!/usr/bin/env node
/* eslint-disable no-async-promise-executor */

"use strict";
const fs = require("fs-extra");
const path = require("path");
const DB = require("./DBOperations");
const executionUtils = require("./executions");
const folderUtils = require("./folders");
const child_process = require("child_process");
const logger = require("./logger");
const yaml = require("js-yaml");

// Copies all the terraform files into the project folder
async function initiateTerraform(projectName, reject) {
  // Copy all the terraform files into the project folder
  fs.copy(
    path.join(__dirname, "core", "terraform"),
    path.join(__dirname, "projects", projectName),
    async function (err) {
      if (err) {
        console.log(
          "\n###### Error copying the scripts and other files to the project folder... ######\n"
        );

        DB.executionJSON["execution_state"] =
          "ERROR: setup - cannot copy terraform files to project folder";
        executionUtils.associateCostBasedOnTimestamps(projectName);
        await DB.putExecutionToDatabaseAPI(
          DB.executionJSON["execution_name"],
          DB.executionJSON
        );

        // Delete the project folder with all the files if an error appears
        await folderUtils.removeProjectFolder(projectName);
        return console.log(err); // Send back an error if something went wrong
      } else {
        try {
          await logger.writeToLogFile(
            projectName,
            "Scripts and tf files copied successfully into the project folder"
          );
          // Init Terraform and plan
          const ls = child_process.spawnSync(
            "terraform init && terraform plan ",
            {
              shell: true,
              cwd: path.join(__dirname, "projects", projectName),
              stdio: [process.stdin, process.stdout, process.stderr],
            }
          );

          if (ls.error) {
            await logger.writeToLogFile(
              projectName,
              "\n##### Error at terraform init and plan: " +
              ls.error +
              " #####\n"
            );
            DB.executionJSON["execution_state"] =
              "ERROR: setup - Terraform could not get past init && plan";
            executionUtils.associateCostBasedOnTimestamps(projectName);
            await DB.putExecutionToDatabaseAPI(
              DB.executionJSON["execution_name"],
              DB.executionJSON
            );
            throw ls.error;
          }

        } catch (err) {
          console.error(err);
          await logger.writeToLogFile(
            projectName,
            "\n##### Error: " + err + " #####\n"
          );

          DB.executionJSON["execution_state"] =
            "ERROR: setup - terraform init && plan encountered an error";
          await executionUtils.finishExecution(projectName);
          await folderUtils.removeProjectFolder(projectName);
          reject(err);
        }
      }
    }
  );
}

async function destroyTerraformInfrastructure(projectName) {
  return new Promise(async (resolve, reject) => {
    try {
      const terraformCommand = "terraform destroy -auto-approve";
      const projectPath = path.join(__dirname, "projects", projectName);
      const terraform = await executionUtils.executeCommand(
        terraformCommand,
        projectPath
      );

      if (terraform.status === 0) {
        await logger.writeToLogFile(projectName, "Terraform Destroy completed");
        resolve();
      } else {
        await logger.writeToLogFile(projectName, "Error in destroy command");
        reject(new Error("Error in destroy command"));
      }
    } catch (error) {
      await logger.writeToLogFile(projectName, "Error: " + error);
      reject(error);
    }
  });
}

// Export the functions
module.exports = {
  initiateTerraform,
  destroyTerraformInfrastructure,
};
