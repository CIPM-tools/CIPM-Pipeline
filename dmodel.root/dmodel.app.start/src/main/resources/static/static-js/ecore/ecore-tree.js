
var ecoreTree = {
		
	icons : {},
	base_path : null,
	ending : null,
	data : {},
	
	registerIconBasePath : function(path, ending) {
		ecoreTree.base_path = path;
		ecoreTree.ending = ending;
	},
	
	registerIcon : function(type, icon) {
		ecoreTree.icons[type] = icon;
	},
		
	build : function(parent, eobject, attributeContainer, showAttributesCallback) {
		$(parent).jstree({
			'core' : {
				"themes" : {
				      "variant" : "medium"
				    },
				'check_callback' : true
			},
			'plugins' : ["types", "theme"]
		});
		
		ecoreTree.data[parent] = {};
		ecoreTree.buildRecursive(parent, eobject, null, {id : 0});
		
		$(parent).delegate("a","dblclick", function(e) {
			var node = $(e.target).closest("li");
            var id = node[0].id; //id of the selected node
            
            if (attributeContainer !== undefined) {
            	ecoreTree.viewAttributes(ecoreTree.data[parent][id], attributeContainer, showAttributesCallback);
            }
		});
	},
	
	viewAttributes : function(data, container, callback) {
		$(container).empty();
		for (var key in data.attributes) {
			var attributeData = '<tr><td>' + key + '</td><td>' + data.attributes[key] + '</td></tr>';
			$(container).append(attributeData);
		}
		
		callback();
	},
	
	buildRecursive : function(tree, data, parent, id_counter) {
		var currId = id_counter.id++;
		var genId = currId.toString(16);
		
		ecoreTree.data[tree][genId] = data;
		
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
		if (data.type === null) {
			return "fa fa-question";
		}
		
		if (ecoreTree.icons.hasOwnProperty(data.type)) {
			return ecoreTree.icons[data.type];
		} else if (ecoreTree.base_path !== null) {
			return ecoreTree.base_path + data.type + ecoreTree.ending;
		} else {
			return "fa fa-question";
		}
	},
	
	generateText(element) {
		if (element.type === null || element.type === undefined) {
			return "Not existing";
		}
		
		baseText = element.type;
		if (element.attributes.hasOwnProperty("id")) {
			baseText += " (ID = " + element.attributes.id + ")";
		}
		return baseText;
	}
};