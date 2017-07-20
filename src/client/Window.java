package client;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;

import javax.swing.*;

public class Window extends JFrame {

	public Window() {
		super("2D Shooter!");

		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(true);

		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		setContentPane(new GameView(glcapabilities));

		setVisible(true);
	}

}
