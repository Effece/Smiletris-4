/*
 * Object used to get to the next frame after a certain delay.
 * 
 * Libraries:
 *  java.util
 */

package game;

import java.util.TimerTask;

public class TimerTaskExtended extends TimerTask {
	
	final Game game;

	public TimerTaskExtended(Game game) {
		
		this.game = game;
		
	}
	
	public void run() {
		
		this.game.loopExecute();
		
	}

}
