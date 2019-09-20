
var ecoreTree = {
		
	icons : {},
	base_path : null,
	ending : null,
	
	registerIconBasePath : function(path, ending) {
		ecoreTree.base_path = path;
		ecoreTree.ending = ending;
	},
	
	registerIcon : function(type, icon) {
		ecoreTree.icons[type] = icon;
	},
		
	build : function(parent, eobject) {
		$(parent).jstree({
			'core' : {
				"themes" : {
				      "variant" : "medium"
				    },
				'check_callback' : true
			},
			'plugins' : ["types", "theme"]
		});
		
		ecoreTree.buildRecursive(parent, eobject, null, {id : 0});
	},
	
	buildRecursive : function(tree, data, parent, id_counter) {
		var currId = id_counter.id++;
		var genId = currId.toString(16);
		
		$(tree).jstree().create_node(parent, {
			"id" : genId,
			"text" : ecoreTree.generateText(data),
			"icon" : ecoreTree.getIcon(data)
		}, "last", function() {
			for (var i = 0; i < data.childs.length; i++) {
				ecoreTree.buildRecursive(tree, data.childs[i], genId, id_counter);
			}
		});
	},
	
	getIcon(data) {
		if (ecoreTree.icons.hasOwnProperty(data.type)) {
			return ecoreTree.icons[data.type];
		} else if (ecoreTree.base_path !== null) {
			return ecoreTree.base_path + data.type + ecoreTree.ending;
		} else {
			return "fa fa-question";
		}
	},
	
	generateText(element) {
		baseText = element.type;
		if (element.attributes.hasOwnProperty("id")) {
			baseText += " (ID = " + element.attributes.id + ")";
		}
		return baseText;
	}
};