package game;

public class MathUtil {

	/**
	 * @param x Koordinate vom Punkt
	 * @param y Koordinate vom Punkt
	 * @param g1 x Koordinate vom Stützvektor der Gerade
	 * @param g2 y Koordinate vom Stützvektor der Gerade
	 * @param n1 x Koordinate vom normalisierten Normalvektor der Gerade
	 * @param n2 y Koordinate vom normalisierten Normalvektor der Gerade
	 * @return den Abstand von einem Punkt zu einer Ebene (wird negativ wenn der Punkt auf der anderen Seite der Gerade liegt)
	 */
	public static float distance(float x, float y, float g1, float g2, float n1, float n2) {
		return n1 * (x - g1) + n2 * (y - g2);
	}
}
