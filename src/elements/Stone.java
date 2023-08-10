/*
 * Elements that appear on the grid and block the cells. Can't be destroyed.
 * They disappear after each level.
 */

package elements;

import game.Grid;

public class Stone extends Cell {

	public Stone(Grid grid, int x, int y) {
		
		// set to a cell that isn't fused with color 1
		super(grid, x, y, false, false, null, 1);
		
	}
	
	public boolean canFall() {
		/*
		 * A stone never falls.
		 */
		
		return false;
		
	}

}
