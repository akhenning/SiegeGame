package siegeGame;

import java.awt.Graphics2D;

public class ConnectedTile extends Interactable {
	private Tile tied;
	private int state = 0;
	private int stateCounter = 0;
	
	public ConnectedTile(int x, int y, int width, int height, int clipType, int id) {
		this(x, y, width, height, clipType, id, new Tile(x,y,200,250,0,1));
	}
	
	public ConnectedTile(int x, int y, int width, int height, int clipType, int id, Tile tied) {
		super(x, y, width, height, clipType, id);
		this.tied = tied;
		switch (id) {
		case -1:
			isTangible = false;
		}
	}

	public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		if (Main.debug) {
			drawCollision(g2, scrollx, scrolly);
			g2.drawRect(scrollx + x, scrolly + y, width, height);
		}
		tied.draw(g2,scrollx,scrolly);
	}

	public String toString() {
		switch (id) {
		case -1:
			type = "Respawn Element";
			break;
		}
		System.out.println(id);
		return "ConnectedTile,\t" + x + ",\t" + y + ",\t" + width + ",\t" + height + ",\t" + clipType + ",\t" + id
			 + ",\t" + type + " " + tied.toString();
	}
	
	public void cleanup() {
		tied = null;
		// This seems like it could cause problems, since it won't be removed from the list. Might be better to just remove.
	}
	
	public void goToTied(double x, double y) {
		tied.goTo(x, y);
	}
	public Tile getTied() {
		return tied;
	}
	public int[] getTiedXY() {
		return new int[]{tied.getX(),tied.getY()};
	}
}
