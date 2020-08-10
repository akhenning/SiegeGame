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
		if (Screen.scrollx + x > 2000 || Screen.scrollx + x + width < -200) {
			if (-Screen.scrolly + y + height < -200 || -Screen.scrolly + y > 1000) {
				//System.out.println("This one is not in frame");
				return;
			}
			// if(Screen.scrolly+y)
		}
		g2.fillRect(Screen.scrollx + x, Screen.scrolly + y, width, height);
	}

	// I could easily compress these two into one, but it would be slower 
	// because I'd need to cast. So probably not worth stressing over
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
	public boolean isInside(int[] point) {
		if (id == 200) {
			System.out.println("S's X: " + point[0]);
			System.out.println(
					(point[0] >= x && point[0] <= x + width) + " " + (point[1] >= y && point[1] <= y + height));
		}
		if (point[1] >= y && point[1] <= y + height && point[0] >= x && point[0] <= x + width) {
			// point.getX() - x <=
			// width && point.getY()
			// - y <= height) {
			return true;
		} else {
			return false;
		}
	}
}
