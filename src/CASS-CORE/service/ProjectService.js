/* eslint-disable no-unused-vars */
/* eslint-disable no-async-promise-executor */
'use strict';
const perses = require('../commands.js');
const DB = require('../DBOperations.js');
const cluster = require('cluster');
const numCPUs = require('os').cpus().length;
const logger = require('../logger.js')
const folderUtils = require("../folders.js");

// Number of current working threads
var numWorkers = 0;


/* As `terraform init` creates asynchronous processes and there is no way 
  of dynamically waiting for them to complete, a wait of 30s is made for them to finish */
const terraform_init_wait = 60000;

// Sleep function
function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

// Action to take if current process is not the main one (launch the Perses Workflow)
if(!cluster.isMaster){
  console.log(`Worker ${process.pid} starting Perses workflow`);
  process.on('message', async (msg) => {
      const { project_name, user_name, api_key } = msg;
      await executeWorkflow(project_name, user_name, api_key);
      await folderUtils.removeProjectAndFinish(project_name, true, false);
  });
}


/**
 * Performs the whole execution process to a Perses project.
 * Based on a project name, creates a local Perses project (setup) and executes the Perses workflow (launch, tests, logs and destroy).
 *
 * project_name : String Name of the project to execute
 * returns : Project
 **/
exports.launchProject = async function (project_name, user_name, api_key) {
  return new Promise(async function (resolve, reject) {
      // Create a new worker thread and make it execute the Perses Workflow
      if (cluster.isMaster){
          console.log(`Primary ${process.pid} is going to fork`);
          const worker = cluster.fork();
          // Send project name, user_name, and api_key to worker thread as an object
          worker.send({ project_name, user_name, api_key });
          numWorkers = numWorkers + 1;
          console.log(`There are now ${numWorkers} currently active worker threads`);

          // Worker thread finishes
          cluster.on('exit', (worker, code) => {
              console.log(`Worker ${worker.process.pid} died with code ${code}`);
              numWorkers = numWorkers - 1;
          });

          resolve("Perses workflow execution will begin soon.");
      }
  });
}

//   })   
// }

/**
 * Executes the workflow. Used in worker threads.
 * 
 * project_name: Name of the project in which the workflow will be executed
 */
async function executeWorkflow(project_name, user_name, api_key){
  return new Promise(async function (resolve, reject) {
  try {
    console.warn = () => {};
    
    // Get Project information from the database archives
    //console.log("-> Obteniendo proyecto de la BD");
    var responseJSONFromDatabase = await DB.getProjectFromDatabaseAPI(project_name, user_name, api_key);
    if(responseJSONFromDatabase === null || responseJSONFromDatabase === undefined){
      //console.log(responseJSONFromDatabase);
      throw "Project not found in the database.";
    }

    //console.log("-> Pasa la parte inicial de comprobaciones");
    
    // --- PERSES WORKFLOW --- //
    
    await perses.setupPerses(project_name, responseJSONFromDatabase.data[0]);
    await sleep(terraform_init_wait); // Check the comment in "terraform_init_wait" above
    logger.writeToLogFile(project_name, "\n----- END OF SETUP PHASE -----\n");

    //console.log("Pasa el setup");
    
    await perses.launchPerses(project_name);
    logger.writeToLogFile(project_name, "\n----- END OF LAUNCH PHASE -----\n");

    //console.log("Pasa el launch");

    await perses.runTests(project_name);
    logger.writeToLogFile(project_name, "\n----- END OF TESTS PHASE -----\n");

    await perses.getLogs(project_name);
    logger.writeToLogFile(project_name, "\n----- END OF LOGS PHASE -----\n");

    await perses.destroyPerses(project_name);
    logger.writeToLogFile(project_name, "\n----- END OF DESTROY PHASE -----\n");

  } catch (error) {

    console.error("\n###### Error: " + error + " ######\n");
    await perses.destroyPerses(project_name);
    reject(error);

  }

  resolve();
  logger.writeToLogFile(project_name, "\n----- END OF PERSES WORKFLOW -----\n");
  });
}
