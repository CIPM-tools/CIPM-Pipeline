var BASE64_SYSTEM_COMPOSITE = "R0lGODlhEAAQANUxAFJSUrOv/y8vLxwcHGlpadPk/8vV/0lJSSsrK8XJ/0xMTMza/9Dd/8XN/9Pl/zw8PFFRUV1dXREREby+/9Hh/woKCkZGRr++/9Lh/87b/83Y/8nU/9Lj/8PG/7i4/7q7/768/8rS/8HA/7Wz/726/87d/9Df/8LJ/7Sx/77C/8jP/9Hg/8vX/7e1/0FBQb25/wAAAP///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAADEALAAAAAAQABAAAAZ/wJhwSCwaj0gCbMkkIGGv6OvSgQkhhwjAFYO+SKJEyKoQbgUDKGhpYMAsD6EL3b3AVBpYwYowwhIwGRgOHFYxEhVEMG1LFAuGfisFBSYwDYZYWlwwDjAlGzATZGZzaY5LHyNvcTGlXSwNKR4oAXx+JxMtAbuGiERKTEtOSMRCQQA7";
var BASE64_ROLE_REQUIRED = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGUAAACNCAYAAACua7QgAAADUXRFWHRteGZpbGUAJTNDbXhmaWxlJTIwbW9kaWZpZWQlM0QlMjIyMDE5LTA5LTA0VDEyJTNBMDIlM0E0MS44MDVaJTIyJTIwaG9zdCUzRCUyMnd3dy5kcmF3LmlvJTIyJTIwYWdlbnQlM0QlMjJNb3ppbGxhJTJGNS4wJTIwKE1hY2ludG9zaCUzQiUyMEludGVsJTIwTWFjJTIwT1MlMjBYJTIwMTBfMTRfNiklMjBBcHBsZVdlYktpdCUyRjUzNy4zNiUyMChLSFRNTCUyQyUyMGxpa2UlMjBHZWNrbyklMjBDaHJvbWUlMkY3Ni4wLjM4MDkuMTMyJTIwU2FmYXJpJTJGNTM3LjM2JTIyJTIwdmVyc2lvbiUzRCUyMjExLjIuNSUyMiUyMGV0YWclM0QlMjJhNng3N3BmZE9LNWt0d0tzZXFNOSUyMiUyMHR5cGUlM0QlMjJkZXZpY2UlMjIlMjBwYWdlcyUzRCUyMjElMjIlM0UlM0NkaWFncmFtJTIwaWQlM0QlMjJCZVZXb0twOVpQdlFxbmJkMWJ1eCUyMiUzRWpaSk5iNE13RElaJTJGRFhkSXBxMjlqblVmMGlaTjZtSG5sTGdRS2NFc21BTDc5UXRMd29lcVNyc2c1JTJGRnI3THhPd25NenZGalJWQjhvUVNjc2xVUENueExHJTJCSDdudmhNWVBXQXM5YUMwU25xVUxlQ29maURBS091VWhIWWpKRVJOcXRuQ0F1c2FDdG93WVMzMlc5a1o5YlpySTBxNEFzZEM2R3Y2cFNSVm51N1l3OEpmUVpWVjdKemQ3MzNHaUNnT04ya3JJYkZmSVg1SWVHNFJ5VWRteUVGUDNrVmZmTjN6amV3OG1JV2ElMkZsUEFmTUZGNkM3Y0xjeEZZN3lzRzdHWlFndmZuYklnMzJvQ2V4YUZZNDhWR2UxU21Rc3ZZRWs1aTk3RkNmUW50b29VMWk1M1FpSTBUaEE2T1JrTU42Zk5aZyUyRmMyd0UwUUhaMGtsREFlTEF0dkp1N2NPeVhKV1JwWU5WNkFWRW93dUxMJTJCZGVMTnk0STlzVGpzb2ElMkYzT290ODhNdiUzQyUyRmRpYWdyYW0lM0UlM0MlMkZteGZpbGUlM0WYs7bxAAAJv0lE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";
var BASE64_ASSEMBLY_CONTEXT = "data:image/gif;base64,R0lGODlhDgANANUxAO7u7vv7++bm5uvr69DQ0NnZ2ejo6OHh4dfX19PT0+Pj49zc3Pn5+fHx8fDw8M3NzeTk5Pj4+PX19dLS0uzs7NXV1dvb297e3uDg4Onp6djY2N3d3dbW1vPz8+rq6u/v78/Pz8zMzPz8/Ofn5+Xl5fT09Pb29u3t7f7+/tHR0c7OztTU1Nra2vr6+vf39729vWxsbP///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAADEALAAAAAAOAA0AAAZwwJjw9YLBikfjSxiDoUQBhkvScQCWw0ArYio1PhSPcnx0nAYGgTEaoTZglMwIciCSiwaSArOBMV8AAxkCdBcLBXdKfBYaHEWBaQoHMIcIFQlJmUWNCRMEfkMXFgUIK54gD1hNLJYJKQQqDyGqdppKQQA7";

