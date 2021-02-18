window.chartColors = {
	red: 'rgb(255, 99, 132)',
	orange: 'rgb(255, 159, 64)',
	yellow: 'rgb(255, 205, 86)',
	green: 'rgb(75, 192, 192)',
	blue: 'rgb(54, 162, 235)',
	purple: 'rgb(153, 102, 255)',
	grey: 'rgb(201, 203, 207)'
};

var colorSuccess = "#28a745";
var colorWarning = "#ffc107";
var colorInfo = "#007bff";
var colorError = "#dc3545";

var maxNumberOfBars = 20;
var barSpacing = 15;
var barHeight = 50;

$(document).ready(function() {
	Chart.scaleService.updateScaleDefaults('linear', {
	    ticks: {
	        min: 0
	    }
	});
	
	updatePerformanceStats();
});

function updatePerformanceStats() {
	var paperWidth = $("#executions-stacked").width();
	var paperHeight = $("#executions-stacked").height();
	
	var paper = Raphael("executions-stacked", paperWidth, paperHeight);
	
	$.getJSON(rest.runtime.performance, function(perfStats) {
		updateStats(perfStats, paper);
	});
}

function updateStats(stats, paper) {
	// bars
	drawLegend(paper);
	drawBars(paper, stats);
	
	// chart
	udpateExecutionChart(stats);
	updateMetrics(stats);
}

function updateMetrics(stats) {
	var recordCounts = [];
	var executionTimes = [];
	
	stats.executionData.forEach(function(it) {
		if (it.success !== "ERROR") {
			recordCounts.push(it.recordCount);
			executionTimes.push(it.executionTimeCumulated / 1000000000);
		}
	});
	
	var metrics = [{
		key : "Average record count",
		value : Math.round(listAverage(recordCounts))
	}, {
		key : "Average execution time",
		value : parseFloat(listAverage(executionTimes)).toFixed(2) + "s"
	}];
	
	metrics.forEach(function(metric) {
		$("#metric-body").append("<tr><td>" + metric.key + "</td><td>" + metric.value + "</td></tr>")
	});
}

function listAverage(list) {
	sum = 0;
	for (var i = 0; i < list.length; i++) {
		sum += list[i];
	}
	return sum / list.length;
}

function udpateExecutionChart(stats) {
	var ctx = document.getElementById("executions-chart").getContext("2d");
	var chart = new Chart(ctx, {
	    type: 'line',
	    data : {
	    	datasets : [generateDataset(stats)]
	    },
	    options: {
	        scales: {
	            xAxes: [{
	                type: 'time',
	                scaleLabel: {
	                	display : true,
	                	labelString : 'Pipeline execution start time'
	                }
	            }],
	            yAxes: [{
	            	scaleLabel: {
	                	display : true,
	                	labelString : 'Execution time in seconds (s)'
	                }
	            }]
	        }
	    }
	});
}

function generateDataset(stats) {
	var innerData = [];
	for (var i = 0; i < stats.executionData.length; i++) {
		innerData.push({
			x : stats.executionData[i].startTime,
			y : stats.executionData[i].executionTimeCumulated / 1000000000
		});
	}
	
	return {
		data : innerData,
		label : 'Execution time',
		backgroundColor: window.chartColors.blue,
		borderColor: window.chartColors.blue,
	};
}

function drawBars(paper, stats) {
	var currentLen = stats.executionData.length;
	var currentBar = 0;
	
	var barWidth = (paper.width - (maxNumberOfBars - 1) * barSpacing) / maxNumberOfBars;
	
	for (var i = Math.max(0, currentLen - maxNumberOfBars); i < currentLen; i++) {
		var barPositionX = (currentBar * barWidth) + (barSpacing * currentBar);
		currentBar++;
		
		var statusColor = getColorSwitch(stats.executionData[i].success);
		
		var nBar = paper.rect(barPositionX, 0, barWidth, barHeight);
		nBar.attr({
			"stroke-width" : 0,
			"fill" : statusColor
		});
	}
}

function getColorSwitch(status) {
	if (status === "WORKING") {
		return colorSuccess;
	} else if (status === "WARNING") {
		return colorWarning;
	} else if (status === "INFO") {
		return colorInfo;
	} else if (status === "ERROR") {
		return colorError;
	}
}

function drawLegend(paper) {
	// rects
	var successRect = paper.rect(0, paper.height - 15, 12, 12);
	successRect.attr({
		fill : colorSuccess,
		"stroke-width" : 0
	});
	
	var infoRect = paper.rect(paper.width / 4, paper.height - 15, 12, 12);
	infoRect.attr({
		fill : colorInfo,
		"stroke-width" : 0
	});
	
	var warningRect = paper.rect(paper.width / 2, paper.height - 15, 12, 12);
	warningRect.attr({
		fill : colorWarning,
		"stroke-width" : 0
	});
	
	var errorRect = paper.rect(paper.width / (4 / 3), paper.height - 15, 12, 12);
	errorRect.attr({
		fill : colorError,
		"stroke-width" : 0
	});
	
	// labels
	var labelSet = paper.set();
	
	var labelSuccess = paper.text(20, paper.height - 8.5, "Successful execution");
	var labelInfo = paper.text(20 + paper.width / 4, paper.height - 8.5, "Success with info messages");
	var labelWarning = paper.text(20 + paper.width / 2, paper.height - 8.5, "Warnings");
	var labelError = paper.text(20 + paper.width / (4 / 3), paper.height - 8.5, "Execuction failed");
	
	labelSet.push(labelSuccess);
	labelSet.push(labelInfo);
	labelSet.push(labelWarning);
	labelSet.push(labelError);
	
	labelSet.attr({
		"text-anchor" : "start"
	});
}