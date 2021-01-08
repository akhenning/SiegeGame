package siegeGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Player {
	public final double GRAVITY = 1.5;

	public static Image walk = Toolkit.getDefaultToolkit().getImage("assets/walk.png");
	public static Image IDLE = Toolkit.getDefaultToolkit().getImage("assets/idle.png");
	// public static Image jump =
	// Toolkit.getDefaultToolkit().getImage("assets/jump.png");
	public static Image jumpsquat = Toolkit.getDefaultToolkit().getImage("assets/jumpsquat.png");
	public static Image jumping = Toolkit.getDefaultToolkit().getImage("assets/jumping.png");
	public static Image hovering = Toolkit.getDefaultToolkit().getImage("assets/hovering.png");
	public static Image strike = Toolkit.getDefaultToolkit().getImage("assets/air_strike.png");
	public static Image landing = Toolkit.getDefaultToolkit().getImage("assets/landing.png");
	public static Image attack = Toolkit.getDefaultToolkit().getImage("assets/attack_fast.png");
	public static Image[] skid = { Toolkit.getDefaultToolkit().getImage("assets/skid.png"),
			Toolkit.getDefaultToolkit().getImage("assets/skid2.png") };

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
	private final double WALK_SPEED = 10;
	private final int[] FOOT_WIDTH = { 5, 57 };

	public enum State {
		GROUNDED, JUMPSQUAT, JUMPING, HOVERING, DESCENDING, LANDING, BASIC_ATTACK
	}

	public enum Animation {
		GROUNDED, JUMPSQUAT, JUMPING, HOVERING, DESCENDING, LANDING, IDLE, WALKING, BASIC_ATTACK, NONE
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
		litx = 500;
		x = 500;
		lity = 600;
		y = 600;

		hitboxes.add(new Hitbox(0, -220, 180, 60));
		hitboxes.add(new Hitbox(130, -190, 70, 120));
		hitboxes.add(new Hitbox(90, -100, 120, 160));
	}

	public void draw(Graphics2D g2) {
		// feet at 67, 223 when facing right
		// System.out.println(state);
		if (state != State.GROUNDED) {
			if (state == State.BASIC_ATTACK) {
				int offset = 325;
				// Check if this is the first frame of the new animation
				if (previousAnimation != Animation.BASIC_ATTACK) {
					animationFrame = 0;
					previousAnimation = Animation.BASIC_ATTACK;
					width = MakeSureImageHasLoaded(attack);
				}
				if (animationFrame == 10) {
					isHitbox = true;
					hitboxes.get(0).setActive(true, direction);
					hitboxes.get(1).setActive(true, direction);
				} else if (animationFrame == 12) {
					hitboxes.get(0).setActive(false, direction);
					hitboxes.get(1).setActive(false, direction);
					hitboxes.get(2).setActive(true, direction);
				} else if (animationFrame == 13) {
					hitboxes.get(2).setActive(false, direction);
					isHitbox = false;
				}

				if (direction == 1) {
					g2.drawImage(attack, x - 115, y - 85 - 155, offset + x - 115, y + 340 - 85 - 155,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				} else {
					g2.drawImage(attack, offset + x - 115 - 32, y - 85 - 155, x - 115 - 32, y + 340 - 85 - 155,
							offset * animationFrame, 0, offset * animationFrame + offset, 340, null);
				}
				animationFrame += 1;
				// check if end of animation without hardcoding number of frames
				if (offset * animationFrame + 300 > width) {
					state = State.GROUNDED;
					animationFrame = 0;
				}
			}
			if (state == State.JUMPSQUAT) {
				int offset = 300;
				// Check if this is the first frame of the new animation
				if (previousAnimation != Animation.JUMPSQUAT) {
					isHitbox = false;
					for (Hitbox box : hitboxes) {
						box.setActive(false, direction);
					}
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
				animationFrame += 2;
				// if (offset * animationFrame + 300 > width) {
				// animationFrame -= 1;
				// state = State.JUMPING;
				// }
			} else if (state == State.JUMPING) {
				int offset = 300;
				// Check if this is the first frame of the new animation
				if (previousAnimation != Animation.JUMPING) {
					isHitbox = false;
					for (Hitbox box : hitboxes) {
						box.setActive(false, direction);
					}
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

				// Have to check this one manually because Jumping state
				// can be transitioned into in multiple ways at different
				// points
				if (animationFrame >= 15) {
					// if (offset * animationFrame + 300 > width) {
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
					isHitbox = false;
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
				animationFrame += 2;
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
			// Special skidding animation for high speeds
			if (Math.abs(xspeed) > 15) {
				// This is kind of slow, but only runs when skidding so it's probably fine
				AffineTransform trans = new AffineTransform();
				// trans rights
				if (direction == 1) {
					trans.translate((double) (x + 72), (double) (y));
					if (animationFrame < 8) {
						trans.rotate(Math.toRadians(Math.abs(4 - animationFrame) - 15));
					} else {
						trans.rotate(Math.toRadians(Math.abs(12 - animationFrame) - 15));
					}
					trans.translate(-130, 15 - 223); // 144
					g2.drawImage(skid[0], trans, null);
				} else {
					trans.translate((double) (x), (double) (y));
					trans.rotate(Math.toRadians(-Math.abs(8 - animationFrame) / 2 + 15));
					trans.translate(-62, 15 - 223); // 72
					g2.drawImage(skid[1], trans, null);
				}
			} else {
				if (direction == 1) {
					g2.drawImage(walk, x - 5 - 67, y + 4 - 223, x + 195 - 67, y + 250 + 4 - 223,
							offset * animationFrame, 0, offset * animationFrame + 200, 250, null);
				} else {
					g2.drawImage(walk, x + 195 - 67 - 4, y + 4 - 223, x - 5 - 67 - 4, y + 250 + 4 - 223,
							offset * animationFrame, 0, offset * animationFrame + 200, 250, null);
				}
			}
			animationFrame += 1;
			if (offset * animationFrame + 300 > width) {
				animationFrame = 0;
			}
		}
		// These are the collision points, which are simply being visualized
		if (Main.debug) {
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
			// NOTE: Very useful for debugging
			//System.out.println("State and AnimationFrame:" + state + " " + animationFrame);
			
			g2.setColor(Color.yellow);
			// Feet I think
			g2.drawRect(x - 2 + FOOT_WIDTH[0], y - 2 + yoffset, 4, 4);
			g2.setColor(Color.red);
			g2.drawRect(x - 2 + FOOT_WIDTH[1], y - 2 + yoffset, 4, 4);

			g2.drawRect(x - 17, y - 47 + yoffset, 4, 4); // left
			g2.drawRect(x + 75, y - 47 + yoffset, 4, 4);
			g2.drawRect(x - 17, y - 152 + yoffset, 4, 4); // left
			g2.drawRect(x + 75, y - 152 + yoffset, 4, 4);

			// Head I think
			g2.drawRect(x - 2 + FOOT_WIDTH[0], y - 182 + yoffset, 4, 4);
			g2.drawRect(x - 2 + FOOT_WIDTH[1], y - 182 + yoffset, 4, 4);

			for (Hitbox box : hitboxes) {
				if (box.isActive()) {
					box.draw(g2, x, y);
				}
			}
			g2.setColor(Color.black);
		}

	}

	public void calcMove(double xMove, boolean isShift, boolean isJump, boolean isAttack) {
		boolean doLandingCheck = false;
		isSpace = isJump;

		if (yspeed > 35) {
			yspeed = 35;
		}
		if (xspeed < .01 && xspeed > -.01) {
			xspeed = 0;
		}
		if (xspeed < -45) {
			xspeed = -45;
		} else if (xspeed > 45) {
			xspeed = 45;
		}

		// Check for start of attack
		if (isAttack && state == State.GROUNDED) {
			state = State.BASIC_ATTACK;
		}
		// Handle movement while attacking
		if (state == State.BASIC_ATTACK) {
			xspeed *= .8;
			litx += xspeed;
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
		// Check if smacking a bounce pad
		if (isHitbox && screen.checkHitboxCollision(hitboxes)) {
			// If so, transition to airborne state and go flying in opposite direction
			state = State.JUMPING;
			yspeed = -30;
			direction *= -1;
			// This is the speed of knockback
			xspeed = 25 * direction;
			for (Hitbox box : hitboxes) {
				box.setActive(false, direction);
			}
		}

		// if not GROUNDED
		if (state != State.GROUNDED) {
			// if not in JUMPSQUAT, then in the air; this handles air maneuverability
			if (state == State.JUMPING || state == State.HOVERING || state == State.DESCENDING) {
				// System.out.println("Airborne"+xspeed);
				if (state == State.HOVERING && isJump) {
					yspeed += .25;
				} else if (state == State.JUMPING && isJump) {
					yspeed += GRAVITY - .5;
				} else {
					yspeed += GRAVITY;
				}
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

					Point2D.Double leftFoot = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[0], lity - Screen.scrolly);
					Point2D.Double rightFoot = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[1], lity - Screen.scrolly);
					int groundLevel = screen.checkLandingCollision(leftFoot, rightFoot);
					if (groundLevel != 1000001) {
						lity = groundLevel + Screen.scrolly;
					}
				}
			} else { // if in JUMPSQUAT, move slowly
			}
		} // transition from GROUNDED to jump squat
		else if (isJump && state == State.GROUNDED) {
			// System.out.println("Starting to jump");
			state = State.JUMPSQUAT;
			xspeed *= .9;
			litx += xspeed;
		} // if GROUNDED and not in JUMPSQUAT, then handle walking stuff
		else {
			// System.out.println("WALKING");
			xspeed = xMove * WALK_SPEED;
			if (isShift) {
				xspeed *= 2;
			}
			litx += xspeed;
		}

		// All good games have jump-cancelling
		// ...though maybe I shouldn't, lol
		// Oh. How about only after the hitboxes come out!
		if (isJump && state == State.BASIC_ATTACK && animationFrame > 12) {
			state = State.JUMPSQUAT;
			xspeed *= .8;
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

		// Check if player is inside of a WALL. If so, shunt them back to in front of
		// it.
		Point2D.Double left = new Point2D.Double(litx - Screen.scrollx - 15, lity - 45 - Screen.scrolly + yoffset);
		Point2D.Double left2 = new Point2D.Double(litx - Screen.scrollx - 15, lity - 150 - Screen.scrolly + yoffset);
		Point2D.Double right = new Point2D.Double(litx - Screen.scrollx + 77, lity - 45 - Screen.scrolly + yoffset);
		Point2D.Double right2 = new Point2D.Double(litx - Screen.scrollx + 77, lity - 150 - Screen.scrolly + yoffset);
		int wallLevel = screen.checkHorizontalCollision(left, left2, right, right2);
		if (wallLevel != -1000001) {
			litx = wallLevel + Screen.scrollx;
			xspeed = 0;
		}

		// Check if (airborne) player has their HEAD in a CEILING or are about to LAND
		if (state != State.GROUNDED && state != State.JUMPSQUAT && state != State.LANDING
				&& state != State.BASIC_ATTACK) {
			// Find location of head (animation dependent)
			Point2D.Double leftHead;
			Point2D.Double rightHead;
			if (state == State.HOVERING) {
				leftHead = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[0], lity - 250 - Screen.scrolly);
				rightHead = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[1], lity - 250 - Screen.scrolly);
			} else if (state == State.JUMPING) {
				leftHead = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[0],
						lity - 180 - Screen.scrolly - animationFrame * 5);
				rightHead = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[1],
						lity - 180 - Screen.scrolly - animationFrame * 5);
			} else {
				leftHead = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[0], lity - 180 - Screen.scrolly);
				rightHead = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[1], lity - 180 - Screen.scrolly);
			}

			Point2D.Double leftFoot = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[0], lity - Screen.scrolly + yoffset);
			Point2D.Double rightFoot = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[1], lity - Screen.scrolly + yoffset);
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
			} else {
				// Check if head in ceiling
				int ceilingLevel = screen.checkCeilingCollision(leftHead, rightHead);
				if (ceilingLevel != -1000001) {
					// If so, stop upwards movement and move player to below the collision point.
					yspeed = 0;
					lity = ceilingLevel + 180 + Screen.scrolly - yoffset; // + 5;
				}
			}
		}

		// Check case where grounded player walks off of the ground (i.e. ledge)
		if ((state == State.GROUNDED || state == State.BASIC_ATTACK) || doLandingCheck) {
			Point2D.Double leftFoot = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[0], lity - Screen.scrolly);
			Point2D.Double rightFoot = new Point2D.Double(litx - Screen.scrollx + FOOT_WIDTH[1], lity - Screen.scrolly);
			int groundLevel = screen.checkLandingCollision(leftFoot, rightFoot);
			// This means that nothing is below the player
			if (groundLevel == 1000001) {
				int check_below = 12; // 10
				if (screen.checkDescendingStairs(new Point2D.Double(leftFoot.getX(), leftFoot.getY() + check_below),
						new Point2D.Double(rightFoot.getX(), rightFoot.getY() + check_below))) {
					lity += 6;// 6;
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
			Screen.bgscrollx -= (int) (litx - Main.scrollPos[1]) / 2;
			litx = Main.scrollPos[1];
		} else if (litx < Main.scrollPos[0]) {
			Screen.scrollx = Screen.scrollx + (int) (Main.scrollPos[0] - litx);
			Screen.bgscrollx += (int) (Main.scrollPos[0] - litx) / 2;
			litx = Main.scrollPos[0];
		}
		x = (int) litx;
		if (lity > Main.scrollPos[3]) {
			Screen.scrolly = Screen.scrolly - (int) (lity - Main.scrollPos[3]);
			Screen.bgscrolly -= (int) (lity - Main.scrollPos[3]) / 2;
			lity = Main.scrollPos[3];
		} else if (lity < Main.scrollPos[2]) {
			Screen.scrolly += (int) (Main.scrollPos[2] - lity);
			Screen.bgscrolly += (int) (Main.scrollPos[2] - lity) / 2;
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
				width = image.getWidth(null);
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

	// To adjut the player when zooming and unzooming.
	public void adjust(int x_diff, int y_diff) {
		litx += x_diff;
		lity += y_diff;
	}

	// Loads images to remove flickering
	public void load(Graphics2D g2) {
		Image sprites[] = { walk, IDLE, jumpsquat, jumping, hovering, strike, landing };
		for (Image sprite : sprites) {
			g2.drawImage(sprite, 0, 0, null);
			// g2.drawImage(sprite, x, y, x - 36 - 67, y + 340 - 85 - 223, animationFrame,
			// 0, animationFrame + 200, 340, null);
		}
	}

}
