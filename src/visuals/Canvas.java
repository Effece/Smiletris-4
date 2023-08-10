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
// import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import game.Game;
import elements.Cell;
import elements.Smiley;
import elements.Stone;

public class Canvas extends JPanel implements KeyListener, MouseListener {
	
	final Game game;
	
	final Frame frame;
	Graphics2D graphics;
	final int rap,      // size of a cell
			  width, height,
			  lvlWidth; // width of a level selector button
	Color background;
	
	/*
	 * boundaries
	 * space used to display the current game
	 */
	final int boundSize,
			  xMin, yMin,
			  xMinO, yMinO, xMaxO, yMaxO,
			  xMinB, yMinB, xMaxB, yMaxB,
			  centerX, centerY;
	
	// images
	
	final Random rnd;
	Image backgroundImage, eventImages, timerImage, buttonImage, buttonExitImage, shadowImage, blockerImage,
		  cellImages, smileyImages, stoneImages, cloudImages, smileyKilledImages;
	final int imageSize,   // size of one image
			  blockerSize, // size of the blocker
			  smileyLen, stoneLen, cloudLen, killsLen; // number of indivudual images on each image
	Image[] boundImages;
	int[] smileyNbs; // image on each smiley
	
	// text
	
	final String fontName;
	final Font font, titleFont, tallFont, textFont;
	
	/*
	 * buttons
	 * sub-arrays contain a boolean-like value to define if the button is active or not, the two corner's positions then an ID
	 * boolean-like values:
	 *  0: false inactive
	 *  1: true   active
	 * IDs:
	 *  look up the game class
	 * Indexs:
	 *  0: exit button
	 *  1: start
	 *  2: yes - events
	 *  3: no - events
	 *  4: resume
	 *  5: title screen
	 */
	int[][] buttons;
	int butLen, butWidth;
	
	// events
	final int eventSize,     // size of the source image
			  eventWidth,    // width of the image
			  eventCellSize, // size of the cells that delimitate each of the five events
			  eventY;        // y coordinate of the event images' centers
	
	final int lineSize;
	
	private static final long serialVersionUID = 4L;
	
