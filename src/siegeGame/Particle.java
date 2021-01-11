package siegeGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class Particle {

	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	protected double width;
	protected double height;
	protected int id;
	protected int timeRemaining;
	protected int alpha = 255;
	protected int time_till_opacity_drops = 5;

	public Particle(int x, int y, int width, int height, int id) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		switch (id) {
		case 1:
			// destructable block debris
			timeRemaining = 20;
			dx = Math.random() * 10 - 5;
			dy = Math.random() * -5;
			break;
		case 2:
			// impact debris
			timeRemaining = 15;
			dx = Math.random() * 30 - 15;
			dy = Math.random() * -15;
			break;
		case 3:
			// jump puff left
			timeRemaining = 6;
			dx = -12;
			dy = -1;
			break;
		case 4:
			// jump puff right
			timeRemaining = 6;
			dx = 12;
			dy = -1;
			break;
		case 5:
			// Large puff of smoke
			timeRemaining = 45;
			dx = Math.random() * 10 - 5;
			dy = Math.random() * 5 - 2.5;
			break;
		}
	}

	public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		if (alpha < 0) {
			return;
		}
		switch (id) {
		case 1:
			g2.drawImage(Interactable.gravel, scrollx + (int) x, scrolly + (int) y, (int) width, (int) height, null);
			break;
		case 2:
			g2.setColor(Color.BLACK);
			g2.draw(new Ellipse2D.Double(scrollx + (int) x, scrolly + (int) y, (int) width, (int) height));
			g2.setColor(Color.gray);
			break;
		case 3:
			g2.setColor(new Color(255, 255, 255, alpha));
			g2.fill(new Ellipse2D.Double(scrollx + (int) x, scrolly + (int) y, (int) width, (int) height));
			g2.fill(new Ellipse2D.Double(scrollx + 4 + (int) x, scrolly - 2 + (int) y, (int) width / 2,
					(int) height / 2));
			g2.setColor(Color.gray);
			break;
		case 4:
			g2.setColor(new Color(255, 255, 255, alpha));
			g2.fill(new Ellipse2D.Double(scrollx + (int) x, scrolly + (int) y, (int) width, (int) height));
			g2.fill(new Ellipse2D.Double(scrollx - 4 + (int) x, scrolly - 2 + (int) y, (int) width / 2,
					(int) height / 2));
			g2.setColor(Color.gray);
			break;
		case 5:
			g2.setColor(new Color(255, 255, 255, alpha));
			g2.fill(new Ellipse2D.Double(scrollx + (int) x, scrolly + (int) y, (int) width, (int) height));
			g2.setColor(Color.gray);
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
			width -= 1;
			height -= 1;
			break;
		case 2:
			x += dx;
			y += dy;
			dy += 1;
			width -= .5;
			height -= .5;
			break;
		case 3:
			dx += 1;
		case 4:
			x += dx;
			y += dy;
			dx -= .5;
			// width-=.5;
			// height -=.5;
			alpha -= 35;
			break;
		case 5:
			x += dx;
			y += dy;
			dx *= .9;
			dy *= .9;
			width -= .5;
			height -= .5;
			if (time_till_opacity_drops > 0) {
				time_till_opacity_drops -= 1;
			} else {
				alpha -= 6;
			}
			break;
		default:
		}
		timeRemaining -= 1;
	}

	public boolean shouldRemove() {
		return (timeRemaining < 0 || alpha <= 0);
	}
}
