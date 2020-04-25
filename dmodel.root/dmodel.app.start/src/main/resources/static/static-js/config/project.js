var treeItemId = 0;
var configuration = {};

// ids
var treeId = "#sourcetree";
var saveId = "#save";
var pathId = "#path";

$(document).ready(function() {
	configurePathAjax();
	configureSaveAjax();

	preconfigureSourceTree();
	configureSourceTree();

	getCurrentConfig();
});

function configureSaveAjax() {
	$(saveId).click(function() {
		var nConfig = {
			"projectPath" : $(pathId).val(),
			"sourceFolders" : getCurrentSourceFolders()
		};
		$.postJSON(rest.config.project.save, {
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

function getFullFolderString(item) {
	selItem = $(treeId).jstree().get_node(item);
	data = selItem.text;
	while(selItem !== false && selItem.parent !== "0") {
		selItem = $(treeId).jstree().get_node(selItem.parent);
		data = selItem.text + "/" + data;
	}
	return data;
}

function getCurrentConfig() {
	$.getJSON(rest.config.project.get, function(data) {
		configuration = data;

		$(pathId).val(data.projectPath == null ? "" : data.projectPath);

		// refresh
		pathValueChanged();

		setTimeout(function() {
			sourceFoldersChanged("/");
		}, 500);
	});
}

function sourceFoldersChanged(delemiter) {
	if (configuration.sourceFolders !== null) {
		configuration.sourceFolders.forEach(function(srcFolder) {
			spSrcFolder = srcFolder.split("/");
			markFolder(spSrcFolder);
		});
	}
}

function markFolder(srcFolderArr) {
	var currNode = $(treeId).jstree("get_node", "#");
	if (currNode.children.length == 1) {
		currNode = $(treeId).jstree("get_node", currNode.children[0]);
		for (var index = 0; index < srcFolderArr.length;) {
			var childs = currNode.children;
			for (var c = 0; c < childs.length; c++) {
				var child = $(treeId).jstree("get_node", childs[c]);
				var text = child.text;
				if (text == srcFolderArr[index]) {
					index++;
					currNode = child;
					break;
				}
			}
		}
	}
	
	$(treeId).jstree('select_node', currNode);
}

function configurePathAjax() {
	$(pathId).change(pathValueChanged);
}

function pathValueChanged() {
	$.postJSON(rest.config.project.validatePath, {
		"path" : $(pathId).val()
	}, function(data) {
		applyTick(pathId + "-image", data.valid);
		if (data.valid) {
			$(pathId + "-text").text(data.typeAsText);
			updateFolderTree(data.possibleFolders);
		} else {
			$(pathId + "-text").text("Invalid");
			$(treeId).jstree().delete_node("0");
		}
	});
}

function updateFolderTree(data) {
	$(treeId).jstree().delete_node("0");
	treeItemId = 0;
	buildTreeRecursive(treeId, data, "#");
}

function buildTreeRecursive(tree, data, parent) {
	var currId = treeItemId++;
	var genId = currId.toString(16);
	$(tree).jstree().create_node(parent, {
		"id" : genId,
		"text" : data.name === null ? "Project root" : data.name
	}, "last", function() {
		for (var i = 0; i < data.subfolders.length; i++) {
			buildTreeRecursive(tree, data.subfolders[i], genId);
		}
	});
}

function configureSourceTree() {
	$(treeId).jstree({
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
	$(treeId).bind('before.jstree', function(event, data) {
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