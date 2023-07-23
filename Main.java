/*
 * SMILEYTRIS4
 * 
 * Smiletris4 is an alternative of Tetris.
 * Tetris is a game where you place, one by one, objects of various forms that fall, by moving them left and
 * right and by rotating them. When a line is full, it disappears.
 * Smiletris changes your objective: instead of having to last the longuest time possible, you now have
 * smileys to destroy by aligning them with three other cells of the same color. The objects that fall are
 * now always 2x1 capsules, composed of two colored cells. Two cells from the same capsule stay joined until
 * one of them is deleted.
 * Smiletris3 adds random twists every once in a while that modify the board and can help or annoy you.
 * 
 * By changing just a few lines, this program can be converted from a PC game to an Android game or even to
 * a Discord Bot.
 * 
 * Packages:
 * * elements: every individual element in the game
 * * * capsule: two falling cells
 * * * cell: part of a fallen capsule
 * * * smiley: element set at the level's creation, can't fall
 * * game: classes that unify the program
 * * * game: contains most of the variables and objects of the game and uses the main functions
 * * * grid: representation of the current board and a few function to read and use it
 * * * timertaskextended: element used to move to the next state every x milliseconds
 * * visuals: interface to play on computer
 * * * canvas: where the elements are drawn
 * * * frame: the window that contains the canvas
 * Libraries:
 * * java.util
 * * java.awt    \ interface
 * * javax.swing /
 */

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
		game.init(0);
		
		game.loopExecute();

	}
	
	/*
	public void consoleGame() {
		/*
		 * Function that lets you play inside the console using a scanner.
		 * Outdated.
		 *\/
		
		Scanner scan = new Scanner(System.in);
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
		
		scan.close();
		
	}
	*/

}
