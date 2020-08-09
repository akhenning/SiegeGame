package siegeGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

public class Player {
	public boolean debug = true;
	
	public static Image walk = Toolkit.getDefaultToolkit().getImage("assets/walk.png");
	public static Image IDLE = Toolkit.getDefaultToolkit().getImage("assets/idle.png");
	// public static Image jump =
	// Toolkit.getDefaultToolkit().getImage("assets/jump.png");
	public static Image jumpsquat = Toolkit.getDefaultToolkit().getImage("assets/jumpsquat.png");
	public static Image jumping = Toolkit.getDefaultToolkit().getImage("assets/jumping.png");
	public static Image hovering = Toolkit.getDefaultToolkit().getImage("assets/hovering.png");
	public static Image strike = Toolkit.getDefaultToolkit().getImage("assets/air_strike.png");
	public static Image landing = Toolkit.getDefaultToolkit().getImage("assets/landing.png");

	private int direction = 1;
	private double xspeed = 0;
	private double yspeed = 0;
	private double litx = 0;
	private double lity = 0;
	private int x;
	private int y;
	private boolean isSpace = false;
	int width = -1;

	public enum State {
		GROUNDED, JUMPSQUAT, JUMPING, HOVERING, DESCENDING, LANDING
	}

	public enum Animation {
		GROUNDED, JUMPSQUAT, JUMPING, HOVERING, DESCENDING, LANDING, IDLE, WALKING, NONE
	}

	State state = State.GROUNDED;
	private Screen screen;

	private int animationFrame = 0;
	private Animation previousAnimation = Animation.NONE;
	private int rotations = 0;

	public Player(Screen screen) {
		this.screen = screen;
		litx = 300;
		x = 300;
		lity = 200;
		y = 200;
	}

