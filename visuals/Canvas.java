/*
 * Canvas displaying the current state of the game. Contained inside a frame object.
 * 
 * Libraries:
 *  java.util
 *  java.awt
 *  java.io
 *  javax.swing
 */

package visuals;

import java.util.Random;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import game.Game;
import elements.Cell;
import elements.Smiley;

public class Canvas extends JPanel implements KeyListener {
	
	final Game game;
	
	final Frame frame;
	Graphics2D graphics;
	final int rap, // dis,      // rap is the size of a cell, dis was a smaller value used for small details
			  width, height;
	Color background;
	
	// images
	
	final Random rnd;
	Image backgroundImage;
	Image[] cellImages, smileyImages, stoneImages;
	Image[] cellHoriImages, cellVertImages;
	
	private static final long serialVersionUID = 4L;
	
	public Canvas(Game game, Frame frame) {
		
		this.game = game;
		
		// adapting the window
		this.frame = frame;
		this.frame.add(this, BorderLayout.CENTER);
		this.frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH); // maximum size, full screen
		
		this.rap = this.frame.rap;
		// this.dis = this.rap / 10;
		this.width = this.frame.width;
		this.height = this.frame.height;
		this.background = this.frame.background;
		
		this.setSize(new Dimension(this.width, this.height));
		this.setPreferredSize(new Dimension(this.width, this.height));
		this.setMinimumSize(new Dimension(this.width, this.height));
		this.setMaximumSize(new Dimension(this.width, this.height));
		
		this.setVisible(true);
		
		this.frame.addKeyListener(this);
		
		// images
		
		this.rnd = new Random(); // used to generate random smileys and similar stuff
		
