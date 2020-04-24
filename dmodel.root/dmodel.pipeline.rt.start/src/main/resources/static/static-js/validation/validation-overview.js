var currentId;
var currentSel;
var currentName;

$(document).ready(function() {
	fetchOverview();

	$("#modal-visualize").click(function() {
		$('#selectModal').modal('hide');
		startVisualization(currentId, $("#sel1").val());
	});

	registerCloseEvent();
});

function fetchOverview() {
	$.getJSON(rest.runtime.validation.overview, function(overview) {
		$("#overview-tab").empty();
		overview.points.forEach(function(point) {
			var improvementLabel = "";
			if (point.visPresent) {
				var improvementClass = point.validationImprovementScore > 0 ? "text-success" : (point.validationImprovementScore < 0 ? "text-danger" : "text-secondary");
				improvementLabel = '<span class="' + improvementClass + '">' + (point.validationImprovementScore >= 0 ? "+" : "") + parseFloat(point.validationImprovementScore * 100).toFixed(2) + '%</span>';
			}
			
			var listItem = '<a class="list-group-item d-flex justify-content-between align-items-center" id="' + point.validationDescriptionEnum  + '" data-toggle="list" href="#list-v1" role="tab" aria-controls="home">' + point.validationDescription + " " + improvementLabel + '<span class="badge badge-primary badge-pill">' + point.validationPointCount + '</span></a>';
			$("#overview-tab").append(listItem);
			
			$("#" + point.validationDescriptionEnum).click(function() {
				switchData(point.validationDescriptionEnum);
			});
		});
		
		checkQueryParameter();
	});
}

function checkQueryParameter() {
	// check for get parameters
	var predefined = getQueryVariable("data");
	if (predefined !== null) {
		if (predefined === "pre") {
			$("#PRE_PIPELINE").click();
		} else if (predefined === "afterusage") {
			$("#AFTER_T_USAGE").click();
		} else if (predefined === "afterrepo") {
			$("#AFTER_T_REPO").click();
		} else if (predefined === "final") {
			$("#FINAL").click();
		}
	}
}

function switchData(point) {
	currentSel = point;
	$.getJSON(rest.runtime.validation.points + "?data=" + point, {}, function(
			data) {
		showData(data);
	});
}

function showData(data) {
	$("#list-points").empty();

	data
			.forEach(function(element) {
				var el = $('<a class="list-group-item list-group-item-action" role="tab" data-toggle="list" href="#">'
						+ element.name + "</a>");
				$("#list-points").append(el);

				$(el).dblclick(function() {
					showValidation(element.id);
					currentName = element.name;
				});
			});
}

function showValidation(id) {
	$('#selectModal').modal('show');
	currentId = id;
}

function startVisualization(id, type) {
	$.getJSON(rest.runtime.validation.data + "?container=" + currentSel
			+ "&id=" + encodeURI(id), {}, function(data) {
		createVisualizationTab(type, data);
	});
}

function createVisualizationTab(type, data) {
	// create tab
	var genId = data.id + "-" + type + "-" + currentSel;
	var genIdContent = genId + "-content";

	var tabItem = $('<li class="nav-item"><a class="nav-link" id="'
			+ genId
			+ '" data-toggle="tab" href="#'
			+ genIdContent
			+ '" role="tab" aria-controls="'
			+ genId
			+ '" aria-selected="false"><button class="close closeTab" type="button" >×</button>'
			+ currentName + ' (' + type + ')</a></li>');
	$("#tab-list").append(tabItem);

	// create content
	var tabContent = $('<div class="tab-pane fade" id="' + genIdContent
			+ '" role="tabpanel" aria-labelledby="' + genId + '"></div>');
	insertContent(type, data, tabContent);
	$("#tab-content").append(tabContent);

	// recreate events
	registerCloseEvent();

	// open tab
	$("#" + genId).tab('show');
}

