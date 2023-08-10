/*
 * Object that detects when an event should happen and makes it happen. Also handles its duration.
 * There are three states:
 *  0: waiting for a new event
 *  1: selecting the event
 *  2: waiting for the event to run out
 * Events:
 *  0:  bomb
 *  1:  locker
 *  2:  three
 *  3:  shuffler
 *  4:  blocker
 *  5:  elevator
 *  6:  green
 *  7:  exit
 *  8:  stone
 *  9:  cutter
 *  10: gift
 *  11: eraser
 *  12: alcoholic
 *  13: five
 *  14: sun
 *  - CUSTOM EVENTS -
 *  15: starter | the next capsule will appear on different columns than the center ones
 *  16: joker   | this color can be counted in any alignment
 *  17: ghost   | the next capsule will be invisible when falling
 *  18: mirror  | left and right are inversed, same for up and down
 *  19: speeder | everything is 2x faster
 * All of the functions named after an event will return the duration during which they will happen. 1 is instantaneous.
 * 
 * Libraries:
 *  java.util
 */

package game;

import java.util.Random;

import elements.Cell;
import elements.Smiley;
import elements.Stone;

public class EventHandler {
	
	final Game game;
	
	public boolean isActive;
	
	int nbEvents;
	public int[] eventsIni, events;
	public int cur;
	
	final int avgDelay;
	
	public Stone[] stones;
	public boolean alcOn,     // is alcoholic on?
				   ghostOn,   // is ghost on?
				   mirrorOn,  // is mirror on?
				   starterOn, // is starter on?
				   blockerOn, // is the blocker on?
				   sunOn;
	
	public final int blockerX, blockerY, blockerWidth, blockerHeight;
	
	public int counter, counterTot, state;
	
	Random rnd;

	public EventHandler(Game game) {
		
		this.game = game;
		
		this.isActive = true;
		
		this.nbEvents = 20;
		
		this.avgDelay = 45;
		
		this.blockerX = this.game.grid.width / 2 - 2; this.blockerY = this.game.gdMin;
		this.blockerWidth = 4; this.blockerHeight = 4;
		
		this.rnd = new Random();
		
		this.reset();
		
	}
	
	public boolean decrease(boolean arrived) {
		/*
		 * Decreases the counter by one every call. Will work when the capsule falls only.
		 * Returns false if nothing happens or if the events aren't enabled.
		 * In:
		 *  arrived: has the capsule just finished falling?
		 */
		
		if (! this.isActive) return false;
		
		this.counter -= 1;
		
		if (this.counter <= 0 && arrived) {
			
			switch (this.state) {
			// finished waiting -> select the event
			case 0:
				this.selectEvent();
				this.counterTot = this.nbEvents;
				this.counter = this.counterTot;
				break;
			// finished selecting -> apply it
			case 1:
				this.counterTot = this.applyEvents(true);
				this.counter = this.counterTot;
				break;
			// event has ended -> cancel it and restart
			case 2:
				this.applyEvents(false);
				this.counterTot = this.avgDelay;
				this.counter = this.counterTot;
				break;
			}
			
			this.state = (this.state + 1) % 3;
			
		}
		
		return true;
		
	}
	
	private void selectEvent() {
		/*
		 * Shuffles this.events and sets this.cur to the last item.
		 */
		
		for (int i = 0; i < this.nbEvents; i++) this.events[i] = i;
		// shuffle this.events with permutations
		int pos, temp;
		for (int i = 0; i < this.nbEvents; i++) {
			pos = this.rnd.nextInt(this.nbEvents);
			temp = this.events[i];
			this.events[i] = this.events[pos];
			this.events[pos] = temp;
		}
		
		this.cur = this.events[this.nbEvents - 1];
		
	}
	
	private int applyEvents(boolean activate) {
		/*
		 * Applies the event put in this.cur.
		 * In:
		 *  activate: activate or desactivate (cancel) the events
		 * Out:
		 *  duration of the event
		 */
		
		switch (this.cur) {
		case 0:
			return this.bomb(activate);
		case 1:
			return this.locker(activate);
		case 2:
			return this.three(activate);
		case 3:
			return this.shuffler(activate);
		case 4:
			return this.blocker(activate);
		case 5:
			return this.elevator(activate);
		case 6:
			return this.green(activate);
		case 7:
			return this.exit(activate);
		case 8:
			return this.stone(activate);
		case 9:
			return this.cutter(activate);
		case 10:
			return this.gift(activate);
		case 11:
			return this.eraser(activate);
		case 12:
			return this.alcoholic(activate);
		case 13:
			return this.five(activate);
		case 14:
			return this.sun(activate);
		case 15:
			return this.starter(activate);
		case 16:
			return this.joker(activate);
		case 17:
			return this.ghost(activate);
		case 18:
			return this.mirror(activate);
		case 19:
			return this.speeder(activate);
		}
		
		return 0;
		
	}
	
