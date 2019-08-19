var treeItemId = 0;
var configuration = {};

var configRestPath = "/config/save/models";

// ids
// TODO

// images
var tickMarkImage = "img/iconfinder_Tick_Mark_1398911.png";
var tickCloseImage = "img/iconfinder_Close_Icon_1398919.png";

// helper function
$.postJSON = function(url, data, func) {
	$.post(url, data, func, 'json');
}

$(document).ready(function() {
	configureModelAjax();

	getCurrentConfig();
});

function configureSaveAjax() {
	$(saveId).click(function() {
		var nConfig = {
		};
		$.postJSON(configRestPath, {
			"config" : JSON.stringify(nConfig)
		}, function(data) {
			if (data.success) {
				toastr.success('Saved configuration successfully.', 'Success');
			} else {
				toastr.error('Failed to save configuration.', 'Error');
			}
		});
	});
}

function getCurrentSourceFolders() {
	var checkedItems = $(treeId).jstree("get_checked",null,true);
	var res = [];
	for (var i = 0; i < checkedItems.length; i++) {
		res.push(getFullFolderString(checkedItems[i]));
	}
	return res;
}

function getCurrentConfig() {
	$.getJSON("/config/get", function(data) {
		configuration = data;
		
		// refresh
		modelValueChanged();
	});
}

function configureModelAjax() {
	$("#repo_path").change(modelValueChanged);
	$("#sys_path").change(modelValueChanged);
	$("#res_path").change(modelValueChanged);
	$("#alloc_path").change(modelValueChanged);
	$("#usage_model").change(modelValueChanged);
}

function modelValueChanged() {
	var modelPaths = {
		repo : $("#repo_path").val(),
		sys : $("#sys_path").val(),
		res : $("#res_path").val(),
		alloc : $("#alloc_path").val(),
		usage : $("#usage_model").val()
	};

	$.postJSON("/config/validate-models", {
		"models" : JSON.stringify(modelPaths)
	}, function(data) {
		applyTick("#repo_image", data.repo);
		applyTick("#sys_image", data.sys);
		applyTick("#res_image", data.res);
		applyTick("#alloc_image", data.alloc);
		applyTick("#usage_image", data.usage);
	});
}

function applyTick(image_id, value) {
	if (value) {
		$(image_id).attr("src", tickMarkImage);
	} else {
		$(image_id).attr("src", tickCloseImage);
	}
}