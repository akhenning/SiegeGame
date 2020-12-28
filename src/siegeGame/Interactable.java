package siegeGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

public class Interactable extends Tile {
	public static Image target = Toolkit.getDefaultToolkit().getImage("assets/target.jpg"); // ID 50
	public static Image gravel = Toolkit.getDefaultToolkit().getImage("assets/gravel.png"); // ID 60
	public static Image q_mark = Toolkit.getDefaultToolkit().getImage("assets/question mark.png"); // ID 70
	public static Image q_mark_activated = Toolkit.getDefaultToolkit().getImage("assets/question mark activated.png"); // ID 71
	private int data = -1;
	// Whether an object can be stood on
	private boolean isTangible = true;
	// Whether moving against the object has an effect;
	// Note that all Interactables have interaction when hit.
	private boolean hasInteraction = true;
	public Interactable(int x, int y, int width, int height) {
		this(x, y, width, height, 50);
	}

	public Interactable(int x, int y, int width, int height, int id) {
		this(x, y, width, height, id, -1);
	}
	
	public Interactable(int x, int y, int width, int height, int id, int information) {
		super(x, y, width, height, id);
		if (id == 70) {
			isTangible = false;
		}
		data = information;
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
		case 70:
			g2.drawImage(q_mark, scrollx + x, scrolly + y,width,height, null);
			break;
		case 71:
			g2.drawImage(q_mark_activated, scrollx + x, scrolly + y,width,height, null);
			break;
		}
	}
	
	public String toString() {
		if (id == 71) {
			id = 70;
		}
		return "Interactable,"+x+","+y+","+width+","+height+","+id+","+data+",\n";
	}
	
	public boolean isTangible() {
		return isTangible;
	}
	public boolean hasInteraction() {
		return hasInteraction;
	}
	
	// return 1 if effects Siege, 2 if changes text
	public int interact() {
		switch (id) {
		case 50:
			return 1;
		case 60:
			toRemove = true;
			return 0;
		case 70:
			id = 71;
			return 2;
		case 71:
			hasInteraction = false;
			return 2;
		default:
			System.out.println("Invalid Interactable type");
		}
		return -1;
	}
	public int getData() {
		return data;
	}
}