	public Canvas(Game game, Frame frame) {
		
		this.game = game;
		
		// adapting the window
		this.frame = frame;
		this.frame.add(this, BorderLayout.CENTER);
		
		this.rap = this.frame.rap;
		this.boundSize = this.frame.boundSize;
		this.width = this.frame.width;
		this.height = this.frame.height;
		this.lvlWidth = Math.round(this.game.grid.width * this.rap / this.game.lvlButMax);
		this.background = this.frame.background;
		
		Dimension dim = new Dimension(this.width, this.height);
		this.setSize(dim);
		this.setPreferredSize(dim);
		this.setMinimumSize(dim);
		this.setMaximumSize(dim);
		
		this.setVisible(true);
		
		this.frame.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseListener(this.frame); this.addMouseMotionListener(this.frame);
		
		// boundaries
		this.xMinO = this.boundSize;
		this.yMinO = this.boundSize;
		this.xMaxO = this.width - this.xMinO;
		this.yMaxO = this.height - this.yMinO;
		this.xMin = this.xMinO;
		this.yMin = this.yMinO + this.boundSize + this.rap * 2;
		this.xMinB = 0;
		this.yMinB = this.yMin - this.boundSize;
		this.xMaxB = this.width - this.boundSize;
		this.yMaxB = this.height - this.boundSize;
		this.centerX = this.width / 2;
		this.centerY = this.height / 2;
		/*
		 * 0, 0                          width, 0      | 1: xMinO, yMinO ; 2: xMaxO, yMinO
		 * 4*----------------------------              |
		 * | -------------------------- |              | 3: xMin, yMin
		 * | |1*                    *2| |   \ 2 * rap  |
		 * | |                        | |   /          | 4: xMinB, 0
		 * 5*------------------------*6 |              | 5: xMinB, yMinB ; 6: xMaxB, yMinB
		 * | -------------------------- |              | 7: xMinB, yMaxB ; 8: xMaxB, yMaxB
		 * | |3*                      | |              |
		 * | |                        | |              |
		 * | |                        | |              |
		 * | |                        | |              |
		 * | |                        | |              |
		 * | |                        | |              |
		 * | |                        | |              |
		 * | |                        | |              |
		 * 7*------------------------*8 |              |
		 * ------------------------------              |
		 * 0, height                     width, height |
		 */
		
		// images
		
		this.rnd = new Random(); // used to generate random smileys and similar stuff
		
		/*
		 * importing every image
		 * images are contained in the image folder which needs to be made a source folder
		 * ImageIO.read can raise the IOException error which forces the use of a try
		 */
		try {
			
			this.backgroundImage = this.importImage("background.png");
			
			this.boundImages = new Image[] {
					this.importImage("bounds/hori.png"),
					this.importImage("bounds/vert.png"),
					this.importImage("bounds/TL.png"),
					this.importImage("bounds/TR.png"),
					this.importImage("bounds/BL.png"),
					this.importImage("bounds/BR.png")
			};
			
			this.cellImages         = this.importImage("cells.png");
			this.smileyImages       = this.importImage("smileys.png");
			this.smileyKilledImages = this.importImage("kills.png");
			this.cloudImages        = this.importImage("clouds.png");
			this.stoneImages        = this.importImage("stones.png");
			
			this.eventImages        = this.importImage("events.png");
			this.timerImage         = this.importImage("timer.png");
			
			this.buttonImage        = this.importImage("button.png");
			this.buttonExitImage    = this.importImage("buttonExit.png");
			
			this.shadowImage        = this.importImage("shadow.png");
			
			this.blockerImage       = this.importImage("blocker.png");
			
		} catch (IOException e) {
			this.backgroundImage = null;
			this.boundImages = null;
			this.cellImages = null;
			this.smileyImages = null;
			this.stoneImages = null;
			this.cloudImages = null;
			this.smileyKilledImages = null;
			this.eventImages = null;
			this.timerImage = null;
			this.buttonImage = null;
			this.buttonExitImage = null;
			this.shadowImage = null;
			this.blockerImage = null;
		}
		
		this.imageSize = 16;
		this.blockerSize = 64;
		this.smileyLen = 9;
		this.killsLen  = 4;
		this.cloudLen  = 3;
		this.stoneLen  = 1;
		
		// text
		this.fontName  = "Sylfaen";
		this.font      = new Font(this.fontName, Font.BOLD, Math.round(this.rap * 2 / 5));
		this.titleFont = new Font(this.fontName, Font.BOLD, this.rap);
		this.tallFont  = new Font(this.fontName, Font.BOLD, Math.round(this.rap * 2 / 3));
		this.textFont  = new Font(this.fontName, Font.PLAIN, Math.round(this.rap / 2));
		
		// buttons
		// this.butLen   = 7 + this.game.lvlButMax;
		// this.buttons  = new int[butLen][6];
		// for some reasons this code isn't executed when needed so it is reported to the paint function
		this.butWidth = (this.game.grid.width - 2) * this.rap;
		
		// events
		this.eventSize     = 16;
		this.eventWidth    = this.rap * 2 / 3;
		this.eventCellSize = this.game.grid.width * this.rap / 5;
		this.eventY        = this.rap * 5 / 2;
		
		this.lineSize = this.rap * 2 / 3;
		
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
		this.drawBackground();
		
		// boundaries
		this.drawBoundaries(this.game.generating || this.game.onGoing || this.game.lost || this.game.pausing);
		// might move it to the drawMenu and drawGame functions and remove the dynamic boolean thing
		
		if (this.butLen == 0)
			this.butLen = 7 + this.game.lvlButMax;
		if (this.buttons == null)
			this.buttons = new int[this.butLen][6];
		
		// exit button
		this.drawButton(this.buttonExitImage, "",
				this.width - this.rap / 2, 0, this.rap, this.rap, 0, true, 4);
		
		// start menu or game
		if (this.game.starting)
			this.drawMenu();
		else
			this.drawGame();
		
		// end
		this.graphics.dispose();
		
	}
	
	private void drawBackground() {
		/*
		 * Draws the background.
		 */
		
		// this.graphics.setBackground(this.background);
		this.graphics.drawImage(this.backgroundImage, 0, 0, this);
		
	}
	
