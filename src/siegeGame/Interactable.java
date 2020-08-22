package siegeGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

public class Interactable extends Tile {
	public static Image target = Toolkit.getDefaultToolkit().getImage("assets/target.jpg");

	public Interactable(int x, int y, int width, int height) {
		this(x, y, width, height, 0);
	}

	public Interactable(int x, int y, int width, int height, int id) {
		super(x, y, width, height, id);
	}

	public void draw(Graphics2D g2) {
		draw(g2,Screen.scrollx,Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		g2.drawImage(target, scrollx + x, scrolly + y,width,height, null);
		g2.drawRect(scrollx + x, scrolly + y, width, height);
	}
	
	public String toString() {
		return "Interactable,"+x+","+y+","+width+","+height+","+id+",\n";
	}
}