		/*
		 * importing every image
		 * images are contained in the image folder which needs to be made a source folder
		 * ImageIO.read can raise the IOException error which forces the use of a try
		 */
		try {
			
			this.backgroundImage = ImageIO.read(new File("images/background.png"));
			
			this.cellImages = new Image[] {
					ImageIO.read(new File("images/cellYellow.png")),
					ImageIO.read(new File("images/cellBlue.png")),
					ImageIO.read(new File("images/cellPurple.png")),
					ImageIO.read(new File("images/cellGreen.png"))
			};
			this.smileyImages = new Image[] {
					ImageIO.read(new File("images/smiley1.png")),
					ImageIO.read(new File("images/smiley2.png")),
					ImageIO.read(new File("images/smiley3.png")),
					ImageIO.read(new File("images/smiley4.png")),
					ImageIO.read(new File("images/smiley5.png"))
			};
			this.stoneImages = new Image[] {
					ImageIO.read(new File("images/stone.png"))
			};
			
			this.cellHoriImages = new Image[] {
					ImageIO.read(new File("images/cellYellowHori.png")),
					ImageIO.read(new File("images/cellBlueHori.png")),
					ImageIO.read(new File("images/cellPurpleHori.png")),
					ImageIO.read(new File("images/cellGreenHori.png"))
			};
			this.cellVertImages = new Image[] {
					ImageIO.read(new File("images/cellYellowVert.png")),
					ImageIO.read(new File("images/cellBlueVert.png")),
					ImageIO.read(new File("images/cellPurpleVert.png")),
					ImageIO.read(new File("images/cellGreenVert.png"))
			};
			
		} catch (IOException e) {
			this.backgroundImage = null;
			this.cellImages = null;
			this.smileyImages = null;
			this.stoneImages = null;
		}
		
	}
	
	@Override
	public void paint(Graphics g) {
		/*
		 * Function called every time the canvas is repainted. Draws everything again.
		 */
		
		this.graphics = (Graphics2D) g;
		
		// remove everything
		this.graphics.clearRect(0, 0, this.width, this.height);
		
		// background
		this.graphics.setBackground(this.background);
		this.graphics.drawImage(this.backgroundImage, 0, 0, this.width, this.height, this);
		
		// when the game is first initialized, game.cells and game.smileys are empty
		if (this.game.cells != null && this.game.smileys != null) {
		
			// paint with images
			
			// cells
			for (Cell c: this.game.cells)
				if (c != null)
					this.drawImageObject(0, c.x, c.y, c.color, c.fused, c.tl, c.hori);
			
			// smileys
			for (Smiley s: this.game.smileys)
				if (s != null)
					this.drawImageObject(1, s.x, s.y, s.color, false, false, false);
			
			// capsule
			if (this.game.curCap != null) {
				this.drawImageObject(0, this.game.curCap.x, this.game.curCap.y, this.game.curCap.c0,
						true, true, this.game.curCap.hori);
				int cap2X = this.game.curCap.x; int cap2Y = this.game.curCap.y;
				if (this.game.curCap.hori) cap2X += 1;
				else cap2Y += 1;
				this.drawImageObject(0, cap2X, cap2Y, this.game.curCap.c1,
						true, false, this.game.curCap.hori);
			}
		
		}
		
		// end
		this.graphics.dispose();
		
	}
	
	@SuppressWarnings("unused")
	private void drawNoImage() {
		/*
		 * Draws the canvas without using any image.
		 * dis was formerly known as this.dis but this.dis was removed because it became unused.
		 */
		
		int dis = this.rap / 5;
		
		// cells
		for (Cell c: this.game.cells)
			if (c != null)
				this.drawObject(c.x, c.y, c.color, c.fused, c.tl, c.hori);
		
		// smileys
		for (Smiley s: this.game.smileys)
			if (s != null) {
				this.drawObject(s.x, s.y, s.color, false, false, false);
				// adding the face
				this.graphics.setColor(Color.BLACK);
				this.graphics.setStroke(new BasicStroke(3));
				this.graphics.drawOval(s.x * this.rap + this.rap / 3 - dis,
						s.y * this.rap + this.rap / 3 - dis,
						dis * 2, dis * 2);
				this.graphics.drawOval(s.x * this.rap + this.rap * 2 / 3 - dis,
						s.y * this.rap + this.rap / 3 - dis,
						dis * 2, dis * 2);
				this.graphics.drawLine(s.x * this.rap + this.rap / 3, s.y * this.rap + this.rap * 2 / 3,
						s.x * this.rap + this.rap * 2 / 3, s.y * this.rap + this.rap * 2 / 3);
			}
		
		// capsule
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
	
	private void drawImageObject(int type, int x, int y, int color, boolean fused, boolean tl, boolean hori) {
		/*
		 * Draws an object based on its type and its attributes.
		 * Types:
		 *  0: cell
		 *  1: smiley
		 *  2: stone
		 * A smiley is drawn from a cell that isn't fused and has a random expression on.
		 * The color is obtained by searching for the element in index: color - 2 in this.cellImages.
		 */
		
		/*
		 * Coordinates:
		 *  x1, y1: top-left coordinates on the canvas
		 *  x2, y2: bottom-right coordinates on the canvas
		 *  sx1, sy1: top-left coordinates on the image
		 *  sx2, sy2: bottom-right coordinates on the image
		 */
		int x1, y1, x2, y2, sx1, sy1, sx2, sy2;
		x1 = x * this.rap;       y1 = y * this.rap;
		x2 = (x + 1) * this.rap; y2 = (y + 1) * this.rap;
		sx1 = 0;                 sy1 = 0;
		sx2 = 16;                sy2 = 16;
		
		switch (type) {
		
		case 0:
			
			this.graphics.drawImage(this.cellImages[color - 2],
					x1, y1, x2, y2,
					sx1, sy1, sx2, sy2, this);
			
			/*
			 * Linking to the other cells
			 * There are two images for each color: a vertical rectangle and an horizontal rectangle. The one to use is determined from hori.
			 * Based on the position of the cell compared to its cell-friend, the canvas coordinates are adapted then the source image's.
			 */
			if (fused) {
				Image join;
				if (hori) {
					join = this.cellHoriImages[color - 2];
					if (tl) {
						x1 += this.rap / 2;
						sx1 = 8;
					} else {
						x2 -= this.rap / 2;
						sx2 = 8;
					}
				} else {
					join = this.cellVertImages[color - 2];
					if (tl) {
						y1 += this.rap / 2;
						sy1 = 8;
					} else {
						y2 -= this.rap / 2;
						sy2 = 8;
					}
				}
				this.graphics.drawImage(join,
						x1, y1, x2, y2,
						sx1, sy1, sx2, sy2, this);
			}
			
			break;
			
		case 1:
			
			this.drawImageObject(0, x, y, color, false, false, false);
			this.graphics.drawImage(this.smileyImages[this.rnd.nextInt(5)],
					x1, y1, x2, y2,
					sx1, sy1, sx2, sy2, this);
			break;
		
		case 2:
			
			break;
			
		}
		
	}
	
	private void drawObject(int x, int y, int color, boolean fused, boolean tl, boolean hori) {
		/*
		 * Draws a cell (without images).
		 * In:
		 *  x, y: coordinates
		 *  color: the cell's color
		 *  fused: is fused with another cell?
		 *  tl: is top-left?
		 *  hori: is horizontal?
		 */
		
		this.graphics.setColor(this.getColor(color));
		this.graphics.setStroke(new BasicStroke(1));
		
		this.graphics.fillOval(x * this.rap, y * this.rap, this.rap, this.rap);
		
		// linking to other cells
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
		/*
		 * Returns the corresponding color.
		 * In:
		 *  color: int value of the color
		 */
		
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
	
	public void reset() {
		/*
		 * Resets the canvas.
		 */
		
		if (this.graphics != null)
			this.graphics.clearRect(0, 0, this.width, this.height);
		
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		/*
		 * Sends an input to the game object.
		 */
		
		int code = e.getKeyCode();
		this.game.action(code);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
}
