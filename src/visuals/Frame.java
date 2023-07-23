/*
 * Window in which the game happens. Contains the canvas.
 * 
 * The window is mesured in DPI while the canvas is mesured in pixels.
 * 
 * Libraries:
 *  java.awt
 */

package visuals;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import game.Game;

public class Frame extends java.awt.Frame implements WindowListener {
	
	Game game;
	
	public final int width, height,
					 rap; // size of a case
	Color background;
	
	private static final long serialVersionUID = 4L;
	
	public Frame(Game game) {
		
		super();
		
		this.game = game;
		
		this.rap = 32;
		this.background = Color.black;
		
		this.setBackground(this.background);
	    
	    this.width = 1920;  // maximum size \ it doesn't matter because the canvas runs
		this.height = 1280; // maximum size / a function to make it full-screen
		
		this.setSize(this.width, this.height);
	    this.setPreferredSize(new Dimension(this.width, this.height));
	    this.setMinimumSize(new Dimension(this.width, this.height));
	    this.setMaximumSize(new Dimension(this.width, this.height));
	    
	    this.setTitle("Smiletris 4");
	    this.setFocusable(true);
		
		this.addWindowListener(this);
		
		this.setVisible(true);
		this.pack();
		
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		/*
		 * End of the program.
		 */
		
		this.dispose();
		this.game.onGoing = false;
		System.exit(0);
		
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
	
}
