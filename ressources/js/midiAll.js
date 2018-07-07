global = new Global("global");
global.note = [];
global.ctl = [];
global.midiLearnPatchToSave = [];

function ctl(){
	var args = arrayfromargs(arguments);
	global.ctl[0] = args[0];
	global.ctl[1] = args[1];
	global.ctl[2] = args[2];
}

function note(){
	var args = arrayfromargs(arguments);
	global.note[0] = args[0];
	global.note[1] = args[1];
	global.note[2] = args[2];
}

function midiMode(i){
	if(i == 0){
		global.actual = 0;
		global.actualDelete = 0;
	}else if(i == 1){
		global.actual = 1;
		global.actualDelete = 0;
	}else if(i == 2){
		global.actual = 0;
		global.actualDelete = 1;
	}
	
}

function openAndSaveWindows(){
	for(var i = 0 ; i < global.midiLearnPatchToSave.length ; i++){
		global.midiLearnPatchToSave[i].message("dirty");
		global.midiLearnPatchToSave[i].message("front");
	}
}