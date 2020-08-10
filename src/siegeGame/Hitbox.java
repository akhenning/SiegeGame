package siegeGame;

import java.awt.Graphics2D;

public class Hitbox {
	// locations of edges to hitbox relative to player
	// p1 p2
	// p3 p4
	public int[] p1;
	// public int[] p2;
	// public int[] p3;
	// public int[] p4;
	public int width;
	public int height;
	private boolean active = false;
	private int direction = 1;

	public Hitbox(int x, int y, int width, int height) {
		int p12[] = { x, y };
		// int p22[] = {x+width,y};
		// int p32[] = {x,y+height};
		// int p42[] = {x+width,y+height};
		p1 = p12;
		// p2=p22;
		// p3=p32;
		// p4=p42;
		this.width = width;
		this.height = height;

	}

	public void draw(Graphics2D g2, int px, int py) {
		if (direction == -1) {
			g2.drawRect(px - p1[0] - width + 62, py + p1[1], width, height);

		} else {
			g2.drawRect(px + p1[0], py + p1[1], width, height);
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean isActive, int facing) {
		active = isActive;
		direction = facing;
	}

	// which is 1-4 regarding which point.
	public int[] getRelativePoint(int which) {
		int rtrn[] = new int[2];
		if (direction == -1) {
			switch (which) {
			case 1:
				rtrn[0] = -p1[0] - width + 62;
				rtrn[1] = p1[1];
				break;
			case 2:
				rtrn[0] = -p1[0] + 62;
				rtrn[1] = p1[1];
				break;
			case 3:
				rtrn[0] = -p1[0] - width + 62;
				rtrn[1] = p1[1] + height;
				break;
			case 4:
				rtrn[0] = -p1[0] + 62;
				rtrn[1] = p1[1] + height;
				break;
			default:
			}
		} else {
			switch (which) {
			case 1:
				rtrn[0] = p1[0];
				rtrn[1] = p1[1];
				break;
			case 2:
				rtrn[0] = p1[0] + width;
				rtrn[1] = p1[1];
				break;
			case 3:
				rtrn[0] = p1[0];
				rtrn[1] = p1[1] + height;
				break;
			case 4:
				rtrn[0] = p1[0] + width;
				rtrn[1] = p1[1] + height;
				break;
			default:
			}
		}
		// rtrn[0] =
		return rtrn;
	}
}
