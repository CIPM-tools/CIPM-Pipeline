toastr.options = {
	"timeOut": "5000",
	"progressBar": true
}

// globals
var basePathIcons = "/static-js/pcm/icons/";

var loadingSpinner = null;
var spinningTarget = null;
var currentConflict = null;
var currentSystem = null;

$(document).ready(function() {
	buildIconDatabase();

	buildTree(repository, "#tree-repository", "Failed to get repository from backend.");

	registerEvents();

	spinningTarget = document.getElementById("system-view");
	loadingSpinner = new Spinner();

	showConflictBody(false);

	$("#resolve-conflict").click(function() {
		resolveConflict();
	});
});

function finishBuildingProcedure() {
	toastr.success('Returning in 3 seconds.', 'Success');

	// finish
	setTimeout(function() {
		window.location.href = "/design/system-extraction.html";
	}, 3000);
}

function resolveConflict() {
	var selectedSolutionId = null;
	if (currentConflict.type === "assembly") {
		var selectedSolution = $("#tree-conflict2").jstree().get_selected(true);
		selectedSolutionId = selectedSolution.length == 0 ? null : ecoreTree.data["#tree-conflict2"][selectedSolution[selectedSolution.length - 1].id].attributes.id;
	} else if (currentConflict.type === "connection") {
		var selectedSolution = $("#tree-conflict2").jstree().get_selected(true);
		selectedSolutionId = selectedSolution.length == 0 ? null : ecoreTree.data["#tree-conflict2"][selectedSolution[selectedSolution.length - 1].id].attributes.id;
	}
	var conflictSolution = {
		nameMapping: {},
		id: currentConflict.id,
		solution: selectedSolutionId
	};

	$.postJSON(rest.design.build.solveConflict, {
		solution: JSON.stringify(conflictSolution)
	}, function() {
		// update process
		loadingSpinner.spin(spinningTarget);
		updateBuildingProcess();
	});

	showConflictBody(false);
	$("#todo-label").text(defaultLabel);
}

function registerEvents() {
	$("#start-procedure").click(function() {
		$("#start-procedure").attr("disabled", "true");

		// start selection of interfaces
		startProcess();
	});

	$("#build-system").click(function() {
		startBuildingProcedure();
	});

	$("#finish-procedure").click(function() {
		finishBuildingProcedure();
	});
}

var defaultLabel = "Nothing to do for now. Start the procedure by using the buttons above.";
function startBuildingProcedure() {
	$("#build-system").attr("disabled", "true");

	// get selected interfaces
	var selectedInterfaces = $("#tree-repository").jstree().get_selected(true);
	var idList = [];
	for (var i = 0; i < selectedInterfaces.length; i++) {
		var c = selectedInterfaces[i];
		var nodeData = ecoreTree.data["#tree-repository"][c.id];

		idList.push(nodeData.attributes.id);
	}

	// clear repository
	clearTree("#tree-repository");
	buildTree(repository, "#tree-repository", "Failed to update repository.", false);

	// clear message
	$("#todo-label").text(defaultLabel);

	// start process
	$.postJSON(rest.design.build.start, { coreInterfaces: JSON.stringify({ interfaceIds: idList }) }, function() {
		loadingSpinner.spin(spinningTarget);
		updateBuildingProcess();
	});
}

function showFinishedBuilding() {
	$("#todo-label").html("Derivation of the system <b>finished</b>. Click 'Finish Procedure' to apply the new model.");
	// alert it
	toastr.success('Finished system building process.', 'Success');

	// make finish button accessible
	$("#finish-procedure").removeAttr("disabled");
}

function updateBuildingProcess() {
	$
		.getJSON(
			rest.design.build.status,
			function(data) {
				if (data.status === "idle") {
					setTimeout(updateBuildingProcess, 250);
				} else {
					$
						.getJSON(
							rest.design.build.get,
							function(curr_system) {
								updateSystemModel(curr_system);
								if (data.status === "conflict") {
									// show conflict
									showConflict();
								} else if (data.status === "finished") {
									showFinishedBuilding();
								} else {
									console
										.error("Oops! This should'nt happen, the building process should never be idle.");
								}

								loadingSpinner.stop();
							});
				}
			});
}

