
const fs_extra = require('fs-extra');
const fs = require('fs');
const path = require('path');
const yaml = require("js-yaml");
const {spawn}  = require('child_process');

var perses_devices = yaml.load(fs_extra.readFileSync(path.join(__dirname,'perses-devices.yaml'), "utf8"));
        
var number_devices=1;
var port=6001




//WRITE METADATA
var contentMetadata= 'LAB_NAME="kathara-PERSES"\n'+ 
'LAB_DESCRIPTION="A kathara network emulating docker deployment"\n'+ 
'LAB_AUTHOR="PERSES-ORG"\n'+
'router[0]="A"\n'+
'router[bridged]="true"\n'+
'router[sysctl]="net.ipv4.ip_forward=1"\n'+
'router[sysctl]="net.ipv6.conf.all.forwarding=1"\n';


fs.writeFile('./lab/lab.conf', contentMetadata, { flag: "a" }, err => {
	if (err) {
	  console.error(err)
	  return
	}
	//file written successfully
  })



perses_devices.forEach(function(setDevices){

  if(setDevices.type == "mobile"){
    for (let step = 0; step < setDevices.devices; step++) {

      console.log("CREATE SET DEVICES: "+setDevices.id+", create android-"+(number_devices))
      //Create Docker containers
    

		//console.log( 'router[port]='+port+':'+port+'/tcp"' >> ./lab/lab.conf)
		var contentPort='router[port]="'+port+':'+port+'/tcp"\n';

		fs.writeFile('./lab/lab.conf', contentPort, { flag: "a" }, err => {
			if (err) {
			  console.error(err)
			  return
			}
			//file written successfully
		  })

		

		var contentDevice= 'device'+number_devices+'[0]="A" \n'+'device'+
		number_devices+'[image]="spilabuex/android'+setDevices.android_version+':gs6"\n'+
		'device'+number_devices+'[cpus]="'+setDevices.hardware.cpu+'"\n'+
		'device'+number_devices+'[mem]="'+setDevices.hardware.ram+'"\n'

		fs.writeFile('./lab/lab.conf', contentDevice, { flag: "a" }, err => {
			if (err) {
			  console.error(err)
			  return
			}
			//file written successfully
		  })

		// console.log('device'+number_devices+'[0]="A"' >> ./lab/lab.conf)
		// console.log('device'+number_devices+'[image]="android'+setDevices.android_version+'"' >> ./lab/lab.conf)
		// console.log('device'+number_devices+'[cpus]="android'+setDevices.hardware.cpu+'"' >> ./lab/lab.conf)
		// console.log('device'+number_devices+'[mem]="android'+setDevices.hardware.ram+'"' >> ./lab/lab.conf)

		//console.log('device'number_devices': router' >> ./lab/lab.dep)


		fs.writeFile('./lab/lab.dep', 'device'+number_devices+': router \n', { flag: "a" }, err => {
			if (err) {
			  console.error(err)
			  return
			}
			//file written successfully
		  })

		number_devices++;   
		port++;     
		
         

	}



  }

});