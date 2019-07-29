
$(document).ready(function() {
	configurePathAjax();
	
	preconfigureSourceTree();
	configureSourceTree();
});

$.postJSON = function(url, data, func)
{
    $.post(url, data, func, 'json');
}

function configurePathAjax() {
	$("#path").change(function() {
		$.postJSON("/config/validate-path", {"path" : this.value}, function(data) {
			if (data.valid) {
				$("#path-text").text(data.typeAsText);
				$("#path-image").attr("src", "img/iconfinder_Tick_Mark_1398911.png");
			} else {
				$("#path-text").text("Invalid");
				$("#path-image").attr("src", "img/iconfinder_Close_Icon_1398919.png");
			}
		});
	});
}

function configureSourceTree() {
	$("#sourcetree").jstree({
		'checkbox': {
	        three_state: false,
	        cascade: 'up'
	    },
	    plugins: ["checkbox"]
	});
}

function preconfigureSourceTree() {
	$('#sourcetree') 
	.bind('before.jstree', function(event, data){ 
	        switch(data.plugin){ 
	                case 'ui': 
	                        if(data.inst.is_leaf(data.args[0])){ 
	                                return false; 
	                        } 
	                        break; 
	                default: 
	                        break; 
	        } 
	}) 
}