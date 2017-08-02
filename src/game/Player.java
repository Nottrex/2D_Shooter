package game;

import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Player {
	public static final float acceleration = 0.001f;
	public static final float reibung = 0.9f;
	public static final float stopping = 0.9f;
	public static final long TIME = 10;

	private float x, y;
	private float vx, vy;
	private float mx, my;
	private float radius;

	private Color c;

	public Player() {
		this(new Color(new Random().nextInt()));
	}

	public Player(Color indicator) {
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;
		radius = 0.05f;

		c = indicator;
	}

	long lastTime = 0;
	public void update(long time, List<Wall> walls) {
		lastTime += time;
		while (lastTime > TIME) {
			vx += mx * acceleration;
			vy += my * acceleration;
			vx *= reibung;
			vy *= reibung;
			if (mx == 0 && my == 0) {
				vx *= stopping;
				vy *= stopping;
			}

			lastTime -= TIME;

			move(vx, vy, walls);
		}
	}

	private void move(float vx, float vy, List<Wall> walls) {
		/*if (Math.abs(vx) <= minMovement)
			vx = 0;
		if (Math.abs(vy) <= minMovement)
			vy = 0;
		if (Math.abs(vx) <= minMovement && Math.abs(vy) <= minMovement)
			return;
*/

		for (Wall w: walls) {
			float distanceStart = MathUtil.distance(x, y, w.getX1(), w.getY1(), w.getNx(), w.getNy());
			float distanceEnd = MathUtil.distance(x+vx, y+vy, w.getX1(), w.getY1(), w.getNx(), w.getNy());

			if(distanceStart < 0) {
				continue;
			}

			if (Math.abs(distanceEnd) < radius) {
				float distanceA = MathUtil.distance(x+vx, y+vy, w.getX1(), w.getY1(), w.getWx(), w.getWy());
				float distanceB = MathUtil.distance(x+vx, y+vy, w.getX2(), w.getY2(), w.getWx(), w.getWy());

				float normalX = w.getNx();
				float normalY = w.getNy();

				if (distanceA * distanceB > 0) {
					distanceA = Math.abs(distanceA);
					distanceB = Math.abs(distanceB);
					if (distanceA < distanceB) {
						distanceEnd = (float) Math.sqrt((x+vx-w.getX1())*(x+vx-w.getX1()) + (y+vy-w.getY1())*(y+vy-w.getY1()));

						normalX = ((x+vx)-w.getX1());
						normalY = ((y+vy)-w.getY1());
						float length = (float) Math.sqrt(normalX * normalX + normalY * normalY);
						normalX /= length;
						normalY /= length;
					} else {
						distanceEnd = (float) Math.sqrt((x+vx-w.getX2())*(x+vx-w.getX2()) + (y+vy-w.getY2())*(y+vy-w.getY2()));

						normalX = ((x+vx)-w.getX2());
						normalY = ((y+vy)-w.getY2());
						float length = (float) Math.sqrt(normalX * normalX + normalY * normalY);
						normalX /= length;
						normalY /= length;
					}
				}

				if (distanceEnd < radius) {
					vx += (radius-Math.abs(distanceEnd))*normalX;
					vy += (radius-Math.abs(distanceEnd))*normalY;

					move(vx, vy, walls);
					return;
				}
			} else if (distanceEnd < 0) {
				//TODO: Fix wall bugging when to fast
				// (x, y)  + r * (vx, vy) = (w.x, w.y) + s * (w.mx, w.my)

				// r*vx + s*(-w.mx)  =  w.x - x
				// r*vy + s*(-w.my)  =  w.y - y

				/*
				if (vx == 0 && w.mx == 0) continue;
				if (vy == 0 && w.my == 0) continue;
				if (vx != 0 && vy != 0 && Math.abs(w.mx / vx - w.my / vy) < 0.00001f) continue;

				float s, r;

				if (vx == 0) {
					if (Math.abs(w.mx) == 0)
						continue;

					s = (w.x-x) / (-w.mx);
					r = ((w.y-y)-s*(-w.my))/vy;
				} else if (vy == 0) {
					if (Math.abs(w.my) == 0)
						continue;

					s = (w.y-y) / (-w.my);
					r = ((w.x-x)-s*(-w.mx))/vx;
				} else {

				}*/
			}
		}

		x += vx;
		y += vy;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getVX() {
		return vx;
	}

	public float getVY() {
		return vy;
	}

	public void updateWalkingDirection(float mx, float my) {
		this.mx = mx;
		this.my = my;
	}

	public float velocity(float time) {
		return time;
	}

	public float invertVelocity(float velocity) {
		return velocity;
	}

	public float getRadius() {
		return radius;
	}

	public Color getColor() {
		return c;
	}
}
