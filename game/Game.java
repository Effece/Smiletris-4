/*
 * A game object unify everything in the program. It contains nearly every element and runs the principal
 * functions.
 * 
 * Libraries:
 * * java.util
 */

package game;

import java.util.Random;
import java.util.Timer;

import elements.Capsule;
import elements.Cell;
import elements.Smiley;

import visuals.Canvas;

public class Game {

	public final Grid grid;
	
	public boolean onGoing;
	
	boolean isFalling, finish;
	
	// elements
	
	public Capsule curCap;
	public Smiley[] smileys;
	public Cell[] cells;
	boolean[] canFall;
	public int upComingC0, upComingC1;
	int nextCapX, nextCapY;
	
	// utils
	
	final Timer timer;
	final int delay,     // time between two falls of the capsule
			  gravDelay; // time between two falls of a cell with the gravity function
	final Random rnd;
	
	// rules
	
	final int alignLength; // number of cells to align
	final int colMax;
	// level setups
	final int gdMin, // minimum height for a smiley to appear on
			  smMin, // number of smileys at level 0
			  lvMax, // level where the number is at its maximum
			  smMax; // number of smileys at the last level
	final double coef1, coef2;
	
	// visuals
	
	Canvas can;
	
	public Game(Grid grid) {
		
		this.grid = grid;
		
		this.onGoing = true;
		
		this.isFalling = false;
		this.finish = false;
		
		this.smileys = null;
		this.cells = new Cell[this.grid.width * this.grid.height];
		/*
		 * this.smileys is set every time the board is reset
		 * this.cells is set up to contain, if needed, an element for each case of the grid
		 * most of its elements are null
		 */
		this.canFall = new boolean[this.cells.length];
		
		this.timer = new java.util.Timer();
		this.delay = 800;
		this.gravDelay = 100;
		this.rnd = new Random();
		
		this.alignLength = 4;
		this.colMax = 3;
		
		this.gdMin = 4;
		this.smMin = 5;
		this.lvMax = 35;
		this.smMax = this.grid.width * (this.grid.height - this.gdMin);
		this.coef1 = 3.0;
		this.coef2 = (this.smMax - this.smMin) / Math.log(coef1 * this.lvMax + 1);
		
		this.can = null; // will be set later
		
	}
	
	private boolean update() {
		/*
		 * Function ran every frame.
		 * Sets the next state of the game.
		 */
		
		// cells are falling after a line was deleted
		if (this.isFalling) {
			
			/*
			 * the function is ran only after every cell has fallen the furthest it could
			 * avoids destroying lines that contain a falling cell
			 */
			if (this.countCanFall() == 0) {
				this.deleteLines();
				// if no cell can fall, the next capsule will be generated
				if (this.countCanFall() == 0) {
					this.isFalling = false;
					this.finish = false;
					this.lastChecks();
				}
			} else
				this.gravity();
		
		// seting up the next capsule after the previous one was placed
		} else if (this.curCap == null && ! this.finish) {
			this.curCap = new Capsule(this.grid, this.nextCapX, this.nextCapY,
					this.upComingC0, this.upComingC1); // the colors are decided in advance
			// next color
			this.upComingC0 = this.rnd.nextInt(this.colMax) + 2;
			this.upComingC1 = this.rnd.nextInt(this.colMax) + 2;
		
		// if the capsule is currently falling (most common state)
		} else if (this.curCap.canFall() && ! this.finish)
			this.curCap.fall();
		
		// the capsule just stopped falling
		else {
			
			/*
			 * finding two empty spots in this.cells
			 * this.cells can look like this after some cells get destroyed:
			 *  cell null cell cell null cell null null null ...
			 *        ^^             ^^
			 * so the new cells need to be added here ("^^")
			 */
			int p0 = 0;
			while (this.cells[p0] != null) p0 += 1;
			int p1 = p0 + 1;
			while (this.cells[p1] != null) p1 += 1;
			
			// creating the new cells
			Cell[] res = this.curCap.endLife();
			this.cells[p0] = res[0];
			this.cells[p1] = res[1];
			
			// emptying the current capsule
			this.curCap = null;
			
			this.finish = true;
			this.isFalling = true;
			// the next update will check for lines to delete and cells to make fall
			
		}
		
		// visual updates
		this.grid.consoleDisplay();
		this.can.repaint();
		
		return true;
		
	}
	
