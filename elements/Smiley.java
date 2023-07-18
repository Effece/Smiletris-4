package elements;

import game.Grid;

public class Smiley extends Cell {

	public Smiley(Grid grid, int x, int y, int color) {
		
		super(grid, x, y, false, false, null, color);
		
	}
	
	public boolean canFall() {
		
		return false;
		
	}

}
