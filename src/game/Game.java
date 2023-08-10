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
	
	public boolean onGoing, starting, pausing, generating, lost, downOn;
	
	boolean isFalling, finish;
	public boolean deletingCells;
	public int[][] lastDeaths;
	
	public int score, smileysLeft, level;
	
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
			  delDelay,  // time spent on a frame where cells are getting deleted
			  gravDelay, // time between two falls of a cell with the gravity function
			  genDelay,  // time between two smiley's appearances at the game's generation
			  downDelay; // time between two falls of the capsule when the down arrow is used
	float speeder; // number to delete by (in case the speeder event is on)
	GameTask latestGT;
	public float timeSpent;
	final Random rnd;
	
	// rules
	
	int alignLength; // number of cells to align
	public final int lvlButMax,  // number of buttons on the menu to select the level
			         lvlButStep; // steps between each button on the menu
	int colMax;
	// level setups
	public final int gdMin, // minimum height for a smiley to appear on
					 smMin, // number of smileys at level 0
					 lvMax, // level where the number is at its maximum
					 smMax; // number of smileys at the last level
	final double coef1, coef2;
	
	// events
	public EventHandler eh;
	
	// visuals
	
	final String mode;
	Canvas can;
	
	// text
	
	public final String[] startMenuText;
	
	public Game(Grid grid) {
		
		this.grid = grid;
		
		this.onGoing = false;
		this.starting = true;
		this.pausing = false;
		this.generating = false;
		this.lost = false;
		this.downOn = false;
		
		this.isFalling = false;
		this.finish = false;
		this.deletingCells = false;
		
		this.smileysLeft = 0;
		this.level = 25;
		
		this.smileys = null;
		this.cells = new Cell[this.grid.width * this.grid.height];
		/*
		 * this.smileys is set every time the board is reset
		 * this.cells is set up to contain, if needed, an element for each case of the grid
		 * most of its elements are null
		 */
		this.canFall = new boolean[this.cells.length];
		
		this.timer = new java.util.Timer();
		this.delay = 650;
		this.delDelay = this.delay;
		this.gravDelay = 50;
		this.genDelay = 20;
		this.downDelay = 50;
		this.speeder = 1;
		this.timeSpent = 0;
		this.rnd = new Random();
		
		this.alignLength = 4;
		this.lvlButMax   = 8;
		this.lvlButStep  = 5;
		this.colMax      = 3;
		
		this.gdMin = 4;
		this.smMin = 5;
		this.lvMax = 35;
		this.smMax = this.grid.width * (this.grid.height - this.gdMin);
		this.coef1 = 3.0;
		this.coef2 = (this.smMax - this.smMin) / Math.log(coef1 * this.lvMax + 1);
		
		this.score = this.countScore(this.level);
		
		this.eh = null; // will be set later
		
		this.mode = "canvas";
		
		this.can = null; // will be set later
		
		this.startMenuText = new String[] {
				// title
				"Smiletris 4",
				// text 1
				"version 1.4 . freeware",
				"c François Choquet 2023",
				"prog: https://francoischoquet.com",
				"design: https://francoischoquet.com",
				"smileys: https://francoischoquet.com",
				// small title 1
				"Rules",
				// text 2
				"Remove all the",
				"smileys by forming",
				"identical lines of",
				"4 colors!",
				// small title 2
				"Level",
				// small title 3
				"Events"
		};
		
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
			
			// adding one frame for the visuals
			if (this.deletingCells) {
				this.deletingCells = false;
				this.lastDeaths = null;
			
			} else if (this.countCanFall() == 0) {
				
				// cancelling the down arrow
				this.downOn = false;
				
				this.deleteLines();
				
				// if no cell can fall, the next capsule will be generated
				if (this.countCanFall() == 0) {
					this.isFalling = false;
					this.finish = false;
					this.lastChecks();
				}
			
			} else {
				this.gravity();
				this.deletingCells = false;
				this.lastDeaths = null;
			}
		
		// seting up the next capsule after the previous one was placed
		} else if (this.curCap == null && ! this.finish) {
			
			// if the start randomizer event is on...
			if (this.eh != null) if (this.eh.isActive) if (this.eh.starterOn) {
				int pos = this.rnd.nextInt(this.grid.width - 1);
				while (this.grid.isStuck(pos, this.nextCapY)) pos = this.rnd.nextInt(this.grid.width - 1);
				this.nextCapX = pos;
			} else {
				this.nextCapX = this.grid.width / 2 - 1;
				this.nextCapY = 0;
			}
			
			this.curCap = new Capsule(this.grid, this.nextCapX, this.nextCapY,
					this.upComingC0, this.upComingC1); // the colors are decided in advance
			// next color
			this.upComingC0 = this.rnd.nextInt(this.colMax) + 2;
			this.upComingC1 = this.rnd.nextInt(this.colMax) + 2;
			
			this.deletingCells = false;
			this.lastDeaths = null;
		
		// if the capsule is currently falling (most common state)
		} else if (this.curCap.canFall() && ! this.finish) {
			this.curCap.fall();
			// events
			if (this.eh != null) this.eh.decrease(false);
		}
		
		// the capsule just stopped falling
		else {
			
			Cell[] src = this.cells;
			
			// sun event
			boolean sunCond = false;
			if (this.eh != null) sunCond = this.eh.isActive && this.eh.sunOn;
			// the program will change a little bit if the sun event is on...
			if (sunCond) src = this.smileys;
			
			/*
			 * finding two empty spots in this.cells
			 * this.cells can look like this after some cells get destroyed:
			 *  cell null cell cell null cell null null null ...
			 *        ^^             ^^
			 * so the new cells need to be added here ("^^")
			 */
			int p0 = 0;
			while (src[p0] != null) p0 += 1;
			int p1 = p0 + 1;
			while (src[p1] != null) p1 += 1;
			
			// creating the new cells
			if (sunCond) {
				Smiley[] resS = this.curCap.endLifeSun();
				src[p0] = resS[0];
				src[p1] = resS[1];
			} else {
				// most common state
				Cell[] res = this.curCap.endLife();
				src[p0] = res[0];
				src[p1] = res[1];
			}
			
			// was it a bomb?
			if (this.curCap.c0 == 8) {
				
				int bx, by; // borns
				bx = this.curCap.hori? 3 : 2;
				by = this.curCap.hori? 2 : 3;
				
				// delete the elements
				int[][] dead = new int[(bx + 1) * (by + 1)][2];
				int c = - 1;
				for (int i = - 1; i < bx; i++) for (int j = - 1; j < by; j++) {
					c += 1;
					dead[c][0] = this.curCap.x + i;
					dead[c][1] = this.curCap.y + j;
				}
				this.deleteElements(dead);
				
				this.deletingCells = true;

			}
			
			if (this.eh != null) if (this.eh.isActive) this.eh.decrease(true);
			// decreasing after the capsule has ended life
			
			// emptying the current capsule
			this.curCap = null;
			
			this.finish = true;
			this.isFalling = true;
			// the next update will check for lines to delete and cells to make fall
			
		}
		
		// visual updates
		this.userDisplay();
		
		return true;
		
	}
	
	public void gravity() {
		/*
		 * Makes every cell fall once (if it can).
		 */
		
		if (this.countCanFall() > 0) {
			for (int i = 0; i < this.cells.length; i++)
				if (this.cells[i] != null) this.canFall[i] = this.cells[i].canFall();
				else this.canFall[i] = false;
			for (int i = 0; i < this.cells.length; i++)
				if (this.cells[i] != null) if (this.canFall[i]) this.cells[i].fall(true);
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
			this.init(this.level + 1, this.score);
		}
		
		// is the game stuck?
		if (this.grid.isStuck(this.nextCapX, this.nextCapY)) {
			this.onGoing = false;
			this.lost = true;
		}
		
	}
	
	private void deleteLines() {
		/*
		 * Gets the cells to destroy and destroys them.
		 */
		
		int[][] dead = this.grid.detect(this.alignLength);
		
		this.deletingCells = dead.length != 0;
		
		// the cells to kill are given by locations so we need to get the object based on its coordinates
		if (dead.length != 0) this.deleteElements(dead);
		
	}
	
	private int[][] fuse(int[][] a1, int[][] a2) {
		/*
		 * Fuses a1 and a2. If a1 and a2 don't have the same sub-array length, the function returns null.
		 * In:
		 *  a1, a2: arrays to fuse
		 */
		
		if (a1 == null || a2 == null) return null;
		if (a1.length == 0 || a2.length == 0) return null;
		if (a1[0].length != a2[0].length) return null;
		
		int[][] f = new int[a1.length + a2.length][a1.length];
		int c = - 1;
		for (int i = 0; i < a1.length; i++) {
			c += 1;
			f[c] = a1[i];
		}
		for (int i = 0; i < a2.length; i++) {
			c += 1;
			f[c] = a1[i];
		}
		
		return f;
		
	}
	
	public void loopExecute() {
		/*
		 * Function that is ran every x milliseconds.
		 * Demands an update then schedules the next call of itself.
		 */
		
		this.update();
		
		// alcoholic
		if (this.eh != null) if (this.eh.alcOn) if (this.curCap != null) {
			int rndAlc = 5;
			if (this.downOn) rndAlc = 15;
			int move = this.rnd.nextInt(rndAlc);
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
		
		int d = this.delay;
		
		if (this.onGoing) {
			
			// change the delay
			if (this.deletingCells) d = this.delDelay;
			else if (this.isFalling) d = this.gravDelay;
			else if (this.downOn) d = this.downDelay;
			d = Math.round(d / this.speeder);
			this.timeSpent += d;
			
			// next frame
			this.latestGT = new GameTask(this);
			this.timer.schedule(this.latestGT, d);
			
		}
		
	}
	
	public void loopExecute(boolean startDelay) {
		
		if (startDelay) {
			this.timeSpent += this.delay;
			this.latestGT = new GameTask(this);
			this.timer.schedule(this.latestGT, this.delay);
		} else
			this.loopExecute();
		
	}
	
	public int getCorrespondingCell(int x, int y) {
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
	
	public int getCorrespondingSmiley(int x, int y) {
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
		this.smileysLeft = elts.length;
		for (int i = 0; i < elts.length; i++)
			this.smileys[i] = new Smiley(this.grid, elts[i][0], elts[i][1], elts[i][2]);
			// the constructor of the smileys automatically changes the grid
		
	}
	
	private void init(int lvl) {
		/*
		 * Creates the grid from a given level. The higher the level is, the more smileys on board there are.
		 * The number of smileys is obtained using the numberSmileys function.
		 * In:
		 *  lvl: level
		 */
		
		int nb = this.numberSmileys(lvl);
		
		this.reset();
		this.eh.reset();
		this.can.reset();
		this.can.setButtonStates("game");
		
		this.level = lvl;
		
		this.onGoing = false;
		this.starting = false;
		this.pausing = false;
		this.generating = true;
		this.lost = false;
		this.downOn = false;
		
		this.isFalling = false;
		this.finish = false;
		this.deletingCells = false;
		
		this.smileys = new Smiley[nb];
		this.smileysLeft = nb;
		
		this.loopNewSmiley(0, nb);
		
	}
	
	private void init(int lvl, int scoreInit) {
		/*
		 * Initialize the grid with a specific score.
		 * Used to get to the next level.
		 * In:
		 *  lvl: level to start from
		 *  scoreInit: initial score when starting the level
		 */
		
		this.score = scoreInit;
		this.init(lvl);
		
	}
	
	public void loopNewSmiley(int n, int nMax) {
		/*
		 * Function used in a loop to create a new smiley every few hundreds of a second for aestetics.
		 * In:
		 *  n: the number of iterations
		 *  nMax: the loop's end
		 */
		
		// find a set of coordinates that isn't occupied yet
		int[] pos = this.randomPos();
		while (this.grid.g[pos[0]][pos[1]] != 0)
			pos = this.randomPos();
		// create the smiley
		this.smileys[n] = new Smiley(this.grid, pos[0], pos[1], this.rnd.nextInt(this.colMax) + 2);
		
		// refresh the interface
		this.userDisplay();
		
		if (n < nMax - 1)
			this.timer.schedule(new GenerationTask(this, n + 1, nMax), this.genDelay);
		else {
			this.onGoing = true;
			this.starting = false;
			this.pausing = true;
			this.generating = true;
			this.lost = false;
			this.downOn = false;
			this.userDisplay();
			// this.loopExecute(true);
			// this.timer.schedule(new GameTask(this), this.delay);
		}
		
	}
	
	public int numberSmileys(int n) {
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
	
	private int countScore(int lvl) {
		/*
		 * Returns the theorical score at a certain level. It is obtained by summing up all of the previous individual levels' scores.
		 * In:
		 *  lvl: current level (isn't counted)
		 */
		
		int s = 0;
		for (int i = 0; i < lvl; i++)
			s += this.numberSmileys(i);
		
		return s;
		
	}
	
	public void deleteElements(int[][] dead) {
		/*
		 * Delete all of the cell and smiley elements given by coordinates in dead.
		 * In:
		 *  dead: array of two-element arrays with x and y coordinates
		 */
		
		int c = - 1;
		
		// unoptimal
		int[][] temp = new int[dead.length][4];
		if (this.lastDeaths == null)
			this.lastDeaths = temp;
		else {
			c = this.lastDeaths.length;
			this.lastDeaths = this.fuse(this.lastDeaths, temp);
		}

		int cor;
		for (int[] i: dead) {
			c += 1;
			cor = this.getCorrespondingSmiley(i[0], i[1]);
			// returns - 1 if it isn't a smiley
			if (cor == - 1) {
				cor = this.getCorrespondingCell(i[0], i[1]);
				if (cor == - 1) c -= 1; // either an error or the cell was already deleted
				else {
					this.lastDeaths[c] = new int[] {i[0], i[1], this.cells[cor].color, 0};
					this.cells[cor].kill();
					this.cells[cor] = null;
				}
			} else {
				this.score += 1; this.smileysLeft -= 1;
				this.lastDeaths[c] = new int[] {i[0], i[1], this.smileys[cor].color, 1};
				this.smileys[cor].kill();
				this.smileys[cor] = null;
			}
		}
		
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
		 * Resets the game and the grid. The canvas needs to be reset individually.
		 */
		
		this.smileys = null;
		this.cells = new Cell[this.grid.width * this.grid.height];
		this.grid.reset();
		
		this.speeder = 1;
		
		this.canFall = new boolean[this.cells.length];
		
		this.curCap = null;
		this.upComingC0 = this.rnd.nextInt(this.colMax) + 2;
		this.upComingC1 = this.rnd.nextInt(this.colMax) + 2;
		this.nextCapX = this.grid.width / 2 - 1; // middle
		this.nextCapY = 0;                       // top
		
	}
	
	public boolean action(int event) {
		/*
		 * Execute an action based on event's value.
		 * Actions:
		 *  38 -- Up    + 90 (Z) + 87 (W)
		 *  40 -- Down  + 83 (S)
		 *  37 -- Left  + 65 (A) + 81 (Q)
		 *  39 -- Right + 68 (D)
		 *  27 -- Escap
		 * In:
		 *  event: action
		 * Out:
		 *  false if the capsule isn't generated yet
		 */
		
		if (this.generating && this.pausing) {
			this.generating = false;
			this.pausing = false;
			this.loopExecute();
			return false;
		}
		
		if (event == 27)
			if (! (this.starting || this.lost || this.generating)) {
				this.latestGT.cancel();
				this.onGoing = ! this.onGoing;
				this.pausing = ! this.pausing;
				this.userDisplay();
				if (this.onGoing) {
					this.can.setButtonStates("game");
					this.loopExecute(true);
				}
				else this.can.setButtonStates("pause");
				return false;
			}
		
		if (! this.onGoing) return false;
		
		if (this.curCap == null) return false;
		
		if (this.downOn) this.downOn = false;
		
		int eP = event;
		boolean tempCond = false;
		if (this.eh != null) if (this.eh.mirrorOn) tempCond = true;
		
		switch (event) {
		case 38: case 90: case 87:
			if (tempCond) eP = 1;
			else          eP = 0;
			break;
		case 40: case 83:
			if (tempCond) eP = 0;
			else          eP = 1;
			break;
		case 37: case 65: case 81:
			if (tempCond) eP = 3;
			else          eP = 2;
			break;
		case 39: case 68:
			if (tempCond) eP = 2;
			else          eP = 3;
			break;
		}
		
		switch (eP) {
		case 0:
			this.curCap.rotate();
			break;
		case 1:
			this.downOn = true;
			this.latestGT.cancel();
			this.loopExecute();
			break;
		case 2:
			this.curCap.move(0);
			break;
		case 3:
			this.curCap.move(1);
			break;
		}
		
		this.userDisplay();
		
		return true;
		
	}
	
	public void start() {
		
		this.userDisplay();
		
	}
	
	public void stop() {
		/*
		 * Stops the game.
		 */
		
		this.stopTimer();
		this.onGoing = false;
		switch (this.mode) {
		case "canvas":
			this.can.stop();
			break;
		}
		System.exit(0);
		
	}
	
	public void buttonPressed(int id, int i) {
		/*
		 * Performs actions when a button is pressed.
		 * IDs:
		 *  0: start button
		 *  1: level selector
		 *  2: title screen
		 */
		
		switch (id) {
		
		// start
		
		case 0:
			
			this.starting = false;
			this.onGoing = true;
			this.init(this.level);
			break;
			
		// changing level
		
		case 1:
			
			this.level = i * this.lvlButStep;
			
			/*
			 * initializing the score
			 * either we do the sum of every number of smileys before, or we consider each level before was filled
			 */
			
			// option 1
			this.score = this.countScore(this.level);
			
			// option 2
			// this.score = this.level * this.grid.width * (this.grid.height - this.gdMin);
			
			break;
		
		// title screen
		
		case 2:
			
			this.reset();
			this.onGoing = false;
			this.starting = true;
			this.pausing = false;
			this.generating = false;
			this.lost = false;
			this.downOn = false;
			
			this.level = 25;
			this.score = this.countScore(this.level);
			
			this.timeSpent = 0;
			
			this.curCap = null;
			this.nextCapX = 0;
			this.nextCapY = 0;
			
			this.userDisplay();
			
			break;
		
		// resume
		
		case 3:
			
			this.action(27);
			
			break;
		
		// exit
		
		case 4:
			
			this.stop();
			
			break;
		
		// on / off events
		
		case 5:
			
			this.eh.isActive = i == 0;
			
			break;
		
		}
		
	}
	
	public void buttonPressed(int id) {
		/*
		 * buttonPressed with i set to 0.
		 */
		
		buttonPressed(id, 0);
		
	}
	
	public void stopTimer() {
		
		this.timer.cancel();
		
	}
	
	public void setCanvas(Canvas can) {
		/*
		 * Sets the canvas attribute.
		 * In:
		 *  can: canvas
		 */
		
		this.can = can;
		
	}
	
	public void setEventHandler(EventHandler eh) {
		/*
		 * Sets the event handler attribute.
		 * In:
		 *  eh: event handler
		 */
		
		this.eh = eh;
		
	}
	
	private void userDisplay() {
		/*
		 * Displays the current state of the game based on the current mode.
		 */
		
		switch (this.mode) {
		case "console":
			this.grid.consoleDisplay();
			break;
		case "canvas":
			this.can.repaint();
			break;
		}
		
	}
	
	// events-related
	
	public void setNextCapsule(int c0, int c1) {
		
		this.upComingC0 = c0;
		this.upComingC1 = c1;
		
	}
	
	public void setAlignLength(int al) {
		
		this.alignLength = al;
		
	}
	
	public void setColMax(int cm) {
		
		this.colMax = cm;
		
	}
	
	public boolean checkNewStone(int x, int y) {
		/*
		 * Returns true if there was a cell or a smiley at (x, y).
		 */
		
		int p = this.getCorrespondingCell(x, y);
		if (p == - 1) {
			p = this.getCorrespondingSmiley(x, y);
			if (p == - 1) return false;
			this.score += 1; this.smileysLeft -= 1;
			this.smileys[p].kill();
			this.smileys[p] = null;
			return true;
		}
		this.cells[p].kill();
		this.cells[p] = null;
		return true;
		
	}
	
	public void setNextCapX(int x) {
		
		this.nextCapX = x;
		
	}
	
	public void setSpeeder(float s) {
		
		this.speeder = s;
		
	}

}
