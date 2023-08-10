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
 * * * stone: element set by the event handler, can't fall, can't be destroyed
 * * game: classes that unify the program
 * * * game: contains most of the variables and objects of the game and uses the main functions
 * * * grid: representation of the current board and a few function to read and use it
 * * * gametask: element used to move to the next state every x milliseconds
 * * * generationtask: element used to show the smileys appearing one by one at the level's creation
 * * visuals: interface to play on computer
 * * * canvas: where the elements are drawn
 * * * frame: the window that contains the canvas
 * Libraries:
 * * java.util
 * * java.awt    \
 * * javax.swing  ) interface
 * * java.io     /
 */

import game.EventHandler;
import game.Game;
import game.Grid;

import visuals.Frame;
import visuals.Canvas;

public class Main {

	public static void main(String[] args) {
		
		// create the game
		Grid grid = new Grid(8, 19);
		Game game = new Game(grid);
		EventHandler evha = new EventHandler(game);
		game.setEventHandler(evha);
		
		// create the interface
		Frame frame = new Frame(game);
		Canvas can = new Canvas(game, frame);
		game.setCanvas(can);
		
		game.start();

	}

}