function showConflict() {
	$.getJSON(rest.design.build.getConflict, function(conflict) {
		currentConflict = conflict;

		if (conflict.type === "connection") {
			showConnectionConflict(conflict);
		} else if (conflict.type === "assembly") {
			showAssemblyConflict(conflict);
		}
		showConflictBody(true);
	});
}

function showConflictBody(show) {
	if (show) {
		$("#conflict-body").show();
	} else {
		$("#conflict-body").hide();
	}
}

function showConnectionConflict(conflict) {
	$("#todo-label").html("There is an <b>Component Wiring Conflict</b> that needs to be resolved.");

	clearTree("#tree-conflict1");
	clearTree("#tree-conflict2");
	$("#resolve-conflict").attr("disabled", "true");

	// build target tree
	var targetSplit = conflict.targetIds[0].split("-");
	var assemblyNode = null;
	var requiredRoleNode = null;
	for (var i = 0; i < currentSystem.childs.length; i++) {
		if (targetSplit[0] === currentSystem.childs[i].attributes.id) {
			assemblyNode = currentSystem.childs[i];
		} else if (targetSplit[1] === currentSystem.childs[i].attributes.id) {
			requiredRoleNode = currentSystem.childs[i];
		}
	}
	if (assemblyNode === null) {
		assemblyNode = JSON.parse(JSON.stringify(currentSystem));
	}
	assemblyNode.childs = [];

	for (var i = 0; i < repository.childs.length; i++) {
		var innerChilds = repository.childs[i].childs;
		for (var j = 0; j < innerChilds.length; j++) {
			if (targetSplit[1] === innerChilds[j].attributes.id) {
				requiredRoleNode = innerChilds[j];
			}
		}
	}
	requiredRoleNode.childs = [];

	var containmentNode = {
		type: "Target Assembly and Role",
		attributes: {},
		childs: [assemblyNode, requiredRoleNode]
	};

	buildTree(containmentNode, "#tree-conflict1", "Failed to update conflict.", false);
	ecoreTree.openUpTree("#tree-conflict1");

	// build solution tree
	var filteredRepository = JSON.parse(JSON.stringify(repository));
	for (var i = 0; i < filteredRepository.childs.length; i++) {
		if (!conflict.possibleIds.includes(filteredRepository.childs[i].attributes.id)) {
			filteredRepository.childs.splice(i, 1);
			i--;
		} else {
			filteredRepository.childs[i].childs = [];
		}
	}

	buildTree(filteredRepository, "#tree-conflict2", "Failed to update conflict solutions.", true, false);
	ecoreTree.openUpTree("#tree-conflict2");

	// register events for selection
	$("#tree-conflict2").on(
		"activate_node.jstree", function(evt, data) {
			//selected node object: data.node;
			var selected = $("#tree-conflict2").jstree().get_selected(true);
			if (selected.length == 0) {
				$("#resolve-conflict").attr("disabled", "true");
			} else {
				$("#resolve-conflict").removeAttr("disabled");
			}
		}
	);
}

