package siegeGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

public class Graphic extends Tile {
	private Image nian_sleep = Toolkit.getDefaultToolkit().getImage("assets/niansleep.png");
	private int animationFrame = 0;
	private int pictureWidth = 0;
	private String text = "";

	public Graphic(int x, int y, int id) {
		super(x, y, -1, -1,0, id);
		switch (id) {
		case 10:
			// Nian sleeping
			width = 349;
			height = 188;
			pictureWidth = 27570;
			break;
		case 11:
			// Level selector
			break;
		default:
		}
	}
	

	public Graphic(int x, int y, int width, int height) {
		this(x, y, width, height, -1);
	}

	public Graphic(int x, int y, int width, int height, int id) {
		this(x, y, id);
		this.width = width;
		this.height = height;
		// slight redundancy in this() call
	}

	public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		switch (id) {
		case 10:
			g2.drawImage(nian_sleep, x + scrollx, y + scrolly, x + scrollx + width, y + scrolly + height,
					width * animationFrame, 0, width * (animationFrame + 1), height, null);
			animationFrame += 1;
			if (width * animationFrame + 300 > pictureWidth) {
				animationFrame = 0;
			}
			break;
		case 11:
			g2.drawRect(scrollx + x, scrolly + y, width, height);
			g2.setColor(Color.WHITE);
			g2.fillRect(scrollx + x, scrolly + y, width, height);
			g2.setColor(Color.BLACK);
			g2.drawString(text,x+scrollx,y+scrolly+height);
		}
	}
	
	public String toString() {
		switch (id) {
		case 10:
			type = "Nian lying down";
			break;
		case 11:
			type = "Level selector graphic (should not be in level file)";
			break;
		}
		return "Graphic,\t" + x + ",\t" + y + ",\t" + width + ",\t" + height + ",\t" + id + ",\t"+type+"\n";
	}
	
	public void setText(String text) {
		this.text = text;
	} 
	public String getText() {
		return text;
	}
}