	public void draw(Graphics2D g2) {
		// feet at 67, 223 when facing right
		System.out.println(state);
		if (state != State.GROUNDED) {
			if (state == State.JUMPSQUAT) {
				int offset = 300;
				// Check if this is the first frame of the new animation
				if (previousAnimation != Animation.JUMPSQUAT) {
					animationFrame = 0;
					previousAnimation = Animation.JUMPSQUAT;
					width = MakeSureImageHasLoaded(jumpsquat);
				}
				if (direction == 1) {
					g2.drawImage(jumpsquat, x - 36 - 67, y - 85 - 223, offset + x - 36 - 67, y + 340 - 85 - 223,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				} else {
					g2.drawImage(jumpsquat, offset + x - 36 - 67 - 32, y - 85 - 223, x - 36 - 67 - 32,
							y + 340 - 85 - 223, offset * animationFrame, 0, offset * animationFrame + offset, 340,
							null);
				}
				animationFrame += 1;
				//if (offset * animationFrame + 300 > width) {
					// animationFrame -= 1;
					// state = State.JUMPING;
				//}
			} else if (state == State.JUMPING) {
				int offset = 300;
				// Check if this is the first frame of the new animation
				if (previousAnimation != Animation.JUMPING) {
					animationFrame = 0;
					previousAnimation = Animation.JUMPING;
					width = MakeSureImageHasLoaded(jumping);
				}
				if (direction == 1) {
					g2.drawImage(jumping, x - 36 - 67, y - 85 - 223, offset + x - 36 - 67, y + 340 - 85 - 223,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				} else {
					g2.drawImage(jumping, offset + x - 36 - 67 - 32, y - 85 - 223, x - 36 - 67 - 32, y + 340 - 85 - 223,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				}
				animationFrame += 1;

				// check if end of animation without hardcoding number of frames
				if (offset * animationFrame + 300 > width) {
					state = State.HOVERING;
				}
			} else if (state == State.HOVERING) {
				int offset = 300;
				if (previousAnimation != Animation.HOVERING) {
					animationFrame = 0;
					previousAnimation = Animation.HOVERING;
					rotations = 0;
					width = MakeSureImageHasLoaded(hovering);
					// The two following if statements allow you to skip hovering if you wish
					if (!isSpace) {
						state = State.DESCENDING;
					}
				}
				if (state != State.DESCENDING) {
					int lengthOfAnimation = width / offset;
					if (direction == 1) {
						g2.drawImage(hovering, x - 36 - 67, y - 85 - 223 - (4 * (lengthOfAnimation - animationFrame)),
								offset + x - 36 - 67, y + 340 - 85 - 223 - (4 * (lengthOfAnimation - animationFrame)),
								offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
					} else {
						g2.drawImage(hovering, offset + x - 36 - 67 - 32,
								y - 85 - 223 - (4 * (lengthOfAnimation - animationFrame)), x - 36 - 67 - 32,
								y + 340 - 85 - 223 - (4 * (lengthOfAnimation - animationFrame)),
								offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
					}
					animationFrame += 1;
					if (offset * animationFrame == width) {
						if (!isSpace || rotations > 2) {
							state = State.DESCENDING;
						}
					}
					if (offset * animationFrame + 300 > width) {
						animationFrame = 0;
						rotations += 1;
					}
				} else {
					// draws the first frame of strike instead of nothing in case that this is skipped frame 1
					if (direction == 1) {
						g2.drawImage(strike, x - 36 - 67, y - 85 - 223, offset + x - 36 - 67, y + 340 - 85 - 223, 0, 0,
								offset, 340, null);
					} else {
						g2.drawImage(strike, offset + x - 36 - 67 - 32, y - 85 - 223, x - 36 - 67 - 32,
								y + 340 - 85 - 223, 0, 0, offset, 340, null);
					}
				}
			} else if (state == State.DESCENDING) {
				int offset = 300;
				if (previousAnimation != Animation.DESCENDING) {
					animationFrame = 0;
					previousAnimation = Animation.DESCENDING;
					width = MakeSureImageHasLoaded(strike);
				}
				if (direction == 1) {
					g2.drawImage(strike, x - 36 - 67, y - 85 - 223, offset + x - 36 - 67, y + 340 - 85 - 223,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				} else {
					g2.drawImage(strike, offset + x - 36 - 67 - 32, y - 85 - 223, x - 36 - 67 - 32, y + 340 - 85 - 223,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				}
				animationFrame += 1;
				if (offset * animationFrame + 300 > width) {
					animationFrame -= 1;
				}
			} else if (state == State.LANDING) {
				int offset = 300;
				if (previousAnimation != Animation.LANDING) {
					animationFrame = 0;
					previousAnimation = Animation.LANDING;
					width = MakeSureImageHasLoaded(landing);
				}
				if (direction == 1) {
					g2.drawImage(landing, x - 36 - 67, y - 85 - 223, offset + x - 36 - 67, y + 340 - 85 - 223,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				} else {
					g2.drawImage(landing, offset + x - 36 - 67 - 32, y - 85 - 223, x - 36 - 67 - 32, y + 340 - 85 - 223,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				}
				animationFrame += 1;
				if (offset * animationFrame + 300 > width) {
					state = State.GROUNDED;
				}
			}
		} else if (xspeed == 0) {
			int offset = 200;
			if (previousAnimation != Animation.IDLE) {
				animationFrame = 0;
				previousAnimation = Animation.IDLE;
				width = MakeSureImageHasLoaded(IDLE);

			}
			if (direction == 1) {
				g2.drawImage(IDLE, x - 67, y - 223, 200 + x - 67, y + 250 - 223, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			} else {
				g2.drawImage(IDLE, 200 + x - 67 - 4, y - 223, x - 67 - 4, y + 250 - 223, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			}
			animationFrame += 1;
			if (offset * animationFrame + 300 > width) {
				animationFrame = 0;
			}
		} else {
			int offset = 200;
			if (previousAnimation != Animation.WALKING) {
				animationFrame = 7;
				previousAnimation = Animation.WALKING;
				width = MakeSureImageHasLoaded(walk);
			}
			if (direction == 1) {
				g2.drawImage(walk, x - 5 - 67, y + 4 - 223, x + 195 - 67, y + 250 + 4 - 223, offset * animationFrame, 0,
						offset * animationFrame + 200, 250, null);
			} else {
				g2.drawImage(walk, x + 195 - 67 - 4, y + 4 - 223, x - 5 - 67 - 4, y + 250 + 4 - 223,
						offset * animationFrame, 0, offset * animationFrame + 200, 250, null);
			}
			animationFrame += 1;
			if (offset * animationFrame + 300 > width) {
				animationFrame = 0;
			}
		}
		// These are the collision points, which are simply being visualized
		if (debug) {
			g2.drawRect(x - 2, y - 2, 4, 4);
			g2.drawRect(x - 2 + 62, y - 2, 4, 4);
			g2.drawRect(x - 17, y - 42, 4, 4); // left
			g2.drawRect(x + 75, y - 42, 4, 4);
		}

	}

	public void calcMove(double xMove, boolean isShift, boolean isJump) {
		isSpace = isJump;
		// System.out.println("start"+xspeed);
		if (state == State.GROUNDED) {
			if (xMove == 1) {
				direction = 1;
			} else if (xMove == -1) {
				direction = -1;
			}
		}
		// System.out.println(lity+" "+Screen.scrolly+" " + (lity-Screen.scrolly));

		// if not GROUNDED
		if (state != State.GROUNDED) {
			// if not in JUMPSQUAT, increased movement
			if (state == State.JUMPING || state == State.HOVERING || state == State.DESCENDING) {
				// System.out.println("Airborne"+xspeed);
				if (state == State.HOVERING && isJump) {
					yspeed -= .75;
				}
				yspeed += 1;
				lity += yspeed;
				xspeed = xspeed + (xMove);
				litx += xspeed;

				// frame of takeoff
			} else if (state == State.JUMPSQUAT) {
				if (animationFrame == 8) {
					// System.out.println("JUMPING"+xspeed);
					yspeed = -20;
					xspeed = xMove * 10;
					litx += xspeed;
					lity += yspeed;
					state = State.JUMPING;
				} else {
					// System.out.println("JUMPSQUAT"+xspeed);
					xspeed *= .7;
					litx += xspeed;
				}
			} else { // if in JUMPSQUAT, move slowly
			}
		} // transition from GROUNDED to jump squat
		else if (state == State.GROUNDED && isJump) {
			// System.out.println("Starting to jump");
			state = State.JUMPSQUAT;
			xspeed *= .9;
			litx += xspeed;
		} // if GROUNDED and not in JUMPSQUAT
		else {
			// System.out.println("WALKING");
			xspeed = xMove * 5.5;
			litx += xspeed;
		}

		if (state != State.GROUNDED && state != State.JUMPSQUAT && state != State.LANDING) {
			// System.out.println("Screen's X: " + litx + " Game's X: " +
			// (litx-Screen.scrollx));
			Point2D.Double leftFoot = new Point2D.Double(litx - Screen.scrollx, lity - Screen.scrolly);
			Point2D.Double rightFoot = new Point2D.Double(litx - Screen.scrollx + 62, lity - Screen.scrolly);
			int groundLevel = screen.checkLandingCollision(leftFoot, rightFoot);
			// System.out.println(x+ " x1: "+litx);
			// System.out.println(groundLevel);
			if (groundLevel != 1000001) {// lity-Screen.scrolly > 600)
				// System.out.println("LANDING");
				lity = groundLevel + Screen.scrolly;
				yspeed = 0;
				xspeed = 0;
				// don't move until finished animation
				if (true) {// 300 * animationFrame + 600 > jump.getWidth(null)) {
					state = State.LANDING;
				}
			}
		}
		if (state == State.GROUNDED) {
			Point2D.Double leftFoot = new Point2D.Double(litx - Screen.scrollx, lity - Screen.scrolly);
			Point2D.Double rightFoot = new Point2D.Double(litx - Screen.scrollx + 62, lity - Screen.scrolly);
			int groundLevel = screen.checkLandingCollision(leftFoot, rightFoot);
			if (groundLevel == 1000001) {
				state = State.JUMPING;
				animationFrame = 1;
				previousAnimation = Animation.JUMPING;
			} else {
				// System.out.println(groundLevel);
				lity = groundLevel + Screen.scrolly;
			}
		}

		Point2D.Double left = new Point2D.Double(litx - Screen.scrollx - 15, lity - 40 - Screen.scrolly);
		Point2D.Double right = new Point2D.Double(litx - Screen.scrollx + 77, lity - 40 - Screen.scrolly);
		int wallLevel = screen.checkHorizontalCollision(left, right);
		if (wallLevel != -1000001) {
			litx = wallLevel + Screen.scrollx;
			xspeed = 0;
		}

		// System.out.println("end"+xspeed);
		if (litx > 800) {
			Screen.scrollx = Screen.scrollx - (int) (litx - 800);
			litx = 800;
		} else if (litx < 200) {
			Screen.scrollx = Screen.scrollx + (int) (200 - litx);
			litx = 200;
		}
		x = (int) litx;
		if (lity > 600) {
			Screen.scrolly = Screen.scrolly - (int) (lity - 600);
			lity = 600;
		} else if (lity < 200) {
			Screen.scrolly = Screen.scrolly + (int) (200 - lity);
			lity = 200;
		}
		y = (int) lity;

	}

	// Was having issues with it not loading in time, where getWidth() was 
	// returning -1, therefore skipping animations
	private int MakeSureImageHasLoaded(Image image) {
		int width = image.getWidth(null);
		int failcase = 0;
		while (width == -1) {
			failcase += 1;
			try {
				Thread.sleep(2);
				width = jumping.getWidth(null);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (failcase > 30) {
				System.out.println("ERROR: IMAGE FAILED TO LOAD");
				return -1;
			}
		}
		return width;
	}

}
