var treeItemId = 0;

// ids
var treeId = "#jartree";

var scgNetwork = undefined;
var scgNodes = undefined;
var scgEdges = undefined;

$(document).ready(function() {
	// make full sized
	$(".container-fluid").css('height', '100%');
	
	createGraph();
	updateGraph();
	
	configureSourceTree();
	updateFolders();
	
	registerEvents();
});

// LOGIC
function updateFolders() {
	$.getJSON(rest.design.scg.jars, function(data) {
		if (data.success === undefined || data.success === true) {
			updateFolderTree(data);
		}
	});
}

function registerEvents() {
	$("#build-scg").click(buildSCG);
}

function buildSCG() {
	var jarFiles = getCurrentSourceFolders();
	var extractBefore = $("#mapBefore")[0].checked;
	
	$.postJSON(rest.design.scg.build, {
		jarFiles : JSON.stringify(jarFiles),
		extractMappingBefore : extractBefore
	}, function(result) {
		if (result.success) {
			updateGraph();
			toastr.success("Successfully extracted and updated the SCG.", "Success");
		} else {
			toastr.error("Failed to extract the SCG, check your project configuration and the selected JAR files.", "Error");
		}
	});
}


// GRAPH FUNCTIONS
function updateGraph() {
	$.getJSON(rest.design.scg.get, function(scg) {
		applyData(scgNetwork, scg);
	});
}

function createGraph() {
	scgNodes = new vis.DataSet({});
	scgEdges = new vis.DataSet({});
	
	// create a network
	var container = document.getElementById('scg-network');
	var data = {
		nodes : scgNodes,
		edges : scgEdges
	};
	var options = {
			autoResize: false,
			nodes: {
	            shape: 'dot',
	            size: 20,
	            font: {
	                size: 15,
	                color: '#ffffff'
	            },
	            borderWidth: 2
	        },
	        edges: {
	            width: 2,
	            font: {
	            	strokeWidth : 0,
	            	strokeColor : 'white',
	            	color : 'white'
	            }
	        }
	    };
	
	scgNetwork = new vis.Network(container, data, options);
	scgNetwork.fit();
}

function applyData(graph, scg) {
	// add data
	var groupMapping = {};
	var currentGroupId = 0;
	
	// clear
	scgNodes.clear();
	scgEdges.clear();

	scg.nodes.forEach(function(node) {
		if (groupMapping.hasOwnProperty(node.parentId)) {
			var groupId = groupMapping[node.parentId];
		} else {
			var groupId = currentGroupId++;
			groupMapping[node.parentId] = groupId;
		}
		
		scgNodes.add({
			id : node.id,
			label : "<" + node.parentName + ">\n" + node.name,
			group : groupId
		});
	});
	
	scg.edges.forEach(function(edge) {
		scgEdges.add({
			from : edge.from,
			to : edge.to,
			label : "<ExternalCall>\n" + edge.extName,
			arrows: 'to',
			smooth: false
		});
	});
	
	// redraw
	scgNetwork.body.emitter.emit('_dataChanged');
	scgNetwork.redraw();
}


// TREE FUNCTIONS
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
		"text" : data.name === null ? "Project root" : data.name,
		"type" : "default",
		"a_attr" : {
	        class: "no_checkbox"
	    }
	}, "last", function() {
		for (var i = 0; i < data.subfolders.length; i++) {
			buildTreeRecursive(tree, data.subfolders[i], genId);
		}
	});
	
	data.files.forEach(function(file) {
		var subId = treeItemId++;
		var subGenId = subId.toString(16);
		
		$(tree).jstree().create_node(genId, {
			"id" : subGenId,
			"text" : file,
			"type" : "jar"
		});
	});
}

function configureSourceTree() {
	$(treeId).jstree({
		"types" : {
		      "default" : {
		        "icon" : "fa fa-folder"
		      },
		      "jar" : {
		        "icon" : "fab fa-java"
		      }
		},
		'checkbox' : {
			three_state : false
		},
		plugins : [ "checkbox" , "types", "conditionalselect" ],
		'core' : {
			'check_callback' : true
		},
		conditionalselect : function (node) { return node.type === "jar"; }
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