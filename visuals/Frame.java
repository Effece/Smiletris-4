package visuals;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;

import game.Game;

public class Frame extends JFrame {
	
	Game game;
	
	final JPanel canPanel;
	public final int width, height, rap;
	Color background;
	
	private static final long serialVersionUID = 4L;

	public Frame(Game game) {
		
		this.game = game;
		
		this.rap = 30;
		this.background = Color.black;
		
		this.setBackground(this.background);
		
		this.setVisible(true);
		
		this.canPanel = new JPanel();
		this.add(this.canPanel, BorderLayout.CENTER);
		this.canPanel.setBackground(this.background);
		this.canPanel.setVisible(true);
	    
	    this.width = this.game.grid.width * this.rap;
		this.height = this.game.grid.height * this.rap;
		
		this.setSize(this.width, this.height);
	    this.setPreferredSize(new Dimension(this.width, this.height));
	    this.setMinimumSize(new Dimension(this.width, this.height));
	    this.setMaximumSize(new Dimension(this.width, this.height));
	    
	    this.setTitle("Smiletris 4");
	    this.setFocusable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.pack();
		
	}
	
}
