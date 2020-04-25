var statusUpdateInterval = 250;

$(document).ready(function() {
	// make full sized
	$(".container-fluid").css('height', '100%');

	// remove outer paddings
	$(".container-fluid").css("padding-left", "0px");
	$(".container-fluid").css("padding-right", "0px");

	$("#content-wrapper").css("padding-top", "0px");
	$("#content-wrapper").css("padding-bottom", "45px");
	
	Split([ '#top-left', '#top-right' ], {
		sizes : [ 55, 45 ]
	});

	registerEvents();
	loadRepository();
});

function loadRepository() {
	currentRepositoryModel = new PCMRepositoryGraph($("#repository-view").get(0));
	
	$.getJSON(rest.design.repository.get, function(data) {
		currentRepository = data;
		currentRepositoryModel.apply(data);
		currentRepositoryModel.layout(currentLayouterRepository);
		currentRepositoryModel.draw();
	});
}

function registerEvents() {
	$("#start-procedure").click(function() {
		$("#start-procedure").attr("disabled", "true");
		
		// start selection of interfaces
		startProcess();
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