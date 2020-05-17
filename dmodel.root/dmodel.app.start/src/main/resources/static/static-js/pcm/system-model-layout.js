
class HorizontalSystemModelLayouter {
	
	constructor() {
		this.positions = {};
		this.rowStartY = {};
		this.grid = {};
		this.igrid = {};
		this.custom = false;
		this.assemblys = {};
	}
	
	getPosition(root, id) {
		return this.positions[root.id][id];
	}
	
	layoutRoot(root) {
		this.parent = root;
		this.positions[root.id] = {};
		this.rowStartY[root.id] = {};
		
		var height_min_base = SystemGraphConsts.COMPOSITE_LINE_SPACING_TOP;
		
		// alts
		var height_min_0 = root.provided.length * SystemGraphConsts.SYSTEM_PROVIDED_SIZE + root.provided.length * SystemGraphConsts.SYSTEM_SPACING_ROLES;
		var height_min_1 = root.required.length * (SystemGraphConsts.SYSTEM_REQUIRED_SIZE * SystemGraphConsts.REQ_ROLE_RELATION) + root.required.length * SystemGraphConsts.SYSTEM_SPACING_ROLES;
		
		// assemblys
		this.layoutAsssemblys(root);
		
		// get heighest assembly
		var max_height = {};
		var max_col = 0;
		
		var curr_grid = this.grid[root.id];
		
		for (var key in curr_grid) {
			var value = curr_grid[key];
			
			var col = value[0];
			var row = value[1];
			
			// column
			if (col > max_col) {
				max_col = col;
			}
			
			// row
			var belongingAssembly = this.assemblys[root.id][key];
			if (max_height.hasOwnProperty(row)) {
				max_height[row] = Math.max(max_height[row], Math.max(belongingAssembly.required.length, belongingAssembly.provided.length));
			} else {
				max_height[row] = Math.max(belongingAssembly.required.length, belongingAssembly.provided.length);
			}
		}
		
		// calculate height and width
		var calc_height = SystemGraphConsts.ASS_MARGIN_TOP;
		var last_i = 0;
		for (var r in max_height) {
			this.rowStartY[root.id][r] = calc_height;
			calc_height += max_height[r] * SystemGraphConsts.ASS_PROVIDED_SIZE +
				((max_height[r] - 1) * SystemGraphConsts.ASS_SPACING_ROLES) + (max_height[r] * SystemGraphConsts.ASS_SPACING_X);
			last_i = r;
		}
		this.rowStartY[root.id][parseInt(last_i) + 1] = calc_height;
		calc_height += SystemGraphConsts.ASS_MARGIN_BOTTOM;
		
		// final values
		var fHeight = calc_height + height_min_base;
		var fHeight = Math.max(fHeight, Math.max(height_min_0, height_min_1));
		
		var fWidth = Math.max(SystemGraphConsts.COMPOSITE_MIN_WIDTH,
				SystemGraphConsts.ASS_MARGIN_LEFT + SystemGraphConsts.ASS_MARGIN_RIGHT +
				SystemGraphConsts.ASSEMBLY_WIDTH * (max_col + 1) +
				((max_col + 1) * ((SystemGraphConsts.ASS_ROLE_LEN + SystemGraphConsts.ASS_PROVIDED_SIZE + SystemGraphConsts.ASS_ROLE_LEN + SystemGraphConsts.ASS_REQUIRED_SIZE))) +
				SystemGraphConsts.ASS_SPACING_X * max_col);
		var fX = SystemGraphConsts.SYSTEM_PROVIDED_SIZE + SystemGraphConsts.COMPOSITE_ROLE_LEN;
		var fY = SystemGraphConsts.COMPOSITE_MARGIN_TOP;
		
		// parent position
		this.positions[root.id][root.id] = {
				x : fX,
				y : fY,
				width : fWidth,
				height : fHeight
		};
		
		// role positions (provs)
		this.layoutProvidedRoles(this.positions[root.id][root.id], root, true, root);
		this.layoutRequiredRoles(this.positions[root.id][root.id], root, true, root);
		
		// recursive calculate positions
		this.layoutRecursive(root);
	}
	
	layoutRecursive(parent) {
		var pos = this.positions[parent.id];
		var parent_position = this.positions[parent.id][parent.id];
		
		for (var i = 0; i < parent.assemblys.length; i++) {
			var grid_position = this.grid[parent.id][parent.assemblys[i].id];
			
			var fX = SystemGraphConsts.ASS_MARGIN_LEFT +
			grid_position[0] *
			(SystemGraphConsts.ASSEMBLY_WIDTH + SystemGraphConsts.ASS_ROLE_LEN * 2 + SystemGraphConsts.ASS_SPACING_X + SystemGraphConsts.ASS_PROVIDED_SIZE + SystemGraphConsts.ASS_REQUIRED_SIZE)
			+ SystemGraphConsts.ASS_ROLE_LEN + SystemGraphConsts.ASS_PROVIDED_SIZE;
			var fY = this.rowStartY[parent.id][grid_position[1]] + grid_position[1] * SystemGraphConsts.ASS_SPACING_Y;
			var fWidth = SystemGraphConsts.ASSEMBLY_WIDTH;
			
			var fHeight = this.rowStartY[parent.id][grid_position[1] + 1] - this.rowStartY[parent.id][grid_position[1]];
			
			this.positions[parent.id][parent.assemblys[i].id] = {
				x : fX,
				y : fY + SystemGraphConsts.COMPOSITE_LINE_SPACING_TOP,
				width : fWidth,
				height : fHeight
			};
			
			// calculate role positions
			this.layoutProvidedRoles(this.positions[parent.id][parent.assemblys[i].id], parent.assemblys[i], false, parent);
			this.layoutRequiredRoles(this.positions[parent.id][parent.assemblys[i].id], parent.assemblys[i], false, parent);
		}
	}
	
