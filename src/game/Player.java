package game;

import java.awt.Color;
import java.util.List;
import java.util.Random;

public class Player {
	private static final float MASS = 20;						//mass of the player (in kg)
	private static final float FORCE_PER_INPUT = 12 * MASS;  	//force that will be applied to the player when input directions are given

	private static final float MAX_SPEED = 10;

	//location
	private float x, y;			//position of the player (in m)
	private float radius;		//radius of the hitbox of the player (in m)

	//movement
	private float mx, my;		//input directions for the player (in 1)
	private float vx, vy;		//movement of the player (in m/s)
	private float fx, fy;		//force that pushes the player in this tick (in N)
	private float nfx, nfy;		//force that will be relevant for the next tick (in N)

	private Color c;

	public Player() {
		this(new Color(new Random().nextInt()));
	}

	public Player(Color indicator) {
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;
		nfx = 0;
		nfy = 0;
		radius = 0.5f;

		c = indicator;
	}

	public void update(long time, List<Wall> walls) {
		updateForce();

		//acceleration for this tick (in m/(s*s))
		float ax = fx / MASS;
		float ay = fy / MASS;

		//apply acceleration
		vx += ax*time/1000.0f;
		vy += ay*time/1000.0f;

		if (Math.sqrt(vx*vx+vy*vy) > MAX_SPEED) {
			double speed = Math.sqrt(vx*vx+vy*vy);
			vx /= speed/10;
			vy /= speed/10;
		}

		//calculate movement
		move(time, vx * time/1000.0f, vy * time/1000.0f, walls);
	}

	private void updateForce() {
		fx = nfx;
		fy = nfy;

		//input force
		nfx = FORCE_PER_INPUT * mx;
		nfy = FORCE_PER_INPUT * my;

		//gravity
		nfy -= 9.81f*MASS;

		//air friction
		//F = 0.5 * area that has friction * air friction constant of the shape * air pressure * v * v
		if (vx*vx + vy*vy != 0) {
			float l = (float) Math.sqrt(vx*vx+vy*vy);
			float fa = 0.5f * (2*radius) * 0.4f * 1.2f * l * l;
			nfx -= fa * vx / l;
			nfy -= fa * vy / l;
		}
	}

	private void move(long time, float dx, float dy, List<Wall> walls) {
		/*if (Math.abs(vx) <= minMovement)
			vx = 0;
		if (Math.abs(vy) <= minMovement)
			vy = 0;
		if (Math.abs(vx) <= minMovement && Math.abs(vy) <= minMovement)
			return;
*/

		for (Wall w: walls) {	//check collision with every wall
			//distance to the line of the wall at the beginning
			float distanceStart = MathUtil.distance(x, y, w.getX1(), w.getY1(), w.getNx(), w.getNy());

			//if the player is on the wrong side of the wall skip this
			if(distanceStart < 0) {
				continue;
			}

			//distance to the line of the wall at the end
			float distanceEnd = MathUtil.distance(x+dx, y+dy, w.getX1(), w.getY1(), w.getNx(), w.getNy());

			//check if collision might be possible
			if (Math.abs(distanceEnd) < radius) {
				//distance to the orthogonal lines through the corners of the wall (at the end)
				float distanceA = MathUtil.distance(x+dx, y+dy, w.getX1(), w.getY1(), w.getWx(), w.getWy());
				float distanceB = MathUtil.distance(x+dx, y+dy, w.getX2(), w.getY2(), w.getWx(), w.getWy());

				//direction the player is pushed back (normalized)
				float normalX = w.getNx();
				float normalY = w.getNy();

				//if the end position is next to the wall
				if (distanceA * distanceB > 0) {
					distanceA = Math.abs(distanceA);
					distanceB = Math.abs(distanceB);

					//check which corner of the wall is near the end position
					if (distanceA < distanceB) {
						//calculate distance to the corner
						distanceEnd = (float) Math.sqrt((x+dx-w.getX1())*(x+dx-w.getX1()) + (y+dy-w.getY1())*(y+dy-w.getY1()));

						//change the push direction
						normalX = ((x+dx)-w.getX1());
						normalY = ((y+dy)-w.getY1());
						float length = (float) Math.sqrt(normalX * normalX + normalY * normalY);
						normalX /= length;
						normalY /= length;
					} else {
						distanceEnd = (float) Math.sqrt((x+dx-w.getX2())*(x+dx-w.getX2()) + (y+dy-w.getY2())*(y+dy-w.getY2()));

						normalX = ((x+dx)-w.getX2());
						normalY = ((y+dy)-w.getY2());
						float length = (float) Math.sqrt(normalX * normalX + normalY * normalY);
						normalX /= length;
						normalY /= length;
					}
				}

				//absolute value of distanceEnd is the distance of the center to the wall

				if (distanceEnd < radius) {
					//collision with the wall
					//distance needed to stop collision
					float ax = (radius-Math.abs(distanceEnd))*normalX;
					float ay = (radius-Math.abs(distanceEnd))*normalY;

					dx += ax;
					dy += ay;

					//Force on impact that stops the collision
					nfx += ((ax/(time/1000f))/(time/1000f))*MASS;
					nfy += ((ay/(time/1000f))/(time/1000f))*MASS;

					if (dx*dx +dy*dy != 0) {
						double l = Math.sqrt(dx*dx + dy*dy);

						//Force into the wall
						float ffx = Math.abs(((ax/(time/1000f))/(time/1000f)) * MASS);
						float ffy = Math.abs(((ay/(time/1000f))/(time/1000f)) * MASS);

						//Force along the wall
						float fdx = ((dx/(time/1000f))/(time/1000f)) * MASS;
						float fdy = ((dy/(time/1000f))/(time/1000f)) * MASS;

						if (Math.sqrt(fdx*fdx+fdy*fdy) < 0.8f * Math.sqrt(ffx*ffx + ffy*ffy)) {
							//stiction
							nfx -= fdx;
							nfy -= fdy;
						} else {

							//sliding friction
							nfx -= 0.7f * Math.sqrt(ffx*ffx+ffy*ffy) * (dx/l);
							nfy -= 0.7f * Math.sqrt(ffx*ffx+ffy*ffy) * (dy/l);
						}
					}


					move(time, dx, dy, walls);
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

		x += dx;
		y += dy;
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

	public void jump() {
		nfy += 8000;
	}

	public float getRadius() {
		return radius;
	}

	public Color getColor() {
		return c;
	}
}
