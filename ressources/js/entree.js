var send = [];
var meters = [];
var gains = [];
var comment = [];
var ancien = 0;
var adc;
var loadmess;

function msg_int(i){
	
	if(typeof(adc) == "undefined"){
		var obj = this.patcher.firstobject;
		while(obj){
			if(obj.maxclass=="send~"){
				send[send.length] = obj;
			}else if(obj.maxclass=="meter~"){
				meters[meters.length] = obj;
			}else if(obj.maxclass=="gain~"){
				gains[gains.length] = obj;
			}else if(obj.maxclass=="message"){
				comment[comment.length] = obj;
			}else if(obj.maxclass=="adc~"){
				adc = obj;
			}
			obj = obj.nextobject;
		}
		ancien = send.length;
	}
	
	this.patcher.remove(adc);
	this.patcher.remove(loadmess);
	for(var j = 0 ; j < ancien ; j++){
		//this.patcher.remove(receive[j]);
		this.patcher.remove(meters[j]);
		this.patcher.remove(gains[j]);
		this.patcher.remove(send[j]);
		this.patcher.remove(comment[j]);
	}
	args = [];
	for(var j = 1 ; j <= i ; j++){
		args[j-1] = j;
	}
	if(i>0){
		adc = this.patcher.newdefault(0, 0, "adc~", args);
		loadmess = this.patcher.newdefault(200, 0, "loadmess", 127);
	}
	for(var j = 0 ; j < i ; j++){
		meters[j] = this.patcher.newdefault(100 + j * 200, 450, "meter~", "@presentation", 1, "@presentation_rect", (j*50)+20,0,20,120);
		gains[j] = this.patcher.newdefault(100 + j * 200, 450, "gain~", "@presentation", 1, "@presentation_rect", j*50,0,20,120);
		send[j] = this.patcher.newdefault(100 + j * 200, 450, "send~", "CGEinput-" + j);
		comment[j] = this.patcher.newdefault(100 + j * 200, 450, "message", "@presentation", 1, "@presentation_rect", j*50,123,40,27);
		comment[j].message("set", j+1);
		this.patcher.connect(adc, j, gains[j], 0);
		this.patcher.connect(loadmess, 0, gains[j], 0);
		gains[j].message(127);
		this.patcher.connect(gains[j], 0, meters[j], 0);
		this.patcher.connect(gains[j], 0, send[j], 0);
	}
	
	ancien = i;
}