function insertContent(type, data, container) {
	var genId = data.id + "-" + type + "-" + "-chart";
	if (type === "XY") {
		// print xy chart
		var canvas = $('<canvas id="' + genId + '"></canvas>');
		container.append(canvas);

		// print chart
		var ctx = $(canvas)[0].getContext('2d');
		var chart = new Chart(ctx, generateDefaultScatter());

		// align data
		var datasets = alignAndSortDistributions(data.monitoringDistribution,
				data.analysisDistribution);

		// sets
		chart.data.datasets.push(createDataset("Monitoring", "red",
				datasets.monitoring));
		chart.data.datasets.push(createDataset("Analysis", "blue",
				datasets.analysis));

		// You update the chart to take into account the new dataset
		chart.update();
	} else if (type === "CDF") {
		// print xy chart
		var canvas = $('<canvas id="' + genId + '"></canvas>');
		container.append(canvas);

		// print chart
		var ctx = $(canvas)[0].getContext('2d');
		var chart = new Chart(ctx, generateDefaultScatter());

		// align data
		var datasets = alignAndSortDistributions(data.monitoringDistribution,
				data.analysisDistribution);

		// calculate cdfs
		var cdf1 = buildCDF(datasets.monitoring);
		var cdf2 = buildCDF(datasets.analysis);

		// sets
		chart.data.datasets
				.push(createDataset("Monitoring", "red", cdf1, true));
		chart.data.datasets.push(createDataset("Analysis", "blue", cdf2, true));

		// You update the chart to take into account the new dataset
		chart.update();
	} else if (type === "Histogram") {
		// print xy chart
		var canvas = $('<canvas id="' + genId + '"></canvas>');
		container.append(canvas);

		// print chart
		var ctx = $(canvas)[0].getContext('2d');
		var chart = new Chart(ctx, generateDefaultBarChart());

		// align data
		var datasets = alignAndSortDistributions(data.monitoringDistribution,
				data.analysisDistribution);

		var histo1 = buildHistogram(datasets.monitoring);
		var histo2 = buildHistogram(datasets.analysis);

		// sets
		chart.data.datasets.push(createDatasetBar(chart, "Monitoring", "red",
				histo1));
		chart.data.datasets.push(createDatasetBar(chart, "Analysis", "blue",
				histo2));

		// You update the chart to take into account the new dataset
		chart.update();
	} else if (type === "Metrics") {
		var tableProto = $('<table class="table"><thead><tr><th scope="col">#</th><th scope="col">Metric name</th><th scope="col">Metric value</th></thead></table>');
		var tableBody = $('<tbody></tbody>');

		if (data.metricValues !== undefined) {
			for (var i = 0; i < data.metricValues.length; i++) {
				var nRow = $('<tr><td>#' + (i + 1) + '</td><td>'
						+ data.metricValues[i].type + '</td><td>'
						+ data.metricValues[i].value + '</td><tr>');
				tableBody.append(nRow);
			}
		}

		tableProto.append(tableBody);
		container.append(tableProto);
	}
}

function buildHistogram(dataset) {
	if (dataset === null || dataset === undefined) {
		return [];
	}

	// extract & sort
	var yExtract = [];
	dataset.forEach(function(d) {
		yExtract.push(d.y);
	});
	yExtract.sort(function(a, b) {
		return a - b;
	});

	var output = [];
	var last_i = 0;
	for (var i = 0; i < yExtract.length; i++) {
		if (i > 0) {
			if (yExtract[i] != yExtract[i - 1]) {
				// add old
				output.push({
					x : yExtract[i - 1],
					y : i - last_i + 1
				});
			}
		}
	}
	output.push({
		x : yExtract[yExtract.length - 1],
		y : yExtract.length - last_i
	});
	return output;
}

function buildCDF(dataset) {
	if (dataset === null || dataset === undefined) {
		return [];
	}

	// extract & sort
	var yExtract = [];
	dataset.forEach(function(d) {
		yExtract.push(d.y);
	});
	yExtract.sort(function(a, b) {
		return a - b;
	});

	// generate points
	var output = [];
	for (var i = 0; i < yExtract.length; i++) {
		output.push({
			x : yExtract[i],
			y : (i + 1) / yExtract.length
		});
	}
	return output;
}

// this method will register event on close icon on the tab..
// http://jsfiddle.net/vinodlouis/pb6EM/1/
function registerCloseEvent() {

	$(".closeTab").click(function() {

		// there are multiple elements which has .closeTab icon so close the tab
		// whose close icon is clicked
		var tabContentId = $(this).parent().attr("href");
		$(this).parent().parent().remove(); // remove li of tab
		$('#myTab a:last').tab('show'); // Select first tab
		$(tabContentId).remove(); // remove respective tab content

	});
}

