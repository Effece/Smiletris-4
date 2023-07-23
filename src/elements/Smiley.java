/*
 * Smiley to destroy to win the game. They are generated when the level is created.
 * They are made from the cell's to the only difference that they can't fall.
 */

package elements;

import game.Grid;

public class Smiley extends Cell {

	public Smiley(Grid grid, int x, int y, int color) {
		
		// set to a cell that isn't fused
		super(grid, x, y, false, false, null, color);
		
	}
	
	public boolean canFall() {
		/*
		 * A smiley never falls.
		 */
		
		return false;
		
	}

}
