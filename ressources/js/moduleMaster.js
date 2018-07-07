var masterSlider;
var meters = [];
var receive = [];
var send = [];
var ancien;

function msg_int(i){
	
	if(typeof(adc) == "undefined"){
		var obj = this.patcher.firstobject;
		while(obj){
			if(obj.maxclass=="patcher"){
				meters[meters.length] = obj;
			}
			obj = obj.nextobject;
		}
		ancien = meters.length;
	}
	post(ancien);
	for(var j = 0 ; j < ancien ; j++){
		this.patcher.remove(meters[j]);
	}
	moduleSlider = this.patcher.getnamed(jsarguments[1] + "-CGEModule");
	for(var j = 0 ; j < i ; j++){
		meters[j] = this.patcher.newdefault(50 * j, 200, "bpatcher", "tableSliderModule", "@args", jsarguments[2], j, "@presentation", 1, "@presentation_rect", 50 + (50/i) * j, 22, 50/i, 128);
		this.patcher.connect(moduleSlider, 0, meters[j], 0);
	}
	
	ancien = i;
}