	// events
	
	private int bomb(boolean activate) {
		
		if (! activate) return 0;
		
		this.game.setNextCapsule(8, 8);
		
		return 1;
		
	}
	
	private int locker(boolean activate) {
		
		if (! activate) {
			this.game.setAlignLength(4);
			return 0;
		}
		
		this.game.setAlignLength(this.game.grid.width + this.game.grid.height); // set the requierement too high to be reachable
		
		return this.randomDuration();
		
	}
	
	private int three(boolean activate) {
		
		if (! activate) {
			this.game.setAlignLength(4);
			return 0;
		}
		
		this.game.setAlignLength(3);
		
		return this.randomDuration();
		
	}
	
	private int shuffler(boolean activate) {
		
		if (! activate) return 0;
		
		for (Smiley s: this.game.smileys)
			if (s != null) s.changeColor(this.rnd.nextInt(this.game.colMax) + 2);
		
		return 1;
		
	}
	
	private int blocker(boolean activate) {
		
		this.blockerOn = activate;
		int fill = activate? 9 : 0; // if we show the blocker, we fill the cases with a 9, otherwise, we empty them
		
		int count = - 1;
		int[][] dead = new int[this.blockerWidth * this.blockerHeight - 2][2]; // the blocker is 4x4 with two empty cases
		for (int i = 0; i < this.blockerWidth; i++) for (int j = 0; j < this.blockerHeight; j++) {
			if (j == 0 && (i == 0 || i == this.blockerWidth - 1)) continue; // those two cases aren't filled
			count += 1;
			dead[count][0] = this.blockerX + i;
			dead[count][1] = this.blockerY + j;
		}
		
		if (activate) this.game.deleteElements(dead);
		for (int[] coords: dead)
			this.game.grid.g[coords[0]][coords[1]] = fill;
		
		this.game.deletingCells = true;
		
		if (! activate) return 0;
		return this.randomDuration();
		
	}
	
	private int elevator(boolean activate) {
		
		if (! activate) return 0;
		
		int[][] gCopy = this.game.grid.g.clone();
		
		/*
		 * killing the cells at the top
		 * to create dead with a correct size, we loop through the top line and count how many cases aren't filled with a 0
		 */
		int count = 0;
		for (int i: gCopy[0]) if (i != 0) count += 1;
		int[][] dead = new int[count][2];
		this.game.deletingCells = count != 0;
		
		/*
		 * moving up
		 * if a cell is at the top, it gets killed;
		 * if it is at the bottom, to avoid asking for items out of range, we immediately set the parameter of moveUp to true;
		 * otherwise, we set the parameter depending on the case under -> if it used to be filled (seen in gCopy), it might have already been
		 * changed by an other cell so we don't change it again
		 * we have to do it for both the cells and the smileys
		 */
		count = - 1;
		for (Cell c: this.game.cells)
			if (c == null) continue;
			else if (c.y == 0) {
				count += 1;
				dead[count][0] = c.x; dead[count][1] = c.y;
				continue;
			} else if (c.y == this.game.grid.height - 1)
				c.moveUp(true);
			else
				c.moveUp(gCopy[c.x][c.y + 1] == 0);
		for (Smiley s: this.game.smileys)
			if (s == null) continue;
			else if (s.y == 0) {
				count += 1;
				dead[count][0] = s.x; dead[count][1] = s.y;
				continue;
			} else if (s.y == this.game.grid.height - 1)
				s.moveUp(true);
			else
				s.moveUp(gCopy[s.x][s.y + 1] == 0);
		
		this.game.deleteElements(dead);
		
		return 1;
		
	}
	
	private int green(boolean activate) {
		
		if (! activate) {
			this.game.setColMax(3);
			return 0;
		}
		
		this.game.setColMax(4);
		
		return this.randomDuration();
		
	}
	
	private int exit(boolean activate) {
		
		if (! activate) return 0;
		
		int[][] dead = new int[this.game.smileys.length][2];
		int count = - 1;
		Smiley s;
		for (int i = 0; i < this.game.smileys.length; i++) {
			s = this.game.smileys[i];
			if (s == null) continue;
			count += 1;
			dead[count][0] = s.x;
			dead[count][1] = s.y;
		}
		this.game.deleteElements(dead);
		
		this.game.deletingCells = true;
		
		return 1;
		
	}
	
	private int stone(boolean activate) {
		
		if (! activate) return 0;
		
		// find the next null element in this.stones
		int pos = 0;
		while (this.stones[pos] != null && pos < this.stones.length) pos += 1;
		if (pos >= this.stones.length) return 1;
		
		int x, y;
		x = this.rnd.nextInt(this.game.grid.width);
		y = this.rnd.nextInt(this.game.grid.height - 1) + 1;
		
		this.game.checkNewStone(x, y);
		
		this.stones[pos] = new Stone(this.game.grid, x, y);
		
		return 1;
		
	}
	
