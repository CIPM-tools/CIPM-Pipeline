var treeItemId = 0;

var validation = {
	models : false,
	paths : false
}

var configuration = {}

$(document).ready(function() {
	configurePathAjax();
	configureModelAjax();
	configureSaveAjax();

	preconfigureSourceTree();
	configureSourceTree();

	getCurrentConfig();
});

$.postJSON = function(url, data, func) {
	$.post(url, data, func, 'json');
}

function configureSaveAjax() {
	$("#save").click(function() {
		var nConfig = {
			"projectPath" : $("#path").val(),
			"instrumentedPath" : $("#instr_path").val()
		};
		$.postJSON("/config/save", {
			"config" : JSON.stringify(nConfig)
		}, function(data) {
			console.log(data);
			if (data.success) {
				toastr.success('Saved configuration successfully.', 'Success');
			} else {
				toastr.error('Failed to save configuration.', 'Error');
			}
		});
	});
}

function getCurrentConfig() {
	$.getJSON("/config/get", function(data) {
		configuration = data;

		$("#path").val(data.projectPath == null ? "" : data.projectPath);
		$("#instr_path").val(
				data.instrumentedPath == null ? "" : data.instrumentedPath);
		
		// refresh
		modelValueChanged();
		pathValueChanged();
		
		sourceFoldersChanged("/");
	});
}

function sourceFoldersChanged(delemiter) {
	configuration.sourceFolders.forEach(function(srcFolder) {
		spSrcFolder = srcFolder.split("/");
		markFolder(spSrcFolder);
	});
}

function markFolder(srcFolderArr) {
	var currNode = $('#sourcetree').jstree("get_node", "#");
	console.log(currNode);
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
		$(image_id).attr("src", "img/iconfinder_Tick_Mark_1398911.png");
	} else {
		$(image_id).attr("src", "img/iconfinder_Close_Icon_1398919.png");
	}
}

function configurePathAjax() {
	$("#path").change(pathValueChanged);
}

function pathValueChanged() {
	$.postJSON("/config/validate-path", {
		"path" : $("#path").val()
	}, function(data) {
		if (data.valid) {
			$("#path-text").text(data.typeAsText);
			$("#path-image").attr("src",
					"img/iconfinder_Tick_Mark_1398911.png");
			updateFolderTree(data.possibleFolders);
		} else {
			$("#path-text").text("Invalid");
			$("#path-image").attr("src",
					"img/iconfinder_Close_Icon_1398919.png");
			$("#sourcetree").jstree().delete_node("0");
		}
	});
}

function updateFolderTree(data) {
	$("#sourcetree").jstree().delete_node("0");
	treeItemId = 0;
	buildTreeRecursive("#sourcetree", data, "#");
}

function buildTreeRecursive(tree, data, parent) {
	var currId = treeItemId++;
	$(tree).jstree().create_node(parent, {
		"id" : currId.toString(16),
		"text" : data.name === null ? "Project root" : data.name
	}, "last", function() {
	});
	for (var i = 0; i < data.subfolders.length; i++) {
		buildTreeRecursive(tree, data.subfolders[i], currId.toString(16));
	}
}

function configureSourceTree() {
	$("#sourcetree").jstree({
		'checkbox' : {
			three_state : false
		},
		plugins : [ "checkbox" ],
		'core' : {
			'check_callback' : true
		}
	});
}

function preconfigureSourceTree() {
	$('#sourcetree').bind('before.jstree', function(event, data) {
		switch (data.plugin) {
		case 'ui':
			if (data.inst.is_leaf(data.args[0])) {
				return false;
			}
			break;
		default:
			break;
		}
	})
}