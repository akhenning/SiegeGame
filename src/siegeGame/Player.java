package siegeGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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
	public double litx = 0;
	public double lity = 0;
	public int x;
	public int y;
	private boolean isSpace = false;
	int width = -1;
	private boolean isHitbox = false;
	boolean onSlope = false;
	double slope = 0;

	public enum State {
		GROUNDED, JUMPSQUAT, JUMPING, HOVERING, DESCENDING, LANDING
	}

	public enum Animation {
		GROUNDED, JUMPSQUAT, JUMPING, HOVERING, DESCENDING, LANDING, IDLE, WALKING, NONE
	}

	State state = State.GROUNDED;
	private Screen screen;
	Tile standingOn = null;

	private int animationFrame = 0;
	private Animation previousAnimation = Animation.NONE;
	private int rotations = 0;

	private ArrayList<Hitbox> hitboxes = new ArrayList<Hitbox>();

	public Player(Screen screen) {
		this.screen = screen;
		litx = 300;
		x = 300;
		lity = 600;
		y = 600;

		hitboxes.add(new Hitbox(0, -220, 180, 60));
		hitboxes.add(new Hitbox(130, -190, 70, 120));
		hitboxes.add(new Hitbox(130, -100, 80, 160));
	}

	public void draw(Graphics2D g2) {
		// feet at 67, 223 when facing right
		// System.out.println(state);
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
				// if (offset * animationFrame + 300 > width) {
				// animationFrame -= 1;
				// state = State.JUMPING;
				// }
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
					if (offset * animationFrame + 300 == width) {
						if (!isSpace || rotations > 2) {
							state = State.DESCENDING;
							animationFrame = 0;
						}
					}
					if (offset * animationFrame + 300 > width) {
						animationFrame = 0;
						rotations += 1;
					}
				} else {
					// draws the first frame of strike instead of nothing in case that this is
					// skipped frame 1
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
				if (animationFrame == 8) {
					isHitbox = true;
					hitboxes.get(0).setActive(true, direction);
					hitboxes.get(1).setActive(true, direction);
				} else if (animationFrame == 9) {
					hitboxes.get(0).setActive(false, direction);
					hitboxes.get(1).setActive(false, direction);
					hitboxes.get(2).setActive(true, direction);
				} else if (animationFrame == 10) {
					hitboxes.get(2).setActive(false, direction);
					isHitbox = false;
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
					for (Hitbox box : hitboxes) {
						box.setActive(false, direction);
					}
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
			int yoffset = 0;
			if (state == State.HOVERING) {
				yoffset = -70;
			} else if (state == State.JUMPING) {
				yoffset = -5 * animationFrame;
			} else if (state == State.DESCENDING) {
				if (animationFrame < 5) {
					yoffset = -80;
				} else if (animationFrame < 10) {
					yoffset = -16 * (4 - (animationFrame - 5));
				}
			}
			System.out.println(state + " " + animationFrame);
			g2.setColor(Color.yellow);
			g2.drawRect(x - 2, y - 2 + yoffset, 4, 4);
			g2.setColor(Color.red);
			g2.drawRect(x - 2 + 62, y - 2 + yoffset, 4, 4);

			g2.drawRect(x - 17, y - 47 + yoffset, 4, 4); // left
			g2.drawRect(x + 75, y - 47 + yoffset, 4, 4);
			g2.drawRect(x - 17, y - 152 + yoffset, 4, 4); // left
			g2.drawRect(x + 75, y - 152 + yoffset, 4, 4);
			g2.drawRect(x - 2, y - 182 + yoffset, 4, 4);
			g2.drawRect(x - 2 + 62, y - 182 + yoffset, 4, 4);

			for (Hitbox box : hitboxes) {
				if (box.isActive()) {
					box.draw(g2, x, y);
				}
			}
			g2.setColor(Color.black);
		}

	}

	public void calcMove(double xMove, boolean isShift, boolean isJump) {
		boolean doLandingCheck = false;
		isSpace = isJump;

		if (yspeed > 35) {
			yspeed = 35;
		}

		// System.out.println("start"+xspeed);
		if (state == State.GROUNDED) {
			if (xMove == 1) {
				direction = 1;
			} else if (xMove == -1) {
				direction = -1;
			}
		}
		// System.out.println(lity+" "+Screen.scrolly+" " + (lity-Screen.scrolly));
		if (isHitbox && screen.checkHitboxCollision(hitboxes)) {
			state = State.JUMPING;
			yspeed = -30;
			direction *= -1;
			xspeed = 20 * direction;
			for (Hitbox box : hitboxes) {
				box.setActive(false, direction);
			}
		}

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

					Point2D.Double leftFoot = new Point2D.Double(litx - Screen.scrollx, lity - Screen.scrolly);
					Point2D.Double rightFoot = new Point2D.Double(litx - Screen.scrollx + 62, lity - Screen.scrolly);
					int groundLevel = screen.checkLandingCollision(leftFoot, rightFoot);
					if (groundLevel != 1000001) {
						lity = groundLevel + Screen.scrolly;
					}
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

		int yoffset = 0;
		if (state == State.HOVERING) {
			yoffset = -70;
		} else if (state == State.JUMPING) {
			yoffset = -5 * animationFrame;
		} else if (state == State.DESCENDING) {
			if (animationFrame < 5) {
				yoffset = -80;
			} else if (animationFrame < 10) {
				yoffset = -16 * (5 - (animationFrame - 5));
			}
		}
		Point2D.Double left = new Point2D.Double(litx - Screen.scrollx - 15, lity - 45 - Screen.scrolly + yoffset);
		Point2D.Double left2 = new Point2D.Double(litx - Screen.scrollx - 15, lity - 150 - Screen.scrolly + yoffset);
		Point2D.Double right = new Point2D.Double(litx - Screen.scrollx + 77, lity - 45 - Screen.scrolly + yoffset);
		Point2D.Double right2 = new Point2D.Double(litx - Screen.scrollx + 77, lity - 150 - Screen.scrolly + yoffset);
		int wallLevel = screen.checkHorizontalCollision(left, left2, right, right2);
		if (wallLevel != -1000001) {
			litx = wallLevel + Screen.scrollx;
			xspeed = 0;
		}

		if (state != State.GROUNDED && state != State.JUMPSQUAT && state != State.LANDING) {
			Point2D.Double leftHead;
			Point2D.Double rightHead;
			if (state == State.HOVERING) {
				leftHead = new Point2D.Double(litx - Screen.scrollx, lity - 250 - Screen.scrolly);
				rightHead = new Point2D.Double(litx - Screen.scrollx + 62, lity - 250 - Screen.scrolly);
			} else if (state == State.JUMPING) {
				leftHead = new Point2D.Double(litx - Screen.scrollx, lity - 180 - Screen.scrolly - animationFrame * 5);
				rightHead = new Point2D.Double(litx - Screen.scrollx + 62,
						lity - 180 - Screen.scrolly - animationFrame * 5);
			} else {
				leftHead = new Point2D.Double(litx - Screen.scrollx, lity - 180 - Screen.scrolly);
				rightHead = new Point2D.Double(litx - Screen.scrollx + 62, lity - 180 - Screen.scrolly);
			}
			int ceilingLevel = screen.checkCeilingCollision(leftHead, rightHead);
			if (ceilingLevel != -1000001) {
				yspeed = 0;
				lity = ceilingLevel + 180 + Screen.scrolly - yoffset + 5;
			}

			Point2D.Double leftFoot = new Point2D.Double(litx - Screen.scrollx, lity - Screen.scrolly + yoffset);
			Point2D.Double rightFoot = new Point2D.Double(litx - Screen.scrollx + 62, lity - Screen.scrolly + yoffset);
			int groundLevel = screen.checkLandingCollision(leftFoot, rightFoot);
			if (groundLevel != 1000001) {
				// System.out.println("FORCED LANDING");
				lity = groundLevel + Screen.scrolly;
				yspeed = 0;
				xspeed = 0;
				// don't move until finished animation
				if (true) {// 300 * animationFrame + 600 > jump.getWidth(null)) {
					state = State.LANDING;
				}
			}
		}
		if (state == State.GROUNDED || doLandingCheck) {
			Point2D.Double leftFoot = new Point2D.Double(litx - Screen.scrollx, lity - Screen.scrolly);
			Point2D.Double rightFoot = new Point2D.Double(litx - Screen.scrollx + 62, lity - Screen.scrolly);
			int groundLevel = screen.checkLandingCollision(leftFoot, rightFoot);
			if (groundLevel == 1000001) {
				if (screen.checkDescendingStairs(new Point2D.Double(leftFoot.getX(), leftFoot.getY() + 10),
						new Point2D.Double(rightFoot.getX(), rightFoot.getY() + 10))) {
					lity += 6;
				} else {
					state = State.JUMPING;
					animationFrame = 1;
					previousAnimation = Animation.JUMPING;
				}
			} else {
				// System.out.println(groundLevel);
				lity = groundLevel + Screen.scrolly;
			}
		}

		// System.out.println("end"+xspeed);
		// System.out.println(lity + " " + Main.scrollPos[2]+ " "+Main.scrollPos[3]);
		if (litx > Main.scrollPos[1]) {
			Screen.scrollx = Screen.scrollx - (int) (litx - Main.scrollPos[1]);
			litx = Main.scrollPos[1];
		} else if (litx < Main.scrollPos[0]) {
			Screen.scrollx = Screen.scrollx + (int) (Main.scrollPos[0] - litx);
			litx = Main.scrollPos[0];
		}
		x = (int) litx;
		if (lity > Main.scrollPos[3]) {
			Screen.scrolly = Screen.scrolly - (int) (lity - Main.scrollPos[3]);
			lity = Main.scrollPos[3];
		} else if (lity < Main.scrollPos[2]) {
			Screen.scrolly = Screen.scrolly + (int) (Main.scrollPos[2] - lity);
			lity = Main.scrollPos[2];
		}
		y = (int) lity;

	}

	// public void setStandingOn(Tile obj) {
	// standingOn = obj;
	// }

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

	// Loads images to remove flickering
	public void load(Graphics2D g2) {
		Image sprites[] = { walk, IDLE, jumpsquat, jumping, hovering, strike, landing };
		for (Image sprite : sprites) {
			g2.drawImage(sprite, x, y, x - 36 - 67, y + 340 - 85 - 223, animationFrame, 0, animationFrame + 200, 340,
					null);
		}
	}

}
