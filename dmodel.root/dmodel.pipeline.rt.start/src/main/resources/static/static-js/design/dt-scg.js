var treeItemId = 0;

// ids
var treeId = "#jartree";

$(document).ready(function() {
	// make full sized
	$(".container-fluid").css('height', '100%');
	
	createGraph();
	
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
		console.log(result);
	});
}


// GRAPH FUNCTIONS
function updateGraph() {
	$.getJSON(rest.design.scg.get, function(scg) {
		createGraph(scg);
	});
}

function createGraph() {
	var nodes = [ {
		id : 0,
		label : "0",
		group : 'source'
	}, {
		id : 1,
		label : "1",
		group : 'icons'
	}, {
		id : 2,
		label : "2",
		group : 'icons'
	}, {
		id : 3,
		label : "3",
		group : 'icons'
	}, {
		id : 4,
		label : "4",
		group : 'icons'
	}, {
		id : 5,
		label : "5",
		group : 'icons'
	}, {
		id : 6,
		label : "6",
		group : 'icons'
	}, {
		id : 7,
		label : "7",
		group : 'icons'
	}, {
		id : 8,
		label : "8",
		group : 'icons'
	}, {
		id : 9,
		label : "9",
		group : 'icons'
	}, {
		id : 10,
		label : "10",
		group : 'mints'
	}, {
		id : 11,
		label : "11",
		group : 'mints'
	}, {
		id : 12,
		label : "12",
		group : 'mints'
	}, {
		id : 13,
		label : "13",
		group : 'mints'
	}, {
		id : 14,
		label : "14",
		group : 'mints'
	}, {
		id : 15,
		group : 'dotsWithLabel'
	}, {
		id : 16,
		group : 'dotsWithLabel'
	}, {
		id : 17,
		group : 'dotsWithLabel'
	}, {
		id : 18,
		group : 'dotsWithLabel'
	}, {
		id : 19,
		group : 'dotsWithLabel'
	}, {
		id : 20,
		label : "diamonds",
		group : 'diamonds'
	}, {
		id : 21,
		label : "diamonds",
		group : 'diamonds'
	}, {
		id : 22,
		label : "diamonds",
		group : 'diamonds'
	}, {
		id : 23,
		label : "diamonds",
		group : 'diamonds'
	}, ];
	var edges = [ {
		from : 1,
		to : 0
	}, {
		from : 2,
		to : 0
	}, {
		from : 4,
		to : 3
	}, {
		from : 5,
		to : 4
	}, {
		from : 4,
		to : 0
	}, {
		from : 7,
		to : 6
	}, {
		from : 8,
		to : 7
	}, {
		from : 7,
		to : 0
	}, {
		from : 10,
		to : 9
	}, {
		from : 11,
		to : 10
	}, {
		from : 10,
		to : 4
	}, {
		from : 13,
		to : 12
	}, {
		from : 14,
		to : 13
	}, {
		from : 13,
		to : 0
	}, {
		from : 16,
		to : 15
	}, {
		from : 17,
		to : 15
	}, {
		from : 15,
		to : 10
	}, {
		from : 19,
		to : 18
	}, {
		from : 20,
		to : 19
	}, {
		from : 19,
		to : 4
	}, {
		from : 22,
		to : 21
	}, {
		from : 23,
		to : 22
	}, {
		from : 23,
		to : 0
	}, ]

	// create a network
	var container = document.getElementById('scg-network');
	var data = {
		nodes : nodes,
		edges : edges
	};
	var options = {
		autoResize: false,
		nodes : {
			shape : 'dot',
			size : 20,
			font : {
				size : 15,
				color : '#ffffff'
			},
			borderWidth : 2
		},
		edges : {
			width : 2
		},
		groups : {
			diamonds : {
				color : {
					background : 'red',
					border : 'white'
				},
				shape : 'diamond'
			},
			dotsWithLabel : {
				label : "I'm a dot!",
				shape : 'dot',
				color : 'cyan'
			},
			mints : {
				color : 'rgb(0,255,140)'
			},
			icons : {
				shape : 'icon',
				icon : {
					face : 'FontAwesome',
					code : '\uf0c0',
					size : 50,
					color : 'orange'
				}
			},
			source : {
				color : {
					border : 'white'
				}
			}
		}
	};
	var network = new vis.Network(container, data, options);
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
		plugins : [ "checkbox" , "types" ],
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