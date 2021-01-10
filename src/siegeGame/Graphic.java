package siegeGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

public class Graphic extends Tile {
	private Image nian_sleep = Toolkit.getDefaultToolkit().getImage("assets/niansleep.png");
	private Image w_dance = Toolkit.getDefaultToolkit().getImage("assets/w_dance.png");
	private int animationFrame = 0;
	private int pictureWidth = 0;
	private String text = "";
	private int image_width;
	private int image_height;

	public Graphic(int x, int y, int id) {
		super(x, y, -1, -1,0, id);
		switch (id) {
		case 9:
			// W dancing
			image_width = 552;
			image_height = 786;
			width =(int) (552/4);
			height =(int) (786/4);
			
			pictureWidth = MakeSureImageHasLoaded(w_dance);
			break;
		case 10:
			// Nian sleeping
			width = 349;
			height = 188;
			image_width = width;
			image_height = height;
			pictureWidth = MakeSureImageHasLoaded(nian_sleep);
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
		if (id == 11) {
			this.width = width;
			this.height = height;
		}
		// slight redundancy in this() call
	}

	public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		switch (id) {
		case 9:
			g2.drawImage(w_dance, x + scrollx, y + scrolly, x + scrollx + width, y + scrolly + height,
					image_width * animationFrame, 0, image_width * (animationFrame + 1), image_height, null);
			animationFrame += 1;
			if (image_width * animationFrame + 300 > pictureWidth) {
				animationFrame = 0;
			}
			break;
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
		case 9:
			type = "W Dancing";
			break;
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
	

	public int MakeSureImageHasLoaded(Image image) {
		int width = image.getWidth(null);
		int failcase = 0;
		while (width == -1) {
			failcase += 1;
			try {
				Thread.sleep(5);
				width = image.getWidth(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (failcase > 30) {
				System.out.println("ERROR: IMAGE FAILED TO LOAD IN GRAPHICS");
				return -1;
			}
		}
		return width;
	}
}
