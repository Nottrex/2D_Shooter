package game;

import java.util.ArrayList;
import java.util.List;

public class Game {
	private List<Player> players;
	private boolean running;

	public Game() {
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
		running = true;
	}

	private void update(long time) {
		for (int i = 0; i < players.size(); i++) {
			players.get(i).update(time);
		}
	}

	private void cleanUp() {

	}

	public List<Player> getPlayers() {
		return players;
	}

	public void stop() {
		running = false;
	}
}
