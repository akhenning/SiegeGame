package siegeGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

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

	private int animationFrame = 0;
	private int previousAnimation = -1;

	public Player() {
		lity = 200;
		y = 200;
	}

	public void draw(Graphics2D g2) {
		// g2.drawImage(siege,100,100,400,533,offset*animationFrame,0,offset*animationFrame+300,400,null);
		// g2.drawImage(siege,100,100,250,315,offset*animationFrame,0,offset*animationFrame+300,430,null);
		if (!grounded) {
			int offset = 300;
			if (previousAnimation != 2) {
				animationFrame = 0;
				previousAnimation = 2;
			}
			if (direction == 1) {
				g2.drawImage(jump, x - 36, y + 100 - 85, offset + x - 36, y + 440 - 85, offset * animationFrame, 0,
						offset * animationFrame + offset, 340, null);
			} else {
				g2.drawImage(jump, offset + x - 36, y + 100 - 85, x - 36, y + 440 - 85, offset * animationFrame, 0,
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
				g2.drawImage(idle, x, y + 100, 200 + x, y + 350, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			} else {
				g2.drawImage(idle, 200 + x, y + 100, x, y + 350, offset * animationFrame, 0,
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
				g2.drawImage(walk, x - 5, y + 100 + 4, x + 195, y + 350 + 4, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			} else {
				g2.drawImage(walk, x + 195, y + 100 + 4, x - 5, y + 350 + 4, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			}
			animationFrame += 1;
			if (offset * animationFrame + 300 > walk.getWidth(null)) {
				animationFrame = 0;
			}
		}
		g2.drawRect(x, y, 5, 5);
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

		// if not grounded
		if (!grounded) {
			// if not in jumpsquat, increased movement
			if (animationFrame > 9) {
				// landing
				if (lity > 200) {
					// System.out.println("Landing");
					lity = 201;
					yspeed = 0;
					xspeed = 0;
					// don't move until finished animation
					if (300 * animationFrame + 600 > jump.getWidth(null)) {
						System.out.println("Landed");
						grounded = true;
					}
					// if airborne
				} else {
					// System.out.println("Airborne"+xspeed);
					yspeed += 1;
					lity += yspeed;
					xspeed = xspeed + (xMove);
					litx += xspeed;
				}
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
		// System.out.println("end"+xspeed);
		x = (int) litx;
		y = (int) lity;

	}

}
