package game;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
	private List<Wall> walls;
	private List<Shape> shapes;
	private boolean shapeChange;

	public GameMap() {
		shapes = new ArrayList<>();
		walls = new ArrayList<>();
		shapeChange = true;
	}

	public void addShape(Shape s) {
		shapes.add(s);
		walls.addAll(s.getWalls());
		shapeChange = true;
	}

	public List<Shape> getShapes() {
		return shapes;
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public boolean hasShapeChange() {
		boolean b = shapeChange;
		shapeChange = false;
		return b;
	}
}
