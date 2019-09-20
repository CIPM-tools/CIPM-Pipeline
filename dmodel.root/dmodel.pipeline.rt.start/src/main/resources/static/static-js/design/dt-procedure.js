var currentSystem = null;
var currentSystemModel = null;
var currentConflict = null;
var currentSystemData = null;
var currentLayouter = new HorizontalSystemModelLayouter();

$(document).ready(function() {
	currentSystemModel = new PCMSystemGraph($("#system-view").get(0));
});

function startBuildingProcedure() {
	$("#build-system").attr("disabled", "true");
	
	$.postJSON(rest.design.build.start, {}, function() {
		updateBuildingProcess();
	});
}

function updateBuildingProcess() {
	$.getJSON(rest.design.build.status, function(data) {
		if (data.status === "idle") {
			setTimeout(updateBuildingProcess, 250);
		} else {
			$.getJSON(rest.design.build.get, function(curr_system) {
				updateSystemModel(curr_system);
				if (data.status === "conflict") {
					// show conflict
					showConflict();
				} else if (data.status === "finished") {
					showFinishedBuilding();
				} else {
					console.error("Oops! This should'nt happen, the building process should never be idle.");
				}
			});
		}
	});
}

function showConflict() {
	$.getJSON(rest.design.build.getConflict, function(conflict) {
		$("#currSystemHeader").append('<span class="conflictLabel">(Open Conflict)</span>');
		currentConflict = conflict;
		
		console.log(conflict);
		if (conflict.type === "connection") {
			showConnectionConflict(conflict);
		}
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
	
	$("#system-view").addClass(
	"progress-bar-striped progress-bar bg-dark");
	$("#build-system").attr("disabled", "true");
	$("#skip-system").attr("disabled", "true");
	
	// alert it
	toastr.success('Finished system building process.\nReturning in 5 seconds.', 'Success');
	setTimeout(function() {
		window.location.href = "/design/";
	}, 5000);
}

function updateSystemModel(sys) {
	currentSystem = sys;
	currentSystemModel.apply(sys);
	currentSystemModel.layout(currentLayouter);
	currentSystemModel.draw();
}