// import java.util.Scanner;

import game.Game;
import game.Grid;

import visuals.Frame;
import visuals.Canvas;

public class Main {

	public static void main(String[] args) {
		
		// create the game
		Grid grid = new Grid(8, 19);
		Game game = new Game(grid);
		
		// create the interface
		Frame frame = new Frame(game);
		Canvas can = new Canvas(game, frame);
		game.setCanvas(can);
		
		// setup elements on the grid
		game.init(35);
		
		game.loopExecute();

	}
	
	public void consoleGame() {
		
		/*Scanner scan = new Scanner(System.in);
		int act;
		boolean keep = true;
		while (keep) {
			grid.consoleDisplay();
			act = scan.nextInt();
			/*
			 * no matter the action asked, the capsule will fall
			 * 0: nothing
			 * 1: rotate
			 * 2: fall (faster)
			 * 12: rotate and fall
			 * 3: left (not implemented yet)
			 * 4: right (not implemented yet)
			 * 100: exit
			 *\/
			switch (act) {
				case 1:
					c.rotate();
					break;
				case 2:
					c.fall();
					break;
				case 12:
					c.rotate();
					c.fall();
					break;
				case 100:
					keep = false;
					break;
			}
			c.fall();
		}
		
		scan.close();*/
		
	}

}
