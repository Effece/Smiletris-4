package visuals;

import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

import game.Game;
import elements.Cell;
import elements.Smiley;

public class Canvas extends JPanel implements KeyListener {
	
	final Game game;
	
	final Frame frame;
	Graphics2D graphics;
	
	final int rap, dis, width, height;
	Color background;
	
	private static final long serialVersionUID = 4L;
	
	public Canvas(Game game, Frame frame) {
		
		this.game = game;
		
		this.frame = frame;
		this.frame.canPanel.add(this);
		
		this.rap = this.frame.rap;
		this.dis = this.rap / 5;
		this.width = this.frame.width;
		this.height = this.frame.height;
		this.background = this.frame.background;
		
		this.setSize(new Dimension(this.width, this.height));
		this.setPreferredSize(new Dimension(this.width, this.height));
		this.setMinimumSize(new Dimension(this.width, this.height));
		this.setMaximumSize(new Dimension(this.width, this.height));
		
		this.setVisible(true);
		
		this.frame.addKeyListener(this);
		
	}
	
	@Override
	public void paint(Graphics g) {
		
		this.graphics = (Graphics2D) g;
		
		this.graphics.setBackground(this.background);
		
		// remove everything
		this.graphics.clearRect(0, 0, this.width, this.height);
		
		// paint
		
		for (Cell c: this.game.cells)
			if (c != null)
				this.drawObject(c.x, c.y, c.color, c.fused, c.tl, c.hori);
		for (Smiley s: this.game.smileys)
			if (s != null) {
				this.drawObject(s.x, s.y, s.color, false, false, false);
				this.graphics.drawLine(s.x * this.rap, s.y * this.rap,
						(s.x + 1) * this.rap, (s.y + 1) * this.rap);
			}
		if (this.game.curCap != null) {
			this.drawObject(this.game.curCap.x, this.game.curCap.y, this.game.curCap.c0,
					true, true, this.game.curCap.hori);
			int cap2X = this.game.curCap.x; int cap2Y = this.game.curCap.y;
			if (this.game.curCap.hori) cap2X += 1;
			else cap2Y += 1;
			this.drawObject(cap2X, cap2Y, this.game.curCap.c1,
					true, false, this.game.curCap.hori);
		}
		
	}
	
	private void drawObject(int x, int y, int color, boolean fused, boolean tl, boolean hori) {
		
		this.graphics.setColor(this.getColor(color));
		this.graphics.fillOval(x * this.rap, y * this.rap, this.rap, this.rap);
		if (fused) {
			if (tl) {
				if (hori)
					this.graphics.fillRect(x * this.rap + this.rap / 2, y * this.rap,
							this.rap / 2, this.rap);
				else
					this.graphics.fillRect(x * this.rap, y * this.rap + this.rap / 2,
							this.rap, this.rap / 2);
			} else if (hori)
				this.graphics.fillRect(x * this.rap, y * this.rap,
						this.rap / 2, this.rap);
			else
				this.graphics.fillRect(x * this.rap, y * this.rap,
						this.rap, this.rap / 2);
		}
		
	}
	
	private Color getColor(int color) {
		
		switch (color) {
		case 1:
			return Color.GRAY;
		case 2:
			return Color.YELLOW;
		case 3:
			return Color.BLUE;
		case 4:
			return Color.MAGENTA;
		case 5:
			return Color.GREEN;
		default:
			return Color.BLACK;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		/*
		 * 224 -- Up
		 * 225 -- Down
		 * 226 -- Left
		 * 227 -- Right
		 */
		
		System.out.println(e.getKeyCode());
		
		int code = e.getKeyCode();
		this.game.action(code);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	/*
	private float pxToDp(float px, Context context) {
		
		return px / (context.getResources().getDisplayMetrics().densityDpi / 160f);
		
	}
	*/
	
}
