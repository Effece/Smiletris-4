/*
 * Two-dimensions array that represents the current game state.
 * Used as follows: Grid.g[x][y] (first coordinate is x, second is y).
 * Also contains functions to analyze the board.
 * 
 * The program holds in the same time a grid with just numbers for each case (this object) and an array of
 * every cell (and one of every smiley). Every time a cell moves or is killed, this object gets updated.
 * However this doesn't keep track of whether a case contains a cell or a smiley.
 * 
 * Elements:
 *  0: empty
 *  1: stone
 *  2: yellow
 *  3: blue
 *  4: purple
 *  5: green
 */

package game;

public class Grid {

	public int[][] g;
	public int width, height;
	
	public Grid(int width, int height) {
		
		this.width = width; this.height = height;
		this.reset();
		
	}
	
	public int[][] detect(int alignLength) {
		/*
		 * Returns an array of every coordinate of cells and smileys to kill.
		 */
		
		int[][] fin, vert, hori, dia1, dia2;
		/*
		 * fin: combination of other arrays
		 * vert: every cell detected vertically
		 * hori: every cell detected horizontally
		 * dia1: every cell detected in a certain type of diagonals
		 * dia2: every cell detected in the other type of diagonals
		 */
		
		vert = this.detect(this.g, alignLength);
		
		hori = this.detect(this.reverse(), alignLength);
		// adapt the coordinates
		int s;
		for (int i = 0; i < hori.length; i++) {
			s = hori[i][0];
			hori[i][0] = hori[i][1];
			hori[i][1] = s;
		}
		
		dia1 = this.detect(this.shift(0, 1), alignLength);
		// adapt the coordinates
		for (int i = 0; i < dia1.length; i++)
			dia1[i][0] -= dia1[i][1];
		
		dia2 = this.detect(this.shift(this.height - 1, - 1), alignLength);
		// adapt the coordinates
		for (int i = 0; i < dia2.length; i++)
			dia2[i][0] -= this.height - dia2[i][1] - 1;
		
		// combine everything
		fin = new int[vert.length + hori.length + dia1.length + dia2.length][2];
		for (int i = 0; i < vert.length; i++) fin[i]                                           = vert[i];
		for (int i = 0; i < hori.length; i++) fin[vert.length + i]                             = hori[i];
		for (int i = 0; i < dia1.length; i++) fin[vert.length + hori.length + i]               = dia1[i];
		for (int i = 0; i < dia2.length; i++) fin[vert.length + hori.length + dia1.length + i] = dia2[i];
		
		return fin;
		
	}
	
	private int[][] detect(int[][] grid, int alignLength) {
		/*
		 * Detect alignments of a certain length in grid.
		 * This doesn't consider horizontal and diagonal lines. This only detects lines in one array, for
		 * example:
		 *  { 0, 0, 3, 3, 2, 0, 0 }
		 *  { 0, 0, 3, 4, 4, 4, 4 } <- the 4, 4, 4, 4 alignment is detected
		 *  { 0, 2, 3, 2, 2, 3, 4 }
		 *  { 2, 4, 3, 2, 3, 3, 3 }
		 *          ^ the 3, 3, 3, 3 alignment isn't
		 * In:
		 *  grid: grid to analyze
		 *  alignLength: number of cells to align to detect a line
		 */
		
		// counting how many cells will die
		int cTot = 0;
		int[] res = new int[] {0, 0, 1};
		while (res[2] != 0) {
			res = this.detectLine(grid, res[0], res[1], alignLength);
			cTot += res[2];
		}
		
		// creating an array of the locations of the cells that will die
		int[][] dead = new int[cTot][2];
		int lastInd = 0;
		res = new int[] {0, 0, 1};
		while (res[2] != 0) {
			res = this.detectLine(grid, res[0], res[1], alignLength);
			// get the entire line
			for (int i = 0; i < res[2]; i++) {
				dead[lastInd] = new int[] {res[0], res[1] - i};
				lastInd += 1;
			}
		}
		
		return dead;
		
		
	}
	
	private int[] detectLine(int[][] grid, int iIni, int jIni, int alignLength) {
		/*
		 * Detects a line in grid. Returns the coordinates of the last cell and the length of the alignment,
		 * like this: {x, y, length}.
		 * It detects the first line with x >= iIni and y >= jIni and only returns one line. It is made to be
		 * called many times in a row, detecting the first line in the first call, then the next line after
		 * the first one (with iIni and jIni being the coordinates of the last cell of the first call) the
		 * second call, until there is no line anymore.
		 * For example, on one line:
		 *  0 0 3 2 2 2 2 4 4 3 4 4 4 4 4 2 3 2 2
		 *  1st call    ^               ^
		 *              |               | 2nd return: {x, 14, 5}
		 *              | 1st return: {x, 6, 4}
		 *                 2nd call: starts from (x, 6)
		 * The function remembers the last color it saw (remCol) and how many times it appeared (c).
		 * When the color changes:
		 * - if the counter is above alignLength, the function returns the coordinates and the length
		 * - otherwise, the counter is reset to 0 and the remembered color is set to the last color
		 * In:
		 *  grid: grid to analyze
		 *  iIni: x coordinate to start from
		 *  jIni: y coordinate to start from
		 *  alignLength: number of cells to align
		 */
		
		int remCol = 0;
		int c = 0;
		for (int i = iIni; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (i == iIni) if (j < jIni) continue;
				if (grid[i][j] >= 2 && grid[i][j] == remCol)
					c += 1;
				else if (c >= alignLength)
					return new int[] {i, j - 1, c}; //{previousX(i, j), this.previousY(i, j), c};
				else {
					remCol = grid[i][j]; c = 1;
				}
			}
			if (c >= alignLength)
				return new int[] {i, grid[0].length - 1, c};
			else {
				remCol = 0; c = 0;
			}
		}
		
