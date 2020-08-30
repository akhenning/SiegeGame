package siegeGame;

import java.awt.Graphics2D;

public class Particle {

	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	protected int width;
	protected int height;
	protected int id;
	protected int timeRemaining;

	public Particle(int x, int y, int width, int height, int id) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		switch (id) {
		case 1:
			timeRemaining = 20;
			dx = Math.random() * 10 - 5;
			dy = Math.random() * -5;
			break;
		}
	}

	public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		switch (id) {
		case 1:
			g2.drawImage(Interactable.gravel, scrollx + (int) x, scrolly + (int) y, width, height, null);
			break;
		default:
		}
	}

	public void calcMove() {
		switch (id) {
		case 1:
			x += dx;
			y += dy;
			dy += 1;
			width-=1;
			height -=1;
			break;
		default:
		}
		timeRemaining-=1;
	}

	public boolean shouldRemove() {
		return (timeRemaining<0);
	}
}
