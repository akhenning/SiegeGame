package siegeGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

// Id:
// 0 = black square
// >50 <100 = interactable
// 50 = bouncy default
// >100 = slope
// 101 = right slope
// 102 = left slope

public class Tile {	
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected int id;
	boolean isVisible = false;
	public static Image cobb_slope_right = Toolkit.getDefaultToolkit().getImage("assets/cobblestonesloperight.png");
	public static Image cobb_slope_left = Toolkit.getDefaultToolkit().getImage("assets/cobblestoneslopeleft.png");
	double slope = 0;
	protected boolean toRemove = false;

	public enum SlopeState {
		NONE, LEFT, RIGHT
	}

	SlopeState slopeState = SlopeState.NONE;
	
	// To make the level files a bit more legible
	protected String type = "Unrecognized";

	public Tile(int x, int y, int width, int height) {
		this(x, y, width, height, 0);
	}

	// id 101=one right-leaning slope
	public Tile(int x, int y, int width, int height, int id) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		if (id > 100) {
			if (id == 101) {
				slopeState = SlopeState.RIGHT;
				slope = ((double) height) / ((double) width);
			} else if (id == 102) {
				slopeState = SlopeState.LEFT;
				slope = -((double) height) / ((double) width);
			}
		}
	}

	public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		switch (id) {
		case 101:
			g2.drawImage(cobb_slope_right, scrollx + x, scrolly + y, width, height, null);
			// g2.drawRect(Screen.scrollx + x, Screen.scrolly + y, width, height);
			break;
		case 102:
			g2.drawImage(cobb_slope_left, scrollx + x, scrolly + y, width, height, null);
			// g2.drawRect(Screen.scrollx + x, Screen.scrolly + y, width, height);
			break;
		default:
			g2.fillRect(scrollx + x, scrolly + y, width, height);
		}
	}

	public void drawSide(Graphics2D g2, int scrollx, int scrolly, int which) {
		g2.setColor(Color.blue);
		g2.setStroke(new BasicStroke(8));
		switch (which) {
		case 0:
			g2.drawLine(scrollx + x, scrolly + y, scrollx + x + width, scrolly + y);
			break;
		case 1:
			g2.drawLine(scrollx + x + width, scrolly + y, scrollx + x + width, scrolly + y + height);
			break;
		case 2:
			g2.drawLine(scrollx + x, scrolly + y + height, scrollx + x + width, scrolly + y + height);
			break;
		case 3:
			g2.drawLine(scrollx + x, scrolly + y, scrollx + x, scrolly + y + height);
			break;
		}
		g2.drawString(Integer.toString(height), scrollx + x + width, scrolly + y + (height / 2) + 25);
		g2.drawString(Integer.toString(width), scrollx + x, scrolly + y + height + 50);
		// System.out.println(width); 
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(4));
	}

	public void goTo(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
	}

	// I could easily compress these two into one, but it would be slower
	// because I'd need to cast. So probably not worth stressing over
	public boolean isInside(Point2D.Double point) {
		if (id == 200) {
			System.out.println("S's X: " + point.getX());
			System.out.println((point.getX() >= x && point.getX() <= x + width) + " "
					+ (point.getY() >= y && point.getY() <= y + height));
		}
		if (id < 100) {
			if (point.getY() >= y && point.getY() <= y + height && point.getX() >= x && point.getX() <= x + width) {
				// point.getX() - x <= width && point.getY() - y <= height) {
				return true;
			} else {
				return false;
			}
		} else {
			if (point.getX() >= x && point.getX() <= x + width) {
				if (slopeState == SlopeState.RIGHT) {
					// System.out.println("Is within X for right object!");
					if (point.getY() <= y + height
							&& point.getY() >= ((double) (width - (point.getX() - x))) * slope + y) {
						// System.out.println("And Y!");
						return true;
					} else {
						// System.out.println("...but not Y");
						return false;
					}
				} else {
					if (point.getY() <= y + height && point.getY() >= -(((double) (point.getX() - x)) * slope) + y) {
						return true;
					} else {
						return false;
					}

				}
			} else {
				return false;
			}
		}
	}

	public boolean isInside(int[] point) {
		if (id == 200) {
			System.out.println("(in tile.java, isInside) S's X: " + point[0]);
			System.out.println(
					(point[0] >= x && point[0] <= x + width) + " " + (point[1] >= y && point[1] <= y + height));
		}
		if (id < 100) {
			if (point[1] >= y && point[1] <= y + height && point[0] >= x && point[0] <= x + width) {
				return true;
			} else {
				return false;
			}
		} else {
			if (point[0] >= x && point[0] <= x + width) {// point.getX() - x <= width && point.getY() - y <= height) {
				if (slopeState == SlopeState.RIGHT) {
					if (point[1] <= y + height && point[1] >= ((double) (width - (point[0] - x))) * slope + y) {
						return true;
					} else {
						return false;
					}
				} else {
					if (point[1] <= y + height && point[1] >= -(((double) (point[0] - x)) * slope) + y) {
						return true;
					} else {
						return false;
					}

				}
			} else {
				return false;
			}
		}
	}

	public int getHeight(double px) {
		if (id < 100) {
			return y;
		} else if (slopeState == SlopeState.RIGHT) {
			int dy = (int) ((width - (px - (double) x)) * slope);
			if (height - dy > 8) {
				return dy + y + 8;
			} else {
				return y + height;
			}
			// return ((int)((px-(double)x)*slope))+y;
		} else {// if (slopeState == SlopeState.LEFT){
			int dy = (int) ((px - (double) x) * slope);
			if (height + dy > 8) {
				return -dy + y + 8;
			} else {
				return y + height;
			}
			// return ((int)((px-(double)x)*slope))+y;
		}
	}

	// Difference between the parameter point and the x/y location of the object?
	public double[] getDifference(Point2D.Double point) {
		double diff[] = new double[2];
		diff[0] = point.getX() - x;
		diff[1] = point.getY() - y;
		return diff;
	}

	public void resize(double dx, double dy, double dwidth, double dheight) {
		x -= dx;
		y -= dy;
		width += dwidth;
		height += dheight;
	}

	public void snap() {
		x = (int) (Math.round(((double) x) / 20) * 20);
		y = (int) (Math.round(((double) y) / 20) * 20);
		width = (int) (Math.round(((double) width) / 20) * 20);
		height = (int) (Math.round(((double) height) / 20) * 20);
	}

	public void checkIsVisible() {
		// If the X value could be rendered
		// if (id==10) {
		// System.out.println((Screen.scrollx + x - 200)+ ">"+ (Main.screenSize.width) +
		// ", ");
		// }
		if (Screen.scrollx + x - 200 > Main.gameSize.width || Screen.scrollx + x + width < -200
				|| Screen.scrolly + y + height < -200 || Screen.scrolly + y - 200 > Main.gameSize.height) {
			// System.out.println("This one is not in frame");
			isVisible = false;
			return;
			// if(Screen.scrolly+y)
		}
		isVisible = true;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public String toString() {
		switch (id) {
		case 101:
			type = "Right-leaning slope";
			break;
		case 102:
			type = "Left-leaning slope";
			break;
		case 0:
			type = "Basic black block";
			break;
		}
		return "Tile,\t\t" + x + ",\t" + y + ",\t" + width + ",\t" + height + ",\t" + id + ",\t"+type+"\n";
	}

	public void setText(String name) {
	}

	public String getText() {
		return "";
	}

	public int getScreenY() {
		return Screen.scrolly + y;
	}

	public int getScreenY(int scrolly) {
		return scrolly + y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		
		if (id > 100) {
			if (id == 101) {
				slopeState = SlopeState.RIGHT;
				slope = ((double) height) / ((double) width);
			} else if (id == 102) {
				slopeState = SlopeState.LEFT;
				slope = -((double) height) / ((double) width);
			}
		} else {
			slopeState = SlopeState.NONE;
			slope = 0;
		}
		if (id>=50 && id <100) {
			System.out.println("Incorrect method of changing object to Interactable.");
		}

	}

	public boolean shouldRemove() {
		return toRemove;
	}

	public void drawBoxAround(Graphics2D g2, int scrollx, int scrolly) {
		g2.setStroke(new BasicStroke(6));
		g2.setColor(Color.BLUE);
		g2.drawRect(scrollx + x, scrolly + y, width, height);
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(4));

	}

	public void drawBoxAround(Graphics2D g2) {
		drawBoxAround(g2, Screen.scrollx, Screen.scrolly);
	}

	// return 1 if effects Siege, 2 if changes text
	// Will eventually need to make more complicated if we want to add buttons or
	// something
	public int interact() {
		return 0;
	}
	
	public boolean isInteractable() {
		return false;
	}
}