	private void drawBoundaries(boolean middle) {
		/*
		 * Draws the boundaries.
		 * In:
		 *  middle: if set to true, adds an additional line in the middle
		 */
		
		if (this.boundImages != null) {
			
			// corners
			this.graphics.drawImage(this.boundImages[2],
					this.xMinB, 0, this.boundSize, this.boundSize, this); // top-left
			this.graphics.drawImage(this.boundImages[3],
					this.xMaxB, 0, this.boundSize, this.boundSize, this); // top-right
			this.graphics.drawImage(this.boundImages[4],
					this.xMinB, this.yMaxB, this.boundSize, this.boundSize, this); // bottom-left
			this.graphics.drawImage(this.boundImages[5],
					this.xMaxB, this.yMaxB, this.boundSize, this.boundSize, this); // bottom-right
			
			// links
			int xB, yB;
			// horizontal
			for (int i = 0; i < this.game.grid.width; i++) {
				xB = this.xMinO + this.rap * i;
				// top
				this.graphics.drawImage(this.boundImages[0],
						xB, 0, this.boundSize, this.boundSize, this);
				// middle
				if (middle) this.graphics.drawImage(this.boundImages[0],
						xB, this.yMinB, this.boundSize, this.boundSize, this);
				// bottom
				this.graphics.drawImage(this.boundImages[0],
						xB, this.yMaxB, this.boundSize, this.boundSize, this);
			}
			// vertical
			for (int i = 0; i < this.game.grid.height + 3; i++) {
				yB = this.yMinO + this.rap * i;
				// left
				this.graphics.drawImage(this.boundImages[1],
						this.xMinB, yB, this.boundSize, this.boundSize, this);
				// right
				this.graphics.drawImage(this.boundImages[1],
						this.xMaxB, yB, this.boundSize, this.boundSize, this);
			}
			
		}
		
	}
	
	private void drawMenu() {
		/*
		 * Draws the starting menu.
		 */
		
		this.graphics.setColor(Color.BLACK);
		
		String str[] = this.game.startMenuText;
		FontMetrics fm;
		
		// title
		this.drawCenteredString(str[0], this.titleFont, 1);
		
		// text 1
		fm = this.setAndGetFont(this.textFont);
		for (int i = 1; i < 6; i++)
			this.drawCenteredString(str[i], fm, 1 + i);
		
		// small title 1
		this.drawCenteredString(str[6], this.tallFont, 8);
		
		// text 2
		fm = this.setAndGetFont(this.textFont);
		for (int i = 7; i < 11; i++)
			this.drawCenteredString(str[i], fm, 2 + i);
		
		// small title 2
		this.drawCenteredString(str[11], this.tallFont, 14);
		
		// level selector
		for (int i = 0; i < this.game.lvlButMax; i++)
			this.drawButton(String.valueOf(i * this.game.lvlButStep), this.rap + this.lvlWidth * (i + 2 / 5) + this.lvlWidth / 2,
					15, this.lvlWidth * 4 / 5, this.lineSize, true,
					this.butLen - this.game.lvlButMax + i, true, 1);
		
		// small title 3
		this.drawCenteredString(str[12], this.tallFont, 18);
		
		// event activator
		this.drawButton("Yes", this.centerX / 2, 19, this.butWidth / 2, this.lineSize, true,
				2, true, 5);
		this.drawButton("No", this.centerX * 3 / 2, 19, this.butWidth / 2, this.lineSize, true,
				3, true, 5);
		
		// start
		this.drawButton("Start", this.centerX, 21, this.butWidth, this.lineSize * 2, true,
				1, true, 0);

	}
	
	private void drawCenteredString(String text, int y) {
		/*
		 * Draws text at the center of the canvas, at y * this.rap, without changing the font.
		 * In:
		 *  text: text to draw
		 *  y: y level (without adaptation using this.lineSize)
		 */
		
		FontMetrics fm = this.graphics.getFontMetrics();
		this.drawCenteredString(text, fm, y);
		
	}
	
	private void drawCenteredString(String text, FontMetrics fm, int y) {
		/*
		 * Draws text at the center of the canvas, at y * this.rap, without changing the font.
		 * In:
		 *  text: text to draw
		 *  fm: font metrics, to avoid creating a new one every time
		 *  y: y level (without adaptation using this.rap)
		 */
		
		this.graphics.drawString(text, this.centerX - fm.stringWidth(text) / 2, this.yMinO + this.lineSize * (y + 1));
		
	}
	