	private int cutter(boolean activate) {
		
		if (! activate) return 0;
		
		// search for a row with elements on it
		int row = 0;
		int co = 0;
		while (co == 0) {
			row = this.rnd.nextInt(this.game.grid.height);
			for (int i = 0; i < this.game.grid.width; i++) if (this.game.grid.g[i][row] != 0) co += 1;
		}
		
		int[][] dead = new int[co][2];
		co = - 1;
		for (int i = 0; i < this.game.grid.width; i++) {
			if (this.game.grid.g[i][row] != 0) {
				co += 1;
				dead[co][0] = i;
				dead[co][1] = row;
			}
		}
		
		this.game.deleteElements(dead);
		
		this.game.deletingCells = true;
		
		return 1;
		
	}
	
	private int gift(boolean activate) {
		
		if (! activate) return 0;
		
		// searching for null elements in this.game.cells
		int[] cols = new int[this.game.grid.width];
		for (int i = 1; i < cols.length; i++) {
			while (this.game.cells[cols[i]] != null) cols[i] += 1;
			if (i != cols.length - 1) cols[i + 1] = cols[i] + 1;
		}
		
		for (int i = 0; i < cols.length; i++)
			if (this.game.grid.g[i][0] == 0)
				this.game.cells[cols[i]] = new Cell(this.game.grid, i, 0, false, false, null, this.rnd.nextInt(this.game.colMax) + 2);
		
		return 1;
		
	}
	
	private int eraser(boolean activate) {
		
		if (! activate) return 0;
		
		int col = this.rnd.nextInt(this.game.colMax);
		
		int co = 0;
		for (Smiley sT: this.game.smileys)
			if (sT != null) if (sT.color == col) co += 1;
		for (Cell cT: this.game.cells)
			if (cT != null) if (cT.color == col) co += 1;
		
		int[][] dead = new int[co][2];
		co = - 1;
		for (Smiley s: this.game.smileys)
			if (s != null) if (s.color == col) {
				co += 1;
				dead[co][0] = s.x;
				dead[co][1] = s.y;
			}
		for (Cell c: this.game.cells)
			if (c != null) if (c.color == col) {
				co += 1;
				dead[co][0] = c.x;
				dead[co][1] = c.y;
			}
		
		this.game.deleteElements(dead);
		
		this.game.deletingCells = true;
		
		return 1;
		
	}
	
	private int alcoholic(boolean activate) {
		
		this.alcOn = activate;
		
		if (! activate) return 0;
		return this.randomDuration();
		
	}
	
	private int five(boolean activate) {
		
		if (! activate) {
			this.game.setAlignLength(4);
			return 0;
		}
		
		this.game.setAlignLength(5);
		
		return this.randomDuration();
		
	}
	
	private int sun(boolean activate) {
		
		this.sunOn = activate;
		
		if (! activate) return 0;
		
		// verifying that there is enough space to store two new smileys
		int count = 0;
		for (Smiley s: this.game.smileys) {
			if (s == null) count += 1;
			if (count >= 2) break;
		}
		if (count < 2)
			this.sunOn = false;
		
		return 1;
		
	}
	
	private int starter(boolean activate) {
		
		this.starterOn = activate;
		
		if (! activate)	return 0;
		return this.randomDuration();
		
	}
	
	private int joker(boolean activate) {
		
		if (! activate) return 0;
		
		if (this.rnd.nextInt(2) == 0)
			this.game.setNextCapsule(7, this.game.upComingC1);
		else
			this.game.setNextCapsule(this.game.upComingC0, 7);
		
		return 1;
		
	}
	
	private int ghost(boolean activate) {
		
		this.ghostOn = activate;
		
		if (! activate) return 0;
		return 2;
		
	}
	
	private int mirror(boolean activate) {
		
		this.mirrorOn = activate;
		
		if (! activate) return 0;
		return this.randomDuration();
		
	}
	
	private int speeder(boolean activate) {
		
		if (! activate) {
			this.game.setSpeeder(1);
			return 0;
		}
		
		this.game.setSpeeder(2);
		
		return this.randomDuration();
		
	}
	
	private int randomDuration() {
		/*
		 * Returns the number of frames an event will last.
		 */
		
		return (this.rnd.nextInt(3) + 3) * 6;
		
	}
	
	public void reset() {
		
		this.applyEvents(false);
		
		eventsIni = new int[this.nbEvents]; events = new int[this.nbEvents];
		for (int i = 0; i < this.eventsIni.length; i++)
			eventsIni[i] = i;
		this.cur = 0;
		
		this.stones = new Stone[5];
		this.alcOn = false; this.ghostOn = false; this.mirrorOn = false; this.starterOn = false; this.blockerOn = false; this.sunOn = false;
		
		this.counterTot = this.avgDelay;
		this.counter = this.counterTot;
		this.state = 0;
		
	}

}
