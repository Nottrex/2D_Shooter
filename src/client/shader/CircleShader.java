package client.shader;

import com.jogamp.opengl.GL2;

public class CircleShader extends ShaderProgram {
	private static final String VERTEX_FILE = "circleVertexShader";
	private static final String FRAGMENT_FILE = "circleFragmentShader";

	private int xLocation, yLocation, radiusLocation, totalLocation, cameraLocation, projectionLocation;

	public CircleShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void bindAttributes(GL2 gl) {

	}

	protected void getUniformLocations(GL2 gl) {
		xLocation = getUniformLocation(gl, "x");
		yLocation = getUniformLocation(gl, "y");
		radiusLocation = getUniformLocation(gl, "radius");
		totalLocation = getUniformLocation(gl, "total");
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");
	}

	public void setBounds(GL2 gl, float x, float y, float radius, float total) {
		setUniform1f(gl, xLocation, x);
		setUniform1f(gl, yLocation, y);
		setUniform1f(gl, radiusLocation, radius);
		setUniform1f(gl, totalLocation, total);
	}

	public void setCamera(GL2 gl, float[] camera) {
		setUniformMat4(gl, cameraLocation, camera);
	}

	public void setProjectionMatrix(GL2 gl, float[] projectionMatrix) {
		setUniformMat4(gl, projectionLocation, projectionMatrix);
	}
}