	public void gravity() {
		/*
		 * Makes every cell fall once (if it can).
		 */
		
		if (this.countCanFall() > 0) {
			for (int i = 0; i < this.cells.length; i++)
				if (this.cells[i] != null) this.canFall[i] = this.cells[i].canFall();
			for (int i = 0; i < this.cells.length; i++)
				if (this.cells[i] != null) this.cells[i].fall(this.canFall[i]);
		}
				
	}
	
	public int countCanFall() {
		/*
		 * Returns the number of cells that can fall.
		 */
		
		int c = 0;
		for (Cell i: this.cells)
			if (i != null) if (i.canFall()) c += 1;
		return c;
		
	}
	
	private void lastChecks() {
		/*
		 * Functions ran after a capsule has reached the bottom and every row has been deleted.
		 */
		
		// is there any smiley left?
		boolean smileysLeft = false;
		for (int i = 0; i < this.smileys.length; i++)
			if (this.smileys[i] != null) {
				smileysLeft = true;
				break;
			}
		if (! smileysLeft) {
			this.onGoing = false;
			System.out.println("You won!");
		}
		
		// is the game stuck?
		if (this.grid.isStuck(this.nextCapX, this.nextCapY)) {
			this.onGoing = false;
			System.out.println("You're stuck.");
		}
		
	}
	
	private void deleteLines() {
		/*
		 * Gets the cells to destroy and destroys them.
		 */
		
		int[][] dead = this.grid.detect(this.alignLength);
		
		/*
		 * The cells to kill are given by locations so we need to get the object based on its coordinates
		 */
		
		int corSmil, corCell;
		for (int[] i: dead) {
			corSmil = this.getCorrespondingSmiley(i[0], i[1]);
			// returns - 1 if it isn't a smiley
			if (corSmil == - 1) {
				corCell = this.getCorrespondingCell(i[0], i[1]);
				if (corCell != - 1) {
					this.cells[corCell].kill();
					this.cells[corCell] = null;
				}
				// if corCell == - 1 it's either an error or the cell was already deleted
			} else {
				this.smileys[corSmil].kill();
				this.smileys[corSmil] = null;
			}
		}
		
	}
	
	public void loopExecute() {
		/*
		 * Function that is ran every x milliseconds.
		 * Demands an update then schedules the next call of itself.
		 */
		
		this.update();
		
		/*
		random movements, can be reused for the alcoolic event
		if (this.curCap != null) {
			int move = this.rnd.nextInt(4);
			switch (move) {
			case 0:
				this.curCap.move(0);
				break;
			case 1:
				this.curCap.move(1);
				break;
			case 2:
				this.curCap.rotate();
				break;
			}
		}
		*/
		
		int d = this.delay;
		if (this.onGoing) {
			// change the delay if some cells are currently falling
			if (this.isFalling) d = this.gravDelay;
			this.timer.schedule(new TimerTaskExtended(this), d);
		} else
			// the game has ended
			this.timer.cancel();
		
	}
	
	private int getCorrespondingCell(int x, int y) {
		/*
		 * Returns a cell based on its coordinates.
		 * In:
		 *  x, y: coordinates
		 */
		
		for (int i = 0; i < this.cells.length; i++)
			if (this.cells[i] == null) continue;
			else if (this.cells[i].x == x && this.cells[i].y == y) return i;
		return - 1;
		// if there is no such cell, returns - 1
		
	}
	
	private int getCorrespondingSmiley(int x, int y) {
		/*
		 * Returns a smiley based on its coordinates.
		 * In:
		 *  x, y: coordinates
		 */
		
		for (int i = 0; i < this.smileys.length; i++)
			if (this.smileys[i] == null) continue;
			else if (this.smileys[i].x == x && this.smileys[i].y == y) return i;
		return - 1;
		// if there is no such smiley, returns - 1
		
	}
	
