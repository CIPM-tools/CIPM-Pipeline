// some globally used vars
var tickMarkImage = "/config/img/iconfinder_Tick_Mark_1398911.png";
var tickCloseImage = "/config/img/iconfinder_Close_Icon_1398919.png";

// globally used helper functions
$.postJSON = function(url, data, func) {
	$.post(url, data, func, 'json');
}

function applyTick(image_id, value) {
	if (value) {
		$(image_id).attr("src", tickMarkImage);
	} else {
		$(image_id).attr("src", tickCloseImage);
	}
}

// rest paths
var rest = {
	
	// configuration purposes
	config : {
		// project purposes
		project : {
			save : "/config/project/save",
			get : "/config/project/get",
			validatePath : "/config/project/validate-path",
			validateCorr : "/config/project/validate-corr"
		},
		
		models : {
			save : "/config/models/save",
			get : "/config/models/get",
			validate : "/config/models/validate"
		}
	}
		
};