package game;

public class MathUtil {

	/**
	 * @param x coordinate of the point
	 * @param y coordinate of the point
	 * @param g1 x coordinate of a point on the wall
	 * @param g2 y coordinate of a point on the wall
	 * @param n1 x coordinate of the normalized normal vector
	 * @param n2 y coordinate of the normalized normal vector
	 * @return distance of a point to a wall (negative if the point is on the other side)
	 */
	public static float distance(float x, float y, float g1, float g2, float n1, float n2) {
		return n1 * (x - g1) + n2 * (y - g2);
	}
}
