package siegeGame;

import java.awt.Color;
import java.awt.Graphics2D;

public class ConnectedTile extends Interactable {
	private Tile tied;

	public ConnectedTile(int x, int y, int width, int height, int clipType, int id) {
		this(x, y, width, height, clipType, id, new Tile(x, y, 200, 250, 0, 1));
	}

	public ConnectedTile(int x, int y, int width, int height, int clipType, int id, Tile tied) {
		super(x, y, width, height, clipType, id);
		this.tied = tied;
		this.tied.dontSaveThis();
		switch (id) {
		case -1:
		case -2:
			isTangible = false;
			break;
		}
	}

	public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		switch (id) {
		case -2:
			Color c = g2.getColor();
			g2.setColor(Color.BLACK);
			g2.drawLine(scrollx + x, scrolly + y + height, scrollx + x + width, scrolly + y + height);
			g2.setColor(Color.GRAY);
			if (flag == false) {
				g2.fillRect(scrollx + x+width/4, scrolly + y, width/2, height);
			} else {
				g2.fillRect(scrollx + x+width/4, scrolly + y+height/2, width/2, height/2);
			}
			g2.setColor(c);
			break;
		}
		if (Main.debug) {
			drawCollision(g2, scrollx, scrolly);
			g2.drawRect(scrollx + x, scrolly + y, width, height);
		}
		tied.draw(g2, scrollx, scrolly);
	}

	public String toString() {
		switch (id) {
		case -1:
			type = "Respawn Element";
			break;
		case -2:
			type = "Door Button";
			break;
		}
		System.out.println(id);
		return "ConnectedTile,\t" + x + ",\t" + y + ",\t" + width + ",\t" + height + ",\t" + clipType + ",\t" + id
				+ ",\t\t" + type + " \t," + tied.toString();
	}

	// return 1 if effects Siege, 2 if changes text, 0 if should be removed
	public int interact() {
		switch (id) {
		case -1:
			return -1;
		case -2:
			flag = true;
			action = 100;
			tied.setData(1);
			return -1;
		default:
			System.out.println("Invalid Interactable type");
		}
		return -1;
	}

	public void nextFrame() {
		if (action >= 0) {
			action -= 1;
			if (action == -1) {
				flag = false;
				tied.setData(0);
			}
		}
	}

	public void cleanup() {
		tied = null;
		// This seems like it could cause problems, since it won't be removed from the
		// list. Might be better to just remove.
	}

	public void goToTied(double x, double y) {
		tied.goTo(x, y);
	}

	public Tile getTied() {
		return tied;
	}

	public int[] getTiedXY() {
		return new int[] { tied.getX(), tied.getY() };
	}
}
