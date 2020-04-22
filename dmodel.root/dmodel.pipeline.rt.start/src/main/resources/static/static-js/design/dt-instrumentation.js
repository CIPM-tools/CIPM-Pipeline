var progressbar = "#progress-el";
var statusUpdateInterval = 500;

$(document).ready(function() {
	registerEvents();
});

function registerEvents() {
	$("#mapping_before").change(validateConfiguration);
	$("#output_path").change(validateConfiguration);
	$("#logarithmicActivated").change(validateConfiguration);
	$("#recovery").change(validateConfiguration);
	$("#inm_url").change(validateConfiguration);

	$("#instrument").click(startInstrumentation);
}

function startInstrumentation() {
	$("#instrument").attr("disabled", "true");
	$
			.postJSON(
					rest.design.instrumentation.instrument,
					{
						config : JSON.stringify(getCurrentConfiguration())
					},
					function(result) {
						if (result.success) {
							instrumentationStatusUpdate();
						} else {
							toastr
									.error(
											"Failed to instrument the application. Check your configuration for errors and inspect the log for detailed information.",
											"Error");
						}
					});
}

function validateConfiguration() {
	var conf = getCurrentConfiguration();

	$.postJSON(rest.design.instrumentation.validate, {
		config : JSON.stringify(conf)
	}, function(result) {
		applyTick("#output_image", result.pathValid);
		applyTick("#recovery_image", result.logarithmicValid);
		applyTick("#inm_image", result.urlValid);

		if (result.valid) {
			$("#instrument").removeAttr("disabled");
		} else {
			$("#instrument").attr("disabled", "true");
		}
	});
}

function getCurrentConfiguration() {
	try {
		var url = new URL($("#inm_url").val());
		var host = url.hostname;
		var port = url.port;
		var path = url.pathname;
	} catch (err) {
		var host = "127.0.0.1";
		var port = "8080";
		var path = "/runtime/pipeline/inm";
	}

	return {
		extractMappingFromCode : $("#mapping_before")[0].checked,

		metadata : {
			outputPath : $("#output_path").val(),
			logarithmicScaling : $("#logarithmicActivated")[0].checked,
			logarithmicRecoveryInterval : $("#recovery").val(),
			hostName : host,
			restPort : port,
			inmRestPath : path
		}
	};
}

function instrumentationStatusUpdate() {
	$
			.getJSON(
					rest.design.instrumentation.status,
					function(data) {
						if (data.status >= 0 && data.status < 100) {
							setProgress(data.status);
							// set progress
							setTimeout(instrumentationStatusUpdate,
									statusUpdateInterval);
						} else {
							$("#instrument").removeAttr("disabled");
							setProgress(100);

							toastr
									.success(
											"Successfully instrumented the source code of the application and saved it to the specified output path.",
											"Success");
						}
					});
}

function setProgress(value) {
	$(progressbar).css('width', value + '%').attr('aria-valuenow', value);
}