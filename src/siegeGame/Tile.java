package siegeGame;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

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

	public void draw(Graphics2D g2) {
		// If the X value could be rendered
		if (Screen.scrollx + x + width > 2000 || Screen.scrollx + x < -100) {
			if (-Screen.scrolly + y + height < -100 || -Screen.scrolly + y > 1000) {
				//System.out.println("This one is not in frame");
			}
			// if(Screen.scrolly+y)
		}
		g2.fillRect(Screen.scrollx + x, Screen.scrolly + y, width, height);
	}

	public boolean isInside(Point2D.Double point) {
		if (id==200) {
			System.out.println("S's X: "+point.getX());
			System.out.println((point.getX() >= x && point.getX()<=x+width)+ " " +(point.getY() >= y && point.getY()<=y+height ));
		}
		if (point.getY() >= y && point.getY()<=y+height && point.getX() >= x && point.getX()<=x+width) {//point.getX() - x <= width && point.getY() - y <= height) {
			return true;
		} else {
			return false;
		}
	}
}
