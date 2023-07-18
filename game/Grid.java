package game;

public class Grid {

	public int[][] g; // 0 empty, 1 stone, 2 yellow, 3 blue, 4 purple, 5 green
	public int width, height;
	
	public Grid(int width, int height) {
		
		this.width = width; this.height = height;
		// this.g = new int[this.width][this.height]; // g[x][y]; common values: 8 and 20
		this.reset();
		
	}
	
	public int[][] detect() {
		// returns an array of every coordinate of cells (or smileys) to kill
		
		int[][] fin, vert, hori, dia1, dia2;
		
		vert = this.detect(this.g);
		
		hori = this.detect(this.reverse());
		// adapt the coordinates
		int s;
		for (int i = 0; i < hori.length; i++) {
			s = hori[i][0];
			hori[i][0] = hori[i][1];
			hori[i][1] = s;
		}
		
		dia1 = this.detect(this.shift(0, 1));
		// adapt
		for (int i = 0; i < dia1.length; i++)
			dia1[i][0] -= dia1[i][1];
		
		dia2 = this.detect(this.shift(this.height - 1, - 1));
		// adapt
		for (int i = 0; i < dia2.length; i++)
			dia2[i][0] -= this.height - dia2[i][1] - 1;
		
		// combine
		fin = new int[vert.length + hori.length + dia1.length + dia2.length][2];
		for (int i = 0; i < vert.length; i++) fin[i]                                           = vert[i];
		for (int i = 0; i < hori.length; i++) fin[vert.length + i]                             = hori[i];
		for (int i = 0; i < dia1.length; i++) fin[vert.length + hori.length + i]               = dia1[i];
		for (int i = 0; i < dia2.length; i++) fin[vert.length + hori.length + dia1.length + i] = dia2[i];
		
		return fin;
		
	}
	
	private int[][] detect(int[][] grid) {
		
		// counting how many cells will die
		int cTot = 0;
		int[] res = new int[] {0, 0, 1};
		while (res[2] != 0) {
			res = this.detectLine(grid, res[0], res[1]);
			cTot += res[2];
		}
		
		// creating an array of the locations of the cells that will die
		int[][] dead = new int[cTot][2];
		int lastInd = 0;
		res = new int[] {0, 0, 1};
		while (res[2] != 0) {
			res = this.detectLine(grid, res[0], res[1]);
			for (int i = 0; i < res[2]; i++) {
				dead[lastInd] = new int[] {res[0], res[1] - i}; // get the entire line
				lastInd += 1;
			}
		}
		
		return dead;
		
		
	}
	
	private int[] detectLine(int[][] grid, int iIni, int jIni) {
		// {x, y, length}
		
		int remCol = 0;
		int c = 0;
		for (int i = iIni; i < grid.length; i++)
			for (int j = 0; j < grid[0].length; j++) {
				if (i == iIni) if (j < jIni) continue;
				if (grid[i][j] >= 2 && grid[i][j] == remCol)
					c += 1;
				else if (c >= 4)
					return new int[] {previousX(i, j), this.previousY(i, j), c};
				else {
					remCol = grid[i][j]; c = 1;
				}
			}
		
		return new int[] {0, 0, 0};
		
	}
	
	private int[][] reverse() {
		
		int[][] rev = new int[this.height][this.width];
		for (int i = 0; i < this.height; i++)
			for (int j = 0; j < this.width; j++)
				rev[i][j] = this.g[j][i];
		return rev;
		
	}
	
	public int[][] shift(int start, int sign) {
		// shift(0, 1) & shift(this.width + 1, - 1)
		// get back the initial x coordinate: xi = xf - start - sign * y
		
		int[][] shf = new int[this.width + this.height - 1][this.height];
		for (int i = 0; i < this.width; i++) for (int j = 0; j < this.height; j++)
			shf[start + i + sign * j][j] = this.g[i][j];
		
		return shf;
		
	}
	
	private int previousX(int x, int y) {
		// if a loop over the Y is made inside a loop over the X, this returns the previous X value
		
		return y == 0? x - 1 : x;
		
	}
	
	private int previousY(int x, int y) {
		// same
		
		return y == 0? this.height - 1 : y - 1;
		
	}
	
	public boolean isStuck() {
		
		return (this.g[this.width / 2 - 1][0] != 0 || this.g[this.width / 2][0] != 0);
		
	}
	
	public void reset() {
		
		this.g = new int[this.width][this.height];
		
	}
	
	public void consoleDisplay() {
		
		this.consoleDisplay(true);
		
	}
	
	public void consoleDisplay(boolean newLine) {
		
		String t = this.format("", "\n", " ");		
		System.out.println(t);
		
		if (newLine) System.out.println();
		
	}
	
	private String format(String seph, String sepv, String zero) {
		
		String t = " ";
		
		for (int i = 0; i < this.width; i++)
			t += "-";
		t += "\n";
		
		for (int i = 0; i < this.g[0].length; i++) {
			t += "|";
			for (int j = 0; j < this.g.length; j++)
				t += (this.g[j][i] == 0? zero : Integer.toString(this.g[j][i])) + seph;
			t += "|" + sepv;
		}
		
		t += " ";
		for (int i = 0; i < this.width; i++)
			t += "-";
		
		return t;
		
	}

}
