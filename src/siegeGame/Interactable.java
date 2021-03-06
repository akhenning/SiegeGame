package siegeGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

public class Interactable extends Tile {
	// See Tile for complete list of IDs
	public static Image target = Toolkit.getDefaultToolkit().getImage("assets/target.jpg"); // ID 50
	public static Image gravel = Toolkit.getDefaultToolkit().getImage("assets/gravel.png"); // ID 60
	public static Image q_mark = Toolkit.getDefaultToolkit().getImage("assets/question mark.png"); // ID 70
	public static Image q_mark_activated = Toolkit.getDefaultToolkit().getImage("assets/question mark activated.png"); // ID
																														// 71
	public static Image finish = Toolkit.getDefaultToolkit().getImage("assets/finish.png"); // ID 99

	// Data that Interactable may hold, other than basic action; for example, which
	// text box Q_Mark should send you to.
	private int data = -1;

	// Whether an object can be stood on
	protected boolean isTangible = true;

	// Whether moving against the object has an effect;
	// Note that all Interactables have interaction when hit.
	protected boolean hasInteraction = true;
	// Counter for how much time until an action will happen
	protected int action = -1;
	// Flag that can be set do do things...
	protected boolean flag = false;

	public Interactable(int x, int y, int width, int height) {
		this(x, y, width, height, 0, 50, -1);
	}
	public Interactable(int x, int y, int width, int height, int id) {
		this(x, y, width, height, 0, id, -1);
	}
	public Interactable(int x, int y, int width, int height, int clipType, int id) {
		this(x, y, width, height, clipType, id, 0);
	}

	public Interactable(int x, int y, int width, int height, int clipType, int id, int information) {
		super(x, y, width, height, clipType, id);
		if (id == 70 || id == 71 || id == 99) {
			isTangible = false;
		}
		data = information;
		if (id == 61) {
			isTangible = false;
			animated_graphic = new Graphic(x, y, 7, data);
		}
	}

	public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		switch (id) {
		case 99:
			if (Main.debug) {
				g2.drawImage(finish, scrollx + x, scrolly + y, width, height, null);
				g2.drawRect(scrollx + x, scrolly + y, width, height);
			}
			break;
		case 50:
			g2.drawImage(target, scrollx + x, scrolly + y, width, height, null);
			g2.drawRect(scrollx + x, scrolly + y, width, height);
			break;
		case 60:
			g2.drawImage(gravel, scrollx + x, scrolly + y, width, height, null);
			g2.drawRect(scrollx + x, scrolly + y, width, height);
			break;
		case 70:
			g2.drawImage(q_mark, scrollx + x, scrolly + y, width, height, null);
			break;
		case 71:
			g2.drawImage(q_mark_activated, scrollx + x, scrolly + y, width, height, null);
			break;
		case 3:
			if (data == 1) {
				g2.drawRect(scrollx + x, scrolly + y, width, height);
			} else {
				g2.fillRect(scrollx + x, scrolly + y, width, height);
			}
			break;
		}
		if (animated_graphic != null) {
			animated_graphic.draw(g2, scrollx, scrolly);
		}
		if (Main.debug && id <= 60) {
			g2.setColor(Color.yellow);
			drawCollision(g2, scrollx, scrolly);
			g2.setColor(Color.black);
		}
	}

	public void nextFrame() {
		if (id == 61) {
			if (flag == true) {
				int frame = animated_graphic.getFrame();
				if (frame == 0 || frame == 40 || frame == 17 || frame == 55) {
					// Last two are less seamless...
					flag = false;
					action = 27;
					animated_graphic = new Graphic(x, y, 8, data);
					System.out.println("Flag tripped");
				}
			} else if (action > 0) {
				System.out.println(action);
				action -= 1;
			} else if (action == 0) {
				toRemove = true;
			}
		}
	}

	public String toString() {
		switch (id) {
		case 99:
			type = "Finish element";
			break;
		case 50:
			type = "Bounce pad";
			break;
		case 60:
			type = "Destructable block";
			break;
		case 61:
			type = "W (leaves when hit)";
			break;
		case 70:
			type = "Text continuer";
			break;
		case 71:
			type = "Activated text continuer";
			break;
		case 3:
			type = "Door Object";
			break;
		}
		return "Interactable,\t" + x + ",\t" + y + ",\t" + width + ",\t" + height + ",\t" + clipType + ",\t" + id
				+ ",\t" + data + ",\t" + type + "\n";
	}

	public boolean isTangible() {
		return isTangible;
	}

	public boolean hasInteraction() {
		return hasInteraction;
	}

	// return 1 if effects Siege, 2 if changes text, 0 if should be removed
	public int interact() {
		switch (id) {
		case 50:
			return 1;
		case 60:
			toRemove = true;
			return 0;
		case 61:
			flag = true;// action = 27;
			return -1;
		case 70:
			id = 71;
			hasInteraction = false;
			return 2;
		case 71:
			return 2;
		case 99:
		case 3:
			return -1;
		default:
			System.out.println("Invalid Interactable type");
		}
		return -1;
	}

	public int getData() {
		return data;
	}
	public void setData(int d) {
		data = d;
		if (id == 3) {
			if (data == 1) {
				makeIntangible();
			} else {
				refreshClipType();
			}
		}
	}

	public boolean isInteractable() {
		return true;
	}

	public void setId(int newId) {
		id = newId;
		if (id < 50 && id >= 100) {
			System.out.println("Incorrect method of changing object from Interactable.");
		}
	}
}
