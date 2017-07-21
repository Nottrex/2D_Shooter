package client.shader;

import com.jogamp.opengl.GL2;

import java.awt.*;

public class GunShader extends ShaderProgram {
	private static final String VERTEX_FILE = "gunVertexShader";
	private static final String FRAGMENT_FILE = "gunFragmentShader";

	private int xLocation, yLocation, radiusLocation, lengthLocation, rotationLocation, rotation2Location, cameraLocation, projectionLocation, colorLocation;

	public GunShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void bindAttributes(GL2 gl) {

	}

	protected void getUniformLocations(GL2 gl) {
		xLocation = getUniformLocation(gl, "x");
		yLocation = getUniformLocation(gl, "y");
		radiusLocation = getUniformLocation(gl, "radius");
		lengthLocation = getUniformLocation(gl, "length");
		rotationLocation = getUniformLocation(gl, "rotation");
		rotation2Location = getUniformLocation(gl, "rotation2");
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");
		colorLocation = getUniformLocation(gl, "color");
	}

	public void setBounds(GL2 gl, float x, float y, float radius, Color c, float length, float rotation, float rotation2) {
		setUniform1f(gl, xLocation, x);
		setUniform1f(gl, yLocation, y);
		setUniform1f(gl, radiusLocation, radius);
		setUniform1f(gl, lengthLocation, length);
		setUniform1f(gl, rotationLocation, rotation);
		setUniform1f(gl, rotation2Location, rotation2);
		setUniform4f(gl, colorLocation, c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/255.0f, c.getAlpha()/255.0f);
	}

	public void setCamera(GL2 gl, float[] camera) {
		setUniformMat4(gl, cameraLocation, camera);
	}

	public void setProjectionMatrix(GL2 gl, float[] projectionMatrix) {
		setUniformMat4(gl, projectionLocation, projectionMatrix);
	}
}
