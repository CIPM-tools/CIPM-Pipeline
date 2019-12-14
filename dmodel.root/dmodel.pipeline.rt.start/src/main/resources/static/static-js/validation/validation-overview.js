var currentId;
var currentSel;
var currentName;

$(document).ready(function() {
	$("#list-v1-list").click(function() {
		switchData("pre");
	});
	$("#list-v2-list").click(function() {
		switchData("afterusage");
	});
	$("#list-v3-list").click(function() {
		switchData("afterrepo");
	});
	$("#list-v4-list").click(function() {
		switchData("final");
	});

	$("#modal-visualize").click(function() {
		$('#selectModal').modal('hide');
		startVisualization(currentId, $("#sel1").val());
	});

	registerCloseEvent();
});

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
	var genId = data.id + "-" + type;
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
		// TODO
	} else if (type === "Histogram") {
		// TODO
	} else if (type === "Metrics") {
		// TODO
	}
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

function createDataset(label, color, values) {
	return {
		label : label,
		data : values,
		tension : 0,
		showLine : false,
		fill : false,
		borderWidth : 2,
		borderColor : window.chartColors[color]
	};
}

function alignAndSortDistributions(monitoring, analysis) {
	// align
	if (monitoring !== null && analysis !== null && monitoring !== undefined
			&& analysis !== undefined) {
		var minBoth = Math.min(Math.min(monitoring.xvalues), Math
				.min(analysis.xvalues));
		for (var i = 0; i < monitoring.xvalues.length; i++) {
			monitoring.xvalues[i] -= minBoth;
		}
		for (var i = 0; i < analysis.xvalues.length; i++) {
			analysis.xvalues[i] -= minBoth;
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

	return {
		monitoring : monitoring_data,
		analysis : analysis_data
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