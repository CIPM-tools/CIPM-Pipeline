var treeItemId = 0;
var configuration = {};

// ids
var saveId = "#save";

// helper function
$.postJSON = function(url, data, func) {
	$.post(url, data, func, 'json');
}

$(document).ready(function() {
	configureModelAjax();
	configureSaveAjax();

	getCurrentConfig();
});

function configureSaveAjax() {
	$(saveId).click(function() {
		var nConfig = getModelPaths();
		$.postJSON(rest.config.models.save, {
			"models" : JSON.stringify(nConfig)
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
	var checkedItems = $(treeId).jstree("get_checked", null, true);
	var res = [];
	for (var i = 0; i < checkedItems.length; i++) {
		res.push(getFullFolderString(checkedItems[i]));
	}
	return res;
}

function getCurrentConfig() {
	$.getJSON(rest.config.models.get, function(data) {
		configuration = data;

		// refresh
		applyModelValues();
		modelValueChanged();
	});
}

function applyModelValues() {
	$("#repo_path").val(configuration.repo == null ? "" : configuration.repo);
	$("#sys_path").val(configuration.sys == null ? "" : configuration.sys);
	$("#res_path").val(configuration.res == null ? "" : configuration.res);
	$("#alloc_path").val(configuration.alloc == null ? "" : configuration.alloc);
	$("#usage_path").val(configuration.usage == null ? "" : configuration.usage);
}

function configureModelAjax() {
	$("#repo_path").change(modelValueChanged);
	$("#sys_path").change(modelValueChanged);
	$("#res_path").change(modelValueChanged);
	$("#alloc_path").change(modelValueChanged);
	$("#usage_path").change(modelValueChanged);
}

function modelValueChanged() {
	var modelPaths = getModelPaths();

	$.postJSON(rest.config.models.validate, {
		"models" : JSON.stringify(modelPaths)
	}, function(data) {
		applyTick("#repo_image", data.repo);
		applyTick("#sys_image", data.sys);
		applyTick("#res_image", data.res);
		applyTick("#alloc_image", data.alloc);
		applyTick("#usage_image", data.usage);
	});
}

function getModelPaths() {
	return {
		repo : $("#repo_path").val(),
		sys : $("#sys_path").val(),
		res : $("#res_path").val(),
		alloc : $("#alloc_path").val(),
		usage : $("#usage_path").val()
	};
}