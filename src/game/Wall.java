package game;

public class Wall {
	private float x1, y1, x2, y2;
	private float nx, ny, wx, wy;

	public Wall(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		calculateNormal();
	}

	private void calculateNormal() {
		nx = y1-y2;
		ny = x2-x1;
		wx = x2-x1;
		wy = y2-y1;

		double length = Math.sqrt(nx*nx + ny*ny);
		if (length == 0) throw new RuntimeException(String.format("Wall size may not be 0! %f/%f %f/%f", x1, y1, x2, y2));
		ny /= length;
		nx /= length;
		wx /= length;
		wy /= length;
	}

	public float getX1() {
		return x1;
	}

	public float getY1() {
		return y1;
	}

	public float getX2() {
		return x2;
	}

	public float getY2() {
		return y2;
	}

	public float getNx() {
		return nx;
	}

	public float getNy() {
		return ny;
	}

	public float getWx() {
		return wx;
	}

	public float getWy() {
		return wy;
	}
}
