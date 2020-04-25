function layoutPipeline(width, height, size) {
	var layout = {};
	var positions = {};

	// straight forward
	step = 0.13;
	positions["pre-validation"] = {
		x : size / 2 + 20,
		y : height / 2,
		size : size,
		text : "Self-Validation"
	};
	positions["t-resourceenv"] = {
		x : step * width + size / 2,
		y : height / 2,
		size : size,
		text : "Update Resource\nEnvironment Model"
	};
	positions["t-allocation"] = {
		x : step * 2 * width + size / 2,
		y : height / 2,
		size : size,
		text : "Update Allocation Model"
	};
	positions["t-system"] = {
		x : step * 3 * width + size / 2,
		y : height / 2,
		size : size,
		text : "Update System Model"
	};

	// switch
	positions["t-usagemodel1"] = {
		x : step * 4 * width + size / 2,
		y : height / 4,
		size : size,
		text : "Update Usage Model"
	};
	positions["t-repository1"] = {
		x : step * 4 * width + size / 2,
		y : (height / 4) * 3,
		size : size,
		text : "Calibrate Resource Demands"
	};

	// after switch simulation
	positions["t-validation2-1"] = {
		x : step * 5 * width + size / 2,
		y : height / 4,
		size : size,
		text : "Self-Validation 2"
	};
	
	positions["t-validation2-2"] = {
			x : step * 5 * width + size / 2,
			y : (height / 4) * 3,
			size : size,
			text : "Self-Validation 3"
		};

	positions["t-usagemodel2"] = {
		x : step * 6.25 * width + size / 2,
		y : height / 4,
		size : size,
		text : "Update Usage Model"
	};
	positions["t-repository2"] = {
		x : step * 6.25 * width + size / 2,
		y : (height / 4) * 3,
		size : size,
		text : "Calibrate Resource Demands"
	};

	// final
	positions["t-validation3"] = {
		x : step * 7.25 * width + size / 2,
		y : height / 2,
		size : size,
		text : "Final Self-Validation"
	};

	layout.positions = positions;

	// do connections
	layout.connections = [ {
		from : "pre-validation",
		to : "t-resourceenv",
		dashed : false
	}, {
		from : "t-resourceenv",
		to : "t-allocation",
		dashed : false
	}, {
		from : "t-allocation",
		to : "t-system",
		dashed : false
	}, {
		from : "t-system",
		to : "t-usagemodel1",
		dashed : false
	}, {
		from : "t-system",
		to : "t-repository1",
		dashed : false
	}, {
		from : "t-repository1",
		to : "t-validation2-2",
		dashed : false
	}, {
		from : "t-usagemodel1",
		to : "t-validation2-1",
		dashed : false
	},
	{
		from : "t-validation2-1",
		to : "t-usagemodel2",
		dashed : true
	}, {
		from : "t-validation2-2",
		to : "t-repository2",
		dashed : true
	},{
		from : "t-validation2-2",
		to : "t-usagemodel2",
		dashed : true
	}, {
		from : "t-validation2-1",
		to : "t-repository2",
		dashed : true
	}, {
		from : "t-repository2",
		to : "t-validation3",
		dashed : true
	}, {
		from : "t-usagemodel2",
		to : "t-validation3",
		dashed : true
	} ];

	return layout;
}

function prepareOverview(layout, width, height) {
	var paper = Raphael("overview", width, height);
	registerArc(paper);

	var transformation_set_circle = paper.set();
	var transformation_set_text = paper.set();

	var transformation_mapping_circle = {};
	var transformation_mapping_text = {};

	// circles
	for (key in layout.positions) {
		var pos = layout.positions[key];

		var nCircle = paper.circle(pos.x, pos.y, pos.size);
		var nText = paper.text(pos.x, pos.y + pos.size / 2 + 25, pos.text);

		transformation_set_circle.push(nCircle);
		transformation_set_text.push(nText);
		transformation_mapping_circle[key] = nCircle;
		transformation_mapping_text[key] = nText;
	}

	transformation_set_circle.attr({
		"stroke-dasharray" : "- ",
		stroke : "#333333"
	});

	// connections
	var connection_set = paper.set();
	var connection_set_dashed = paper.set();
	var connection_mapping = {};

	layout.connections.forEach(function(conn) {
		var fromObj = transformation_mapping_circle[conn.from];
		var toObj = transformation_mapping_circle[conn.to];

		var startX = fromObj.attrs.cx + fromObj.attrs.r;
		var startY = fromObj.attrs.cy;
		var endX = toObj.attrs.cx - toObj.attrs.r;
		var endY = toObj.attrs.cy;

		var strLine = "M" + startX + "," + startY + "L" + endX + "," + endY;

		var nPath = paper.path(strLine);
		connection_mapping[conn.from + ";" + conn.to] = nPath;

		if (!conn.dashed) {
			connection_set.push(nPath);
		} else {
			connection_set_dashed.push(nPath);
		}
	});

	connection_set.attr({
		'arrow-end' : 'classic-wide-long',
		'stroke-width' : 1.75
	});
	connection_set_dashed.attr({
		'arrow-end' : 'classic-wide-long',
		'stroke-width' : 1.75,
		"stroke-dasharray" : "- "
	});

	// additionals
	var additional_mapping = {};
	
	var runningText = paper.text(25, 10, "Currently running: ");
	runningText.attr({
		"text-anchor" : "start",
		"font-size" : 14
	});
	var runningCircle = paper.circle(153, 10.5, 9);
	runningCircle.attr({
		"fill" : "#8b0000",
		"stroke" : "#8b0000"
	});
	
	additional_mapping["runningText"] = runningText;
	additional_mapping["runningCircle"] = runningCircle;

	return {
		paper : paper,
		circles : transformation_mapping_circle,
		texts : transformation_mapping_text,
		connections : connection_mapping,
		additionals : additional_mapping
	};
}

function registerArc(paper) {
	paper.customAttributes.arc = function(xloc, yloc, value, total, R) {
		var alpha = 360 / total * value, a = (90 - alpha) * Math.PI / 180, x = xloc
				+ R * Math.cos(a), y = yloc - R * Math.sin(a), path;
		if (total == value) {
			path = [ [ "M", xloc, yloc - R ],
					[ "A", R, R, 0, 1, 1, xloc - 0.01, yloc - R ] ];
		} else {
			path = [ [ "M", xloc, yloc - R ],
					[ "A", R, R, 0, +(alpha > 180), 1, x, y ] ];
		}
		return {
			path : path
		};
	};
}