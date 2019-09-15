
class HorizontalSystemModelLayouter {
	
	constructor(spacing) {
		this.spacing = spacing;
		
		this.positions = {};
		this.grid = {};
		this.igrid = {};
		this.custom = false;
	}
	
	layoutRoot(root) {
		this.parent = root;
		
		var height_min_base = SystemGraphConsts.COMPOSITE_LINE_SPACING_TOP;
		
		// alts
		var height_min_0 = root.provided.length * SystemGraphConsts.SYSTEM_PROVIDED_SIZE + root.provided.length * this.spacing;
		var height_min_1 = root.required.length * (SystemGraphConsts.SYSTEM_REQUIRED_SIZE * SystemGraphConsts.REQ_ROLE_RELATION) + root.required.length * this.spacing;
		
		// assemblys
		this.layoutAsssemblys(root);
		
		// get heighest assembly
		var max_height = {};
		
		console.log(height_min_0);
	}
	
	layoutAsssemblys(composite) {
		this.igrid[composite.id] = Array(composite.assemblys.length).fill(0);
		this.grid[composite.id] = {};
		
		var curr_grid = this.grid[composite.id];
		
		if (composite.assemblys.length < 1) return;
		
		// get all positions
		for (var i = 0; i < composite.assemblys.length; i++) {
			var current_assembly = composite.assemblys[i];
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
				var inner_pos = this.calibratePosition(out_ass, grid);
				
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
			
			return this.grid[ass.id];
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