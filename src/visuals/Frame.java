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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import game.Game;

public class Frame extends java.awt.Frame implements WindowListener, MouseListener, MouseMotionListener {
	
	Game game;
	
	public final int width, height,
					 rap,       // size of a case
					 boundSize; // size of the boundaries
	Color background;
	
	// moving the window
	boolean dragOn;
	int deltaX, deltaY;
	
	private static final long serialVersionUID = 4L;
	
	public Frame(Game game) {
		
		super();
		
		this.game = game;
		
		this.rap = 28;
		this.boundSize = this.rap;
		this.background = Color.black;
		
		this.setBackground(this.background);
		
		this.width  = this.game.grid.width * this.rap + this.boundSize * 2;
		this.height = (this.game.grid.height + 2) * this.rap + this.boundSize * 3;
		
		this.setSize(this.width, this.height);
		this.setPreferredSize(new Dimension(this.width, this.height));
		this.setMinimumSize(new Dimension(this.width, this.height));
		this.setMaximumSize(new Dimension(this.width, this.height));
		
		this.setTitle("Smiletris 4");
		this.setUndecorated(true);
		this.setFocusable(true);
		
		this.addWindowListener(this);
		
		this.setVisible(true);
		this.pack();
		
		this.dragOn = false;
		this.deltaX = 0; this.deltaY = 0;

	}
	
	public void stop() {
		/*
		 * Stops the frame. Called when the game is stopped (window closing).
		 */
		
		this.dispose();
		
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		/*
		 * End of the program.
		 */
		
		this.game.stop();
		
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

	@Override
	public void mouseDragged(MouseEvent e) {
		
		if (this.dragOn)
			this.setBounds(e.getXOnScreen() - this.deltaX, e.getYOnScreen() - this.deltaY, this.width, this.height);
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		
		if (e.getY() <= this.boundSize) {
			this.dragOn = true;
			this.deltaX = e.getX(); this.deltaY = e.getY();
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		if (this.dragOn) {
			this.dragOn = false;
			this.deltaX = 0; this.deltaY = 0;
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
}