	private void drawCenteredString(String text, Font f, int y) {
		/*
		 * Draws text at the center of the canvas, at y * this.rap.
		 * In:
		 *  text: text to draw
		 *  f: font to use
		 *  y: y level (without adaptation using this.rap)
		 */
		
		this.graphics.setFont(f);
		this.drawCenteredString(text, y);
		
	}
	
	private void drawButton(Image bg, String text, int x, int y, int width, int height, int index, boolean active, int type) {
		/*
		 * Draws a button. This button is placed between:
		 * - x coordinates: x - width / 2, x + width / 2;
		 * - y coordinates: y, y + height.
		 * In:
		 *  bg: background image
		 *  text: text displayed in the button
		 *  x, y: coordinates of the button
		 *  width, height: size of the button
		 */
		
		int textHeight = Math.round(height * 2 / 3);
		
		// this.graphics.drawImage(bg, x - width / 2, y, x + width / 2, y + height, 0, 0, 36, 20, this);
		// doesn't take in account which image it is
		this.graphics.drawImage(bg, x - width / 2, y, width, height, this);
		
		this.graphics.setColor(Color.BLACK);
		this.graphics.setFont(new Font(this.fontName, Font.BOLD, textHeight));
		this.graphics.drawString(text, x - this.graphics.getFontMetrics().stringWidth(text) / 2, y + textHeight);
		
		int[] r = new int[] {active? 1 : 0, x - width / 2, y, x + width / 2, y + height, type};
		// this button hadn't been created yet
		if (this.buttons[index] == null)
			this.buttons[index] = r;
		// the button was already created -> only modify certain attributes
		else
			for (int i = 0; i < this.buttons[0].length; i++)
				if (this.buttons[index][i] != r[i]) this.buttons[index][i] = r[i];
		
	}
	
	private void drawButton(String text, int x, int y, int width, int height, int index, boolean active, int type) {
		/*
		 * Draws a button. When the bg argument isn't given, the button has the buttonImage.png (this.buttonImage) background. This button
		 * is placed between:
		 * - x coordinates: x - width / 2, x + width / 2;
		 * - y coordinates: y, y + height.
		 * In:
		 *  text: text displayed in the button
		 *  x, y: coordinates of the button
		 *  width, height: size of the button
		 */
		
		this.drawButton(this.buttonImage, text, x, y, width, height, index, active, type);
		
	}
	
	private void drawButton(String text, int x, int y, int width, int height, boolean adaptY, int index, boolean active, int type) {
		/*
		 * Draws a button and adapts its y coordinates if adaptY is set to true.
		 * In:
		 *  text: text displayed in the button
		 *  x, y: coordinates of the button
		 *  width, height: size of the button
		 *  adaptY: if the y coordinate should be adapted
		 */
		
		if (adaptY)
			this.drawButton(text, x, this.yMinO + (y + 1) * this.lineSize, width, height, index, active, type);
		else
			this.drawButton(text, x, y, width, height, index, active, type);
		
	}
	
	private void drawGame() {
		/*
		 * Draws the current state of the game.
		 */
		
		// text
		this.drawInfos();
		
		if (this.game.pausing && ! this.game.generating)
			this.drawPause();
		
		else {
		
			/*
			 * change the smiley's faces randomly
			 * there is a 1/8 chance a smiley will change face every time the canvas is refreshed
			 */
			for (int i = 0; i < this.smileyNbs.length; i++)
				if (i < this.game.smileys.length)
					if (this.game.smileys[i] != null || i == 0 || i == 1) // the first two elements could be useful to update for the sun event
						if (this.rnd.nextInt(8) == 0)
							this.smileyNbs[i] = this.rnd.nextInt(this.smileyLen);
			
			// cells
			this.drawCells();
			
			// blocker
			if (this.game.eh != null) if (this.game.eh.isActive && this.game.eh.blockerOn) {
				int bX = this.game.eh.blockerX; int bY = this.game.eh.blockerY;
				this.drawImage(this.blockerImage,
						bX * this.rap, bY * this.rap, (bX + this.game.eh.blockerWidth) * this.rap, (bY + this.game.eh.blockerHeight) * this.rap,
						0, 0, this.blockerSize, this.blockerSize);
			}
			
			// end-screen
			if (this.game.lost)
				this.drawEndScreen();
		
		}
		
	}
	