function showAssemblyConflict(conflict) {
	$("#todo-label").html("There is an <b>Assembly Conflict</b> that needs to be resolved. Select the assembly that should be used or select none if a new one should be created.");

	// build target tree
	var targetSplit = conflict.targetIds[0].split("-");
	clearTree("#tree-conflict1");
	var assemblyNode = null;
	var requiredRoleNode = null;
	for (var i = 0; i < currentSystem.childs.length; i++) {
		if (targetSplit[0] === currentSystem.childs[i].attributes.id) {
			assemblyNode = currentSystem.childs[i];
		} else if (targetSplit[1] === currentSystem.childs[i].attributes.id) {
			requiredRoleNode = currentSystem.childs[i];
		}
	}
	if (assemblyNode === null) {
		assemblyNode = JSON.parse(JSON.stringify(currentSystem));
	}
	assemblyNode.childs = [];

	for (var i = 0; i < repository.childs.length; i++) {
		var innerChilds = repository.childs[i].childs;
		for (var j = 0; j < innerChilds.length; j++) {
			if (targetSplit[1] === innerChilds[j].attributes.id) {
				requiredRoleNode = innerChilds[j];
			}
		}
	}
	requiredRoleNode.childs = [];

	var containmentNode = {
		type: "Target Assembly and Role",
		attributes: {},
		childs: [assemblyNode, requiredRoleNode]
	};

	buildTree(containmentNode, "#tree-conflict1", "Failed to update conflict.", false);
	ecoreTree.openUpTree("#tree-conflict1");

	// build solution tree
	clearTree("#tree-conflict2");

	var filteredSystem = JSON.parse(JSON.stringify(currentSystem));
	for (var i = 0; i < filteredSystem.childs.length; i++) {
		if (!conflict.possibleIds.includes(filteredSystem.childs[i].attributes.id)) {
			filteredSystem.childs.splice(i, 1);
			i--;
		} else {
			filteredSystem.childs[i].childs = [];
		}
	}

	buildTree(filteredSystem, "#tree-conflict2", "Failed to update conflict solutions.", true, false);
	ecoreTree.openUpTree("#tree-conflict2");
}

function updateSystemModel(sys) {
	currentSystem = sys;

	clearTree("#tree-system");
	buildTree(sys, "#tree-system", "Failed to update system.", false);
}

function startProcess() {
	toastr.info('Started system building process. Please select the interfaces that should be provided by the system.', 'Start');
	toastr.info('Mark the interfaces by double-clicking them.', 'Info');

	// filter repository and show tree with interfaces
	var filteredRepository = JSON.parse(JSON.stringify(repository));
	for (var i = 0; i < filteredRepository.childs.length; i++) {
		if (filteredRepository.childs[i]["type"] !== "OperationInterface") {
			filteredRepository.childs.splice(i, 1);
			i--;
		} else {
			filteredRepository.childs[i].childs = [];
		}
	}

	clearTree("#tree-repository");
	buildTree(filteredRepository, "#tree-repository", "Failed to update repository.", true);
	ecoreTree.openUpTree("#tree-repository");
	$("#todo-label").text("Select the interfaces that should be provided by the system as a whole and continue the procedure by clicking 'Start Building System'.");

	// register events for selection
	$("#tree-repository").on(
		"activate_node.jstree", function(evt, data) {
			//selected node object: data.node;
			var selected = $("#tree-repository").jstree().get_selected(true);
			if (selected.length == 0) {
				$("#build-system").attr("disabled", "true");
			} else {
				$("#build-system").removeAttr("disabled");
			}
		}
	);
}

// tree functions
function buildIconDatabase() {
	ecoreTree.registerIconBasePath(basePathIcons, ".gif");

	// correspondences
	ecoreTree.registerIcon("Correspondences", "fa fa-long-arrow-alt-right");
	ecoreTree.registerIcon('ManualCorrespondence', "fa fa-hand-pointer");
	ecoreTree.registerIcon('ReactionsCorrespondence', "fa fa-vial");

	// instrumentation model
	ecoreTree.registerIcon("InstrumentationModel", "fa fa-search");
	ecoreTree.registerIcon("ServiceInstrumentationPoint", basePathIcons + "ResourceDemandingSEFF.gif");
	ecoreTree.registerIcon("ActionInstrumentationPoint", basePathIcons + "InternalAction.gif");
}

function clearTree(id) {
	$(id).jstree("destroy").empty();
}

function buildTree(eobj, parent, error_msg, selectable, multiple) {
	selectable = typeof selectable !== 'undefined' ? selectable : false;
	multiple = typeof multiple !== 'undefined' ? multiple : true;
	if (eobj != null) {
		ecoreTree.build(parent, eobj, "#property-container", function() {
		}, selectable, multiple);
	} else {
		console.error(error_msg);
	}
}