	public void custom(int[][] elts) {
		/*
		 * Creates the grid from the given elements. elts can have any length but the arrays inside must be
		 * three numbers in length.
		 * In:
		 *  elts: array containing arrays that define smileys to add on the grid
		 *  elts[x]: contains the two coordinates and the color
		 *   {x, y, color}
		 */
		
		this.reset();
		
		this.smileys = new Smiley[elts.length];
		for (int i = 0; i < elts.length; i++)
			this.smileys[i] = new Smiley(this.grid, elts[i][0], elts[i][1], elts[i][2]);
			// the constructor of the smileys automatically changes the grid
		
	}
	
	public void init(int lvl) {
		/*
		 * Creates the grid from a given level. The higher the level is, the more smileys on board there are.
		 * The number of smileys is obtained using the numberSmileys function.
		 * In:
		 *  lvl: level
		 */
		
		this.reset();
		
		int nb = this.numberSmileys(lvl);
		this.smileys = new Smiley[nb];
		int[] pos;
		for (int i = 0; i < nb; i++) {
			// find a set of coordinates that isn't occupied yet
			pos = this.randomPos();
			while (this.grid.g[pos[0]][pos[1]] != 0)
				pos = this.randomPos();
			// create the smiley
			this.smileys[i] = new Smiley(this.grid, pos[0], pos[1], this.rnd.nextInt(3) + 2);
		}
		
	}
	
	private int numberSmileys(int n) {
		/*
		 * Returns the number of smileys to generate at level n.
		 * The number of smileys is calculated with a formula that takes in account the level, the maximum
		 * level, the number of cells on level 0 and the number of cells on the last level; all of these
		 * except the first one are constants defined in the game's constructor.
		 * In:
		 *  n: level
		 */
		
		// above level 35, every level is the same (one smiley per case)
		if (n >= this.lvMax) return this.smMax;
		
		return (int) (this.coef2 * Math.log(this.coef1 * n + 1) + this.smMin);
		
	}
	
	private int[] randomPos() {
		/*
		 * Returns a set of two random coordinates. The x coordinate is between 0 and the width of the grid,
		 * the y coordinate is between the minimum level for a smiley to generate on and the height of the
		 * grid.
		 */
		
		return new int[] {
				this.rnd.nextInt(this.grid.width),
				this.rnd.nextInt(this.grid.height - this.gdMin) + this.gdMin
		};
		
	}
	
	private void reset() {
		/*
		 * Resets the game and the grid, and the canvas if there is one.
		 */
		
		this.smileys = null;
		this.cells = new Cell[this.grid.width * this.grid.height];
		this.grid.reset();
		
		if (this.can != null)
			this.can.reset();
		
		this.upComingC0 = this.rnd.nextInt(3) + 2;
		this.upComingC1 = this.rnd.nextInt(3) + 2;
		this.nextCapX = this.grid.width / 2 - 1; // middle
		this.nextCapY = 0;                       // top
		
	}
	
	public boolean action(int event) {
		/*
		 * Execute an action based on event's value.
		 * Actions:
		 *  38 -- Up
		 *  40 -- Down
		 *  37 -- Left
		 *  39 -- Right
		 * In:
		 *  event: action
		 * Out:
		 *  false if the capsule isn't generated yet
		 */
		
		if (this.curCap == null) return false;
		
		switch (event) {
		case 38:
			this.curCap.rotate();
			break;
		case 40:
			this.curCap.fall();
			break;
		case 37:
			this.curCap.move(0);
			break;
		case 39:
			this.curCap.move(1);
			break;
		}
		
		this.grid.consoleDisplay();
		this.can.repaint();
		
		return true;
		
	}
	
	public void setCanvas(Canvas can) {
		/*
		 * Sets the canvas attribute.
		 */
		
		this.can = can;
		
	}

}