	private void drawPause() {
		
		this.graphics.setColor(Color.GRAY);
		this.graphics.fillRect(this.xMin, this.yMin, this.game.grid.width * this.rap, this.game.grid.height * this.rap);
		
		this.graphics.setFont(this.titleFont);
		FontMetrics fm = this.graphics.getFontMetrics();
		this.graphics.setColor(Color.BLACK);
		String str = "Pause";
		this.graphics.drawString(str, this.centerX - fm.stringWidth(str) / 2, this.centerY - this.rap);
		
		this.drawButton("Resume", this.centerX, this.centerY, this.butWidth, this.rap, 4, true, 3);
		this.drawButton("Title screen", this.centerX, this.centerY + this.rap, this.butWidth, this.rap, 5, true, 2);
		
	}
	
	private void drawEndScreen() {
		
		this.drawImage(this.shadowImage, 0, 0, this.game.grid.width * this.rap, this.game.grid.height * this.rap,
				0, 0, 1, 1);
		
		this.graphics.setFont(this.titleFont);
		FontMetrics fm = this.graphics.getFontMetrics();
		String str = "You lost!";
		
		this.graphics.drawString(str, this.centerX - fm.stringWidth(str) / 2, this.centerY - this.rap);
		
		this.drawButton("Title screen", this.centerX, this.centerY + this.rap / 2, this.butWidth, this.rap * 4 / 3,
				5, true, 2);
		
	}
	
	private void drawInfos() {
		/*
		 * Draws the text informations on screen.
		 * These include: the time spent, the level, the score and the number of smileys left.
		 */
		
		this.graphics.setFont(this.font);
		String str;
		int textY;
		int decToBounds = this.rap / 3;
		FontMetrics fm = this.graphics.getFontMetrics();
		
		textY = this.rap * 3 / 2;
		
		// timer
		this.graphics.drawString("time", this.rap + decToBounds, textY);
		str = this.formatTimeSpent(this.game.timeSpent);
		this.graphics.drawString(str, this.game.grid.width / 2 * this.rap - fm.stringWidth(str) - decToBounds, textY);
		
		// score
		this.graphics.drawString("score", (this.game.grid.width / 2 + 2) * this.rap + decToBounds, textY);
		str = String.valueOf(this.game.score);
		this.graphics.drawString(str, (this.game.grid.width + 1) * this.rap - fm.stringWidth(str) - decToBounds, textY);
		
		textY = this.rap * 2;
		
		// level
		this.graphics.drawString("level", this.rap + decToBounds, textY);
		str = String.valueOf(this.game.level);
		this.graphics.drawString(str, this.game.grid.width / 2 * this.rap - fm.stringWidth(str) - decToBounds, textY);
		
		// smileys
		this.graphics.drawString("smileys", (this.game.grid.width / 2 + 2) * this.rap + decToBounds, textY);
		str = String.valueOf(this.game.smileysLeft);
		this.graphics.drawString(str, (this.game.grid.width + 1) * this.rap - fm.stringWidth(str) - decToBounds, textY);
		
		// events
		if (this.game.eh != null) if (this.game.eh.isActive) this.drawEvents();
		
	}
	
	private void drawEvents() {
		/*
		 * Draws the events and the timer bar.
		 * Assumes the (game.eh != null) and (game.eh.isActive) have already been run.
		 */
		
		int counter = this.game.eh.counter;
		if (counter <= 0) counter = 0;
		int state = this.game.eh.state;
		int evts[] = this.game.eh.events;
		
		// represent the events
		
		if (state == 0)
			this.drawCenteredString("no event yet...", this.eventY);
		
		if (state == 2) {
			
			// timer
			this.graphics.drawImage(this.timerImage, this.xMinO, this.yMinO + this.rap, this.xMinO + this.rap / 2, this.yMinO + this.rap * 2,
					0, 0, 8, 16, this);
			int dis = (this.xMaxO - this.xMinO - this.rap) * counter / this.game.eh.counterTot;
			this.graphics.drawImage(this.timerImage, this.xMinO + this.rap / 2, this.yMinO + this.rap,
					this.xMinO + this.rap / 2 + dis, this.yMinO + this.rap * 2,
					8, 0, 40, 16, this);
			this.graphics.drawImage(this.timerImage, this.xMinO + this.rap / 2 + dis, this.yMinO + this.rap,
					this.xMinO + this.rap + dis, this.yMinO + this.rap * 2,
					40, 0, 48, 16, this);
			
			counter = 0;
			
		}
		
		if (state >= 1) {
			
			int ind;
			for (int i = 0; i < 5; i++) {
				ind = evts.length - counter - 3 + i;
				if (ind >= 0 && ind <= evts.length - 1) this.drawEventImage(evts[ind], i);
			}
		
		}
		
	}
	
