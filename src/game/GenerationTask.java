/*
 * Object used during the level generation, to space out the smiley's appearances.
 * 
 * Libraries:
 *  java.util
 */

package game;

import java.util.TimerTask;

public class GenerationTask extends TimerTask {
	
	final Game game;
	final int n, nMax;

	public GenerationTask(Game game, int n, int nMax) {
		
		this.game = game;
		this.n = n; this.nMax = nMax;
		
	}
	
	public void run() {
		
		this.game.loopNewSmiley(this.n, this.nMax);
		
	}

}
