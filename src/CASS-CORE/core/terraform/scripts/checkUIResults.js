const fs = require('fs-extra');
const path = require('path');


//Version 2
var files = 0;
var actualResults=0;

var beginHR = process.hrtime()
var begin = beginHR[0] * 1000000 + beginHR[1] / 1000;




checkFiles();



async function checkFiles(){
  
  await sleep(3000); // sleep 3s
  
  fs.readdir(path.join(__dirname, '../devices-logs/espresso/'), function(err, filenames) {
    if (err) {
      return console.log(err);
    }
    
    files=filenames.length
    filenames.forEach(function(filename) {

    var file=fs.readFileSync(path.join(__dirname,'../devices-logs/espresso/') + filename, 'utf-8');

    if(file.indexOf('INSTRUMENTATION_CODE: -1') >= 0){
          actualResults++;
    }

    var endHR = process.hrtime()
    var end = endHR[0] * 1000000 + endHR[1] / 1000;
    var duration = (end - begin) / 1000;
    var roundedDuration = Math.round(duration * 1000) / 1000;

    //CHECK IF FILE IS EMPTY
    if(file.length == 0 && roundedDuration >= 35000)
      actualResults++;


    //    TODO: CATCH TIME 
    //   // let arr = content.split(/\r?\n/);
    //   // arr.forEach((line, idx)=> {
    //   //     if(line.includes("INSTRUMENTATION_CODE: -1")){
    //   //       now++;
    //   //     }
    //   // });
    // });
    
    //console.log("---------------------")
  });
  if(actualResults!=files){
    actualResults=0
    checkFiles()
  }
});
  
  
}


function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}





