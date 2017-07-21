package game;

public class GameMap {

	private int[][] map;

	public GameMap(int width, int height) {
		map = new int[width][height];

		for(int i = 0; i < width; i++) {
			map[i][0] = 1;
			map[i][height-1] = 1;
		}

		for(int i = 0; i < height; i++) {
			map[0][i] = 1;
			map[width-1][i] = 1;
		}
	}

	public int getWidth() {
		return getWidth();
	}

	public int getHeight() {
		return getHeight();
	}

	public boolean isSolid(int x, int y) {
		return map[x][y] == 1;
	}

	public int getTile(int x, int y) {
		return map[x][y];
	}
}