$(document).ready(function() {
	getConfiguration();

	registerEvents();
});

function getConfiguration() {
	$.getJSON(rest.config.conceptual.get, function(conf) {
		$("#sliding_window_size").val(conf.slidingWindowSize);
		$("#sliding_window_trigger").val(conf.slidingWindowTrigger);

		$("#headless_url").val(conf.simulatorUrl);
		$("#simulation_time").val(conf.simulationTime);
		$("#measurements").val(conf.measurements);
		$("#slider-split").val(conf.validationSplit);
		$("#slider-split").change();

		validateConfiguration();
	});
}

function validateConfiguration() {
	$.postJSON(rest.config.conceptual.validate, {
		'config' : JSON.stringify(createConfigurationObject())
	}, function(val) {
		applyTick("#simulation_time_image", val.simulationTimeValid);
		applyTick("#headless_url_image", val.simulatorValid);
		applyTick("#sliding_window_simage", val.slidingWindowSizeValid);
		applyTick("#sliding_window_timage", val.slidingWindowTriggerValid);
		applyTick("#measurements_image", val.measurementsValid);

		if (val.validationSplitValid) {
			$("#slider-split-parent").removeClass("range-danger");
			$("#slider-split-parent").addClass("range-success");
		} else {
			$("#slider-split-parent").removeClass("range-success");
			$("#slider-split-parent").addClass("range-danger");
		}
	});
}

function registerEvents() {
	$("#save").click(function() {
		saveConfiguration();
	});

	$("#sliding_window_size").change(validateConfiguration);
	$("#sliding_window_trigger").change(validateConfiguration);

	$("#simulation_time").change(validateConfiguration);
	$("#measurements").change(validateConfiguration);
	$("#headless_url").change(validateConfiguration);
	$("#slider-split").change(validateConfiguration);
}

function saveConfiguration() {
	$
			.postJSON(
					rest.config.conceptual.save,
					{
						'config' : JSON.stringify(createConfigurationObject())
					},
					function(result) {
						if (result.success) {
							toastr.success(
									'Successfully saved the configuration.',
									'Success');
						} else {
							toastr
									.error('Failed to save the configuration, maybe only some properties were adopted. Please check the inputs for invalid properties.')
						}
					});
}

function createConfigurationObject() {
	return {
		'slidingWindowSize' : $("#sliding_window_size").val(),
		'slidingWindowTrigger' : $("#sliding_window_trigger").val(),

		'simulatorUrl' : $("#headless_url").val(),
		'simulationTime' : $("#simulation_time").val(),
		'measurements' : $("#measurements").val(),
		'validationSplit' : $("#slider-split").val()
	};
}