function createDatasetBar(chart, label, color, values) {
	if (values.length == 0) {
		return null;
	}

	var labels = [];
	var data = [];
	for (var i = 0; i < values.length; i++) {
		labels.push(values[i].x);
		data.push(values[i].y);
	}

	chart.data.labels = data;

	return {
		borderColor : window.chartColors[color],
		data : data,
		label : label
	};
}

function createDataset(label, color, values, connect) {
	if (connect === undefined) {
		connect = false;
	}
	return {
		label : label,
		data : values,
		tension : 0,
		showLine : connect,
		fill : false,
		borderWidth : 2,
		borderColor : window.chartColors[color]
	};
}

function alignAndSortDistributions(monitoring, analysis) {
	// align
	if (monitoring !== null && analysis !== null && monitoring !== undefined
			&& analysis !== undefined) {
		var minMonitoring = minOfArrayWorkaround(monitoring.xvalues);
		var minAnalysis = minOfArrayWorkaround(analysis.xvalues);

		for (var i = 0; i < monitoring.xvalues.length; i++) {
			monitoring.xvalues[i] -= minMonitoring;
		}
		for (var i = 0; i < analysis.xvalues.length; i++) {
			analysis.xvalues[i] -= minAnalysis;
		}
	}

	// sort after x
	var monitoring_data = [];
	var analysis_data = [];
	if (monitoring !== null) {
		// reduce data amount
		skipFactor = Math.floor(monitoring.xvalues.length / 1000.0);
		for (var i = 0; i < monitoring.xvalues.length; i++) {
			if (skipFactor < 1 || i % skipFactor == 0) {
				monitoring_data.push({
					x : monitoring.xvalues[i],
					y : monitoring.yvalues[i]
				});
			}
		}
	}
	if (analysis !== null) {
		// reduce data amount
		skipFactor = Math.floor(analysis.xvalues.length / 1000.0);
		for (var i = 0; i < analysis.xvalues.length; i++) {
			if (skipFactor < 1 || i % skipFactor == 0) {
				analysis_data.push({
					x : analysis.xvalues[i],
					y : analysis.yvalues[i]
				});
			}
		}
	}

	monitoring_data.sort(function(a, b) {
		return a.x - b.x;
	});
	analysis_data.sort(function(a, b) {
		return a.x - b.x;
	});

	console.log(analysis_data);

	return {
		monitoring : monitoring_data,
		analysis : analysis_data
	};
}

function minOfArrayWorkaround(arr) {
	if (arr.length == 0) {
		return 0;
	}

	var kMin = arr[0];
	for (var k = 1; k < arr.length; k++) {
		if (arr[k] < kMin) {
			kMin = arr[k];
		}
	}
	;
	return kMin;
}

function removeNaNs(distr) {
	if (distr !== null && distr !== undefined && distr.xvalues !== undefined) {
		for (var i = 0; i < distr.xvalues.length; i++) {
			if (isNaN(distr.xvalues[i])) {
				distr.xvalues[i] = 0;
			}
			if (isNaN(distr.yvalues[i])) {
				distr.yvalues[i] = 0;
			}
		}
	}
}

function generateDefaultBarChart() {
	return {
		type : 'bar',
		data : {
			datasets : []
		},
		options : {
			responsive : true,
			legend : {
				position : 'bottom',
			},
			tooltips : {
				enabled : false
			},
			animation : {
				duration : 0
			// general animation time
			},
			scales : {
				xAxes : [ {
					type : 'linear',
					position : 'bottom',
					ticks : {
						suggestedMin : 0
					}
				} ],
				yAxes : [ {
					ticks : {
						suggestedMin : 0
					}
				} ]
			}
		}
	};
}

function generateDefaultScatter() {
	return {
		type : 'scatter',
		data : {
			datasets : []
		},
		options : {
			responsive : true,
			legend : {
				position : 'bottom',
			},
			tooltips : {
				enabled : false
			},
			animation : {
				duration : 0
			// general animation time
			},
			scales : {
				xAxes : [ {
					type : 'linear',
					position : 'bottom',
					ticks : {
						suggestedMin : 0
					}
				} ],
				yAxes : [ {
					ticks : {
						suggestedMin : 0
					}
				} ]
			}
		}
	};
}

function getQueryVariable(variable) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			return pair[1];
		}
	}
}