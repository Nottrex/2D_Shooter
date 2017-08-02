package game;

import java.util.ArrayList;
import java.util.List;

public class Shape {
	private Location center;
	private List<Location> vertices;
	private List<Wall> walls;

	public Shape(List<Location> vertices, Location center) {
		this.center = center;
		this.vertices = vertices;

		walls = new ArrayList<>();

		for (int i = 0; i < vertices.size(); i++) {
			Location l1 = vertices.get(i);
			Location l2 = vertices.get((i+1) % vertices.size());
			walls.add(new Wall(l1.x, l1.y, l2.x, l2.y));
		}
	}

	public List<Wall> getWalls() {
		return walls;
	}

	public List<Location> getVertices() {
		return vertices;
	}

	public Location getCenter() {
		return center;
	}
}
