package client.shader;

import com.jogamp.opengl.GL2;

import java.awt.*;

public class WallShader extends ShaderProgram {
	private static final String VERTEX_FILE = "wallVertexShader";
	private static final String FRAGMENT_FILE = "wallFragmentShader";

	private int cameraLocation, projectionLocation;
	private int positionLocation;

	public WallShader(GL2 gl) {
		super(gl, VERTEX_FILE, FRAGMENT_FILE);
	}

	protected void bindAttributes(GL2 gl) {
		positionLocation = getAttributeLocation(gl, "position");
	}

	protected void getUniformLocations(GL2 gl) {
		cameraLocation = getUniformLocation(gl, "cameraMatrix");
		projectionLocation = getUniformLocation(gl, "projectionMatrix");
	}

	public void setCamera(GL2 gl, float[] camera) {
		setUniformMat4(gl, cameraLocation, camera);
	}

	public void setProjectionMatrix(GL2 gl, float[] projectionMatrix) {
		setUniformMat4(gl, projectionLocation, projectionMatrix);
	}

	public int getPositionLocation() {
		return positionLocation;
	}
}
