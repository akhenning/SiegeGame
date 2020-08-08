package siegeGame;

import java.awt.Graphics2D;

public class Tile {
	int x;
	int y;
	int width;
	int height;
	int id;

	public Tile(int x, int y, int width, int height) {
		this(x, y, width, height, 0);
	}

	public Tile(int x, int y, int width, int height, int id) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
	}

	void draw(Graphics2D g2) {
		g2.drawRect(x, y, width, height);
	}
}
