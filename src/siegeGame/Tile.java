package siegeGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

// Id:
// 0 = black square
// 1 = respawn destination (appears as Siege)
// 2 = invisible block
// 3 = open/closed door
// >50 <100 = interactable
// 50 = bouncy default
// 60 = destructable block
// 70 = text prompt
// 71 = activated text prompt
// 99 = finish line
// >100 = slope
// 101 = right slope
// 102 = left slope
// -1 = respawn block 
// -2 = button for open/closed door

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

	protected int clipType = 0;
	// 0 is all angles, 1 is vertical only, 2 is left only, and 3 is right only
	protected boolean can_clip_vertical = true;
	protected boolean can_clip_left = true;
	protected boolean can_clip_right = true;
	protected Graphic animated_graphic = null;
	public boolean should_be_saved = true;

	public enum SlopeState {
		NONE, LEFT, RIGHT
	}

	SlopeState slopeState = SlopeState.NONE;

	// To make the level files a bit more legible
	protected String type = "Unrecognized";

	public Tile(int x, int y, int width, int height) {
		this(x, y, width, height, 0, 0);
	}

	// id 101=one right-leaning slope
	public Tile(int x, int y, int width, int height, int colissionType, int id) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.id = id;
		this.clipType = colissionType;
		if (id > 100) {
			if (id == 101) {
				slopeState = SlopeState.RIGHT;
				slope = ((double) height) / ((double) width);
			} else if (id == 102) {
				slopeState = SlopeState.LEFT;
				slope = -((double) height) / ((double) width);
			}
		}
		// Let's try to make it so that this doesn't need to calculate every frame
		switch (clipType) {
		case 0:
			can_clip_vertical = true;
			can_clip_left = true;
			can_clip_right = true;
			break;
		case 1:
			can_clip_vertical = true;
			can_clip_left = false;
			can_clip_right = false;
			break;
		case 2:
			can_clip_vertical = false;
			can_clip_left = true;
			can_clip_right = false;
			break;
		case 3:
			can_clip_vertical = false;
			can_clip_left = false;
			can_clip_right = true;
			break;
		}

		if (id == 1) {
			makeIntangible();
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
		case 0:
			g2.fillRect(scrollx + x, scrolly + y, width, height);
			break;
		case 1:
			if (Main.debug) {
				g2.drawImage(Player.IDLE, scrollx + x - 67, scrolly + y - 223 + 250, scrollx + 200 + x - 67,
						scrolly + y + 250 - 223 + 250, 0, 0, 200, 250, null);
			}
		default:
			if (Main.debug) {
				g2.drawRect(scrollx + x, scrolly + y, width, height);
			}
		}
		if (Main.debug) {
			g2.setColor(Color.yellow);
			drawCollision(g2, scrollx, scrolly);
			g2.setColor(Color.black);
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

	public void drawCollision(Graphics2D g2, int scrollx, int scrolly) {
		if (clipType == 0) {
			g2.drawRect(scrollx + (width / 2) + x - 20, scrolly + y - 20 + (height / 2), 40, 40);
		} else if (clipType == 1) {
			int[] xpts = { scrollx + x + (width / 2), scrollx + x + (width / 3), scrollx + x + (width * 2 / 3) };
			int[] ypts = { y + scrolly, y + height + scrolly, y + (height / 4) + scrolly,
					scrolly + y + (height * 3 / 4) };
			g2.drawLine(xpts[0], ypts[0], xpts[0], ypts[1]);
			g2.drawLine(xpts[0], ypts[0], xpts[1], ypts[2]);
			g2.drawLine(xpts[0], ypts[0], xpts[2], ypts[2]);
			g2.drawLine(xpts[0], ypts[1], xpts[1], ypts[3]);
			g2.drawLine(xpts[0], ypts[1], xpts[2], ypts[3]);
		} else if (clipType == 2) {
			int[] xpts = { scrollx + x + (width / 2), scrollx + x, scrollx + x + (width / 4) };
			int[] ypts = { y + (height / 2) + scrolly, y + (height / 3) + scrolly, scrolly + y + (height * 2 / 3) };
			g2.drawLine(xpts[0], ypts[0], xpts[1], ypts[0]);
			g2.drawLine(xpts[1], ypts[0], xpts[2], ypts[1]);
			g2.drawLine(xpts[1], ypts[0], xpts[2], ypts[2]);
		} else if (clipType == 3) {
			int[] xpts = { scrollx + x + (width / 2), scrollx + x + width, scrollx + x + (width * 3 / 4) };
			int[] ypts = { y + (height / 2) + scrolly, y + (height / 3) + scrolly, scrolly + y + (height * 2 / 3) };
			g2.drawLine(xpts[0], ypts[0], xpts[1], ypts[0]);
			g2.drawLine(xpts[1], ypts[0], xpts[2], ypts[1]);
			g2.drawLine(xpts[1], ypts[0], xpts[2], ypts[2]);
		}
	}

	public void goTo(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
		if (animated_graphic != null) {
			animated_graphic.goTo(x, y);
		}
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
		int check_below = 12;
		if (id < 100) {
			return y;
		} else if (slopeState == SlopeState.RIGHT) {
			int dy = (int) ((width - (px - (double) x)) * slope);
			if (height - dy > check_below) {
				return dy + y + check_below;
			} else {
				return y + height;
			}
			// return ((int)((px-(double)x)*slope))+y;
		} else {// if (slopeState == SlopeState.LEFT){
			int dy = (int) ((px - (double) x) * slope);
			if (height + dy > check_below) {
				return -dy + y + check_below;
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

	public void cycleCollision() {
		clipType += 1;
		if (clipType > 3) {
			clipType = 0;
		}
		// Technically, this will never be used
		switch (clipType) {
		case 0:
			can_clip_vertical = true;
			can_clip_left = true;
			can_clip_right = true;
			break;
		case 1:
			can_clip_vertical = true;
			can_clip_left = false;
			can_clip_right = false;
			break;
		case 2:
			can_clip_vertical = false;
			can_clip_left = true;
			can_clip_right = false;
			break;
		case 3:
			can_clip_vertical = false;
			can_clip_left = false;
			can_clip_right = true;
			break;
		}
	}

	// Snap the tile to nearest values that are a multiple of 20, to make it look
	// more seamless.
	public void snap() {
		if (animated_graphic != null) {
			return;
		}
		x = (int) (Math.round(((double) x) / 20) * 20);
		y = (int) (Math.round(((double) y) / 20) * 20);
		// This prevents an obscure bug where, if the width was an exact multiple of 10,
		// the x location and width would round in different directions, causing them to
		// snap unevenly
		if (width % 10 == 0) {
			width -= 1;
		}
		if (height % 10 == 0) {
			height -= 1;
		}
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
		case 1:
			type = "Respawn Destination";
			break;
		case 2:
			type = "Invisible block";
			break;
		}
		return "Tile,\t\t" + x + ",\t" + y + ",\t" + width + ",\t" + height + ",\t" + clipType + ",\t" + id + ",\t\t"
				+ type + "\n";
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
		if (id >= 50 && id < 100) {
			System.out.println("Incorrect method of changing object to Interactable.");
		}

	}

	public boolean shouldRemove() {
		return toRemove;
	}

	public void goToTied(double x, double y) {
	}

	public Tile getTied() {
		System.out.println("This message should not appear (getTied)");
		return null;
	}

	public int[] getTiedXY() {
		System.out.println("This message should not appear (getTiedXY)");
		return null;
	}

	public void cleanup() {
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
	
	public void dontSaveThis() {
		should_be_saved = false;
	}

	// return 1 if effects Siege, 2 if changes text
	// Will eventually need to make more complicated if we want to add buttons or
	// something
	public int interact() {
		return 0;
	}
	public void setData(int d) {
	}

	public boolean canDetectLeft() {
		return can_clip_left;
	}

	public boolean canDetectRight() {
		return can_clip_right;
	}

	public boolean canDetectVertical() {
		return can_clip_vertical;
	}

	public boolean isInteractable() {
		return false;
	}

	public void setToRemove() {
		toRemove = true;
	}
	
	protected void makeIntangible() {
		can_clip_vertical = false;
		can_clip_left = false;
		can_clip_right = false;
	}
	
	protected void refreshClipType() {
		switch (clipType) {
		case 0:
			can_clip_vertical = true;
			can_clip_left = true;
			can_clip_right = true;
			break;
		case 1:
			can_clip_vertical = true;
			can_clip_left = false;
			can_clip_right = false;
			break;
		case 2:
			can_clip_vertical = false;
			can_clip_left = true;
			can_clip_right = false;
			break;
		case 3:
			can_clip_vertical = false;
			can_clip_left = false;
			can_clip_right = true;
			break;
		}
	}
}
