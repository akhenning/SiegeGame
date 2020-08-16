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

	// not tested properly yet
	public void draw(Graphics2D g2) {
		g2.drawImage(target, Screen.scrollx + x, Screen.scrolly + y,width,height, null);
		g2.drawRect(Screen.scrollx + x, Screen.scrolly + y, width, height);
	}
}
