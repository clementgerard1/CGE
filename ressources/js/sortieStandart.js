var send = [];
var matrix;
var ramp;
var actuel = 0;
var vitesse = 1000;

function msg_int(i){
	/*matrixCtrl = this.patcher.getnamed(jsarguments[1] + "-matrixCtrl");
	receive = this.patcher.getnamed(jsarguments[1] + "-receive");
	ramp = this.patcher.getnamed(jsarguments[1] + "-ramp");
	for( var j = 0 ; j < actuel ; j++){
		this.patcher.remove(send[j]);
	}
	this.patcher.remove(matrix);
	matrix = this.patcher.newdefault(67, 588, "matrix~", 1, i, 1., "@ramp", vitesse);
	this.patcher.connect(matrixCtrl, 0, matrix, 0);
	this.patcher.connect(receive, 0, matrix, 0);
	this.patcher.connect(ramp, 0, matrix, 0);
	for( var j = 0 ; j < i ; j++){
		send[j] = this.patcher.newdefault(67 + 100 * j, 610, "send~", jsarguments[2] + "-master-" + j);
		this.patcher.connect(matrix, j, send[j], 0);
	}
	actuel = i;*/
}

function setVitesse(i){
	vitesse = i;
}