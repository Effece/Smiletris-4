/*
 * Cell placed on the board by the player, made from a capsule.
 * A new cell is joined to the other one that composed the capsule. They are mutually "cell-friends".
 */

package elements;

import game.Grid;

public class Cell {
	
	final Grid grid;
	public int x, y;
	
	public boolean hori; // is horizontal?
	public boolean tl;   // top-left; if the object is fused, is it the "most top-left" cell?
	
	public int color; // look up the grid file for each number's color
	
	public boolean fused; // is fused with another cell?
	Cell cf; // cell-friend

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
		/*
		 * Changes the cell-friend.
		 * Can be used with cf = null to remove the cell-friend.
		 * In:
		 *  cf: new cell-friend
		 */
		
		this.fused = cf != null;
		this.cf = cf;
		
		if (! this.fused) {
			this.hori = false;
			this.tl = false;
		}
		
	}
	
	public boolean fall(boolean force) {
		/*
		 * Make the cell fall. Returns false if it can't fall.
		 */
		
		if ((! force) && (! this.canFall()))
			return false;
		
		/*
		 * If the cell is fused with another one and is vertical, it is possible that the cell on top will
		 * fall before the one on the bottom and in that case, replace the case on the grid first; to avoid
		 * changing it again, we run a test to see if the case might have already been changed.
		 * Steps:                     | Without the test:
		 *  0 0 0     0 0 0     0 0 0 | 0 0 0     0 0 0     0 0 0
		 *  0 1 0     0 0 0     0 0 0 | 0 1 0     0 0 0     0 0 0
		 *  0 2 0 --> 0 1 0 --> 0 1 0 | 0 2 0 --> 0 1 0 --> 0 0 0
		 *  0 0 0     0 0 0     0 2 0 | 0 0 0     0 0 0     0 2 0
		 *  0 0 0     0 0 0     0 0 0 | 0 0 0     0 0 0     0 0 0
		 */
		if ((this.fused && (! this.hori) && this.tl)|| (! this.fused) || (this.fused && this.hori)) 
			this.grid.g[this.x][this.y] = 0;
		this.y += 1;
		this.grid.g[this.x][this.y] = this.color;
		
		return true;
		
	}
	
	public boolean canFall() {
		/*
		 * Returns if the cell can fall, taking in account the cell-friend if the cell is fused.
		 * If the cell is fused with another one:
		 * - if the cells are horizontal, both need to be able to fall individually;
		 * - if the cells are vertical, only the bottom one needs to be able to fall individually (the top
		 *   one can't)
		 * If the cell isn't fused, it only needs to be able to fall individually.
		 * Checking if a cell can fall "individually" is done with the canFallIndiv function.
		 */
		
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
		/*
		 * Returns if the cell can fall individually, without considering a potential cell-friend.
		 * Done by checking if the case under is set to 0.
		 */
		
		// avoid being out of range
		if (this.y == this.grid.height - 1) return false;
		
		return this.grid.g[this.x][this.y + 1] == 0;
		
	}
	
	public void kill() {
		/*
		 * Kill a cell.
		 * Also updates the cell-friend is it exists.
		 */
		
		this.grid.g[this.x][this.y] = 0;
		if (this.fused) {
			this.fused = false;
			this.cf.updateCf(null);
		}
		
	}

}
