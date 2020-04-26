var metricFilters = [ {
	id : "kstest",
	label : "KS-Test",
}, {
	id : "avg_rel",
	label : "Relative average distance"
}, {
	id : "avg_absolute",
	label : "Absolute average distance",
}, {
	id : "wasserstein",
	label : "Wasserstein distance"
}, {
	id : "median_absolute",
	label : "Absolute median distance"
} ];

$(document).ready(function() {
	buildQueryBuilder();
	registerEvents();
	getCurrentRules();
});

function getCurrentRules() {
	$.getJSON(rest.config.monitoring.predicate.get, function(rules) {
		if (rules !== null && rules !== undefined) {
			setRules(rules);
		}
	});
}

function buildQueryBuilder() {
	var filters = buildFilters(metricFilters);

	$('#builder-basic').queryBuilder({
		plugins : [ 'bt-tooltip-errors' ],

		filters : filters
	});
	
	$('#builder-basic').queryBuilder('reset');
}

function buildFilters(data) {
	output = [];

	for (var i = 0; i < data.length; i++) {
		output.push({
			id : data[i].id,
			label : data[i].label,
			type : 'double',
			validation : {
				min : 0,
				step : 0.01
			},
			operators: ['greater', 'less', 'greater_or_equal', 'less_or_equal', 'equal']
		});
	}

	return output;
}

function registerEvents() {
	$("#save").click(function() {
		var result = $('#builder-basic').queryBuilder('getRules');

		if (!$.isEmptyObject(result)) {
			// send to backend
			$.postJSON(rest.config.monitoring.predicate.save, {
				predicate : JSON.stringify(result)
			}, function(result) {
				if (result.success) {
					toastr.success("Successfully saved the predicate configuration.", "Success");
				} else {
					toastr.error("Failed to save the predicate configuration.", "Error");
				}
			});
		}
	});
}

function setRules(rules) {
	$('#builder-basic').queryBuilder('setRules', rules);
}