package client;

import client.shader.CircleShader;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.FPSAnimator;
import game.Game;
import game.Player;

import java.util.List;

public class GameView extends GLJPanel implements GLEventListener {
	public FPSAnimator animator;

	private CircleShader circleShader;
	private Camera cam;
	private Game game;

	private float[] projectionMatrix;
	private float[] viewMatrix = null;
	private float[] cameraPosition = null;

	public GameView(GLCapabilities capabilities, Camera cam, Game game) {
		super(capabilities);
		this.cam = cam;
		this.game = game;

		setFocusable(true);
		this.addGLEventListener(this);
	}

	public GameView(GLCapabilities capabilities, Game game) {
		this(capabilities, new Camera(), game);
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		GL2 gl = glAutoDrawable.getGL().getGL2();
		glAutoDrawable.setGL((new DebugGL2(gl)));

		gl.setSwapInterval(1);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glClearColor(0f, 1f, 1f, 1f);

		circleShader = new CircleShader(gl);
		circleShader.start(gl);
		circleShader.stop(gl);

		animator = new FPSAnimator(this, 60);
		animator.setUpdateFPSFrames(60, null);
		animator.start();
		requestFocus();
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		GL2 gl = glAutoDrawable.getGL().getGL2();
		float aspect = width * 1.0f / height;
		float fov = 90;
		float near = 0.01f;
		float far = 10000f;

		projectionMatrix = FloatUtil.makePerspective(new float[16], 0, true, (float) Math.toRadians(fov), aspect, near, far);

		circleShader.start(gl);
		circleShader.setProjectionMatrix(gl, projectionMatrix);
		circleShader.stop(gl);

		gl.glViewport(x, y, width, height);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		GL2 gl = glAutoDrawable.getGL().getGL2();

		circleShader.cleanUp(gl);
	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		GL2 gl = glAutoDrawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		Player p = game.getPlayers().get(0);
		cam.setPosition(p.getX(), p.getY());
		updateCamera(gl, cam);

		circleShader.start(gl);

		List<Player> playerList = game.getPlayers();
		for (int i = 0; i < playerList.size(); i++) {
			Player pl = playerList.get(i);
			circleShader.setBounds(gl, pl.getX(), pl.getY(), pl.getRadius(), 25);

			gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, 25+2);
		}

		circleShader.stop(gl);

		gl.glFlush();
	}

	private void updateCamera(GL2 gl, Camera cam) {
		boolean b = cam.update();
		if (cam.zoom == Double.POSITIVE_INFINITY || cam.zoom == Double.NEGATIVE_INFINITY || cam.zoom == Double.NaN) {
			cam.setZoom(1);
			b = cam.update();
		}

		if (viewMatrix == null || b) {
			float[] target = {cam.x, cam.y, 0};
			cameraPosition = new float[]{cam.x, cam.y - cam.tilt / cam.zoom, 1 / cam.zoom};

			float[] dir = new float[]{cameraPosition[0] - target[0], cameraPosition[1] - target[1], cameraPosition[2] - target[2]};
			float[] up = new float[3];
			VectorUtil.crossVec3(up, dir, new float[]{1, 0, 0});
			VectorUtil.normalizeVec3(up);

			viewMatrix = FloatUtil.makeLookAt(new float[16], 0, cameraPosition, 0, target, 0, up, 0, new float[16]);

			circleShader.start(gl);
			circleShader.setCamera(gl, viewMatrix);
			circleShader.stop(gl);
		}
	}

	public float[] screenPositionToWorldPosition(int x, int y) {
		if (projectionMatrix == null || viewMatrix == null || cam.zoom == Double.POSITIVE_INFINITY || cam.zoom == Double.NEGATIVE_INFINITY || cam.zoom == Double.NaN)
			return new float[]{-1, -1};

		float[] ray_nds = {(x * 1.0f / getWidth()) * 2 - 1, (1 - (y * 1.0f / getHeight()) * 2), 1.0f};
		float[] ray_clip = {ray_nds[0], ray_nds[1], -1.0f, 1.0f};

		float[] ray_eye = FloatUtil.multMatrixVec(FloatUtil.invertMatrix(projectionMatrix, new float[16]), ray_clip, new float[4]);
		ray_eye[2] = -1.0f;
		ray_eye[3] = 0.0f;

		float[] ray_wor4 = FloatUtil.multMatrixVec(FloatUtil.invertMatrix(viewMatrix, new float[16]), ray_eye, new float[4]);
		float[] ray_wor = {ray_wor4[0], ray_wor4[1], ray_wor4[2]};
		ray_wor = VectorUtil.normalizeVec3(ray_wor);

		float distance = -cameraPosition[2] / ray_wor[2];
		float[] point = {distance * ray_wor[0] + cameraPosition[0], distance * ray_wor[1] + cameraPosition[1]};

		return point;
	}
}
