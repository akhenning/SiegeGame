package siegeGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

public class Tile {
	int x;
	int y;
	int width;
	int height;
	int id;
	boolean isVisible = false;
	public static Image cobb_slope_right = Toolkit.getDefaultToolkit().getImage("assets/cobblestonesloperight.png");
	public static Image cobb_slope_left = Toolkit.getDefaultToolkit().getImage("assets/cobblestoneslopeleft.png");
	double slope = 0;
	public enum SlopeState {
		NONE, LEFT, RIGHT
	}
	SlopeState slopeState = SlopeState.NONE;

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
		if (id>100) {
			if (id == 101) {
				slopeState = SlopeState.RIGHT;
				slope = ((double)height)/((double)width);
			} else if (id == 102) {
				slopeState = SlopeState.LEFT;
				slope = -((double)height)/((double)width);
			}
		}
	}

	public void draw(Graphics2D g2) {
		 switch(id) {
		  case 101:
			g2.drawImage(cobb_slope_right, Screen.scrollx + x, Screen.scrolly + y,width,height, null);
			//g2.drawRect(Screen.scrollx + x, Screen.scrolly + y, width, height);
		    break;
		  case 102:
			g2.drawImage(cobb_slope_left, Screen.scrollx + x, Screen.scrolly + y,width,height, null);
			//g2.drawRect(Screen.scrollx + x, Screen.scrolly + y, width, height);
			break;
		  default: 
			g2.fillRect(Screen.scrollx + x, Screen.scrolly + y, width, height);
		}
	}

	// I could easily compress these two into one, but it would be slower 
	// because I'd need to cast. So probably not worth stressing over
	public boolean isInside(Point2D.Double point) {
		if (id==200) {
			System.out.println("S's X: "+point.getX());
			System.out.println((point.getX() >= x && point.getX()<=x+width)+ " " +(point.getY() >= y && point.getY()<=y+height ));
		}
		if (id<100) {
			if (point.getY() >= y && point.getY()<=y+height && point.getX() >= x && point.getX()<=x+width) {//point.getX() - x <= width && point.getY() - y <= height) {
				return true;
			} else {
				return false;
			}
		} else {
			if (point.getX() >= x && point.getX()<=x+width) {
				if (slopeState == SlopeState.RIGHT) {
					if(point.getX()<=y+height && point.getY() >= ((double)(width-(point.getX()-x)))*slope+y) {
						return true;
					} else {
						return false;
					}
				} else {
					if(point.getX()<=y+height && point.getY() >= ((double)(point.getX()-x))*slope+y) {
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
			System.out.println("S's X: " + point[0]);
			System.out.println(
					(point[0] >= x && point[0] <= x + width) + " " + (point[1] >= y && point[1] <= y + height));
		}
		if (id<100) {
			if (point[1] >= y && point[1] <= y + height && point[0] >= x && point[0] <= x + width) {
				return true;
			} else {
				return false;
			}
		} else {
			if (point[0] >= x && point[0]<=x+width) {//point.getX() - x <= width && point.getY() - y <= height) {
				if (slopeState == SlopeState.RIGHT) {
					if(point[0]<=y+height && point[1] >= ((double)(width-(point[0]-x)))*slope+y) {
						return true;
					} else {
						return false;
					}
				} else {
					if(point[0]<=y+height && point[1] >= ((double)(point[0]-x))*slope+y) {
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
		if(id<100) {
			return y;
		} else {
			int dy = (int)((width-(px-(double)x))*slope);
			if(height-dy>6) {
				return dy+y+6;
			} else {
				return y+height;
			}
			//return ((int)((px-(double)x)*slope))+y;
		}
	}
	
	public void checkIsVisible() {
		// If the X value could be rendered
		if (Screen.scrollx + x > 2000 || Screen.scrollx + x + width < -200) {
			if (-Screen.scrolly + y + height < -200 || -Screen.scrolly + y > 1000) {
				//System.out.println("This one is not in frame");
				isVisible=false;
				return;
			}
			// if(Screen.scrolly+y)
		}
		isVisible=true;
	}
	public boolean isVisible() {
		return isVisible;
	}
}
