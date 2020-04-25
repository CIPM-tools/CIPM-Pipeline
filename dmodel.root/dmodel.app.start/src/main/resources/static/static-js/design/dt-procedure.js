mxConstants.VERTEX_SELECTION_COLOR = "#FFFF33";
mxConstants.CURSOR_MOVABLE_VERTEX = "hand";

var currentSystem = null;
var currentSystemModel = null;
var currentConflict = null;
var currentSystemData = null;
var currentLayouterSystem = new HorizontalSystemModelLayouter();

var loadingSpinner = null;
var spinningTarget = null;

var currentRepositoryModel = null;
var currentRepository = null;
var currentLayouterRepository = new SimpleRepositoryLayouter(50, 100, 175, 40, true);
var selectedInterfaces = new Set();

toastr.options = {
	"timeOut": "5000",
	"progressBar": true
}

$(document).ready(
		function() {
			currentSystemModel = new PCMSystemGraph($("#system-view").get(0));
			currentSystemModel.addEventListener({
				selectMarkedAssembly : function(assId) {
					if (currentConflict != null
							&& currentConflict.type === "assembly"
							&& currentConflict.possibleIds.includes(assId)) {
						$("#new-assembly").attr("disabled", "true");
						resolveConflict(assId);
					}
				}
			});

			$("#new-assembly").click(
					function() {
						if (currentConflict != null
								&& currentConflict.type === "assembly") {
							$("#new-assembly").attr("disabled", "true");
							resolveConflict(null);
						}
					});
			
			// loading spinner
			spinningTarget = document.getElementById("system-view");
			loadingSpinner = new Spinner();
		});

function startProcess() {
	toastr.info('Started system building process. Please select the interfaces that should be provided by the system.', 'Start');
	toastr.info('Mark the interfaces by double-clicking them.', 'Info');
	currentRepositoryModel.clearEventListeners();
	selectedInterfaces.clear();
	
	currentRepositoryModel.addEventListener(buildRepositoryModelListener());
	
	currentRepository.interfaces.forEach(function(iface) {
		currentRepositoryModel.markInterface(iface.id, false);
	});
}

function updateProcess() {
	if (selectedInterfaces.size == 0) {
		$("#build-system").attr("disabled", "true");
	} else  {
		$("#build-system").removeAttr("disabled");
	}
}

function buildRepositoryModelListener() {
	return {
		markInterface : function(iface) {
			currentRepositoryModel.markInterface(iface, true);
			selectedInterfaces.add(iface);
			updateProcess();
		},
		unmarkInterface : function(iface) {
			currentRepositoryModel.markInterface(iface, false);
			selectedInterfaces.delete(iface);
			updateProcess();
		},
		selectMarkedComponent : function(comp) {
			eventSelectedNode(comp);
		}
	};
}

function finishBuildingProcedure() {
	toastr.success('Returning in 3 seconds.', 'Success');

	// finish
	setTimeout(function() {
		window.location.href = "/design/system-extraction.html";
	}, 3000);
}

function eventSelectedNode(node) {
	// check if valid solution
	if (currentConflict != null && currentConflict.type === "connection"
			&& currentConflict.possibleIds.includes(node)) {
		// => valid
		resolveConflict(node);
		
		// unselect the selected ones
		currentRepository.components.forEach(function(comp) {
			currentRepositoryModel.unmarkComponent(comp.id);
		});
	}
}

function resolveConflict(id) {
	var conflictSolution = {
		nameMapping : currentSystemModel.getRenamings(),
		id : currentConflict.id,
		solution : id
	};

	$("#currSystemHeader .conflictLabel").remove();
	$("#currRepositoryHeader .conflictLabel").remove();
	
	$.postJSON(rest.design.build.solveConflict, {
		solution : JSON.stringify(conflictSolution)
	}, function() {
		// update process
		loadingSpinner.spin(spinningTarget);
		updateBuildingProcess();
	});
}

function startBuildingProcedure() {
	// unmark all interfaces
	currentRepository.interfaces.forEach(function(iface) {
		currentRepositoryModel.unmarkInterface(iface.id);
	});
	
	$("#build-system").attr("disabled", "true");

	$.postJSON(rest.design.build.start, {coreInterfaces : JSON.stringify({interfaceIds : Array.from(selectedInterfaces)})}, function() {
		loadingSpinner.spin(spinningTarget);
		updateBuildingProcess();
	});
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
		$("#currSystemHeader").append(
				'<span class="conflictLabel">(Open Conflict)</span>');
		currentConflict = conflict;
		
		if (conflict.type === "connection") {
			showConnectionConflict(conflict);
		} else if (conflict.type === "assembly") {
			showAssemblyConflict(conflict);
		}
	});
}

function showAssemblyConflict(conflict) {
	toastr.info('Please select an assembly that can satisfy the role or click "New Assembly" to create a new one.', 'Info');
	
	conflict.possibleIds.forEach(function(id) {
		currentSystemModel.markAssemblyContext(id);
		$("#new-assembly").removeAttr("disabled");
	});
}

function showConnectionConflict(conflict) {
	$("#currRepositoryHeader").append(
	'<span class="conflictLabel">(Open Conflict)</span>');
	toastr.info('Please select a component that is compatible with the open required role.', 'Info');
	
	currentSystemModel.markRequiredRole(conflict.targetIds[0]);

	conflict.possibleIds.forEach(function(id) {
		currentRepositoryModel.markComponent(id);
	});
}

function showFinishedBuilding() {
	currentConflict = null;
	$("#currSystemHeader .conflictLabel").remove();

	$("#build-system").attr("disabled", "true");
	$("#skip-system").attr("disabled", "true");

	// alert it
	toastr.success('Finished system building process.', 'Success');

	// make finish button accessible
	$("#finish-procedure").removeAttr("disabled");
}

function updateSystemModel(sys) {
	currentSystem = sys;
	currentSystemModel.apply(sys);
	currentSystemModel.layout(currentLayouterSystem);
	currentSystemModel.draw();
}