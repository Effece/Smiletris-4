/*
 * Capsule that is falling.
 * There is usually only one capsule on screen, as it converts to two joined cells when it finishes its fall.
 * Composed of two colors.
 */

package elements;

import game.Grid;

public class Capsule {
	
	final Grid grid;
	public int x, y; // coordinates of the most top-left cell
	
	public boolean hori; // is horizontal?
	public int c0, c1;
	/*
	 * c0: color of the cell the most at the top-left
	 * c1: other cell's color
	 */
	
	/*
	 * Illustration of the "top-left" concept:
	 *  2-3 : the 2 is the most on the left so its tl is set to true
	 *  2
	 *  | : the 2 is the most on the top so its tl is set to true
	 *  3
	 */

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
		/*
		 * Make the capsule fall in the grid. Returns false if the capsule can't fall.
		 * If the capsule is horizontal, the old cases must be set to 0 and the new ones to the colors.
		 * If the capsule is vertical, the top case must be set to 0, the new top one and so former bottom
		 * one to the top color and the new bottom one to the bottom color.
		 */
		
		if (! this.canFall()) return false;
		
		if (this.hori) {
			// free empty cases
			this.grid.g[this.x][this.y] = 0;     // former c0
			this.grid.g[this.x + 1][this.y] = 0; // former c1
			// update coordinates
			this.y += 1;
			// fill in new cases
			this.grid.g[this.x][this.y] = this.c0;
			this.grid.g[this.x + 1][this.y] = this.c1;
			return true;
		}
		
		// simultaneous
		this.grid.g[this.x][this.y] = 0;           // free the top
		this.grid.g[this.x][this.y + 1] = this.c0; // former c1 replaced with c0
		this.grid.g[this.x][this.y + 2] = this.c1; // fill in with c1
		// update coordinates
		this.y += 1;
		
		return true;
		
	}
	
	public boolean canFall() {
		/*
		 * Returns if the capsule can fall.
		 * If it is horizontal, the two cases under both of the capsule's cells need to be 0s.
		 *  0 2-3 0              | 0 2-3 0                | 0 2-3 0
		 *  0 0 0 0 <-- can fall | 0 1 1 0 <-- can't fall | 0 1 0 0 <-- can't fall
		 */
		
		/*
		 * If the capsule is already at the bottom, we avoid asking for the element under because it would be
		 * asking for an item out of range.
		 */
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
	
	public boolean rotate() {
		/*
		 * Makes the capsule rotate. Returns false if it can't.
		 */
		
		if (! this.canRotate()) return false;
		
		this.hori = ! this.hori;
		
		// change from horizontal to vertical
		if (! this.hori) {
			
			// left bar
			if (this.grid.g[this.x][this.y - 1] == 0) {
				this.grid.g[this.x + 1][this.y] = 0;   // free new empty case
				this.y -= 1;                           // update coordinates
				this.grid.g[this.x][this.y] = this.c1; // update grid
				this.swapColors(false);
				return true;
			}
			
			// right bar
			this.grid.g[this.x][this.y] = 0; // free new empty case
			this.x += 1; this.y -= 1;        // upd coordinates
			// update grid
			this.grid.g[this.x][this.y + 1] = this.c0;
			this.grid.g[this.x][this.y] = this.c1;
			this.swapColors(false);
			return true;
			
		}
		
		// change from vertical to horizontal
		
		// right side
		if (this.x != this.grid.width - 1) if (this.grid.g[this.x + 1][this.y + 1] == 0) {
			this.grid.g[this.x][this.y] = 0; // free new empty case
			this.y += 1;                     // update coordinates
			// update grid
			this.grid.g[this.x][this.y] = this.c0;
			this.grid.g[this.x + 1][this.y] = this.c1;
			return true;
		}
		
		// left side
		this.grid.g[this.x][this.y] = 0;       // free new empty case
		this.x -= 1; this.y += 1;              // update coordinates
		this.grid.g[this.x][this.y] = this.c0; // update grid
		return true;
		
	}
	
	private boolean canRotate() {
		/*
		 * Returns if the capsule can fall.
		 * This depends on whether the capsule is horizontal or vertical. For both cases, it could rotate in
		 * two ways.
		 * From vertical to horizontal:
		 *  0 0 1 0    0 0 0 0 || 0 0 0 0 | the second one is used by default
		 *      |   ->         ||         | if an obstacle blocks it, the first one is used
		 *  0 0 2 0    0 1-2 0 || 0 0 1-2 | if it is also blocked, the capsule can't rotate
		 * => a vertical capsule will always rotate on its bottom line
		 * From horizontal to vertical:
		 *  0 0 0 0    0 2 0 0 || 0 0 2 0 | the first one is used by default
		 *          ->   |     ||     |   | same thing for the obstacles, if the first one is blocked, the
		 *  0 1-2 0    0 1 0 0 || 0 0 1 0 | second one is tried
		 * The capsules always rotate counter-clockwisely.
		 */
		
		if (this.y == 0 && this.hori) return false;
		
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
		
		/*
		 * one-line attempt but it doesn't take in account the edges
		return
				// horizontal
				(this.hori && (
						this.grid.g[this.x][this.y - 1] == 0 || // top-left is free
						this.grid.g[this.x + 1][this.y - 1] == 0)) || // top-right is free
				// vertical
				((! this.hori) && (
						this.grid.g[this.x - 1][this.y + 1] == 0 || // bottom-left is free
						this.grid.g[this.x + 1][this.y + 1] == 0)); // bottom-right is free
		*/
		
	}
	
	public boolean move(int dir) {
		/*
		 * Moves the capsule left or right. Returns false if the capsule can't move in the specified
		 * direction.
		 * 0 moves to the left, 1 to the right.
		 * In:
		 *  dir: direction
		 */
		
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
		/*
		 * Returns if the capsule can move in the specified direction.
		 * 0 is for left, 1 for right.
		 * If the capsule is horizontal, only one case needs to be free, the first one next to the capsule
		 * in the specified direction.
		 * If it is vertical, both cases in the specified direction need to be free.
		 * In:
		 *  dir: direction
		 */
		
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
		/*
		 * Kills the capsule and returns two cells to compose it instead.
		 * The first one is the top-left one.
		 */
		
		Cell[] res = new Cell[2];
		
		// top-left cell
		res[0] = new Cell(this.grid, this.x, this.y, this.hori, true, null, this.c0);
		
		// get the coordinates of the second cell
		int xp = this.x;
		int yp = this.y;
		if (this.hori) xp += 1;
		else yp += 1;
		// create it
		res[1] = new Cell(this.grid, xp, yp, this.hori, false, res[0], this.c1);
		
		// update the cell-friend of the first cell
		res[0].updateCf(res[1]);
		
		return res;
		
	}
	
	private void swapColors(boolean doGrid) {
		/*
		 * Swaps the colors of the capsule, both in the attributes and on-grid if doGrid is true.
		 * In:
		 *  doGrid: swap on the grid
		 */
		
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
