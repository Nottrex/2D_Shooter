package client;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import game.Game;

public class Main {
	public static void main(String[] args) {
		Window window = new Window();

		Game game = new Game();

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GameView gameView = new GameView(glcapabilities, game);

		window.setContentPane(gameView);
		window.setVisible(true);

		game.gameLoop();
	}
}
