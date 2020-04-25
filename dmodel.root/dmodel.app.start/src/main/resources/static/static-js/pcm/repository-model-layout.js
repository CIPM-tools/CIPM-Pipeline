
class SimpleRepositoryLayouter {
	
	constructor(xSpacing, ySpacing, componentWidth, componentHeight, isShifted) {
		this.positions = {};
		
		this.xSpacing = xSpacing;
		this.ySpacing = ySpacing;
		
		this.componentWidth = componentWidth;
		this.componentHeight = componentHeight;
		
		this.isShifted = isShifted;
		
	}
	
	layout(model) {
		var _this = this;
		model.interfaces.forEach(function(iface, index) {
			if (_this.isShifted) {
				_this.positions[iface.id] = {x : index + 0.5, y : 0};
			}
		});
		
		model.components.forEach(function(component, index) {
			_this.positions[component.id] = {x : index, y : 1};
		});
	}
	
	getPosition(id) {
		var x = this.positions[id].x;
		var y = this.positions[id].y;
		
		var left = x * this.componentWidth + x * this.xSpacing + RepositoryGraphConsts.REPO_MARGIN_LEFT;
		var top = y * this.componentHeight + y * this.ySpacing + RepositoryGraphConsts.REPO_MARGIN_TOP;
		var width = this.componentWidth;
		var height = this.componentHeight;
		
		return {
			x : left,
			y : top,
			width: width,
			height : height
		};
	}
	
}