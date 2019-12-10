$(document).ready(function() {
	
	// make full sized
	$(".container-fluid").css('height', '100%');
	
	$('#bologna-list a').on('click', function(e) {
		e.preventDefault()
		$(this).tab('show')
	});
	
	// resize canvas
	var width = $("#overview").width();
	var height = $("#overview").height();
	
	prepareOverview(layoutPipeline(width, height, 25), width, height);
	
});

function layoutPipeline(width, height, size) {
	var positions = {};
	positions["pre-validation"] = {
			x : size / 2 + 10,
			y : height / 2,
			size : size
	};
	
	return positions;
}

function prepareOverview(layout, width, height) {
	var paper = Raphael("overview", width, height);
	
	var st = paper.set();
	st.push(
	    paper.circle(100, 50, 20),
	    paper.circle(300, 50, 20)
	);
	st.attr({stroke: "#333333", "stroke-dasharray": "- "});
}