
class PCMRepositoryGraph {
	constructor(container) {
		if (!mxClient.isBrowserSupported())
		{
			// Displays an error message if the browser is not supported.
			mxUtils.error('Browser is not supported!', 200, false);
		}
		
		// Creates the graph inside the given container
		this.graph = new mxGraph(container);
		
		// Gets the default parent for inserting new cells. This
		// is normally the first child of the root (ie. layer 0).
		this.parent = this.graph.getDefaultParent();
		
		this.containerMapping = {};
		this.containerMappingReversed = {};
		
		this.interfaceMarkedImage = {};
		
		this.setup();
		this.registerEvents();
		
		this.eventListeners = [];
	}
	
	setup() {
		// Disables global features
		this.graph.collapseToPreferredSize = false;
		this.graph.setConnectable(false);
		this.graph.setAllowDanglingEdges(false);
		this.graph.setAutoSizeCells(true);
		this.graph.setPanning(true);
		
		// no reconnect for edges
		var graph = this.graph;
		if (graph.connectionHandler.connectImage == null)
		{
			graph.connectionHandler.isConnectableCell = function(cell)
			{
			   return false;
			};
			mxEdgeHandler.prototype.isConnectableCell = function(cell)
			{
				return graph.connectionHandler.isConnectableCell(cell);
			};
		}
		
		// default style
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
		
		// special styles
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_STROKECOLOR] = 'gray';
		style[mxConstants.STYLE_DASHED] = false;
		this.graph.getStylesheet().putCellStyle('component', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_STROKECOLOR] = '#037d50';
		style[mxConstants.STYLE_FILLCOLOR] = '#C0C0C0';
		style[mxConstants.STYLE_DASHED] = false;
		this.graph.getStylesheet().putCellStyle('component_marked', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_LABEL;
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_MOVABLE] = false;
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_LEFT;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_CENTER;
		this.graph.getStylesheet().putCellStyle('name_label', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_IMAGE] = BASE64_COMPONENT;
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_MOVABLE] = false;
		this.graph.getStylesheet().putCellStyle('component_image', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_STROKECOLOR] = 'gray';
		style[mxConstants.STYLE_DASHED] = true;
		this.graph.getStylesheet().putCellStyle('interface', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_STROKECOLOR] = '#037d50';
		style[mxConstants.STYLE_DASHED] = false;
		this.graph.getStylesheet().putCellStyle('interface_marked', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_STROKECOLOR] = '#8B0000';
		style[mxConstants.STYLE_DASHED] = false;
		this.graph.getStylesheet().putCellStyle('interface_unmarked', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_IMAGE] = BASE64_INTERFACE;
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_MOVABLE] = false;
		this.graph.getStylesheet().putCellStyle('interface_image', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_IMAGE] = BASE64_CHECKMARK;
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_MOVABLE] = false;
		this.graph.getStylesheet().putCellStyle('check_image', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_IMAGE] = BASE64_CLOSE;
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_MOVABLE] = false;
		this.graph.getStylesheet().putCellStyle('close_image', style);
		
		// edges
		style = [];
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
		style[mxConstants.STYLE_STROKECOLOR] = 'gray';
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
		style[mxConstants.STYLE_EDGE] = mxEdgeStyle.ElbowConnector;
		style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
		style[mxConstants.STYLE_FONTSIZE] = '10';
		style[mxConstants.STYLE_EDITABLE] = false;
		style[mxConstants.STYLE_MOVABLE] = false;
		style[mxConstants.STYLE_DASHED] = true;
		this.graph.getStylesheet().putDefaultEdgeStyle(style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
		style[mxConstants.STYLE_EDGE] = undefined;
		style[mxConstants.STYLE_STROKECOLOR] = 'black';
		this.graph.getStylesheet().putCellStyle('provides_line', style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
		style[mxConstants.STYLE_EDGE] = undefined;
		style[mxConstants.STYLE_STROKECOLOR] = 'black';
		this.graph.getStylesheet().putCellStyle('requires_line', style);
	}
	
	markComponent(id) {
		var el = this.containerMapping[id];
		
		this.update(function(graph) {
			graph.getModel().setStyle(el, 'component_marked');
		}, this.graph);
	}
	
	unmarkComponent(id) {
		var el = this.containerMapping[id];
		
		this.update(function(graph) {
			graph.getModel().setStyle(el, 'component');
		}, this.graph);
	}
	
	markInterface(id, green) {
		var el = this.containerMapping[id];
		var _this = this;
		
		this.update(function(graph) {
			graph.getModel().setStyle(el, green ? 'interface_marked' : 'interface_unmarked');
			
			if (id in _this.interfaceMarkedImage) {
				graph.getModel().remove(_this.interfaceMarkedImage[id]);
			}
			
			if (green) {
				_this.interfaceMarkedImage[id] = graph.insertVertex(el, null, '', el.geometry.width - RepositoryGraphConsts.MARK_SIZE, el.geometry.height / 2 - RepositoryGraphConsts.MARK_SIZE / 2, RepositoryGraphConsts.MARK_SIZE, RepositoryGraphConsts.MARK_SIZE, 'check_image');
			} else {
				_this.interfaceMarkedImage[id] = graph.insertVertex(el, null, '', el.geometry.width - RepositoryGraphConsts.MARK_SIZE, el.geometry.height / 2 - RepositoryGraphConsts.MARK_SIZE / 2, RepositoryGraphConsts.MARK_SIZE, RepositoryGraphConsts.MARK_SIZE, 'close_image');
			}
		}, this.graph);
	}
	
	unmarkInterface(id) {
		var el = this.containerMapping[id];
		var _this = this;
		
		this.update(function(graph) {
			graph.getModel().setStyle(el,'interface');
			if (id in _this.interfaceMarkedImage) {
				graph.getModel().remove(_this.interfaceMarkedImage[id]);
			}
		}, this.graph);
	}
	
	apply(repository) {
		if (typeof repository === "string") {
			this.model = JSON.parse(repository);
		} else {
			this.model = repository;
		}
	}
	
	layout(obj) {
		this.layouter = obj;
	}
	
	addEventListener(listener) {
		this.eventListeners.push(listener);
	}
	
	clearEventListeners() {
		this.eventListeners = [];
	}
	
	registerEvents() {
		var _this = this;
		
		this.graph.addListener(mxEvent.DOUBLE_CLICK, function(sender, evt) {
			var cell = evt.getProperty("cell");
			if (cell !== undefined) {
				// clicked on a cell
				if (cell.style === "interface_unmarked") {
					_this.eventListeners.forEach(function(list) {
						list.markInterface(_this.containerMappingReversed[cell.id]);
					});
				} else if (cell.style === "interface_marked") {
					_this.eventListeners.forEach(function(list) {
						list.unmarkInterface(_this.containerMappingReversed[cell.id]);
					});
				} else if (cell.style === "component_marked") {
					_this.eventListeners.forEach(function(list) {
						list.selectMarkedComponent(_this.containerMappingReversed[cell.id]);
					});
				}
			}
			
			evt.consume();
		});
	}
	
	draw() {
		// remove old
		this.graph.getModel().clear();
		
		if (this.model !== undefined && this.layouter !== undefined) {
			this.layouter.layout(this.model);
			
			// draw
			this.drawInterfaces();
			this.drawComponents();
		}
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
	
	drawComponents() {
		var _this = this;
		this.model.components.forEach(function(comp) {
			var layout = _this.layouter.getPosition(comp.id);
			
			// component
			var compContainer = _this.drawComponent(comp.entityName, layout.x, layout.y, layout.width, layout.height);
			_this.containerMapping[comp.id] = compContainer;
			_this.containerMappingReversed[compContainer.id] = comp.id;
			
			// provided roles
			comp.providedInterfaces.forEach(function(prov) {
				_this.drawProvidedRole(compContainer, _this.containerMapping[prov]);
			}, _this);
			
			// required roles
			comp.requiredInterfaces.forEach(function(req) {
				_this.drawRequiredRole(compContainer, _this.containerMapping[req]);
			}, _this);
		});
	}
	
	drawRequiredRole(from, to) {
		var edge;
		
		this.update(function(graph) {
			edge = graph.insertEdge(null, null, "<requires>", from, to, 'requires_line');
		}, this.graph);
		
		return edge;
	}
	
	drawProvidedRole(from, to) {
		var edge;
		
		this.update(function(graph) {
			edge = graph.insertEdge(null, null, "<provides>", from, to, 'provides_line');
		}, this.graph);
		
		return edge;
	}
	
	drawComponent(name, x, y, width, height) {
		var container, image, text, text_comp;
		
		this.update(function(graph) {
			container = graph.insertVertex(null, null, '', x, y, width, height, 'component');
			
			image = graph.insertVertex(container, null, '', 7, 7, RepositoryGraphConsts.INTERFACE_IMAGE_SIZE, RepositoryGraphConsts.INTERFACE_IMAGE_SIZE, 'component_image');
			text = graph.insertVertex(container, null, name, image.geometry.x + image.geometry.width + 3, 1, 0, 0, 'name_label');
		}, this.graph);
		
		return container;
	}
	
	drawInterfaces() {
		var _this = this;
		this.model.interfaces.forEach(function(iface) {
			var layout = _this.layouter.getPosition(iface.id);
			var container = _this.drawInterface(iface.entityName, layout.x, layout.y, layout.width, layout.height);
			
			_this.containerMapping[iface.id] = container;
			_this.containerMappingReversed[container.id] = iface.id;
		});
	}
	
	drawInterface(name, x, y, width, height) {
		var container, image, text, text_comp;
		
		this.update(function(graph) {
			container = graph.insertVertex(null, null, '', x, y, width, height, 'interface');
			
			image = graph.insertVertex(container, null, '', 7, 7, RepositoryGraphConsts.INTERFACE_IMAGE_SIZE, RepositoryGraphConsts.INTERFACE_IMAGE_SIZE, 'interface_image');
			text = graph.insertVertex(container, null, name, image.geometry.x + image.geometry.width + 3, 1, 0, 0, 'name_label');
		}, this.graph);
		
		return container;
	}
}