	layoutRequiredRoles(parent_pos, parent, is_system, root) {
		var spaceEachReq = parent_pos.height / parent.required.length;
		var roleLen = is_system ? SystemGraphConsts.COMPOSITE_ROLE_LEN : SystemGraphConsts.ASS_ROLE_LEN;
		var requiredSize = is_system ? SystemGraphConsts.SYSTEM_REQUIRED_SIZE : SystemGraphConsts.ASS_REQUIRED_SIZE;
		
		parent.required.forEach(function(role, index) {
			this.positions[root.id][role.id] = {
				x : parent_pos.x + parent_pos.width + roleLen,
				y : parent_pos.y + spaceEachReq * index + spaceEachReq / 2 - (requiredSize * SystemGraphConsts.REQ_ROLE_RELATION) / 2,
				width : requiredSize,
				height : requiredSize * SystemGraphConsts.REQ_ROLE_RELATION
			};
		}, this);
	}
	
	layoutProvidedRoles(parent_pos, parent, is_system, root) {
		var spaceEachProv = parent_pos.height / parent.provided.length;
		var roleLen = is_system ? SystemGraphConsts.COMPOSITE_ROLE_LEN : SystemGraphConsts.ASS_ROLE_LEN;
		var providedSize = is_system ? SystemGraphConsts.SYSTEM_PROVIDED_SIZE : SystemGraphConsts.ASS_PROVIDED_SIZE;
		
		parent.provided.forEach(function(role, index) {
			this.positions[root.id][role.id] = {
				x : parent_pos.x - roleLen - (is_system ? SystemGraphConsts.SYSTEM_PROVIDED_SIZE : SystemGraphConsts.ASS_PROVIDED_SIZE),
				y : parent_pos.y + spaceEachProv * index + spaceEachProv / 2 - providedSize / 2,
				width : providedSize,
				height : providedSize
			};
		}, this);
	}
	
	layoutAsssemblys(composite) {
		this.igrid[composite.id] = Array(composite.assemblys.length).fill(0);
		this.grid[composite.id] = {};
		this.assemblys[composite.id] = {};
		
		var curr_grid = this.grid[composite.id];
		
		if (composite.assemblys.length < 1) return;
		
		// get all positions
		for (var i = 0; i < composite.assemblys.length; i++) {
			var current_assembly = composite.assemblys[i];
			this.assemblys[composite.id][current_assembly.id] = current_assembly;
			this.calibratePosition(current_assembly, curr_grid, this.igrid[composite.id]);
		}
	}
	
	calibratePosition(ass, grid, igrid) {
		// already calculated the position?
		if (grid.hasOwnProperty(ass.id)) {
			return grid[ass.id];
		}
		
		// get outgoing roles
		var provs = this.getProvided(this.parent, ass);
		
		if (provs.length == 0) {
			grid[ass.id] = [0, igrid[0]++];
			return grid[ass.id];
		} else {
			// get all roles
			var psumx = 0;
			var pmaxy = 0;
			for (var i = 0; i < provs.length; i++) {
				var out_ass = this.assemblyByProvidedRole(provs[i].role2);
				var inner_pos = this.calibratePosition(out_ass, grid, igrid);
				
				psumx += (inner_pos[0] + 1);
				pmaxy = Math.max(inner_pos[1], pmaxy);
			}
			
			// calculate the pos
			var pos_x = Math.round(psumx / provs.length);
			var pos_y;
			if (pmaxy > 0 && pmaxy + 1 >= igrid[pos_x]) {
				pos_y = pmaxy + 1;
				igrid[pos_x] = pmaxy + 2;
			} else {
				pos_y = igrid[pos_x]++;
			}
			grid[ass.id] = [pos_x, pos_y];
			
			return grid[ass.id];
		}
	}
	
	getProvided(composite, ass) {
		var prov_roles = ass.provided.map(p => p.id);
		return composite.connectors.filter(conn => !conn.delegation && prov_roles.includes(conn.role1)); 
	}
	
	getRequired(composite, ass) {
		var req_roles = ass.required.map(req => req.id);
		return composite.connectors.filter(conn => !conn.delegation && req_roles.includes(conn.role2)); 
	}
	
	assemblyByProvidedRole(roleId) {
		return this.parent.assemblys.filter(a => a.required.some(p => p.id === roleId))[0];
	}
	
	assemblyById(id) {
		return this.parent.assemblys.filter(a => a.id === id)[0];
	}
	
}