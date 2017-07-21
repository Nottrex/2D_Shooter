package game;

import java.awt.*;
import java.util.Random;

public class Player {
	public static final float acceleration = 0.001f;
	public static final float reibung = 0.9f;
	public static final float stopping = 0.9f;
	public static final long TIME = 10;

	private float x, y;
	private float vx, vy;
	private float mx, my;

	private Color c;

	public Player() {
		this(new Color(new Random().nextInt()));
	}

	public Player(Color indicator) {
		x = 0;
		y = 0;
		vx = 0;
		vy = 0;

		c = indicator;
	}

	long lastTime = 0;
	public void update(long time) {
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

			x += vx;
			y += vy;
		}
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
		return 0.05f;
	}

	public Color getColor() {
		return c;
	}
}
