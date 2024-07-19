const fs = require('fs'); // This is for file system operations
const path = require('path'); // This is for path operations

// This function creates the log file with name localLogs.txt
async function createLogFile(projectName) {
    
    const logFilePath = path.join(__dirname, "test", projectName, 'localLogs.txt');

    //Check whether the file already exists, if so, it replaces it
    if (fs.existsSync(logFilePath)) {
        fs.unlinkSync(logFilePath);
    }

    // Creates the empty log file
    fs.writeFileSync(logFilePath, '', { flag: 'w' });
    console.log(`Fichero de logs creado en: ${logFilePath}`);
}

//Function to write into the log file
async function writeToLogFile(projectName, data) {
    
    const logFilePath = path.join(__dirname, "test", projectName, 'localLogs.txt');
    console.log(data);
    // Opens the file in write mode and appends the content
    fs.appendFileSync(logFilePath, `${data}\n`);
}

// Exports the functions
module.exports = {
    createLogFile,
    writeToLogFile,
};
