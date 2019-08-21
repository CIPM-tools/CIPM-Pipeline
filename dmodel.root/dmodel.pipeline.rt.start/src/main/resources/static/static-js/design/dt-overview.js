var configuration = {};

var pathId = "#path";
var instrPathId = "#instr_path";
var instrButton = "#instrument";
var corrPath = "#corr_path";

$(document).ready(function() {
	getCurrentConfig();
	
	registerInstrumentation();
});

function getCurrentConfig() {
	$.getJSON(rest.config.project.get, function(data) {
		configuration = data;

		$(pathId).val(data.projectPath == null ? "" : data.projectPath);
		$(instrPathId).val(
				data.instrumentedPath == null ? "" : data.instrumentedPath);
		$(corrPath).val(data.correspondencePath == null ? "" : data.correspondencePath);
		
		// enable button
		$(instrButton).prop('disabled', false);

		// refresh
		pathValueChanged();
		corrPathChanged();
	});
}

function corrPathChanged() {
	$.postJSON(rest.config.project.validateCorr, {
		"path" : $(corrPath).val()
	}, function(data) {
		applyTick("#corr_image", data.success);
		if (!data.success) {
			$(instrButton).prop('disabled', true);
		}
	});
}

function pathValueChanged() {
	$.postJSON(rest.config.project.validatePath, {
		"path" : $(pathId).val()
	}, function(data) {
		applyTick(pathId + "-image", data.valid);
		if (data.valid) {
			$(pathId + "-text").text(data.typeAsText);
		} else {
			$(pathId + "-text").text("Invalid");
			$(instrButton).prop('disabled', true);
		}
	});
}

// DYNAMIC EVENTS
function registerInstrumentation() {
	$(instrButton).click(function() {
	});
}