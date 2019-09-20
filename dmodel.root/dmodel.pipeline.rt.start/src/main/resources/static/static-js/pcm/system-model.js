
class PCMSystemGraph {
	constructor(container) {
		if (!mxClient.isBrowserSupported())
		{
			// Displays an error message if the browser is not supported.
			mxUtils.error('Browser is not supported!', 200, false);
		}
		
		// Lists
		this.providedRoles = [];
		this.requiredRoles = [];
		this.parents = {};
		this.roleLine = {};
		this.delegates = [];
		this.elementMapping = {};
		this.elementMappingReversed = {};
		this.requiredRoleMarked = {};
		
		this.renamings = {};

		// Enables crisp rendering of rectangles in SVG
		mxRectangleShape.prototype.crisp = true;
		
		// Creates the graph inside the given container
		this.graph = new mxGraph(container);
		
		// Gets the default parent for inserting new cells. This
		// is normally the first child of the root (ie. layer 0).
		this.parent = this.graph.getDefaultParent();
		
		this.setup();
		this.registerEvents();
	}
	
	setup() {
		// Disables global features
		this.graph.collapseToPreferredSize = false;
		this.graph.setConnectable(false);
		this.graph.setAllowDanglingEdges(false);
		this.graph.setAutoSizeCells(true);
		this.graph.setPanning(true);
		mxGraph.prototype.ordered = false;
		mxPanningHandler.prototype.useLeftButtonForPanning = true;
		
		var style = [];
		style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
		style[mxConstants.STYLE_FILLCOLOR] = '#ffffff';
		style[mxConstants.STYLE_STROKECOLOR] = 'black';
		style[mxConstants.STYLE_FONTCOLOR] = 'gray';
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
		style[mxConstants.STYLE_RESIZABLE] = false;
		style[mxConstants.STYLE_ROTATABLE] = false;
		style[mxConstants.STYLE_DELETABLE] = false;
		style[mxConstants.STYLE_EDITABLE] = false;
		style[mxConstants.STYLE_FOLDABLE] = false;
		style[mxConstants.STYLE_RESIZE_WIDTH] = false;
		style[mxConstants.STYLE_RESIZE_HEIGHT] = false;
		style[mxConstants.STYLE_AUTOSIZE] = false;
		style[mxConstants.STYLE_SPACING_TOP] = 5;
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_RECTANGLE;
		this.graph.getStylesheet().putDefaultVertexStyle(style);
		
		var org_style = style;
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_STROKECOLOR] = 'gray';
		style[mxConstants.STYLE_DASHED] = true;
		this.graph.getStylesheet().putCellStyle('assembly', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_IMAGE] = BASE64_ASSEMBLY_CONTEXT;
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_MOVABLE] = false;
		this.graph.getStylesheet().putCellStyle('assembly_image', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_IMAGE] = 'data:image/png,' + BASE64_SYSTEM_COMPOSITE;
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_MOVABLE] = false;
		this.graph.getStylesheet().putCellStyle('comp_image', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_IMAGE] = BASE64_PROVIDED_DELEGATION;
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_MOVABLE] = false;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_PERIMETER] = undefined;
		this.graph.getStylesheet().putCellStyle('provided_delegate', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_IMAGE] = BASE64_REQUIRED_DELEGATION;
		this.graph.getStylesheet().putCellStyle('required_delegate', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_LABEL;
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_MOVABLE] = false;
		this.graph.getStylesheet().putCellStyle('norm_text', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_LABEL;
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_MOVABLE] = false;
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_CENTER;
		this.graph.getStylesheet().putCellStyle('ass_text', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_LABEL;
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_MOVABLE] = false;
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_EDITABLE] = true;
		this.graph.getStylesheet().putCellStyle('ass_text_modifiable', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_ELLIPSE;
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_FILLCOLOR] = 'white';
		style[mxConstants.STYLE_VERTICAL_LABEL_POSITION] = mxConstants.ALIGN_BOTTOM;
		this.graph.getStylesheet().putCellStyle('provided', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_IMAGE] = BASE64_ROLE_REQUIRED;
		style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
		style[mxConstants.STYLE_ENTRY_PERIMETER] = false;
		style[mxConstants.STYLE_EXIT_PERIMETER] = false;
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_ROTATION] = 0;
		style[mxConstants.STYLE_VERTICAL_LABEL_POSITION] = mxConstants.ALIGN_BOTTOM;
		this.graph.getStylesheet().putCellStyle('req_left', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ROTATION] = 90;
		this.graph.getStylesheet().putCellStyle('req_top', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ROTATION] = 180;
		this.graph.getStylesheet().putCellStyle('req_right', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ROTATION] = 270;
		this.graph.getStylesheet().putCellStyle('req_bottom', style);
		
		// marked ones
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_IMAGE] = BASE64_ROLE_REQUIRED_MARKED;
		style[mxConstants.STYLE_ROTATION] = 0;
		this.graph.getStylesheet().putCellStyle('req_left_marked', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ROTATION] = 90;
		this.graph.getStylesheet().putCellStyle('req_top_marked', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ROTATION] = 180;
		this.graph.getStylesheet().putCellStyle('req_right_marked', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ROTATION] = 270;
		this.graph.getStylesheet().putCellStyle('req_bottom_marked', style);
		
		style = [];
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
		style[mxConstants.STYLE_STROKECOLOR] = 'gray';
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
		style[mxConstants.STYLE_EDGE] = mxEdgeStyle.ElbowConnector;
		style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
		style[mxConstants.STYLE_FONTSIZE] = '10';
		style[mxConstants.STYLE_EDITABLE] = false;
		this.graph.getStylesheet().putDefaultEdgeStyle(style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_STROKECOLOR] = 'black';
		style[mxConstants.STYLE_ENDARROW] = undefined;
		style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
		style[mxConstants.STYLE_VERTICAL_LABEL_POSITION] = mxConstants.ALIGN_BOTTOM;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_TOP;
		this.graph.getStylesheet().putCellStyle('provided_line', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_STROKECOLOR] = 'red';
		this.graph.getStylesheet().putCellStyle('provided_line_marked', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
		style[mxConstants.STYLE_STROKECOLOR] = 'gray';
		style[mxConstants.STYLE_EDGE] = undefined;
		this.graph.getStylesheet().putCellStyle('connect_line', style);
	}
	
	registerEvents() {
		// LISTENER FOR MOVEMENTS
		var _this = this;
		this.graph.addListener(mxEvent.CELLS_MOVED, function (sender, evt) {
			_this.realignRoles();
			_this.realignDelegates();
			
			evt.consume();
        });
		
		this.graph.addListener(mxEvent.LABEL_CHANGED, function (sender, evt) {
			var cell = evt.getProperty("cell");
			var parent = cell.parent;
			
			// add renaming
			_this.renamings[_this.elementMappingReversed[parent.id]] = cell.value;
			
			evt.consume();
		});
	}
	
	apply(system) {
		if (typeof system === "string") {
			this.model = JSON.parse(system);
		} else {
			this.model = system;
		}
	}
	
	layout(obj) {
		this.layouter = obj;
	}
	
	draw() {
		if (this.model !== undefined && this.layouter !== undefined) {
			var compositeRoot = this.layouter.layoutRoot(this.model.root);
			
			var rootPosition = this.layouter.getPosition(this.model.root, this.model.root.id);
			var rootObject = this.drawComposite(this.model.root.name, rootPosition.x, rootPosition.y, rootPosition.width, rootPosition.height);
			this.drawRoles(rootObject, this.model.root);
			this.elementMappingReversed[rootObject.id] = this.model.root.id;
			this.elementMapping[this.model.root.id] = rootObject;
			
			// recursive subelements
			this.drawRecursive(rootObject);
			
			// connections
			this.drawConnectors();
			
			// this creates mappings and relations
			this.finalizeDrawing();
		}
	}
	
	drawConnectors() {
		this.model.root.connectors.forEach(function(connector) {
			if (connector.delegation) {
				var role1 = this.elementMapping[connector.role1];
				var role2 = this.elementMapping[connector.role2];
				this.delegateRoles(role2, role1, connector.delegationDirection);
			} else {
				var roleFrom = this.elementMapping[connector.role1];
				var roleTo = this.elementMapping[connector.role2];
				this.connectRoles(roleTo, roleFrom);
			}
		}, this);
	}
	
	markRequiredRole(roleId) {
		var role = this.elementMapping[roleId];
		var line = this.roleLine[role.id];
		
		line.setStyle('provided_line_marked');
		this.requiredRoleMarked[role.id] = true;
		
		this.realignRoles();
	}
	
	unmarkRequiredRole(roleId) {
		var role = this.elementMapping[roleId];
		var line = this.roleLine[role.id];
		
		line.setStyle('provided_line');
		
		this.requiredRoleMarked[role] = false;
		this.realignRoles();
	}
	
	drawRoles(parent, obj) {
		obj.provided.forEach(function(role) {
			var pos = this.layouter.getPosition(this.model.root, role.id);
			
			this.elementMapping[role.id] = this.drawProvidedRole(parent, pos.x, pos.y, pos.width, role.name.substring(0, 5));
			this.elementMappingReversed[this.elementMapping[role.id]] = role.id;
		}, this);
		
		obj.required.forEach(function(role) {
			var pos = this.layouter.getPosition(this.model.root, role.id);
			
			this.elementMapping[role.id] = this.drawRequiredRole(parent, pos.x, pos.y, pos.width, role.name.substring(0, 5));
			this.elementMappingReversed[this.elementMapping[role.id]] = role.id;
		}, this);
	}
	
	drawRecursive(parent) {
		this.model.root.assemblys.forEach(function(ass) {
			var pos = this.layouter.getPosition(this.model.root, ass.id);
			
			var assembly = this.drawAssembly(parent, ass.name, ass.componentName, pos.x, pos.y, pos.width, pos.height);
			this.drawRoles(assembly, ass);
			this.elementMapping[ass.id] = assembly;
			this.elementMappingReversed[assembly.id] = ass.id;
		}, this);
	}
	
	example() {
		// Adds cells to the model in a single step
		var v2 = this.drawComposite("WholeSystem", 150, 150, 500, 200);
		
		var p1 = this.drawProvidedRole(v2, 50, 220, 35);
		var r1 = this.drawRequiredRole(v2, 700, 220, 35);
		
		// this.connectRoles(r1, p1);
		
		var a1 = this.drawAssembly(v2, "assembly", 170, 70, 150, 50);
		var p2 = this.drawProvidedRole(a1, 120, 90, 20);
		
		this.delegateRoles(p1, p2, true);
		
		// this creates mappings and relations
		this.finalizeDrawing();
	}
	
	finalizeDrawing() {
		this.realignRoles();
		this.realignDelegates();
	}
	
	getRenamings() {
		return this.renamings;
	}
	
	// private methods (not supported by all browsers - therefore we omit it -
	// but the functions should only be used as privates)
	update(fn) {
		this.graph.getModel().beginUpdate();
		
		var args = [];
		for (var i = 1; i < arguments.length; i++) {
		    args.push(arguments[i]);
		}
		var res = fn.apply(this, args);
		
		this.graph.getModel().endUpdate();
		
		return res;
	}
	
	labelSize(el) {
		var box = this.graph.view.getState(el).shape.state.text.boundingBox;
		return {
			x : box.width,
			y : box.height
		};
	}
	
	absoluteToRelative(point) {
		var offset = this.graph.getView().translate;
		return new mxPoint(point.x - offset.x, point.y - offset.y);
	}
	
	realignDelegates() {
		var _this = this;
		setTimeout(function() {
			_this.update(function(graph) {
				for (var i = 0; i < _this.delegates.length; i++) {
					var delegate = _this.delegates[i];
					var state = graph.getView().getState(delegate.line);
					
					// zero is the point on the parent because we draw the edge
					// from parent
					// to role
					var pointOnRect = _this.absoluteToRelative(state.absolutePoints[0]);
					
					var nGeometry = new mxGeometry(pointOnRect.x + 4, pointOnRect.y - 16 / 2, 16, 16);
					delegate.img.setGeometry(nGeometry);
				}
				
				graph.refresh();
			}, _this.graph);
		}, 200);
	}
	
	realignRoles() {
		this.update(function() {
			for (var i = 0; i < this.requiredRoles.length; i++) {
				var r = this.requiredRoles[i];
				var p = this.parents[r.id];
				
				var marked = this.requiredRoleMarked[r.id];
				var marked_ext = marked ? "_marked" : "";
				
				if (r.geometry.y + r.geometry.height < p.geometry.y) {
					this.graph.getModel().setStyle(r, 'req_top' + marked_ext);
				} else if (r.geometry.y > p.geometry.y + p.geometry.height) {
					this.graph.getModel().setStyle(r, 'req_bottom' + marked_ext);
				} else if (r.geometry.x > p.geometry.x + p.geometry.width) {
					this.graph.getModel().setStyle(r, 'req_right' + marked_ext);
				} else {
					this.graph.getModel().setStyle(r, 'req_left' + marked_ext);
				}
			}
		});
	}
	
	connectRoles(required, provided) {
		var graph = this.graph;
		
		var p1 = this.parents[required.id];
		
		// easy connection
		this.update(function() {
			graph.insertEdge(p1.getParent(), null, '', required, provided, 'connect_line');
		});
	}
	
	delegateRoles(outer, inner, is_provided) {
		var graph = this.graph;
		var outer_line = this.roleLine[outer.id];
		var container = this.parents[outer.id];
		
		var state = graph.getView().getState(outer_line);
		
		// zero is the point on the parent because we draw the edge from parent
		// to role
		var pointOnRect = this.absoluteToRelative(state.absolutePoints[0]);
		
		var img_style = is_provided ? 'provided_delegate' : 'required_delegate';
		var image;
		
		var outer_cell = outer_line.source.parent;
		
		this.update(function() {
			image = graph.insertVertex(outer_cell, null, '', pointOnRect.x + 4, pointOnRect.y - 16 / 2, 16, 16, 'provided_delegate');
			graph.insertEdge(outer_cell, null, '', image, inner, 'connect_line');
		});
		
		this.delegates.push({
			img : image,
			line : outer_line,
			container : container
		});
		
		return image;
	}
	
	drawRequiredRole(parent, x, y, width, name) {
		var _parent = parent.getParent();
		var graph = this.graph;
		var role, edge;
		
		this.update(function() {
			role = graph.insertVertex(_parent, null, '', x, y, width, SystemGraphConsts.REQ_ROLE_RELATION * width, 'req_left');
			edge = graph.insertEdge(_parent, null, name, parent, role, 'provided_line');
		});
		
		// add it internally
		this.requiredRoles.push(role);
		this.parents[role.id] = parent;
		this.roleLine[role.id] = edge;
		
		return role;
	}
	
	drawProvidedRole(parent, x, y, diameter, name) {
		var _parent = parent.getParent();
		var graph = this.graph;
		var role, edge;
		
		this.update(function() {
			role = graph.insertVertex(_parent, null, '', x, y, diameter, diameter, 'provided');
			edge = graph.insertEdge(_parent, null, name, parent, role, 'provided_line');
		});
		
		// add it internally
		this.providedRoles.push(role);
		this.parents[role.id] = parent;
		this.roleLine[role.id] = edge;
		
		return role;
	}
	
	drawAssembly(parent, name, comp_name, x, y, width, height) {
		var container, image, text, text_comp;
		
		this.update(function(graph) {
			container = graph.insertVertex(parent, null, '', x, y, width, height, 'assembly');
			
			image = graph.insertVertex(container, null, '', 7, 7, SystemGraphConsts.ASSEMBLY_IMAGE_SIZE, SystemGraphConsts.ASSEMBLY_IMAGE_SIZE, 'assembly_image');
			text = graph.insertVertex(container, null, name, image.geometry.x + image.geometry.width + 3, 0, 0, 0, 'ass_text_modifiable');
			text_comp = graph.insertVertex(container, null, "(" + comp_name + ")", image.geometry.x + image.geometry.width + 3, 13, 0, 0, 'ass_text');
		}, this.graph);
		
		return container;
	}
	
	drawComposite(name, x, y, width, height) {
		var graph = this.graph;
		var _this = this;
		
		var container, line, text;
		this.update(function() {
			container = graph.insertVertex(null, null, '<<CompositeStructure>>', x, y, width, height);
			line = graph.insertVertex(container, null, '', 0, SystemGraphConsts.COMPOSITE_LINE_SPACING_TOP, width, 1);
			text = graph.insertVertex(container, null, name, width / 2, SystemGraphConsts.COMPOSITE_TEXT_Y, 0, 0, 'norm_text');
		});
		
		this.update(function() {
			var labelBox = _this.labelSize(text);
			var size = SystemGraphConsts.COMPOSITE_IMAGE_SIZE;
			graph.insertVertex(container, null, '',
					(width / 2) - (labelBox.x / 2) - size - 5,
					SystemGraphConsts.COMPOSITE_TEXT_Y + ((size - labelBox.y) / 2) + 3,
					size, size, 'comp_image');
		});
		
		return container;
	}
}