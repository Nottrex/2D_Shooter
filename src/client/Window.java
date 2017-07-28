package client;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {


	public Window() {
		super("2D Shooter!");

		setMinimumSize(new Dimension(800, 600));
		setSize(800, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(true);
	}

	public void fullscreen() {
		this.dispose();
		this.setUndecorated(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
	}

	public void framed() {
		this.dispose();
		this.setExtendedState(JFrame.NORMAL);
		this.setUndecorated(false);
		this.setSize(800, 600);
		this.setVisible(true);
	}
}