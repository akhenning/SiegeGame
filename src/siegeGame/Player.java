package siegeGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

public class Player {
	public static Image walk = Toolkit.getDefaultToolkit().getImage("assets/walk.png");
	public static Image idle = Toolkit.getDefaultToolkit().getImage("assets/idle.png");
	public static Image jump = Toolkit.getDefaultToolkit().getImage("assets/jump.png");

	private int direction = 1;
	private double xspeed = 0;
	private double yspeed = 0;
	private double litx = 0;
	private double lity = 0;
	private int x;
	private int y;
	private boolean grounded = true;
	private Screen screen;

	private int animationFrame = 0;
	private int previousAnimation = -1;

	public Player(Screen screen) {
		this.screen=screen;
		litx=300;
		x=300;
		lity = 200;
		y = 200;
	}

	public void draw(Graphics2D g2) {
		// feet at 67, 223
		// g2.drawImage(siege,100,100,400,533,offset*animationFrame,0,offset*animationFrame+300,400,null);
		// g2.drawImage(siege,100,100,250,315,offset*animationFrame,0,offset*animationFrame+300,430,null);
		if (!grounded) {
			int offset = 300;
			if (previousAnimation != 2) {
				animationFrame = 0;
				previousAnimation = 2;
			}
			if (direction == 1) {
				g2.drawImage(jump, x - 36-67, y - 85-223, offset + x - 36-67, y + 340 - 85-223, offset * animationFrame, 0,
						offset * animationFrame + offset, 340, null);
			} else {
				g2.drawImage(jump, offset + x - 36-67, y - 85-223, x - 36-67, y + 340 - 85-223, offset * animationFrame, 0,
						offset * animationFrame + offset, 340, null);
			}
			animationFrame += 1;
			if (offset * animationFrame + 300 > jump.getWidth(null)) {
				animationFrame -= 1;
			}
		} else if (xspeed == 0) {
			int offset = 200;
			if (previousAnimation != 0) {
				animationFrame = 0;
				previousAnimation = 0;
			}
			// g2.drawImage(idle,100,100,300,350,offset*animationFrame,0,offset*animationFrame+200,250,null);
			if (direction == 1) {
				g2.drawImage(idle, x-67, y-223, 200 + x-67, y + 250-223, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			} else {
				g2.drawImage(idle, 200 + x - 67, y-223, x-67, y + 250-223, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			}
			animationFrame += 1;
			if (offset * animationFrame + 300 > idle.getWidth(null)) {
				animationFrame = 0;
			}
		} else {
			int offset = 200;
			if (previousAnimation != 1) {
				animationFrame = 7;
				previousAnimation = 1;
			}
			if (direction == 1) {
				g2.drawImage(walk, x - 5-67, y + 4-223, x + 195-67, y + 250 + 4-223, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			} else {
				g2.drawImage(walk, x + 195-67, y + 4-223, x - 5-67, y + 250 + 4-223, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			}
			animationFrame += 1;
			if (offset * animationFrame + 300 > walk.getWidth(null)) {
				animationFrame = 0;
			}
		}
		g2.drawRect(x-2, y-2, 4, 4);
		g2.drawRect(x-2+62, y-2, 4, 4); 
		g2.drawRect(x-17, y-42, 4, 4); // left
		g2.drawRect(x+75, y-42, 4, 4);
		
	}

	public void calcMove(double xMove, boolean isShift, boolean isJump) {
		// System.out.println("start"+xspeed);
		if (grounded) {
			if (xMove == 1) {
				direction = 1;
			} else if (xMove == -1) {
				direction = -1;
			}
		}
		//System.out.println(lity+" "+Screen.scrolly+" " + (lity-Screen.scrolly));

		// if not grounded
		if (!grounded) {
			// if not in jumpsquat, increased movement
			if (animationFrame > 9) {
					// System.out.println("Airborne"+xspeed);
					if(!isJump) {
						yspeed += 1;
					}
					lity += yspeed;
					xspeed = xspeed + (xMove);
					litx += xspeed;
				
				// frame of takeoff
			} else if (animationFrame == 9) {
				// System.out.println("Jumping"+xspeed);
				xspeed = xMove * 10;
				litx += xspeed;
				lity += yspeed;
			} else { // if in jumpsquat, move slowly
				// System.out.println("Jumpsquat"+xspeed);
				xspeed *= .7;
				litx += xspeed;
			}
		} // transition from grounded to jump squat
		else if (grounded && isJump) {
			// System.out.println("Starting to jump");
			grounded = false;
			yspeed = -15;
			xspeed *= .9;
			litx += xspeed;
		} // if grounded and not in jumpsquat
		else {
			// System.out.println("Walking");
			xspeed = xMove * 5.5;
			litx += xspeed;
		}
		
		if (!grounded && animationFrame > 9 ) {
			//System.out.println("Screen's X: " + litx + " Game's X: " + (litx-Screen.scrollx));
			Point2D.Double leftFoot = new Point2D.Double(litx-Screen.scrollx, lity-Screen.scrolly);
			Point2D.Double rightFoot =new Point2D.Double(litx-Screen.scrollx+62, lity-Screen.scrolly);
			int groundLevel= screen.checkLandingCollision(leftFoot,rightFoot);
			//System.out.println(x+ " x1: "+litx);
			//System.out.println(groundLevel);
			if (groundLevel!=1000001) {//lity-Screen.scrolly > 600)
				// System.out.println("Landing");
				lity = groundLevel+Screen.scrolly;
				yspeed = 0;
				xspeed = 0;
				// don't move until finished animation
				if (true) {//300 * animationFrame + 600 > jump.getWidth(null)) {
					grounded = true;
				}
			}
		}
		if (grounded) {
			Point2D.Double leftFoot =new Point2D.Double(litx-Screen.scrollx, lity-Screen.scrolly);
			Point2D.Double rightFoot = new Point2D.Double(litx-Screen.scrollx+62, lity-Screen.scrolly);
			int groundLevel= screen.checkLandingCollision(leftFoot,rightFoot);
			if (groundLevel==1000001) {
				grounded=false;
				animationFrame = 9;
				previousAnimation = 2;
			} else {
				//System.out.println(groundLevel);
				lity=groundLevel+Screen.scrolly;
			}
		}

		Point2D.Double left =  new Point2D.Double(litx-Screen.scrollx-15, lity-40-Screen.scrolly);
		Point2D.Double right =  new Point2D.Double(litx-Screen.scrollx+77, lity-40-Screen.scrolly);
		int wallLevel= screen.checkHorizontalCollision(left,right);
		if (wallLevel!=-1000001) {
			litx=wallLevel+Screen.scrollx;
			xspeed=0;
		}
		
		
		// System.out.println("end"+xspeed);
		if (litx > 800) {
			Screen.scrollx=Screen.scrollx-(int)(litx-800);
			litx=800;
		} else if (litx<200) {
			Screen.scrollx=Screen.scrollx+(int)(200-litx);
			litx=200;
		}
		x = (int) litx;
		if (lity > 600) {
			Screen.scrolly=Screen.scrolly-(int)(lity-600);
			lity=600;
		} else if (lity<200) {
			Screen.scrolly=Screen.scrolly+(int)(200-lity);
			lity=200;
		}
		y = (int) lity;

	}

}
