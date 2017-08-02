package game;

public class Location {
	public float x;
	public float y;

	public Location(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return String.format("(%d | %d)", x, y);
	}
}
