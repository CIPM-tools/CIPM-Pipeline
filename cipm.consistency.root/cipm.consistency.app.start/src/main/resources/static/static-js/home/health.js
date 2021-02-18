var containerMapping = {};
var currentId = 0;
var problemMapping = {};

var accordionId = "#healthAccordion";

var cardTemplate = `

<div class="card">

<div class="card-header" id="heading-{heading-id}">
	<div class="row align-items-center">
	<div class="col-md-9">
		{severity-logo}{comp-name}
	</div>
	<div class="col-md-3 text-right">
		<button type="button" id="button-collapse-{heading-id}" class="btn {button-severity} btn-circle"
			data-toggle="collapse" data-target="#collapse-{heading-id}"
			aria-expanded="true" aria-controls="collapse-{heading-id}">{problem-count}</button>
	</div>
</div>
</div>

<div id="collapse-{heading-id}" class="collapse" aria-labelledby="heading-{heading-id}"
	data-parent="#healthAccordion">
	<div class="card-body">
		<table class="table">
			<thead>
				<tr>
					<th scope="col">ID</th>
					<th scope="col">Severity</th>
					<th class="th-lg" scope="col">Description</th>
				</tr>
			</thead>
			<tbody id="tbody-collapse-{heading-id}">
			</tbody>
		</table>
	</div>
</div>

</div>`;

$(document).ready(function() {
	refreshHealthStates();
});

function refreshHealthStates() {
	$.getJSON(rest.health.get, function(states) {
		$.getJSON(rest.health.problems, function(problems) {
			currentId = 0;
			containerMapping = {};
			problemMapping = {};
			
			preprocessProblems(problems);
			processStates(states);
		});
	});
}

function preprocessProblems(problems) {
	problems.problems.forEach(function(prob) {
		if (prob.component in problemMapping) {
			problemMapping[prob.component].push(prob);
		} else {
			problemMapping[prob.component] = [prob];
		}
	});
}

function processStates(states) {
	$(accordionId).empty();
	
	for (key in states.states) {
		var dataSplit = key.substring(1, key.length - 1).split(",");
		var nId = currentId++;
		var severity = generateSeverity(states.states[key]);
		
		// add header
		var generatedCard = replaceAll(cardTemplate, '{heading-id}', nId);
		generatedCard = replaceAll(generatedCard, '{severity-logo}', severity);
		generatedCard = replaceAll(generatedCard, '{button-severity}', generateButtonSeverity(states.states[key]));
		generatedCard = replaceAll(generatedCard, '{comp-name}', dataSplit[1]);
		generatedCard = replaceAll(generatedCard, '{problem-count}', getProblemCount(dataSplit[0]));
		
		$(accordionId).append(generatedCard);
		
		// add problems
		var containerId = '#tbody-collapse-' + nId;
		getProblems(dataSplit[0]).forEach(function(problem) {
			var generatedRow = '<tr class="' + generateTableSeverity(states.states[key]) + '"><td>' + problem.id + '</td><td>' + titleCase(problem.severity) + '</td><td>' + problem.message + '</td></tr>';
			$(containerId).append(generatedRow);
		});
	}
}

function getProblems(comp) {
	if (comp in problemMapping) {
		return problemMapping[comp];
	} else {
		return [];
	}
}

function getProblemCount(comp) {
	if (comp in problemMapping) {
		return problemMapping[comp].length;
	} else {
		return 0;
	}
}

function generateTableSeverity(sev) {
	if (sev === 'ERROR') {
		return 'table-danger';
	} else if (sev === 'WARNING') {
		return 'table-warning';
	} else if (sev === 'INFO') {
		return 'table-info';
	} else if (sev === 'UNKNOWN') {
		return 'table-primary';
	}
	return 'table-info';
}

function generateButtonSeverity(sev) {
	if (sev === 'ERROR') {
		return 'btn-danger';
	} else if (sev === 'WARNING') {
		return 'btn-warning';
	} else if (sev === 'INFO') {
		return 'btn-info';
	} else if (sev === 'UNKNOWN') {
		return 'btn-primary';
	} else if (sev === 'WORKING') {
		return 'btn-success';
	}
	return 'btn-info';
}

function generateSeverity(sev) {
	if (sev === 'ERROR') {
		icon = 'fa-times';
		clazz = 'text-danger';
	} else if (sev === 'WARNING') {
		icon = 'fa-exclamation';
		clazz = 'text-warning';
	} else if (sev === 'INFO') {
		icon = 'fa-info';
		clazz = 'text-info';
	} else if (sev === 'UNKNOWN') {
		icon = 'fa-question';
		clazz = 'text-primary';
	} else if (sev === 'WORKING') {
		icon = 'fa-check';
		clazz = 'text-success';
	}
	
	return '<i class="fas ' + icon + ' ' + clazz + '" style="margin-right: 15px;"></i>';
}

function titleCase(str) {
	return str.split(' ').map(item => 
		item.charAt(0).toUpperCase() + item.slice(1).toLowerCase()).join(' ');
}

function escapeRegExp(string) {
	return string.replace(/[.*+\-?^${}()|[\]\\]/g, '\\$&'); // $& means the
															// whole matched
															// string
}

function replaceAll(str, find, replace) {
	return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}