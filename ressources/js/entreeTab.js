
function msg_int(i){
	var args = []; 
	args[0] = "all";
	for( var j = 0 ; j < i ; j++ ){
		args[j + 1] = (j + 1);
	}
	outlet(0, "tabs", args);
	outlet(0, "presentation_rect", 0, 0, (i + 2) * 48, 22);
	outlet(0, 0);
}