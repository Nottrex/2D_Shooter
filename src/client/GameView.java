package client;

import client.audio.AudioHandler;
import client.audio.AudioPlayer;
import client.shader.CircleShader;
import client.shader.GunShader;
import client.shader.WallShader;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.math.VectorUtil;
import com.jogamp.opengl.util.FPSAnimator;
import game.*;
import game.Shape;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public class GameView extends GLJPanel implements GLEventListener {

	public static final float MAX_OFFSET = 0.2f;

	public FPSAnimator animator;

	private AudioPlayer aPlayer;

	private CircleShader circleShader;
	private GunShader gunShader;
	private WallShader wallShader;
	private IntBuffer buffers = IntBuffer.allocate(2);
	private IntBuffer vao = IntBuffer.allocate(1);
	private IntBuffer vao2 = IntBuffer.allocate(1);
	private int ind;

	private Camera cam;
	private Game game;

	private float[] projectionMatrix;
	private float[] viewMatrix = null;
	private float[] cameraPosition = null;
	private float[] cameraOffset = null;

	private int mouseX, mouseY;

	public GameView(GLCapabilities capabilities, Camera cam, Game game) {
		super(capabilities);
		this.cam = cam;
		cam.setZoom(0.1f);

		this.game = game;
		this.aPlayer = new AudioPlayer();

		setFocusable(true);
		this.addGLEventListener(this);

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				game.pressed.put(e.getKeyCode(), true);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				game.pressed.put(e.getKeyCode(), false);
			}
		});

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				aPlayer.playAudio("shoot");
			}
		});

		this.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseX = e.getX();
				mouseY = e.getY();
			}
		});
	}

	public GameView(GLCapabilities capabilities, Game game) {
		this(capabilities, new Camera(), game);
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		AudioHandler.loadMusicWav("hit", "hit");
		AudioHandler.loadMusicWav("shoot", "shoot");

		cameraOffset = new float[2];
		GL2 gl = glAutoDrawable.getGL().getGL2();
		glAutoDrawable.setGL((new DebugGL2(gl)));

		gl.setSwapInterval(1);
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl.glClearColor(0.6f, 0.6f, 0.6f, 1f);

		gl.glEnable(GL.GL_MULTISAMPLE);

		circleShader = new CircleShader(gl);
		circleShader.start(gl);
		circleShader.stop(gl);

		gunShader = new GunShader(gl);
		gunShader.start(gl);
		gunShader.stop(gl);

		initWallShader(gl);

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

		gunShader.start(gl);
		gunShader.setProjectionMatrix(gl, projectionMatrix);
		gunShader.stop(gl);

		wallShader.start(gl);
		wallShader.setProjectionMatrix(gl, projectionMatrix);
		wallShader.stop(gl);

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

		GameMap map = game.getMap();
		Player p = game.getPlayers().get(0);
		cam.setPosition(p.getX() - cameraOffset[0], p.getY() - cameraOffset[1]);
		updateCamera(gl, cam);

		if (map.hasShapeChange()) {
			updateWallShader(gl);
		}

		float[] mouse = screenPositionToWorldPosition(mouseX, mouseY);
		float dx = p.getX() - mouse[0] + cameraOffset[0];
		float dy = p.getY() - mouse[1] + cameraOffset[1];

		dx/=5.0f;
		dy/=5.0f;

		cameraOffset[0] = dx <= MAX_OFFSET? dx >= -MAX_OFFSET? dx: -MAX_OFFSET: MAX_OFFSET;
		cameraOffset[1] = dy <= MAX_OFFSET? dy >= -MAX_OFFSET? dy: -MAX_OFFSET: MAX_OFFSET;

		List<Player> playerList = game.getPlayers();

		wallShader.start(gl);
		gl.glBindVertexArray(vao.get(0));
		gl.glDrawElements(GL.GL_TRIANGLES, ind, GL.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(vao2.get(0));
		wallShader.stop(gl);

		gunShader.start(gl);
		for (int i = 0; i < playerList.size(); i++) {
			Player pl = playerList.get(i);

			dx = pl.getX() - mouse[0];
			dy = pl.getY() - mouse[1];

			float rot = 180.0f + (float) Math.toDegrees(Math.acos(dy/(Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)))));
			if(dx < 0) rot = 360 - rot;

			gunShader.setBounds(gl, pl.getX(), pl.getY(), pl.getRadius()/1.5f, pl.getColor().brighter().brighter(), pl.getRadius(), rot);
			gl.glDrawArrays(GL.GL_TRIANGLE_STRIP, 0, 4);
		}
		gunShader.stop(gl);


		circleShader.start(gl);
		for (int i = 0; i < playerList.size(); i++) {
			Player pl = playerList.get(i);
			circleShader.setBounds(gl, pl.getX(), pl.getY(), pl.getRadius(), pl.getColor(), 256);
			gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, 256 + 2);
		}
		circleShader.setBounds(gl, 0, 0, 0.05f, Color.RED, 256);
		gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, 256 + 2);

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

			gunShader.start(gl);
			gunShader.setCamera(gl, viewMatrix);
			gunShader.stop(gl);

			wallShader.start(gl);
			wallShader.setCamera(gl, viewMatrix);
			wallShader.stop(gl);
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

	private void initWallShader(GL2 gl) {
		wallShader = new WallShader(gl);
		wallShader.start(gl);
		wallShader.stop(gl);

		List<Shape> shapes = game.getMap().getShapes();
		int length = 0;
		ind = 0;
		for (int i = 0; i < shapes.size(); i++) {
			length += shapes.get(i).getVertices().size()+1;
			ind += shapes.get(i).getVertices().size()*3;
		}

		float[] locations = new float[length * 2];
		int[] indices = new int[ind];
		int loc = 0;
		int loc2 = 0;

		for (int i = 0; i < shapes.size(); i++) {
			Shape s = shapes.get(i);

			int a = s.getVertices().size();
			for (int j = 0; j < a; j++) {
				Location l = s.getVertices().get(j);
				locations[2 * (loc+j)] = l.x;
				locations[2 * (loc+j) + 1] = l.y;

				indices[loc2 + 3*j + 0] = loc+j;
				indices[loc2 + 3*j + 1] = loc+((j+1)%a);
				indices[loc2 + 3*j + 2] = loc+a;
			}
			locations[2* (loc+a)] = s.getCenter().x;
			locations[2* (loc+a) + 1] = s.getCenter().y;
			loc += a+1;
			loc2 += a*3;
		}

		FloatBuffer locationBuffer = FloatBuffer.wrap(locations);
		IntBuffer indicesBuffer = IntBuffer.wrap(indices);


		gl.glGenVertexArrays(1, vao);
		gl.glBindVertexArray(vao.get(0));

		gl.glGenBuffers(2, buffers);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, 4 * length * 2, locationBuffer, GL2.GL_STATIC_DRAW);

		gl.glEnableVertexAttribArray(wallShader.getPositionLocation());
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glVertexAttribPointer(wallShader.getPositionLocation(), 2, GL.GL_FLOAT, false, 0, 0);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, buffers.get(1));
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, ind * 4, indicesBuffer, GL.GL_STATIC_DRAW);

		gl.glGenVertexArrays(1, vao2);
		gl.glBindVertexArray(vao2.get(0));
	}

	private void updateWallShader(GL2 gl) {
		List<Shape> shapes = game.getMap().getShapes();
		int length = 0;
		ind = 0;
		for (int i = 0; i < shapes.size(); i++) {
			length += shapes.get(i).getVertices().size()+1;
			ind += shapes.get(i).getVertices().size()*3;
		}

		float[] locations = new float[length * 2];
		int[] indices = new int[ind];
		int loc = 0;
		int loc2 = 0;

		for (int i = 0; i < shapes.size(); i++) {
			Shape s = shapes.get(i);

			int a = s.getVertices().size();
			for (int j = 0; j < a; j++) {
				Location l = s.getVertices().get(j);
				locations[2 * (loc+j)] = l.x;
				locations[2 * (loc+j) + 1] = l.y;

				indices[loc2 + 3*j + 0] = loc+j;
				indices[loc2 + 3*j + 1] = loc+((j+1)%a);
				indices[loc2 + 3*j + 2] = loc+a;
			}
			locations[2* (loc+a)] = s.getCenter().x;
			locations[2* (loc+a) + 1] = s.getCenter().y;
			loc += a+1;
			loc2 += a*3;
		}

		gl.glBindVertexArray(vao.get(0));

		FloatBuffer locationBuffer = FloatBuffer.wrap(locations);
		IntBuffer indicesBuffer = IntBuffer.wrap(indices);

		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, buffers.get(0));
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, 4 * length * 2, locationBuffer, GL2.GL_STATIC_DRAW);

		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, buffers.get(1));
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, ind * 4, indicesBuffer, GL.GL_STATIC_DRAW);

		gl.glBindVertexArray(vao2.get(0));
	}
}