	private void drawEventImage(int evt, int x) {
		/*
		 * Draws an event's image.
		 * In:
		 *  evt: the event's number (ex: 1 <-> locker)
		 *  x: the event's position on screen (0 for far left, 2 for middle, 4 for far right)
		 */
		
		int cX = this.rap + this.eventCellSize * x + this.eventCellSize / 2;
		int wi = x == 2? this.rap / 2 : this.eventWidth / 2;
		
		this.graphics.drawImage(this.eventImages, cX - wi, this.eventY - wi, cX + wi, this.eventY + wi,
				this.eventSize * evt, 0, this.eventSize * (evt + 1), this.eventSize, this);
		
	}
	
	private void drawCells() {
		/*
		 * Draws the cells, the smileys and the capsule.
		 */
		
		// when the game is first initialized, game.cells and game.smileys are empty
		if (this.game.cells != null && this.game.smileys != null) {
			
			// cells
			for (Cell c: this.game.cells)
				if (c != null)
					this.drawImageObject(0, c.x, c.y, c.color, c.fused, c.tl, c.hori, false);
			
			// smileys
			Smiley s;
			for (int i = 0; i < this.game.smileys.length; i++) {
				s = this.game.smileys[i];
				if (s != null)
					this.drawImageObject(1, s.x, s.y, s.color, i);
			}
			
			// capsule
			
			// temporary conditions
			boolean ghostCond = false; // temporary condition in case the ghost event is active (true if it is)
			if (this.game.eh != null) if (this.game.eh.isActive && this.game.eh.ghostOn)
				ghostCond = true;
			boolean sunCond = false;   // temporary condition in case the sun event is active (true if it is)
			if (this.game.eh != null) if (this.game.eh.isActive && this.game.eh.sunOn)
				sunCond = true;
			// to still change the smiley's faces if the sun event is on, we draw the same face as the smileys in 0 and 1
			
			// drawing it
			if (this.game.curCap != null && (! ghostCond) && (! this.game.generating)) {
				
				// the first cell might be out of the grid
				if (this.game.curCap.y != - 1) {
					if (sunCond)
						this.drawImageObject(1, this.game.curCap.x, this.game.curCap.y, this.game.curCap.c0, 0);
					else
						this.drawImageObject(0, this.game.curCap.x, this.game.curCap.y, this.game.curCap.c0,
								true, true, this.game.curCap.hori, false);
				}
				
				// coordinates of the second cell
				int cap2X = this.game.curCap.x; int cap2Y = this.game.curCap.y;
				if (this.game.curCap.hori) cap2X += 1;
				else                       cap2Y += 1;
				if (sunCond)
					this.drawImageObject(1, cap2X, cap2Y, this.game.curCap.c1, 1);
				else
					this.drawImageObject(0, cap2X, cap2Y, this.game.curCap.c1,
							true, false, this.game.curCap.hori, false);
				
				// landing point
				int capLY = this.game.curCap.landingPoint;
				if (! this.game.curCap.hori) capLY += 1;
				if (sunCond) {
					this.drawImageObject(0, this.game.curCap.x, this.game.curCap.landingPoint, 9,
							false, false, false, false);
					this.drawImageObject(0, cap2X, capLY, 9,
							false, false, false, false);
				} else {
					this.drawImageObject(0, this.game.curCap.x, this.game.curCap.landingPoint, 9,
							true, true, this.game.curCap.hori, false);
					this.drawImageObject(0, cap2X, capLY, 9,
							true, false, this.game.curCap.hori, false);
				}
				
			}
			
			// cells that just died
			if (this.game.deletingCells && this.game.lastDeaths != null)
				for (int[] i: this.game.lastDeaths) if (i[2] != 0)
					this.drawImageObject(i[3], i[0], i[1], i[2], false, false, false, true);
			
			// next capsule
			if (this.game.upComingC0 != 0 && this.game.upComingC1 != 0) {
				this.drawImageObject(0, this.game.grid.width / 2 - 1, - 3, this.game.upComingC0,
						true, true, true, false);
				this.drawImageObject(0, this.game.grid.width / 2, - 3, this.game.upComingC1,
						true, false, true, false);
			}
			
			// stones
			if (this.game.eh != null) if (this.game.eh.isActive) if (this.game.eh.stones != null)
				for (Stone st: this.game.eh.stones) if (st != null)
					this.drawImageObject(2, st.x, st.y, 1, false, false, false, false);
		
		}
		
	}
	
