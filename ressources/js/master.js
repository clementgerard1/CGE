var masterSlider;
var meters = [];
var receive = [];
var send = [];
var ancien;
var dac;
var masterModule;

function msg_int(i){
	
	if(typeof(dac) == "undefined"){
		var obj = this.patcher.getnamed("CGEMasterModulePatch").subpatcher().firstobject;
		while(obj){
			if(obj.maxclass=="receive~"){
				receive[receive.length] = obj;
			}else if(obj.maxclass=="send~"){
				send[send.length] = obj;
			}
			obj = obj.nextobject;
		}
		obj = this.patcher.firstobject;
		while(obj){
			if(obj.maxclass=="dac~"){
				dac = obj;
			}else if((obj.maxclass=="patcher") && (obj.varname != "CGEMasterModulePatch")){
				meters[meters.length] = obj;
			}
			obj = obj.nextobject;
		}
		ancien = meters.length;
	}
	
	this.patcher.remove(dac);
	for(var j = 0 ; j < ancien ; j++){
		this.patcher.remove(meters[j]);
		this.patcher.remove(receive[j]);
		this.patcher.remove(send[j]);
	}
	masterModule = this.patcher.getnamed("CGEMasterModulePatch");
	masterSlider = this.patcher.getnamed(jsarguments[1] + "-CGEMaster");
	args = [];
	for(var j = 1 ; j <= i ; j++){
		args[j-1] = j;
	}
	dac = this.patcher.newdefault(0, 0, "dac~", args);
	for(var j = 0 ; j < i ; j++){
		receive[j] = masterModule.subpatcher().newdefault(100 + j * 200, 50, "receive~", "CGEmasterPre-" + j); 
		send[j] = masterModule.subpatcher().newdefault(100 + j * 200, 450, "send~", "CGEmasterPost-" + j);
		masterModule.subpatcher().connect(receive[j], 0, send[j], 0);
		meters[j] = this.patcher.newdefault(50 * j, 200, "bpatcher", "tableSlider", "@args", j, "@presentation", 1, "@presentation_rect", 50 + (50/i) * j, 22, 50/i, 128);
		this.patcher.connect(masterSlider, 0, meters[j], 0);
		this.patcher.connect(meters[j], 0, dac, j);
	}
	this.patcher.connect(this.patcher.getnamed(jsarguments[1]+"-onoff"), 0, dac, 0)
	dac.message(0);
	ancien = i;
}