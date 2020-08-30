package siegeGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

public class Interactable extends Tile {
	public static Image target = Toolkit.getDefaultToolkit().getImage("assets/target.jpg");
	public static Image gravel = Toolkit.getDefaultToolkit().getImage("assets/gravel.png");

	public Interactable(int x, int y, int width, int height) {
		this(x, y, width, height, 50);
	}

	public Interactable(int x, int y, int width, int height, int id) {
		super(x, y, width, height, id);
	}

	public void draw(Graphics2D g2) {
		draw(g2,Screen.scrollx,Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		switch (id) {
		case 50:
			g2.drawImage(target, scrollx + x, scrolly + y,width,height, null);
			g2.drawRect(scrollx + x, scrolly + y, width, height);
			break;
		case 60:
			g2.drawImage(gravel, scrollx + x, scrolly + y,width,height, null);
			g2.drawRect(scrollx + x, scrolly + y, width, height);
			break;
			
		}
	}
	
	public String toString() {
		return "Interactable,"+x+","+y+","+width+","+height+","+id+",\n";
	}
	public boolean interact() {
		if (id <60) {
			return true;
		} else {
			toRemove = true;
			return false;
		}
	}
}
