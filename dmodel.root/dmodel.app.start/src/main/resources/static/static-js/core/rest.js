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
			validate : "/config/models/validate",
			initInstrumentation : "/config/models/inm/init"
		},
		
		conceptual : {
			get : "/config/conceptual/get",
			validate : "/config/conceptual/validate",
			save : "/config/conceptual/save"
		}
	},
	
	design : {
		instrumentation : {
			instrument : "/design/instrument",
			status : "/design/instrument/status",
			validate : "/design/instrument/validate"
		},
		
		repository : {
			get : "/design/system/repository/get"
		},
		
		build : {
			start : "/design/system/build/start",
			getConflict : "/design/system/build/conflict/get",
			solveConflict : "/design/system/build/conflict/solve",
			status : "/design/system/build/status",
			get : "/design/system/build/get"
		},
		
		mapping : {
			resolve : "/design/mapping/resolve"
		},
		
		scg : {
			get : "/design/system/scg/get",
			build : "/design/system/scg/build",
			jars : "/design/system/scg/jars"
		}
	},
	
	runtime : {
		pipeline : {
			status : "/runtime/pipeline/status"
		},
		validation : {
			overview : "/runtime/validation/overview",
			points : "/runtime/validation/points",
			data : "/runtime/validation/data"
		},
		performance : "/runtime/pipeline/performance"
	},
	
	health : {
		get : "/health/get",
		problems : "health/problems"
	}
		
};