var SystemGraphConsts = {
	COMPOSITE_LINE_SPACING_TOP : 50,
	COMPOSITE_TEXT_Y : 22,
	COMPOSITE_IMAGE_SIZE : 16,
	
	REQ_ROLE_RELATION : 62 / 45,
	
	ASSEMBLY_IMAGE_SIZE : 16
};

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
		this.providedParents = {};
		this.requiredParents = {};
		
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
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_ELLIPSE;
		style[mxConstants.STYLE_FONTCOLOR] = 'black';
		style[mxConstants.STYLE_FILLCOLOR] = 'white';
		this.graph.getStylesheet().putCellStyle('provided', style);
		
		style = mxUtils.clone(org_style);
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_IMAGE;
		style[mxConstants.STYLE_IMAGE] = BASE64_ROLE_REQUIRED;
		style[mxConstants.STYLE_PERIMETER] = mxPerimeter.RectanglePerimeter;
		style[mxConstants.STYLE_ENTRY_PERIMETER] = false;
		style[mxConstants.STYLE_EXIT_PERIMETER] = false;
		style[mxConstants.STYLE_ROTATION] = 0;
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
		
		style = [];
		style[mxConstants.STYLE_SHAPE] = mxConstants.SHAPE_CONNECTOR;
		style[mxConstants.STYLE_STROKECOLOR] = 'gray';
		style[mxConstants.STYLE_ALIGN] = mxConstants.ALIGN_CENTER;
		style[mxConstants.STYLE_VERTICAL_ALIGN] = mxConstants.ALIGN_MIDDLE;
		style[mxConstants.STYLE_EDGE] = mxEdgeStyle.ElbowConnector;
		style[mxConstants.STYLE_ENDARROW] = mxConstants.ARROW_CLASSIC;
		style[mxConstants.STYLE_FONTSIZE] = '10';
		this.graph.getStylesheet().putDefaultEdgeStyle(style);
		
		style = mxUtils.clone(style);
		style[mxConstants.STYLE_STROKECOLOR] = 'black';
		style[mxConstants.STYLE_ENDARROW] = undefined;
		style[mxConstants.STYLE_EDGE] = mxEdgeStyle.TopToBottom;
		this.graph.getStylesheet().putCellStyle('provided_line', style);
		
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
			
			evt.consume();
        });
	}
	
	draw() {
		// Adds cells to the model in a single step
		var v2 = this.drawComposite("WholeSystem", 20, 150, 500, 200);
		
		var p1 = this.drawProvidedRole(v2, 200, 20, 35);
		var r1 = this.drawRequiredRole(v2, 600, 150, 35);
		
		this.connectRoles(r1, p1);
		
		var a1 = this.drawAssembly(v2, "assembly", 40, 70, 150, 50);
		
		this.realignRoles();
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
	
	realignRoles() {
		this.update(function() {
			for (var i = 0; i < this.requiredRoles.length; i++) {
				var r = this.requiredRoles[i];
				var p = this.requiredParents[r];
				
				if (r.geometry.y + r.geometry.height < p.geometry.y) {
					this.graph.getModel().setStyle(r, 'req_top');
				} else if (r.geometry.y > p.geometry.y + p.geometry.height) {
					this.graph.getModel().setStyle(r, 'req_bottom');
				} else if (r.geometry.x > p.geometry.x + p.geometry.width) {
					this.graph.getModel().setStyle(r, 'req_right');
				} else {
					this.graph.getModel().setStyle(r, 'req_left');
				}
			}
		});
	}
	
	connectRoles(required, provided) {
		var graph = this.graph;
		
		var p1 = this.providedParents[provided];
		var p2 = this.requiredParents[required];
		
		if (p1 == p2) {
			// easy connection
			this.update(function() {
				graph.insertEdge(p1.getParent(), null, '', required, provided, 'connect_line');
			});
		} else {
			// delegation
			// TODO
		}
	}
	
	drawRequiredRole(parent, x, y, width) {
		var _parent = parent.getParent();
		var graph = this.graph;
		var role;
		
		this.update(function() {
			role = graph.insertVertex(_parent, null, '', x, y, width, SystemGraphConsts.REQ_ROLE_RELATION * width, 'req_left');
			graph.insertEdge(_parent, null, '', parent, role, 'provided_line');
		});
		
		// add it internally
		this.requiredRoles.push(role);
		this.requiredParents[role] = parent;
		
		return role;
	}
	
	drawProvidedRole(parent, x, y, diameter) {
		var _parent = parent.getParent();
		var graph = this.graph;
		var role;
		
		this.update(function() {
			role = graph.insertVertex(_parent, null, '', x, y, diameter, diameter, 'provided');
			graph.insertEdge(_parent, null, '', parent, role, 'provided_line');
		});
		
		// add it internally
		this.providedRoles.push(role);
		this.providedParents[role] = parent;
		
		return role;
	}
	
	drawAssembly(parent, name, x, y, width, height) {
		var container, image, text;
		
		this.update(function(graph) {
			container = graph.insertVertex(parent, null, '', x, y, width, height, 'assembly');
			
			image = graph.insertVertex(container, null, '', 7, 7, SystemGraphConsts.ASSEMBLY_IMAGE_SIZE, SystemGraphConsts.ASSEMBLY_IMAGE_SIZE, 'assembly_image');
			text = graph.insertVertex(container, null, name, image.geometry.x + image.geometry.width + 3, 0, 0, 0, 'ass_text');
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