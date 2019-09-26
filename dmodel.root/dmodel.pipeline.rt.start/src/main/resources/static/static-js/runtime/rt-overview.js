$(document).ready(function() {
	
	// make full sized
	$(".container-fluid").css('height', '100%');
	
	$('#bologna-list a').on('click', function(e) {
		e.preventDefault()
		$(this).tab('show')
	});
	
});