	private void drawImageObject(int type, int x, int y, int color, int ind, boolean fused, boolean tl, boolean hori, boolean deleting) {
		/*
		 * Draws an object based on its type and its attributes.
		 * Types:
		 *  0: cell
		 *  1: smiley
		 *  2: stone
		 * A smiley is drawn from a cell that isn't fused and has a random expression on.
		 * The color is obtained by searching for the element in index: color - 2 in this.cellImages.
		 * In:
		 *  type: explained above
		 *  x, y: coordinates
		 *  color: cell's color
		 *  ind: index in the list (for smileys)
		 *  fused: is fused?
		 *  tl: is top-left?
		 *  hori: is horizontal?
		 *  deleting: is getting deleted?
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
		
		switch (type) {
		
		// cell
		case 0:
			
			/*
			 * Drawing and linking to the other cells
			 * Old version:
			 *  There are two images for each color: a vertical rectangle and an horizontal rectangle. The one to use is determined from hori.
			 *  Based on the position of the cell compared to its cell-friend, the canvas coordinates are adapted then the source image's.
			 * New version:
			 *  To give my-self more flexibility and considering I am now importing every image at once, there are now five sprites for each
			 *  color, two for the vertical capsule, two for the horizontal capsule and one for a lone cell.
			 */
			sx1 = (color - 2) * this.imageSize;
			sy1 = (fused? 1 : 0) * (1 + (hori? 0 : 2) + (tl? 0 : 1)) * this.imageSize; // determines the row in cells.png
			sx2 = sx1 + this.imageSize; sy2 = sy1 + this.imageSize;
			// draw
			this.drawImage(this.cellImages,
					x1, y1, x2, y2,
					sx1, sy1, sx2, sy2);
			
			// add clouds if the cell is deleting
			int nsx = this.rnd.nextInt(this.cloudLen);
			if (deleting)
				this.drawImage(this.cloudImages,
						x1, y1, x2, y2,
						nsx * this.imageSize, 0, (nsx + 1) * this.imageSize, this.imageSize);
			
			break;
		
		// smiley
		case 1:
			
			this.drawImageObject(0, x, y, color, false, false, false, deleting);
			
			Image source; int face;
			if (deleting) {
				source = this.smileyKilledImages;
				face   = this.rnd.nextInt(this.killsLen);
			} else {
				source = this.smileyImages;
				face = smileyNbs[ind];
			}
			
			sx1 = face * this.imageSize; sx2 = sx1 + this.imageSize;
			sy1 = 0; sy2 = this.imageSize;
			
			this.drawImage(source,
					x1, y1, x2, y2,
					sx1, sy1, sx2, sy2);
			
			break;
		
		// stone
		case 2:
			
			sx1 = this.rnd.nextInt(this.stoneLen) * this.imageSize; sx2 = sx1 + this.imageSize;
			sy1 = 0; sy2 = this.imageSize;
			
			this.drawImage(this.stoneImages,
					x1, y1, x2, y2,
					sx1, sy1, sx2, sy2);
			
			break;
			
		}
		
	}
	
	private void drawImageObject(int type, int x, int y, int color, boolean fused, boolean tl, boolean hori, boolean deleting) {
		/*
		 * drawImageObject with ind set to 0. Usually because it isn't useful.
		 * In:
		 *  look up drawImageObject's documentation
		 */
		
		this.drawImageObject(type, x, y, color, 0, fused, tl, hori, deleting);
		
	}
	
	private void drawImageObject(int type, int x, int y, int color, int ind) {
		/*
		 * drawImageObject with fused, tl, hori and deleting set to false. Usually used for smileys.
		 * In:
		 *  look up drawImageObject's documentation
		 */
		
		this.drawImageObject(type, x, y, color, ind, false, false, false, false);
		
	}
	
	private void drawImage(Image img, int x1, int y1, int x2, int y2, int sx1, int sy1, int sx2, int sy2) {
		
		this.graphics.drawImage(img,
				x1 + this.xMin, y1 + this.yMin,
				x2 + this.xMin, y2 + this.yMin,
				sx1, sy1, sx2, sy2, this);
		
	}
	
	private Image importImage(String src) throws IOException {
		/*
		 * Returns the corresponding image.
		 * In:
		 *  src: name of the file (with or without directories before)
		 */
		
		return ImageIO.read(new File("images/" + src));
		
	}

	private String formatTimeSpent(float timeSpent) {
		/*
		 * Returns a formatted version of the timer.
		 * Model: hh:mm:ss.
		 * In:
		 *  timeSpent: time spent, in milliseconds
		 */
		
		String toString = "";
		
		int tempI;
		int s = Math.round(timeSpent / 1000);
		
		tempI = s % 60;
		toString = ":" + (tempI > 10? "" : "0") + String.valueOf(tempI);
		
		tempI = s / 60;
		toString = ":" + (tempI > 10? "" : "0") + String.valueOf(tempI) + toString;
		
		tempI = s / 3600;
		toString =       (tempI > 10? "" : "0") + String.valueOf(tempI) + toString;
		
		/*
		String temp;
		for (double i = 0; i < 3; i++) {
			temp = String.valueOf(Math.round((timeSpent % (Math.pow(10, i + 5))) / Math.pow(10, i + 3)));
			if (i != 0) toString = ":" + toString;
			temp = temp.length() == 1? "0" + temp : temp;
			toString = temp + toString;
		}
		*/
		
		return toString;
		
	}
	
	private FontMetrics setAndGetFont(Font f) {
		/*
		 * Set the font to f and returns the font metrics.
		 * In:
		 *  f: font to set
		 */
		
		this.graphics.setFont(f);
		return this.graphics.getFontMetrics();
		
	}
	
	public void reset() {
		/*
		 * Resets the canvas.
		 */
		
		if (this.graphics != null)
			this.graphics.clearRect(0, 0, this.width, this.height);
		
		this.smileyNbs = new int[this.game.numberSmileys(this.game.lvMax)];
		for (int i = 0; i < this.smileyNbs.length; i++)
			this.smileyNbs[i] = this.rnd.nextInt(this.smileyLen);
		
	}
	
	public void stop() {
		/*
		 * Stops the canvas. Called when the game is stopped (window closing).
		 */
		
		this.frame.stop();
		
	}
	
	public void setButtonStates(String state) {
		
		// exit button (always active)
		this.buttons[0][0] = 1;
		
		switch (state) {
		
		case "game":
			
			for (int i = 1; i < this.butLen; i++)
				this.buttons[i][0] = 0;
			
			break;
		
		case "menu":
			
			for (int i = 1; i < 4; i++)
				this.buttons[i][0] = 1;
			this.buttons[4][0] = 0;
			this.buttons[5][0] = 0;
			for (int i = 6; i < this.butLen; i++)
				this.buttons[i][0] = 1;
			
			break;
		
		case "titlescreen":
			
			for (int i = 1; i < this.butLen; i++)
				this.buttons[i][0] = 0;
			this.buttons[5][0] = 1;
			
			break;
		
		case "pause":
			
			for (int i = 1; i < this.butLen; i++)
				this.buttons[i][0] = 0;
			this.buttons[4][0] = 1;
			this.buttons[5][0] = 1;
			
			break;
		
		}
		
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

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		/*
		 * Detects where the mouse was clicked, and if it corresponds to a button, some functions are performered in the game object.
		 */
		
		int x, y, addedData;
		x = e.getX(); y = e.getY();
		
		for (int[] i: this.buttons)
			// button is active
			if (i[0] == 1)
				// coordinates match
				if (i[1] < x && x < i[3] && i[2] < y && y < i[4]) {
					// send it
					addedData = 0;
					if (i[5] == 1)
						addedData = (i[1] - this.boundSize) / this.lvlWidth;
					else if (i[5] == 5)
						addedData = i[1] < this.centerX? 0 : 1;
					this.game.buttonPressed(i[5], addedData);
				}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
	
}
