package elements;

import game.Grid;

public class Cell {
	
	final Grid grid;
	public int x, y;
	
	public final boolean hori; // is horizontal?
	public final boolean tl; // top-left; if the object is fused, is it the "most top-left" cell?
	
	public int color; // see Grid for each number's color
	
	public boolean fused; // is fused with another cell?
	Cell cf; // cell-friend; cell fused with the object (if any)

	public Cell(Grid grid, int x, int y, boolean hori, boolean tl, Cell cf, int color) {
		
		// variables
		
		this.grid = grid;
		this.x = x;
		this.y = y;
		
		this.hori = hori;
		this.tl = tl;
		
		this.color = color;
		
		this.fused = cf != null;
		this.cf = cf;
		
		// procedures
		
		this.grid.g[x][y] = this.color;
		
	}
	
	public void updateCf(Cell cf) {
		
		this.fused = cf != null;
		this.cf = cf;
		
	}
	
	public boolean fall() {
		
		if (! this.canFall())
			return false;
		
		if ((this.fused && this.tl)|| ! this.fused) 
			this.grid.g[this.x][this.y] = 0;
		this.y += 1;
		this.grid.g[this.x][this.y] = this.color;
		
		return true;
		
	}
	
	public boolean canFall() {
		
		if (this.fused) {
			if (this.hori)
				return this.canFallIndiv() && this.cf.canFallIndiv();
			else if (this.tl)
				return this.cf.canFallIndiv();
			else
				return this.canFallIndiv();
		} else
			return this.canFallIndiv();
		
	}
	
	public boolean canFallIndiv() {
		// without considering the cell-friend
		
		if (this.y == this.grid.height - 1) return false;
		// System.out.println("a" + Integer.toString(this.grid.g[this.x][this.y + 1]));
		return this.grid.g[this.x][this.y + 1] == 0;
		
	}
	
	public void kill() {
		
		this.grid.g[this.x][this.y] = 0;
		if (this.fused) {
			this.fused = false;
			this.cf.fused = false;
			this.cf.cf = null;
		}
		
	}

}
