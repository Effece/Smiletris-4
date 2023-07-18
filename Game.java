package game;

import java.util.Random;
import java.util.Timer;

import elements.Capsule;
import elements.Cell;
import elements.Smiley;

import visuals.Canvas;

public class Game {

	public final Grid grid;
	
	public Capsule curCap;
	public Smiley[] smileys;
	public Cell[] cells;
	public int upComingC0, upComingC1;
	
	boolean onGoing;
	
	final Timer timer;//, gravTimer;
	// final TimerTaskExtended tte;
	final int delay, gravDelay;
	final Random rnd;
	
	// rules
	
	final int alignLength; // number of cells to align
	final int colMax;
	// level setups
	final int gdMin, // minimal level for a smiley to appear on
			  smMin, // number of smileys at level 0
			  lvMax, // level where the number is at its maximum
			  smMax; // number of smileys at the last level
	final double coef1, coef2;
	
	boolean isFalling, finish;
	
	// visuals
	Canvas can;
	
	public Game(Grid grid) {
		
		this.grid = grid;
		
		this.smileys = null;
		// this.smileys = new Smiley[this.grid.width * this.grid.height]; // maximum of 1 smiley per case
		this.cells = new Cell[this.grid.width * this.grid.height];
		
		this.onGoing = true;
		
		this.timer = new java.util.Timer();
		// this.tte = new TimerTaskExtended(this);
		this.delay = 800;
		//this.gravTimer = new java.util.Timer();
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
		
		this.isFalling = false;
		this.finish = false;
		
		this.can = null;
		
	}
	
	private boolean update() {
		
		if (this.isFalling) {

			this.deleteLines();
			this.gravity();
			this.deleteLines();
			if (this.countCanFall() == 0) {
				this.isFalling = false;
				this.finish = false;
				this.lastChecks();
			}
		
		} else if (this.curCap == null && ! this.finish)
			this.curCap = new Capsule(this.grid, this.grid.width / 2 - 1, 0,
					this.upComingC0, this.upComingC1);
		
		else if (this.curCap.canFall() && ! this.finish)
			this.curCap.fall();
		
		else {
			
			int p0 = 0;
			while (cells[p0] != null) p0 += 1;
			int p1 = p0 + 1;
			while (cells[p1] != null) p1 += 1;
			
			Cell[] res = this.curCap.endLife();
			this.cells[p0] = res[0];
			this.cells[p1] = res[1];
			
			this.curCap = null;
			
			// remove lines of 4 or more
			this.finish = true;
			this.deleteLines();
			this.gravity();
			if (this.isFalling) return true;
			this.finish = false;
			
			this.lastChecks();
			
		}
		
		this.grid.consoleDisplay();
		this.can.repaint();
		
		return true;
		
	}
	
	public void gravity() {
		// applies gravity on every cell
		
		/*
		while (this.countCanFall() > 0)
			for (Cell i: this.cells)
				if (i != null) i.fall();
		// could be replaced using the timer to make it smoother
		*/
		if (this.countCanFall() > 0) {
			for (Cell i: this.cells)
				if (i != null) i.fall();
			this.isFalling = true;
		}
				
	}
	
	public int countCanFall() {
		// returns the number of cells that can fall
		
		int c = 0;
		for (Cell i: this.cells) {
			if (i == null) continue;
			if (i.canFall()) c += 1;
		}
		return c;
		
	}
	
	private void lastChecks() {
		
		this.upComingC0 = this.rnd.nextInt(this.colMax) + 2;
		this.upComingC1 = this.rnd.nextInt(this.colMax) + 2;
		
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
		if (this.grid.isStuck()) {
			this.onGoing = false;
			System.out.println("You're stuck.");
		}
		
	}
	
	private void deleteLines() {
		
		int[][] dead = this.grid.detect();
		int corSmil, corCell;
		for (int[] i: dead) {
			corSmil = this.getCorrespondingSmiley(i[0], i[1]);
			if (corSmil == - 1) {
				corCell = this.getCorrespondingCell(i[0], i[1]);
				// either an error or the cell was already deleted
				// if (corCell == - 1) System.out.println("There's a problem");
				if (corCell != - 1) {
					this.cells[corCell].kill();
					this.cells[corCell] = null;
				}
			} else {
				this.smileys[corSmil].kill();
				this.smileys[corSmil] = null;
			}
		}
		
	}
	
	public void loopExecute() {
		
		this.update();
		
		/*
		// random moves until I implement event listeners
		UPDATE: can be reused for alcoolic event now
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
			if (this.isFalling) d = this.gravDelay;
			this.timer.schedule(new TimerTaskExtended(this), d);
		} else
			this.timer.cancel();
		
	}
	
	private int getCorrespondingCell(int x, int y) {
		
		for (int i = 0; i < this.cells.length; i++)
			if (this.cells[i] == null) continue;
			else if (this.cells[i].x == x && this.cells[i].y == y) return i;
		return - 1;
		
	}
	
	private int getCorrespondingSmiley(int x, int y) {
		
		for (int i = 0; i < this.smileys.length; i++)
			if (this.smileys[i] == null) continue;
			else if (this.smileys[i].x == x && this.smileys[i].y == y) return i;
		return - 1;
		
	}
	
	public void custom(int[][] elts) {
		// elts: int[choosable length][3]
		// elts[i]: [x, y, color]
		
		this.reset();
		
		this.smileys = new Smiley[elts.length];
		for (int i = 0; i < elts.length; i++)
			this.smileys[i] = new Smiley(this.grid, elts[i][0], elts[i][1], elts[i][2]);
			// this.g[elts[i][0]][elts[i][1]] = elts[i][2];
		
	}
	
	public void init(int lvl) {
		
		this.reset();
		
		int nb = this.numberSmileys(lvl);
		this.smileys = new Smiley[nb];
		int[] pos;
		for (int i = 0; i < nb; i++) {
			pos = this.randomPos();
			while (this.grid.g[pos[0]][pos[1]] != 0)
				pos = this.randomPos();
			this.smileys[i] = new Smiley(this.grid, pos[0], pos[1], this.rnd.nextInt(3) + 2);
		}
		
	}
	
	private int numberSmileys(int n) {
		// function that calculates the number of smileys to generate
		
		if (n >= this.lvMax) return this.smMax;
		
		return (int) (this.coef2 * Math.log(this.coef1 * n + 1) + this.smMin);
		
	}
	
	private int[] randomPos() {
		
		return new int[] {
				this.rnd.nextInt(this.grid.width),
				this.rnd.nextInt(this.grid.height - this.gdMin) + this.gdMin
		};
		
	}
	
	private void reset() {
		
		this.smileys = null;
		/*for (int i = 0; i < this.smileys.length; i++)
			this.smileys[i] = null;*/
		this.cells = new Cell[this.grid.width * this.grid.height];
		this.grid.reset();
		
		this.upComingC0 = this.rnd.nextInt(3) + 2;
		this.upComingC1 = this.rnd.nextInt(3) + 2;
		
	}
	
	public boolean action(int event) {
		/*
		 * 38 -- Up
		 * 40 -- Down
		 * 37 -- Left
		 * 39 -- Right
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
		
		this.can = can;
		
	}

}
