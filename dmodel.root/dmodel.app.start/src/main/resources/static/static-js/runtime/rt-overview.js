var layout;

var states = {
	NOT_REACHED : "not_reached",
	RUNNING : "running",
	FINISHED : "finished"
};

var current_states = {};
var loading_mapping = {};

var validation_data = {
	nodes : [ "pre-validation", "t-validation2-1", "t-validation2-2",
			"t-validation3" ],
	links : {
		"pre-validation" : "/validation/index.html?data=pre",
		"t-validation2-1" : "/validation/index.html?data=afterusage",
		"t-validation2-2" : "/validation/index.html?data=afterrepo",
		"t-validation3" : "/validation/index.html?data=final"
	}
};

$(document).ready(function() {

	// make full sized
	$(".container-fluid").css('height', '100%');

	$('#bologna-list a').on('click', function(e) {
		e.preventDefault()
		$(this).tab('show')
	});

	// resize canvas
	var width = $("#overview").width();
	var height = $("#card-body").height() * 0.8;
	layout = prepareOverview(layoutPipeline(width, height, 25), width, height);

	// logic
	triggerLogic();

});

function triggerLogic() {
	setInterval(function() {
		$.getJSON(rest.runtime.pipeline.status, {}, function(data) {
			// process update
			processUpdate(data);
		});
	}, 500);
}

function processUpdate(data) {
	var change = false;
	for (key in data.mapping) {
		if (updateNode(key, data.mapping[key])) {
			change = true;
		}
	}

	if (change) {
		updateConnections();
		
		if (data.running) {
			layout.additionals.runningCircle.attr({
				"fill" : "#006400",
				"stroke" : "#006400"
			});
		} else {
			layout.additionals.runningCircle.attr({
				"fill" : "#8b0000",
				"stroke" : "#8b0000"
			});
		}
	}
}

function updateConnections() {
	for (conn in layout.connections) {
		var from = conn.split(";")[0];
		var to = conn.split(";")[1];

		if (current_states[from] !== undefined
				&& current_states[from] === states.FINISHED
				&& current_states[to] !== undefined
				&& current_states[to] !== states.NOT_REACHED) {
			layout.connections[conn].attr({
				"stroke" : "#006400"
			});
		} else {
			layout.connections[conn].attr({
				"stroke" : "#000000"
			});
		}
	}
}

function updateNode(name, state) {
	var circle = layout.circles[name];
	var text = layout.texts[name];

	if (current_states[name] !== state) {
		resetNode(name);
		if (state === states.RUNNING) {
			loading_mapping[name] = setLoading(circle, text);
		} else if (state === states.FINISHED) {
			loading_mapping[name] = setFinished(name, circle, text);
		}
		current_states[name] = state;
		return true;
	}
	return false;
}

function resetNode(name) {
	if (loading_mapping[name] !== undefined) {
		loading_mapping[name].stop();
		loading_mapping[name].remove();
	}

	if (layout !== undefined && layout.texts !== undefined
			&& layout.texts[name] !== undefined) {
		var text = layout.texts[name];
		text.attr({
			"fill" : "#000000"
		});
	}
}

function setFinished(name, circle, text) {
	var circle = layout.paper.circle(circle.attrs.cx, circle.attrs.cy,
			circle.attrs.r + 2);
	circle.attr({
		"stroke" : "#006400",
		"stroke-width" : 2
	});
	text.attr({
		"fill" : "#006400"
	});

	// include inner circle
	if (validation_data.nodes.includes(name)) {
		var set = layout.paper.set();
		set.push(circle);
		var innerCircle = layout.paper.circle(circle.attrs.cx, circle.attrs.cy,
				circle.attrs.r - 10);
		innerCircle.attr({
			"fill" : "#151965",
			"cursor" : "pointer"
		});
		set.push(innerCircle);
		
		var refUrl = validation_data.links[name];
		innerCircle.click(function() {
			window.location.href = refUrl;
		});

		// overwrite
		circle = set;
	}

	return circle;
}

function setLoading(circle, text) {
	var arc = layout.paper.path();
	arc.attr({
		"stroke" : "#ee7600",
		"stroke-width" : 2,
		arc : [ circle.attrs.cx, circle.attrs.cy, 1, 4, circle.attrs.r + 2 ]
	});
	text.attr({
		"fill" : "#ee7600"
	});

	animateEndless(arc);

	return arc;
}

function animateEndless(arc) {
	var animation = Raphael.animation({
		transform : "r360," + arc.attrs.arc[0] + "," + arc.attrs.arc[1]
	}, 3000, 'linear').repeat(Infinity);
	arc.animate(animation);
}