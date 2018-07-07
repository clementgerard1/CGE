var global = new Global("global");
if(!global.actual){
	global.actual = 0;
}

var midiObject;
var midiObjectExist = false;
var object = this.patcher.getnamed(jsarguments[1] + "-slider");
var thisobject = this.patcher.getnamed(jsarguments[3]);

function init(){
	object = this.patcher.getnamed(jsarguments[1] + "-slider");
	thisobject = this.patcher.getnamed(jsarguments[3]);
	this.patcher.hiddenconnect(object, 0, thisobject, 0);
	midiObject= this.patcher.getnamed(jsarguments[1] + "-cgeDetect");
}

function anything(a){
	if(global.actual && !midiObjectExist){
		if(jsarguments[2] == "note"){
			midiObject = this.patcher.newdefault(0, 0, "detectNote", global.note[0], global.note[2]);
		}else if(jsarguments[2] == "ctl"){
			midiObject = this.patcher.newdefault(0, 0, "detectCtl", global.ctl[1], global.ctl[2]);
			midiObject.hidden = 1;
		}
		midiObject.varname =  "#0-cgeDetect";
		midiObject.hidden = 1;
		object.message("bgcolor", 0, 0, 255);
		this.patcher.hiddenconnect(midiObject, 0, object, 0);
		midiObjectExist = true;
		
		var save = true;
		var k = 0;
	
		while(k < global.midiLearnPatchToSave.length && save){
			if(global.midiLearnPatchToSave[k] === this.patcher){
				save = false;
			}
			k++;
		}
		if(save){
			global.midiLearnPatchToSave[global.midiLearnPatchToSave.length] = this.patcher;
		}
	}
	if(global.actualDelete){
		if(midiObject){
			this.patcher.remove(midiObject);
			midiObjectExist = false;
			object.message("bgcolor", 0, 0, 0);
		}
	}
}
