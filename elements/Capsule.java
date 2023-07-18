package elements;

import game.Grid;

public class Capsule {
	
	final Grid grid;
	public int x, y; // coordinates of the most top-left cell
	
	public boolean hori; // is horizontal?
	public int c0, c1; // c0 = color at the top-left, c1 = the other color

	public Capsule(Grid grid, int x, int y, int c0, int c1) {
		
		// variables
		
		this.grid = grid;
		this.x = x;
		this.y = y;
		
		this.hori = true;
		this.c0 = c0;
		this.c1 = c1;
		
		// procedures
		
		this.grid.g[this.x][this.y] = this.c0;
		this.grid.g[this.x + 1][this.y] = this.c1;
		/*
		 * default:
		 * ____ ____ ____ ____
		 * ____ xy__ x+1y ____
		 * by default a capsule is horizontal, this cell in (x, y) has the color c0
		 */
		
	}
	
	public boolean fall() {
		
		if (! this.canFall()) return false;
		
		if (this.hori) {
			// free empty cases
			this.grid.g[this.x][this.y] = 0; // former c0
			this.grid.g[this.x + 1][this.y] = 0; // former c1
			// update coordinates
			this.y += 1;
			// fill in new cases
			this.grid.g[this.x][this.y] = this.c0;
			this.grid.g[this.x + 1][this.y] = this.c1;
			return true;
		}
		
		// simultaneous
		this.grid.g[this.x][this.y] = 0; // free the top
		this.grid.g[this.x][this.y + 1] = this.c0; // former c1 replaced with c0
		this.grid.g[this.x][this.y + 2] = this.c1; // fill in with c1
		// update coordinates
		this.y += 1;
		
		return true;
		
	}
	
	public boolean canFall() {
		
		// if already at the bottom
		// avoids asking for items out of range
		if (this.hori && this.y == this.grid.height - 1) return false;
		if (! this.hori && this.y == this.grid.height - 2) return false;
		
		return (
				this.hori &&
				this.grid.g[this.x][this.y + 1] == 0 &&
				this.grid.g[this.x + 1][this.y + 1] == 0
				) || (
				(! this.hori) &&
				this.grid.g[this.x][this.y + 2] == 0);
		
	}
	
	public boolean rotate() { // false if can't
		
		if (! this.canRotate()) return false;
		
		this.hori = ! this.hori;
		
		if (! this.hori) { // change from horizontal to vertical
			
			// left bar
			if (this.grid.g[this.x][this.y - 1] == 0) {
				this.grid.g[this.x + 1][this.y] = 0; // free new empty case
				this.y -= 1; // update coordinates
				this.grid.g[this.x][this.y] = this.c1; // update grid
				this.swapColors(false);
				return true;
			}
			
			// right bar
			this.grid.g[this.x][this.y] = 0; // free
			this.x += 1; this.y -= 1; // upd coords
			// upd grid
			this.grid.g[this.x][this.y + 1] = this.c0;
			this.grid.g[this.x][this.y] = this.c1;
			this.swapColors(false);
			return true;
			
		} // change from vertical to horizontal
		
		// right side
		if (this.x != this.grid.width - 1) if (this.grid.g[this.x + 1][this.y + 1] == 0) {
			this.grid.g[this.x][this.y] = 0; // free
			this.y += 1; // upd coords
			// upd grid
			this.grid.g[this.x][this.y] = this.c0;
			this.grid.g[this.x + 1][this.y] = this.c1;
			return true;
		}
		
		// left side
		this.grid.g[this.x][this.y] = 0; // free
		this.x -= 1; this.y += 1; // upd coords
		this.grid.g[this.x][this.y] = this.c0; // upd grid
		return true;
		
	}
	
	private boolean canRotate() {
		
		if (this.y == 0) return false;
		
		if (this.hori)
			return (this.grid.g[this.x][this.y - 1] == 0 ||
					this.grid.g[this.x + 1][this.y - 1] == 0);
		else if (this.x == 0)
			return (this.grid.g[this.x + 1][this.y + 1] == 0);
		else if (this.x == this.grid.width - 1)
			return (this.grid.g[this.x - 1][this.y + 1] == 0);
		else
			return (this.grid.g[this.x - 1][this.y + 1] == 0 ||
					this.grid.g[this.x + 1][this.y + 1] == 0);
		
		/*return
				// horizontal
				(this.hori && (
						this.grid.g[this.x][this.y - 1] == 0 || // top-left is free
						this.grid.g[this.x + 1][this.y - 1] == 0)) || // top-right is free
				// vertical
				((! this.hori) && (
						this.grid.g[this.x - 1][this.y + 1] == 0 || // bottom-left is free
						this.grid.g[this.x + 1][this.y + 1] == 0)); // bottom-right is free*/
		
	}
	
	public boolean move(int dir) {
		// dir = 0 -> left, dir = 1 -> right
		
		if (! this.canMove(dir)) return false;
		
		if (dir == 0)
			this.x -= 1;
		else
			this.x += 1;
		
		if (this.hori)
			if (dir == 0) {
				this.grid.g[this.x][this.y] = this.c0;
				this.grid.g[this.x + 1][this.y] = this.c1;
				this.grid.g[this.x + 2][this.y] = 0;
			} else {
				this.grid.g[this.x - 1][this.y] = 0;
				this.grid.g[this.x][this.y] = this.c0;
				this.grid.g[this.x + 1][this.y] = this.c1;
			}
		else {
			if (dir == 0) {
				this.grid.g[this.x][this.y] = this.c0;
				this.grid.g[this.x][this.y + 1] = this.c1;
				this.grid.g[this.x + 1][this.y] = 0;
				this.grid.g[this.x + 1][this.y + 1] = 0;
			} else {
				this.grid.g[this.x - 1][this.y] = 0;
				this.grid.g[this.x - 1][this.y + 1] = 0;
				this.grid.g[this.x][this.y] = this.c0;
				this.grid.g[this.x][this.y + 1] = this.c1;
			}
		}
		
		return true;
		
	}
	
	private boolean canMove(int dir) {
		
		// avoid asking for items out of range
		if (dir == 0 && this.x == 0) return false;
		if (dir == 1 && (this.x == this.grid.width - 1 || (this.x == this.grid.width - 2 && this.hori)))
			return false;
		
		if (this.hori) {
			if (dir == 0)
				return (this.grid.g[this.x - 1][this.y] == 0);
			return (this.grid.g[this.x + 2][this.y] == 0);
		}
		
		if (dir == 0)
			return (this.grid.g[this.x - 1][this.y] == 0 && this.grid.g[this.x - 1][this.y + 1] == 0);
		return (this.grid.g[this.x + 1][this.y] == 0 && this.grid.g[this.x + 1][this.y + 1] == 0);
		
	}
	
	public Cell[] endLife() {
		
		Cell[] res = new Cell[2];
		
		res[0] = new Cell(this.grid, this.x, this.y, this.hori, true, null, this.c0);
		
		int xp = this.x;
		int yp = this.y;
		if (this.hori) xp += 1;
		else yp += 1;
		res[1] = new Cell(this.grid, xp, yp, this.hori, false, res[0], this.c1);
		
		res[0].updateCf(res[1]);
		
		return res;
		
	}
	
	private void swapColors(boolean doGrid) {
		
		int ct = this.c0;
		this.c0 = this.c1;
		this.c1 = ct;
		
		if (doGrid) {
			this.grid.g[x][y] = this.c0;
			if (this.hori)
				this.grid.g[x + 1][y] = this.c1;
			else
				this.grid.g[x][y + 1] = this.c1;
		}
		
	}

}
