package siegeGame;

import javax.swing.JPanel;

import siegeGame.Player.State;
import siegeGame.Tile.SlopeState;

import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.event.KeyEvent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Screen extends JPanel {
	// Dunno what this does
	private static final long serialVersionUID = 1L;

	// Well, I needed a way to signify a break point in a list of images that wasn't
	// null...
	private static final Image BREAK_POINT_SIGNIFIER = Toolkit.getDefaultToolkit().getImage("");
	// Controls zoom of entire game area
	public static double zoom = 1;
	// Image[s] for backgrounds
	private static Image bg = Toolkit.getDefaultToolkit().getImage("assets/bg1.jpg");
	// Size of bg images; 0 is x of background image, 1 is y.
	private int bgDimensions[] = new int[2];
	// Toolkit.getDefaultToolkit().getImage("assets/carlymonster_clouds.jpg");
	// Enter key prompt image
	private static Image key = Toolkit.getDefaultToolkit().getImage("assets/enter.png");
	// Background for stage select
	private static Image selectbg = Toolkit.getDefaultToolkit().getImage("assets/selectbg1.png");
	// Title screen background
	private static Image title = Toolkit.getDefaultToolkit().getImage("assets/title1.jpg");
	// Face images for use in text boxes
	private static Image[] face_images = { Toolkit.getDefaultToolkit().getImage("assets/SiegeNormal.png"),
			Toolkit.getDefaultToolkit().getImage("assets/SiegeLion.png"),
			Toolkit.getDefaultToolkit().getImage("assets/SiegeEnigmatic.png"),
			Toolkit.getDefaultToolkit().getImage("assets/nian.png"),
			Toolkit.getDefaultToolkit().getImage("assets/w.png"),
			Toolkit.getDefaultToolkit().getImage("assets/w_smile.png"),
			Toolkit.getDefaultToolkit().getImage("assets/w_mad.png"),
			Toolkit.getDefaultToolkit().getImage("assets/w_crazy.png") };

	// Handles scroll for player and background
	public static int scrollx = 0;
	public static int scrolly = 0;
	public static int bgscrollx = 0;
	public static int bgscrolly = 0;
	private boolean first = true;

	// Collection of level objects
	private ArrayList<Tile> area = new ArrayList<Tile>();
	private ArrayList<Interactable> interactables = new ArrayList<Interactable>();
	private ArrayList<Graphic> graphics = new ArrayList<Graphic>();
	private ArrayList<Particle> particles = new ArrayList<Particle>();
	private int numSelected = 0;
	private Player player = new Player(this);

	// Fonts and texts for speech bubbles
	private final int CHARS_PER_LINE = 50;
	private Font font = new Font("Serif", Font.PLAIN, 100);
	private Font fade_font = new Font("Serif", Font.PLAIN, 200);
	private Font text_fonts[] = { new Font("Serif", Font.PLAIN, 40), new Font("Serif", Font.PLAIN, 80) };
	public boolean activeText = false;
	private int textBoxNum = -1;
	private int[] textScrollNum = { -1, -1 };
	private ArrayList<ArrayList<String>> text = new ArrayList<ArrayList<String>>();
	private ArrayList<Image> faces = new ArrayList<Image>();
	private ArrayList<String> faceNames = new ArrayList<String>();

	private boolean isShift = false;
	private boolean isJump = false;
	private boolean isAttack = false;
	private boolean isHeavyAttack = false;
	private int fade = 0;
	private String fade_text = "TEST";
	// private int direction = 1;

	static int windowWidth = 0;
	// private int linearDirection = 0;
	private double left = 0;
	private double right = 0;

	public enum GameState {
		TITLE, LEVEL, SELECT
	}

	public GameState state = GameState.TITLE;

	public Screen() {
		setBackground(Color.WHITE);
		// addMouseListener(new ClickListener());
		// addMouseMotionListener(new MovementListener());
		setFocusable(true);
		addKeyListener(new KeysListener());
		loadLevel("");
	}

	public Dimension getPreferredSize() {
		Dimension d = new Dimension(350, 300);
		return d;
	}

	public void paintComponent(Graphics g) {
		windowWidth = (int) getSize().getWidth();
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		int zoom_mult = 1;
		if (zoom == .5) {
			zoom_mult = 2;
		}
		g2.setStroke(new BasicStroke(4 * zoom_mult));

		if (state == GameState.LEVEL) {
			g2.scale(zoom, zoom);

			// g2.translate(-Main.screenSize.width/2+Main.screenSize.width/zoom/2,
			// -Main.screenSize.height/2+Main.screenSize.height/zoom/2);

			// For first time this method is run, set a few things up
			if (first) {
				first = false;
				player.load(g2);
				int i = 0;
				while (bgDimensions[0] <= 0) {
					bgDimensions[0] = bg.getWidth(null);
					bgDimensions[1] = bg.getHeight(null);
					i++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					if (i > 10) {
						bgDimensions[0] = 1;
						System.out.println("bad background image");
					}
				}

				// Load all images?
				ArrayList<Image> sprites = new ArrayList<Image>();
				for (Image face : face_images) {
					sprites.add(face);
				}
				for (Image sprite : sprites) {
					g2.drawImage(sprite, 0, 0, null);
				}
				for (Graphic graphic : graphics) {
					graphic.draw(g2);
				}
				Graphic.loadTwoParters(g2);
				sprites = null;
			}
			drawBG(g2);

			// System.out.println(offset*animationFrame);

			for (Tile tile : area) {
				if (tile.isVisible()) {
					tile.draw(g2);
				}
			}
			for (Interactable tile : interactables) {
				if (tile.isVisible()) {
					tile.draw(g2);
				}
			}
			for (Graphic tile : graphics) {
				if (tile.isVisible()) {
					tile.draw(g2);
				}
			}
			player.draw(g2);
		} else if (state == GameState.TITLE) {
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, 2000, 1500);
			g2.setFont(font);
			g2.drawImage(title, 0, 0, null);
			g2.setColor(Color.WHITE);
			g2.drawString("Press Enter", Main.screenSize.width / 3, Main.screenSize.height / 2);
			for (Tile tile : area) {
				tile.draw(g2);
			}
		} else if (state == GameState.SELECT) {
			// todo check visibility on level icons
			g2.setFont(font);
			g2.fillRect(0, 1000, 2000, 9999);
			g2.drawImage(selectbg, scrollx, scrolly, null);
			int i = 0;
			g2.setColor(Color.BLACK);
			for (Tile tile : area) {
				// if (tile.isVisible()) {
				tile.draw(g2);
				if (i == numSelected) {
					tile.drawBoxAround(g2);
				}
				i++;
			}
		}
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).draw(g2);
		}
		// Secion handling text boxes
		if (activeText) {
			if (textBoxNum == -1) {
				System.err.println("ERROR: Text box with no assigned text box");
				textBoxNum = 0;
			}
			if (BREAK_POINT_SIGNIFIER.equals(faces.get(textBoxNum))) {
				activeText = false;
			} else {
				// Draw box itself
				g2.setColor(Color.WHITE);
				g2.fillRect(0, Main.gameSize.height * 3 / 4, Main.gameSize.width, Main.gameSize.height);
				g2.setColor(Color.BLACK);
				g2.drawRect(0, Main.gameSize.height * 3 / 4, Main.gameSize.width, Main.gameSize.height);
				if (zoom == 1) {
					g2.setFont(text_fonts[0]);
				} else {
					g2.setFont(text_fonts[1]);
				}
				int offset = (int) (40 / zoom);

				int dist_from_side = 0;
				if (faces.get(textBoxNum) == null) {
					dist_from_side = Main.gameSize.width / 10;
				} else {
					dist_from_side = (int) (Main.gameSize.width / (4.4));
				}
				if (text.get(textBoxNum).size() > 0) {
					if (textScrollNum[0] == -1) {
						int times = 0;
						// System.out.println(text.toString()+", "+textBoxNum);
						for (String line : text.get(textBoxNum)) {
							g2.drawString(line, dist_from_side, (Main.gameSize.height * 4 / 5) + (times * offset));
							times += 1;
						}
						g2.drawImage(key, Main.gameSize.width - (212 * zoom_mult), Main.gameSize.height * 4 / 5,
								212 * zoom_mult, 91 * zoom_mult, null); // 212x91
					} else {
						int times = 0;
						for (String line : text.get(textBoxNum)) {
							if (times < textScrollNum[0]) {
								g2.drawString(line, dist_from_side, Main.gameSize.height * 4 / 5 + (times * offset));
								times += 1;
							} else if (times == textScrollNum[0]) {
								// System.out.println("Attempting to substring string: " + line + " with length
								// " + line.length()
								// + " into substring 0, " + textScrollNum[1]);
								g2.drawString(line.substring(0, textScrollNum[1] - 1), dist_from_side,
										Main.gameSize.height * 4 / 5 + (times * offset));
								times += 1;
							}
						}
						if (fade <= 0) {
							textScrollNum[1] += 1;
						}
						if (text.get(textBoxNum).get(textScrollNum[0]).length() < textScrollNum[1]) {
							// System.out.println("Incrementing line. Now: " + textScrollNum[0] + ", " +
							// textScrollNum[1]);
							textScrollNum[0] += 1;
							textScrollNum[1] = 1;
							if (text.get(textBoxNum).size() <= textScrollNum[0]) {
								// System.out.println("Reached end at: " + textScrollNum[0] + ", " +
								// textScrollNum[1]);
								textScrollNum[0] = -1;
								textScrollNum[1] = -1;
							}
						}
					}
				}
				if (faces.get(textBoxNum) != null) {
					int off = (int) (270 / zoom);
					int off2 = (int) (50 / zoom);
					g2.setColor(Color.WHITE);
					g2.fillRect(off2 / 2, Main.gameSize.height - off - (off2 * 5 / 2), off, off);
					// g2.fillRect(offset/2, Main.gameSize.height * 2 / 3 - offset,
					// Main.gameSize.height / 4,
					// Main.gameSize.height / 4);
					g2.setColor(Color.BLACK);
					g2.drawImage(faces.get(textBoxNum), off2 / 2, Main.gameSize.height - off - (off2 * 5 / 2), off, off,
							null);
					g2.drawRect(off2 / 2, Main.gameSize.height - off - (off2 * 5 / 2), off, off);
					g2.drawRect(off2 / 2, Main.gameSize.height - (off2 * 5 / 2), off, off2 * 5 / 4);
					g2.drawString(faceNames.get(textBoxNum), off2 * 2 / 3, Main.gameSize.height - (off2 * 7 / 4));
				}
			}
		}

		// put fade-in above
		if (fade > 0) {
			g2.setFont(fade_font);
			// if above 300, reduce by one (black) until is normal alpha
			Color fade_in_text = null;
			if (fade >= 300) {
				g2.setColor(Color.BLACK);
				fade -= 1;
				if (fade <= 300) {
					fade = 255;
				}
				fade_in_text = Color.WHITE;
			} else {
				// if normal alpha, fade out.
				Color fade_in = new Color(0, 0, 0, fade);
				g2.setColor(fade_in);
				fade -= (270 - fade) / 2;
				if (fade < 0) {
					fade = 0;
				}
				fade_in_text = new Color(255, 255, 255, fade);
			}
			g2.fillRect(0, 0, 3500, 1500);
			g2.setColor(fade_in_text);
			g2.drawString(fade_text, Main.screenSize.width / 3, Main.screenSize.height / 2);
		} else {
			fade -= 1;
			if (fade == -5 && text.size() > 0) {
				activeText = true;
			}
		}

	}

	public void drawBG(Graphics2D g2) {

		if (bgscrollx < -bgDimensions[0] + Main.gameSize.width) {
			bgscrollx += bgDimensions[0];
		} else if (bgscrollx > Main.gameSize.width) {
			bgscrollx -= bgDimensions[0];
		}
		if (bgscrolly < -bgDimensions[1] + Main.gameSize.height) {
			bgscrolly += bgDimensions[1];
		} else if (bgscrolly > +Main.gameSize.height) {
			bgscrolly -= bgDimensions[1];
		}
		// System.out.println("Background culling: " + bgscrollx + ", " +
		// Main.gameSize.width + ", "
		// + (bgscrollx + bgDimensions[0]) + ", " + Main.gameSize.height);
		if (((bgscrollx > Main.gameSize.width) || (bgscrollx + bgDimensions[0] < 0))
				|| ((bgscrolly > Main.gameSize.height) || (bgscrolly + bgDimensions[1] < 0))) {
			// System.out.println("Not printing bottom right");
		} else {
			g2.drawImage(bg, bgscrollx, bgscrolly, null);
		}
		if (((bgscrollx - bgDimensions[0] > Main.gameSize.width) || (bgscrollx < 0))
				|| ((bgscrolly > Main.gameSize.height) || (bgscrolly + bgDimensions[1] < 0))) {
			// System.out.println("Not printing bottom left");
		} else {
			g2.drawImage(bg, bgscrollx - bgDimensions[0], bgscrolly, null);
		}
		if (((bgscrollx > Main.gameSize.width) || (bgscrollx + bgDimensions[0] < 0))
				|| ((bgscrolly - bgDimensions[1] > Main.gameSize.height) || (bgscrolly < 0))) {
			// System.out.println("Not printing bottom right");
		} else {
			g2.drawImage(bg, bgscrollx, bgscrolly - bgDimensions[1], null);
		}

		if (((bgscrollx - bgDimensions[0] > Main.gameSize.width) || (bgscrollx < 0))
				|| ((bgscrolly - bgDimensions[1] > Main.gameSize.height) || (bgscrolly < 0))) {
			// System.out.println("Not printing top left");
		} else {
			g2.drawImage(bg, bgscrollx - bgDimensions[0], bgscrolly - bgDimensions[1], null);
		}
		// g2.drawImage(bg,(scrollx+2000/2)%bgDimensions[0]-bgDimensions[0],(scrolly/2)%bgDimensions[1]+bgDimensions[1],null);
		// g2.drawImage(bg,(scrollx+2000/2)%bgDimensions[0],(scrolly/2)%bgDimensions[1]+bgDimensions[1],null);
	}

	public void nextFrame() {
		if (state == GameState.LEVEL) {
			// Make sure we don't waste time checking tiles out of frame
			for (Tile tile : area) {
				tile.checkIsVisible();
			}
			for (int i = 0; i < interactables.size(); i++) {
				interactables.get(i).checkIsVisible();
				interactables.get(i).nextFrame();
				if (interactables.get(i).shouldRemove()) {
					if (interactables.get(i).getId() == 61) {
						// todo make puff of smoke
						makeEffect(interactables.get(i).getX(), interactables.get(i).getY(), 4, 0);
					}
					interactables.remove(interactables.get(i));
					i -= 1;
				}
			}
			for (Graphic tile : graphics) {
				tile.checkIsVisible();
			}

			// Player moves; calculating collision is also handled in this method
			if (fade < 50) {
				player.calcMove(left + right, isShift, isJump, isAttack, isHeavyAttack);
			} else {
				player.calcMove(0, false, false, false, false);
			}
		}

		// Avoid particle flooding
		if (particles.size() > 15) {
			particles.remove(0);
		}
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).calcMove();
			if (particles.get(i).shouldRemove()) {
				particles.remove(i);
				i -= 1;
			}
		}
		// System.out.println("Amount of particles in circulation:"+particles.size());

		// Draw everything
		repaint();
		requestFocusInWindow();

	}

	// returns height
	public int checkLandingCollision(Point2D.Double leftFoot, Point2D.Double rightFoot) {
		int highest = 1000001;
		// For every tile
		for (Tile tile : area) {
			if (tile.isVisible() && tile.canDetectVertical()) {
				// See if either foot is inside something
				if (tile.slopeState == SlopeState.NONE) {
					if (tile.isInside(leftFoot) || tile.isInside(rightFoot)) {
						// player.setStandingOn(tile);
						// If it is, save the lowest Y value
						if (tile.y < highest) {
							highest = tile.getHeight(rightFoot.getX());
						}
					}
				} else if (tile.slopeState == SlopeState.RIGHT) {
					// System.out.println("Checking for object");
					if (tile.isInside(rightFoot)) {
						// System.out.println("Successfully found object)");
						if (tile.y < highest) {
							highest = tile.getHeight(rightFoot.getX());
						}
					}
				} else {
					if (tile.isInside(leftFoot)) {
						if (tile.y < highest) {
							highest = tile.getHeight(leftFoot.getX());
						}
					}
				}
			}
		}
		for (Interactable tile : interactables) {
			if (tile.isVisible() && tile.hasInteraction() && tile.canDetectVertical()) {
				// See if either foot is inside something
				if (tile.slopeState == SlopeState.NONE) {
					if (tile.isInside(leftFoot) || tile.isInside(rightFoot)) {
						// player.setStandingOn(tile);
						// If it is, save the lowest Y value
						if (tile.isTangible()) {
							if (tile.y < highest) {
								highest = tile.getHeight(rightFoot.getX());
							}
						// if the object is a respawn element
						} else if (tile.getId() == -1) {
							int[] dest_xy = tile.getTiedXY();
							// int[] src_xy = {tile.getX()+(tile.getWidth()/2), tile.getY() +
							// (tile.getHeight()/2)};
							// int[] player_xy = {player.getAbsoluteX(), player.getAbsoluteY()};
							player.goToAbsolute(dest_xy[0], dest_xy[1] + 250);
							player.forceLanding();
							fade = 300;
							fade_text = "";
						}
					}
					// I commented the below, because it shouldn't ever matter
				} // else if (tile.slopeState == SlopeState.RIGHT) {
					// System.out.println("Checking for object");
					// if (tile.isInside(rightFoot)) {
					// System.out.println("Successfully found object)");
					// if (tile.y < highest) {
					// highest = tile.getHeight(rightFoot.getX());
					// }
					// }
					// } else {
					// if (tile.isInside(leftFoot)) {
					// if (tile.y < highest) {
					// highest = tile.getHeight(leftFoot.getX());
					// }
					// }
					// }
			}
		}
		return highest;
	}

	// returns height
	public int checkCeilingCollision(Point2D.Double left, Point2D.Double right) {
		int highest = -1000001;
		// For every tile
		for (Tile tile : area) {
			if (tile.isVisible() && tile.canDetectVertical()) {
				if (tile.isInside(left) || tile.isInside(right)) {
					// player.setStandingOn(tile);
					// If it is inside, save the lowest Y value
					if (tile.y > highest) {
						highest = tile.getHeight(right.getX()) + tile.height;
					}
				}
			}
		}
		for (Interactable tile : interactables) {
			if (tile.isVisible() && tile.hasInteraction() && tile.canDetectVertical()) {
				if (tile.isInside(left) || tile.isInside(right)) {
					// player.setStandingOn(tile);
					// If it is, save the lowest Y value
					if (tile.isTangible()) {
						if (tile.y > highest) {
							highest = tile.getHeight(right.getX()) + tile.height;
						}
					}
				}
			}
		}
		return highest;
	}

	// Method to check if the horizontal contact points of the player are
	// in contact with anything
	public int checkHorizontalCollision(Point2D.Double leftTop, Point2D.Double leftBot, Point2D.Double rightTop,
			Point2D.Double rightBot) {
		// For every tile
		for (int t = 0; t < area.size(); t++) {
			if (area.get(t).isVisible() && area.get(t).id < 100) {
				// See if either foot is inside something
				if (area.get(t).canDetectRight()) {
					if (area.get(t).isInside(leftTop)) {
						// If so, return the location that the player should
						// be snapped to
						return area.get(t).x + area.get(t).width + 15 + 2;
					} else if (area.get(t).isInside(leftBot)) {
						return area.get(t).x + area.get(t).width + 15 + 2;
					}
				}
				if (area.get(t).canDetectLeft()) {
					if (area.get(t).isInside(rightTop)) {
						return area.get(t).x - 75 - 2;
					} else if (area.get(t).isInside(rightBot)) {
						return area.get(t).x - 75 - 2;
					}
				}
			}
		}
		// for every Interactable
		for (Interactable tile : interactables) {
			if (tile.isVisible() && tile.hasInteraction()) {
				// This should be removed eventually
				if (tile.getId() == 71) {
					System.out.println("Error: Interactable with no contact Interaction is registered as having one.");
				}
				// See if either foot is inside something
				if (tile.canDetectRight()) {
					if (tile.isInside(leftTop)) {
						// Do this if the object can be stood on/ran into
						if (tile.isTangible()) {
							return tile.x + tile.width + 15 + 2;
						}
						// If the object is an un-activated question mark
						if (tile.getId() == 70) {
							if (!activeText) {
								tile.interact();
								activeText = true;
								textBoxNum = tile.getData();
								textScrollNum[0] = 0;
								textScrollNum[1] = 1;
							}
							// if the object is a respawn element
						} else if (tile.getId() == -1) {
							int[] dest_xy = tile.getTiedXY();
							// int[] src_xy = {tile.getX()+(tile.getWidth()/2), tile.getY() +
							// (tile.getHeight()/2)};
							// int[] player_xy = {player.getAbsoluteX(), player.getAbsoluteY()};
							player.goToAbsolute(dest_xy[0], dest_xy[1] + 250);
							player.forceLanding();
							fade = 300;
							fade_text = "";
							// If the object is a finish line
						} else if (tile.getId() == 99) {
							levelAdvance(tile.getData());
							return -1000001;
						}
					} else if (tile.isInside(leftBot)) {
						if (tile.isTangible()) {
							return tile.x + tile.width + 15 + 2;
						}
						if (tile.getId() == 70) {
							if (!activeText) {
								tile.interact();
								activeText = true;
								textBoxNum = tile.getData();
								textScrollNum[0] = 0;
								textScrollNum[1] = 1;
							}
						}
					}
				}
				if (tile.canDetectLeft()) {
					if (tile.isInside(rightTop)) {
						if (tile.isTangible()) {
							return tile.x - 75 - 2;
						}
						if (tile.getId() == 70) {
							if (!activeText) {
								tile.interact();
								activeText = true;
								textBoxNum = tile.getData();
								textScrollNum[0] = 0;
								textScrollNum[1] = 1;
							}
						} else if (tile.getId() == -1) {
							int[] dest_xy = tile.getTiedXY();
							player.goToAbsolute(dest_xy[0], dest_xy[1] + 250);
							player.forceLanding();
							fade = 300;
							fade_text = "";
							// If the object is a finish line
						} else if (tile.getId() == 99) {
							levelAdvance(tile.getData());
							return -1000001;
						}
					} else if (tile.isInside(rightBot)) {
						if (tile.isTangible()) {
							return tile.x - 75 - 2;
						}
						if (tile.getId() == 70) {
							if (!activeText) {
								tile.interact();
								activeText = true;
								textBoxNum = tile.getData();
								textScrollNum[0] = 0;
								textScrollNum[1] = 1;
							}
						}
					}
				}
			}
		}
		return -1000001;
	}

	// Checks if the player is attacking something
	// Passes in all hitboxes, and checks if anything is within the active ones.
	public boolean checkHitboxCollision(ArrayList<Hitbox> hitboxes) {
		// calculate location of player relative to the level, since the hitboxes
		// themselves are only relative to the player
		int dx = player.x - Screen.scrollx;
		int dy = player.y - Screen.scrolly;
		int point[];
		boolean rtrn = false;
		for (Hitbox box : hitboxes) {
			if (box.isActive()) {
				for (Interactable tile : interactables) {
					if (tile.isVisible()) {
						// For each corner of the hitbox, find the hitboxes' location in the game
						// (which is different from the player-relative location stored in the object)
						for (int i = 1; i < 5; i++) {
							point = box.getRelativePoint(i);
							point[0] += dx;
							point[1] += dy;
							// If there is a point of contact;
							if (tile.isInside(point)) {
								// Check for the type of interaction
								int effect = tile.interact();
								// If it has a bouncing effect
								if (effect == 1) {
									rtrn = true;
									// if it breaks upon contact
								} else if (effect == 0) {
									// Create new particles with semirandom properties in area of tile
									int heightover4 = tile.getHeight() / 4;
									int widthover4 = tile.getWidth() / 4;
									particles.add(new Particle(tile.getX() + widthover4, tile.getY() + heightover4,
											heightover4 / 2, heightover4 / 2, 1));
									particles.add(new Particle(tile.getX() + (widthover4 * 3),
											tile.getY() + heightover4, heightover4 / 2, heightover4 / 2, 1));
									particles.add(new Particle(tile.getX() + widthover4,
											tile.getY() + (heightover4 * 3), heightover4 / 2, heightover4 / 2, 1));
									particles.add(new Particle(tile.getX() + (widthover4 * 3),
											tile.getY() + (heightover4 * 3), heightover4 / 2, heightover4 / 2, 1));
									// Remove the tile from the list
									tile.setToRemove();
									// If is a text box prompt (? mark)
								} else if (effect == 2) {
									// Tell the game to activate text boxes, and move to the proper box
									activeText = true;
									textBoxNum = tile.getData();
									textScrollNum[0] = 0;
									textScrollNum[1] = 1;
								}
							}
						}
					}
				}
			}
		}
		return rtrn;
	}

	// Method that handles desending staircases.
	// In the case where the player steps from solid ground onto air, this method
	// is called to check if they are simply decending a staircase.
	// Note that leftFoot and rightFoot are offset from the player's actual feet
	// location to check if there is something below them.
	public boolean checkDescendingStairs(Point2D.Double leftFoot, Point2D.Double rightFoot) {
		// For all tiles
		for (Tile tile : area) {
			// If a staircase near player
			if (tile.isVisible() && tile.getId() >= 100) {
				// If right-facing staircase
				if (tile.slopeState == SlopeState.RIGHT) {
					// Check if there is a staircase right below the right foot, basically
					if (tile.isInside(rightFoot)) {
						// System.out.println("Successfully found object)");
						return true;
					}
					// If left-facing staircase
				} else {
					// Check if there is a staircase right below the left foot, basically
					if (tile.isInside(leftFoot)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void makeEffect(int x, int y, int type, int direction) {
		switch (type) {
		case 1:
			// impact
			for (int i = 0; i < 4; i++) {
				particles.add(new Particle(x + 3, y + 3, 2 + (int) (Math.random() * 5 + 5),
						2 + (int) (Math.random() * 5 + 5), 2));
			}
			break;
		case 3:
			// landing
			break;
		case 2:
			// jumping
			particles.add(new Particle(x - 15 + 5, y - 5 + 2, 10, 5, 3));
			particles.add(new Particle(x + 60 + 2, y - 5 + 2, 10, 5, 4));
			break;
		case 4:
			// smoke
			int size = 100;
			particles.add(new Particle(x - 25, y, size, size, 5));
			particles.add(new Particle(x + 100, y, size, size, 5));
			particles.add(new Particle(x - 25, y + 50, size, size, 5));
			particles.add(new Particle(x + 100, y + 50, size, size, 5));
			particles.add(new Particle(x - 25, y + 100, size, size, 5));
			particles.add(new Particle(x + 100, y + 100, size, size, 5));
			particles.add(new Particle(x + 40, y + 25, size, size, 5));
			particles.add(new Particle(x + 40, y + 75, size, size, 5));
			particles.add(new Particle(x + 40, y - 25, size, size, 5));
			particles.add(new Particle(x + 40, y + 125, size, size, 5));
			break;
		case 5:
			// strong impact
			for (int i = 0; i < 7; i++) {
				particles.add(
						new Particle(x, y, 2 + (int) (Math.random() * 8 + 7), 2 + (int) (Math.random() * 8 + 7), 6));
			}
			break;
		}
	}

	// Method to advance the level, usually after finishing.
	// Currently simply moves to the next level.
	// Eventually needs much more fanfare.
	public void levelAdvance(int next) {
		// Reset all values edited during the level's progress
		scrollx = 0;
		scrolly = 0;
		bgscrollx = 0;
		bgscrolly = 0;
		activeText = false;
		textBoxNum = -1;
		textScrollNum[0] = -1;
		textScrollNum[1] = -1;
		numSelected = 0;
		player = new Player(this);

		// And prepare to load the next level
		state = GameState.LEVEL;
		fade = 320;
		String next_lvl = "";
		switch (next) {
		case 1:
			next_lvl = "Stage 1-1";
			break;
		case 2:
			next_lvl = "Stage 1-2";
			break;
		case 3:
			next_lvl = "Stage 1-3";
			break;
		case 4:
			next_lvl = "Stage 1-4";
			break;
		default:
			System.err.println("Error: Finish element has no assigned destination");
			next_lvl = "Default";
		}

		// todo-determine the next level (fairly easy, provided more levels exist)
		loadLevel(next_lvl);
	}

	// Method to return to level select from level
	// Eventually needs a y/n prompt
	public void exitLevel() {
		// Reset all values edited during the level's progress
		scrollx = 0;
		scrolly = 0;
		bgscrollx = 0;
		bgscrolly = 0;
		activeText = false;
		textBoxNum = -1;
		textScrollNum[0] = -1;
		textScrollNum[1] = -1;
		numSelected = 0;
		player = new Player(this);
		graphics = new ArrayList<Graphic>();

		// And load level select
		fade = 310;
		state = GameState.SELECT;
		loadLevel("");
	}

	public void loadLevel(String which) {
		// Clear everything
		text = new ArrayList<ArrayList<String>>();
		faces = new ArrayList<Image>();
		faceNames = new ArrayList<String>();
		area = new ArrayList<Tile>();
		interactables = new ArrayList<Interactable>();
		particles = new ArrayList<Particle>();
		graphics = new ArrayList<Graphic>();

		// If loading a level
		if (state == GameState.LEVEL) {

			FileInputStream in = null;
			File file = null;
			String raw_stage = null;

			// Find level data of that stage
			fade_text = which;
			try {
				file = new File("stages/" + which + "/level.txt");
				in = new FileInputStream(file);
				// out = new FileOutputStream("stages/1.txt");

				byte[] data = new byte[(int) file.length()];
				in.read(data);

				raw_stage = new String(data, "UTF-8");
			} catch (Exception e) {
				System.out.println("Error when reading stage file");
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					System.out.println("Error when closing stage file");
				}
			}

			int first = 2;
			// Process level file
			String[] lines = raw_stage.split("\n");
			for (String line : lines) {
				if (first > 0) {
					first -= 1;
					continue;
				}
				String[] elements = line.split(",");
				if (elements[0].trim().equals("Tile")) {
					try {
						area.add(new Tile(Integer.parseInt(elements[1].trim()), Integer.parseInt(elements[2].trim()),
								Integer.parseInt(elements[3].trim()), Integer.parseInt(elements[4].trim()),
								Integer.parseInt(elements[5].trim()), Integer.parseInt(elements[6].trim())));
					} catch (Exception e) {
						System.out.println("Error reading stage tile: " + line);
					}
				} else if (elements[0].trim().equals("Graphic")) {
					try {
						graphics.add(
								new Graphic(Integer.parseInt(elements[1].trim()), Integer.parseInt(elements[2].trim()),
										Integer.parseInt(elements[3].trim()), Integer.parseInt(elements[4].trim()),
										Integer.parseInt(elements[5].trim()), Integer.parseInt(elements[6].trim())));
					} catch (Exception e) {
						System.out.println(e + "Error reading stage graphic: " + line);
					}
				} else if (elements[0].trim().equals("ConnectedTile")) {
					Tile tied = new Tile(Integer.parseInt(elements[8].trim()), Integer.parseInt(elements[9].trim()),
							Integer.parseInt(elements[10].trim()), Integer.parseInt(elements[11].trim()),
							Integer.parseInt(elements[12].trim()), Integer.parseInt(elements[13].trim()));
					interactables.add(new ConnectedTile(Integer.parseInt(elements[1].trim()),
							Integer.parseInt(elements[2].trim()), Integer.parseInt(elements[3].trim()),
							Integer.parseInt(elements[4].trim()), Integer.parseInt(elements[5].trim()),
							Integer.parseInt(elements[6].trim()), tied));
				} else {
					try {
						interactables.add(new Interactable(Integer.parseInt(elements[1].trim()),
								Integer.parseInt(elements[2].trim()), Integer.parseInt(elements[3].trim()),
								Integer.parseInt(elements[4].trim()), Integer.parseInt(elements[5].trim()),
								Integer.parseInt(elements[6].trim()), Integer.parseInt(elements[7].trim())));
					} catch (Exception e) {
						System.out.println(e + "Error reading stage interactable: " + line);
					}
				}
			}

			// Read text file
			FileInputStream text_in = null;
			File text_file = null;
			String raw_text = null;
			try {
				text_file = new File("stages/" + which + "/text.txt");
				text_in = new FileInputStream(text_file);
				// out = new FileOutputStream("stages/1.txt");

				byte[] data = new byte[(int) text_file.length()];
				text_in.read(data);

				raw_text = new String(data, "UTF-8");
			} catch (Exception e) {
				// Might want to make this set the final variable to something else other than
				// null
				// System.out.println("Error when reading stage file");
			}
			if (text_in != null) {
				try {
					text_in.close();
				} catch (IOException e) {
					System.out.println("Error when closing text file");
				}
			}
			if (raw_text != null) {
				// System.out.println(raw_text);
				String[] text_lines = raw_text.split("\n");
				// System.out.println(text_lines);
				for (String line : text_lines) {
					double line_length = CHARS_PER_LINE;
					String[] words = line.split(" ");
					switch (words[0]) {
					case "[SiegeNormal]":
						faces.add(face_images[0]);
						faceNames.add("Siege");
						break;
					case "[SiegeEnigmatic]":
						faces.add(face_images[2]);
						faceNames.add("Siege");
						break;
					case "[SiegeLion]":
						faces.add(face_images[1]);
						faceNames.add("Siege");
						break;
					case "[Nian]":
						faces.add(face_images[3]);
						faceNames.add("Nian");
						break;
					case "[W]":
						faces.add(face_images[4]);
						faceNames.add("W");
						break;
					case "[WSmile]":
						faces.add(face_images[5]);
						faceNames.add("W");
						break;
					case "[WMad]":
						faces.add(face_images[6]);
						faceNames.add("W");
						break;
					case "[WCrazy]":
						faces.add(face_images[7]);
						faceNames.add("W");
						break;
					case "[break]":
						faces.add(BREAK_POINT_SIGNIFIER);
						faceNames.add("");
						break;
					default:
						faces.add(null);
						faceNames.add("");
						line_length *= 1.25;
					}

					ArrayList<String> quip = new ArrayList<String>();
					for (String word : words) {
						if (word.charAt(0) != '[') {
							if (quip.size() == 0) {
								quip.add(word);
							} else if (quip.get(quip.size() - 1).length() + word.length() < line_length) {
								String newStr = quip.get(quip.size() - 1) + " " + word;
								quip.set(quip.size() - 1, newStr);
							} else {
								quip.add(word);
							}
						}
					}

					text.add(quip);
				}
				textBoxNum = 0;
				textScrollNum[0] = 0;
				textScrollNum[1] = 1;
				// System.out.println(text);
			}

			// If title screen, don't really need to do anythign special
		} else if (state == GameState.TITLE) {
			area = new ArrayList<Tile>();
			area.add(new Graphic(100, 100, 10));
			fade_text = "";
			// if loading level select
		} else if (state == GameState.SELECT) {
			File file = new File("stages");
			String[] files = file.list();
			area = new ArrayList<Tile>();
			int i = 0;
			for (String name : files) {
				area.add(new Graphic(800, 150 * i + 50, 500, 100, 1, 11));
				area.get(area.size() - 1).setText(name);// .substring(0, name.length() - 4));
				i++;
			}
			fade_text = "";
		}

	}

	public Color randomColor() {
		return new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	}

	public class KeysListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_SPACE:
				if (state == GameState.LEVEL) {
					isJump = true;
				}
				break;
			case KeyEvent.VK_E:
				if (state == GameState.LEVEL) {
					isAttack = true;
				}
				break;
			case KeyEvent.VK_F:
				if (state == GameState.LEVEL) {
					isHeavyAttack = true;
				}
				break;
			case KeyEvent.VK_ENTER:
				if (state == GameState.TITLE) {
					state = GameState.SELECT;
					fade = 310;
					loadLevel("");
				} else if (state == GameState.SELECT) {
					System.out.println(area.get(numSelected).getText());
					state = GameState.LEVEL;
					loadLevel(area.get(numSelected).getText());
					fade = 320;
					scrollx = 0;
					scrolly = 0;
					// loadLevel();
				} else {
					if (textScrollNum[0] == -1 && textScrollNum[1] == -1) {
						textBoxNum += 1;
						textScrollNum[0] = 0;
						textScrollNum[1] = 1;
						if (textBoxNum >= text.size()) {
							activeText = false;
						}
					} else {
						textScrollNum[0] = -1;
						textScrollNum[1] = -1;
					}
				}
				break;
			case 16:
				isShift = true;
				break;
			case KeyEvent.VK_R:
				scrollx = 0;
				scrolly = 100;
				player.goTo(0, 0);
				break;
			case KeyEvent.VK_A:
				left = -1;
				break;
			case KeyEvent.VK_D:
				right = 1;
				break;
			case KeyEvent.VK_Q:
				if (Main.debug) {
					Main.debug = false;
				} else {
					Main.debug = true;
				}
				break;
			case KeyEvent.VK_UP:
				if (state == GameState.LEVEL) {
					boolean change = false;
					zoom += .5;
					if (zoom > 1) {
						zoom = 1;
					} else {
						change = true;
					}
					// Why didn't I put this in the loop above?
					Main.gameSize.width = (int) ((double) Main.screenSize.width / zoom);
					Main.gameSize.height = (int) ((double) Main.screenSize.height / zoom);
					Main.scrollPos[0] = Main.gameSize.width / 5;
					Main.scrollPos[1] = Main.gameSize.width * 2 / 3;
					Main.scrollPos[2] = Main.gameSize.width / 5;
					Main.scrollPos[3] = Main.gameSize.width * 2 / 5;
					if (change && Main.scrollPos[3] < player.y
							&& (player.state == State.GROUNDED || player.state == State.BASIC_ATTACK
									|| player.state == State.LANDING || player.state == State.JUMPSQUAT)) {
						System.out.println("Adjusting player location");
						player.forceLanding();
						player.adjust(0, 100);
					}
				}
				break;
			case KeyEvent.VK_DOWN:
				if (state == GameState.LEVEL) {
					zoom -= .5;
					if (zoom < .5) {
						zoom = .5;
					} else {
						// So, when it zooms out, we want to reduce scrollx by... half the original
						scrollx += Main.gameSize.width / 4;
						scrolly += Main.gameSize.height / 4;
						player.adjust(Main.gameSize.width / 4, Main.gameSize.height / 4);
					}
					Main.gameSize.width = (int) ((double) Main.screenSize.width / zoom);
					Main.gameSize.height = (int) ((double) Main.screenSize.height / zoom);
					Main.scrollPos[0] = Main.gameSize.width / 5;
					Main.scrollPos[1] = Main.gameSize.width * 2 / 3;
					Main.scrollPos[2] = Main.gameSize.width / 5;
					Main.scrollPos[3] = Main.gameSize.width * 2 / 5;
				}
				break;
			case KeyEvent.VK_W:
				if (state == GameState.SELECT) {
					numSelected -= 1;
					if (numSelected < 0) {
						numSelected = area.size() - 1;
						scrolly = area.size() * -150 + 600;
					}
					if (area.get(numSelected).getScreenY() < 0) {
						scrolly += 300;
					}
				}
				break;
			case KeyEvent.VK_S:
				if (state == GameState.SELECT) {
					numSelected += 1;
					if (numSelected >= area.size()) {
						numSelected = 0;
						scrolly = 0;
					}
					if (area.get(numSelected).getScreenY() + 200 > Main.screenSize.height) {
						scrolly -= 300;
					}
				}
				break;
			case KeyEvent.VK_ESCAPE:
				if (state == GameState.LEVEL) {
					state = GameState.SELECT;
					exitLevel();
				}
			default:

			}
			requestFocusInWindow();
		}

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_SPACE:
				isJump = false;
				break;
			case KeyEvent.VK_E:
				isAttack = false;
				break;
			case KeyEvent.VK_F:
				isHeavyAttack = false;
				break;
			case 16:
				isShift = false;
				break;
			case KeyEvent.VK_A:
				left = 0;
				break;
			case KeyEvent.VK_D:
				right = 0;
				break;
			default:

			}
		}

		public void keyTyped(KeyEvent e) {

		}
	}
}
