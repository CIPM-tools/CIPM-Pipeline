var statusUpdateInterval = 250;
var cy = null; // call graph

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
		startBuildingProcedure();
	});
	
	$("#finish-procedure").click(function() {
		finishBuildingProcedure();
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
	cy = window.cy = cytoscape({
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
				'line-color' : '#00468b',
				'target-arrow-color' : '#00008b',
				'target-arrow-shape' : 'triangle',
				'width' : 5
			}
		}, {
			selector : '.highlighted',
			css : {
				'line-color' : '#d80000',
				'curve-style' : 'bezier',
				'target-arrow-color' : '#8b0000',
				'target-arrow-shape' : 'triangle',
				'width' : 5
			}
		}, {
			selector : '.highlighted2',
			css : {
				'background-color' : '#c2a90f'
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
	
	// events
	registerDoubleTap(cy);
	cy.on('doubleTap', 'node', function(event) {
		eventSelectedNode(event.target);
	});

	// set system enabled
	$("#build-system").removeAttr("disabled");
	$("#skip-system").removeAttr("disabled");
}

function registerDoubleTap(cy) {
	var tappedBefore;
	var tappedTimeout;
	cy.on('tap', function(event) {
		var tappedNow = event.target;
		if (tappedTimeout && tappedBefore) {
			clearTimeout(tappedTimeout);
		}
		if (tappedBefore === tappedNow) {
			event.target.trigger('doubleTap');
			tappedBefore = null;
		} else {
			tappedTimeout = setTimeout(function() {
				tappedBefore = null;
			}, 300);
			tappedBefore = tappedNow;
		}
	});
}