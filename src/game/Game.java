package game;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	private List<Player> players;
	private GameMap map;
	private boolean running;

	public Map<Integer, Boolean> pressed;

	public Game() {
		this.pressed = new HashMap<>();
	}

	public void gameLoop() {
		init();

		long last = System.currentTimeMillis();
		while (running) {
			long curr = System.currentTimeMillis();

			update(curr - last);

			last = curr;
		}
		cleanUp();
	}

	private void init() {
		players = new ArrayList<>();
		players.add(new Player());

		map = new GameMap();

		List<Location> triangleVertices = new ArrayList<>();
		triangleVertices.add(new Location(-0.3f, -0.3f));
		triangleVertices.add(new Location(0.3f, -0.3f));
		triangleVertices.add(new Location(0f, -0.6f));
		map.addShape(new Shape(triangleVertices, new Location(0, -0.45f)));

		running = true;
	}

	public boolean isPressed(int i) {
		return pressed.containsKey(i) ? pressed.get(i) : false;
	}

	private void update(long time) {
		float mx = 0, my = 0;
		if (isPressed(KeyEvent.VK_D)) mx += 1;
		if (isPressed(KeyEvent.VK_A)) mx -= 1;
		if (isPressed(KeyEvent.VK_W)) my += 1;
		if (isPressed(KeyEvent.VK_S)) my -= 1;
		if (mx*mx + my*my != 0) {
			float a = (float) Math.sqrt(mx*mx + my*my);
			mx /= a;
			my /= a;
		}
		players.get(0).updateWalkingDirection(mx, my);

		for (int i = 0; i < players.size(); i++) {
			players.get(i).update(time, map.getWalls());
		}
	}

	private void cleanUp() {

	}

	public GameMap getMap() {
		return map;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void stop() {
		running = false;
	}
}
