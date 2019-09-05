var statusUpdateInterval = 250;

$(document).ready(function() {
	// make full sized
	$(".container-fluid").css('height', '100%');

	// remove outer paddings
	$(".container-fluid").css("padding-left", "0px");
	$(".container-fluid").css("padding-right", "0px");

	$("#content-wrapper").css("padding-top", "0px");
	$("#content-wrapper").css("padding-bottom", "45px");

	Split([ '#top', '#bottom' ], {
		direction : 'vertical',
		sizes : [ 70, 30 ]
	});
	Split([ '#top-left', '#top-right' ], {
		sizes : [ 65, 35 ]
	});

	if (!system_present) {
		grayOutSystem();
	}

	registerEvents();
});

function grayOutSystem() {
	$("#old-system-view").addClass("progress-bar-striped progress-bar bg-dark");
}

function registerEvents() {
	$("#start-procedure").click(function() {
		$("#start-procedure").attr("disabled", "true");
		$("#build-callgraph").removeAttr("disabled");
	});

	$("#build-callgraph").click(function() {
		$("#build-callgraph").attr("disabled", "true");
		triggerCallgraphBuild();
	});

	$("#skip-system")
			.click(
					function() {
						$("#system-view").addClass(
								"progress-bar-striped progress-bar bg-dark");
						$("#build-system").attr("disabled", "true");
						$("#skip-system").attr("disabled", "true");

						// alert it
						toastr
								.success(
										'Finished system building process.\nReturning in 5 seconds.',
										'Success');
						setTimeout(function() {
							window.location.href = "/design/";
						}, 5000);
					});
	
	$("#build-system").click(function() {
		startBuildingProcedure($("#system-view").get(0));
	});
}

function triggerCallgraphBuild() {
	$.postJSON(rest.design.callgraph.build, {}, function(data) {
		// start getting updates
		setTimeout(updateCallGraphStatus, statusUpdateInterval);
	});
}

function updateCallGraphStatus() {
	$.getJSON(rest.design.callgraph.finished, function(data) {
		if (data.finished) {
			$.getJSON(rest.design.callgraph.get, function(data) {
				buildCallGraph(data);
			});
		} else {
			setTimeout(updateCallGraphStatus, statusUpdateInterval);
		}
	});
}

function buildCallGraph(graph) {
	console.log(graph);
	// build nodes & edges

	// 1. build nodes
	var nodes = [];
	var components = new Set();
	graph.nodes.forEach(function(node) {
		nodes.push({
			data : {
				parent : node.componentId,
				text : node.serviceName,
				id : node.serviceId
			}
		});

		if (!components.has(node.componentId)) {
			nodes.push({
				data : {
					id : node.componentId,
					text : node.componentName
				}
			});
		}
	});

	// 2. build edges
	var edges = [];
	graph.edges.forEach(function(edge) {
		edges.push({
			data : {
				source : edge.serviceFrom,
				target : edge.serviceTo,
				id : edge.serviceFrom + "#" + edge.serviceTo
			}
		});
	});

	// build graph
	var cy = window.cy = cytoscape({
		container : document.getElementById('callgraph-view'),

		boxSelectionEnabled : false,

		style : [ {
			selector : 'node',
			css : {
				'background-color' : '#61bffc',
				'content' : 'data(text)',
				'text-valign' : 'bottom'
			}
		}, {
			selector : ':parent',
			css : {
				'text-valign' : 'top',
				'text-halign' : 'center',
				'background-color' : '#D3D3D3'
			}
		}, {
			selector : 'edge',
			css : {
				'curve-style' : 'bezier',
				'line-color' : '#61bffc',
				'target-arrow-color': '#61bffc',
				'target-arrow-shape' : 'triangle'
			}
		} ],

		elements : {
			nodes : nodes,
			edges : edges
		},

		layout : {
			name : 'grid'
		}
	});

	// set system enabled
	$("#build-system").removeAttr("disabled");
	$("#skip-system").removeAttr("disabled");
}