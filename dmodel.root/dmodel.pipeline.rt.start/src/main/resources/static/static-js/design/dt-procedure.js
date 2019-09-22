var currentSystem = null;
var currentSystemModel = null;
var currentConflict = null;
var currentSystemData = null;
var currentLayouter = new HorizontalSystemModelLayouter();

var loadingSpinner = null;
var spinningTarget = null;

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

function finishBuildingProcedure() {
	toastr.success('Returning in 3 seconds.', 'Success');

	// finish
	setTimeout(function() {
		window.location.href = "/design/";
	}, 3000);
}

function eventSelectedNode(node) {
	// in the callgraph
	if (node.parent().length > 0) {
		// this is not the parent
		node = node.parent()[0];
	}

	// get selected ID
	var selectedId = node.id();

	// check if valid solution
	if (currentConflict != null && currentConflict.type === "connection"
			&& currentConflict.possibleIds.includes(selectedId)) {
		// => valid
		resolveConflict(selectedId);
		// unselect the selected ones
		cy.elements().removeClass("highlighted2");
	}
}

function resolveConflict(id) {
	var conflictSolution = {
		nameMapping : currentSystemModel.getRenamings(),
		id : currentConflict.id,
		solution : id
	};

	$("#currSystemHeader .conflictLabel").remove();
	$.postJSON(rest.design.build.solveConflict, {
		solution : JSON.stringify(conflictSolution)
	}, function() {
		// update process
		updateBuildingProcess();
	});
}

function startBuildingProcedure() {
	$("#build-system").attr("disabled", "true");

	$.postJSON(rest.design.build.start, {}, function() {
		updateBuildingProcess();
	});
}

function updateBuildingProcess() {
	loadingSpinner.spin(spinningTarget);
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

		console.log(conflict);
		if (conflict.type === "connection") {
			showConnectionConflict(conflict);
		} else if (conflict.type === "assembly") {
			showAssemblyConflict(conflict);
		}
	});
}

function showAssemblyConflict(conflict) {
	conflict.possibleIds.forEach(function(id) {
		currentSystemModel.markAssemblyContext(id);
		$("#new-assembly").removeAttr("disabled");
	});
}

function showConnectionConflict(conflict) {
	currentSystemModel.markRequiredRole(conflict.targetId);

	conflict.possibleIds.forEach(function(id) {
		cy.elements("#" + id).addClass("highlighted2");
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
	currentSystemModel.layout(currentLayouter);
	currentSystemModel.draw();
}