		if (c >= alignLength)
			return new int[] {grid.length - 1, grid[0].length - 1, c};
		
		// if nothing was found, the function returns a length of 0 which is impossible
		return new int[] {0, 0, 0};
		
	}
	
	private int[][] reverse(int[][] grid) {
		/*
		 * Reverses grid, making the rows the columns and the columns the rows.
		 * For example:
		 *  0 1 2     0 3 6
		 *  3 4 5 --> 1 4 7
		 *  6 7 8     2 5 8
		 *  In:
		 *   grid: grid to reverse
		 */
		
		int[][] rev = new int[this.height][this.width];
		
		for (int i = 0; i < this.height; i++)
			for (int j = 0; j < this.width; j++)
				rev[i][j] = grid[j][i];
		
		return rev;
		
	}
	
	private int[][] reverse() {
		/*
		 * Reverses the grid.
		 */
		
		return this.reverse(this.g);
		
	}
	
	private int[][] shift(int[][] grid, int start, int sign) {
		/*
		 * Shifts grid, by moving each line a certain number of cases to the right or to the left depending
		 * of sign. Used to detect diagonals.
		 * For example:
		 *  0 1 2     0 1 2 0 0
		 *  3 4 5 --> 0 3 4 5 0
		 *  6 7 8     0 0 6 7 8
		 * There are two ways to shift grid, to detect both types of diagonals:
		 * - shift(grid, 0, 1)
		 * - shift(grid, this.width + 1, - 1)
		 * To get back the initial coordinates based on the shifted ones, apply:
		 *  xi = xf - start - sign * y
		 * In:
		 *  grid: grid to shift
		 *  start: position from which the coordinates are recalculated
		 *  sign: direction in which the cells are moved
		 */
		
		int[][] shf = new int[this.width + this.height - 1][this.height];
		
		for (int i = 0; i < this.width; i++) for (int j = 0; j < this.height; j++)
			shf[start + i + sign * j][j] = grid[i][j];
		
		return shf;
		
	}
	
	private int[][] shift(int start, int sign) {
		/*
		 * Shifts the grid.
		 * See further explanation on the documentation of the shift(int[][], int, int) function.
		 * In:
		 *  start: position from which the coordinates are recalculated
		 *  sign: direction in which the cells are moved
		 */
		
		return this.shift(this.g, start, sign);
		
	}
	
	public boolean isStuck(int nextCapX, int nextCapY) {
		/*
		 * Returns if the game is stuck. Does so by checking if the location where the next capsule is
		 * generated is filled.
		 * By default, nextCapX = this.width / 2 - 1 and nextCapY = 0.
		 * In:
		 *  nextCapX: lefter x coordinate of the next capsule
		 *  nextCapY: y coordinate of the next capsule
		 */
		
		return (this.g[nextCapX][nextCapY] != 0 || this.g[nextCapX + 1][nextCapY] != 0);
		
	}
	
	public void reset() {
		/*
		 * Empties out the grid.
		 */
		
		this.g = new int[this.width][this.height];
		
	}
	
	public void consoleDisplay() {
		/*
		 * Prints the grid inside the console, with a line seperator after.
		 */
		
		this.consoleDisplay(true);
		
	}
	
	public void consoleDisplay(boolean newLine) {
		/*
		 * Prints the grid inside the console.
		 * In:
		 *  newLine: if true, adds an empty line separator
		 */
		
		String t = this.format("", "\n", " ");		
		System.out.println(t);
		
		if (newLine) System.out.println();
		
	}
	
	private String format(String seph, String sepv, String zero) {
		/*
		 * Sets up a string version of the grid.
		 * In:
		 *  seph: horizontal seperator (between two elements on the same line)
		 *  sepv: vertical separator (bewteen two elements on the same column)
		 *  zero: way of representing the 0
		 */
		
		String t = " ";
		
		// top border
		for (int i = 0; i < this.width; i++)
			t += "-";
		t += "\n";
		
		// elements
		for (int i = 0; i < this.g[0].length; i++) {
			t += "|"; // side border
			for (int j = 0; j < this.g.length; j++)
				t += (this.g[j][i] == 0? zero : Integer.toString(this.g[j][i])) + seph;
			t += "|" + sepv;
		}
		
		// bottom border
		t += " ";
		for (int i = 0; i < this.width; i++)
			t += "-";
		
		return t;
		
	}

}
