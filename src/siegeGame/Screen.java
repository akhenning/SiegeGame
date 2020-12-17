package siegeGame;

import javax.swing.JPanel;

import siegeGame.Tile.SlopeState;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.BasicStroke;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Screen extends JPanel {
	/**
	 * TODO; only way to fix priority issue is by making walls one-way, which 
	 * we should have done from the beginning...
	 */
	private static final long serialVersionUID = 1L;
	public static double zoom = 1;
	private static Image bg = Toolkit.getDefaultToolkit().getImage("assets/bg1.jpg");
			//Toolkit.getDefaultToolkit().getImage("assets/carlymonster_clouds.jpg");
	private static Image selectbg = Toolkit.getDefaultToolkit().getImage("assets/selectbg1.png");
	private static Image title = Toolkit.getDefaultToolkit().getImage("assets/title1.jpg");
	private int bgDimensions[] = new int[2]; // 0 is x size of background image, 1 is y size.

	public static int scrollx = 0;
	public static int scrolly = 0;
	public static int bgscrollx = 0;
	public static int bgscrolly = 0;
	private boolean first = true;

	private ArrayList<Tile> area = new ArrayList<Tile>();
	private ArrayList<Interactable> interactables = new ArrayList<Interactable>();
	private ArrayList<Particle> particles = new ArrayList<Particle>();
	private int numSelected = 0;
	private Player player = new Player(this);
	private Font font = new Font("Serif", Font.PLAIN, 100);
	private Font fade_font = new Font("Serif", Font.PLAIN, 200);

	private boolean isShift = false;
	private boolean isJump = false;
	private boolean isAttack = false;
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
		g2.setStroke(new BasicStroke(4));

		if (state == GameState.LEVEL) {
			g2.scale(zoom, zoom);

			// g2.translate(-Main.screenSize.width/2+Main.screenSize.width/zoom/2,
			// -Main.screenSize.height/2+Main.screenSize.height/zoom/2);

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
				// }
			}
		}
		for (Particle tile : particles) {
			tile.draw(g2);
		}
		
		// put fade-in above 
		if (fade > 0) {
			g2.setFont(fade_font);
			// if above 300, reduce by one (black) until is normal alpha
			Color fade_in_text = null;
			if (fade>300) {
				g2.setColor(Color.BLACK);
				fade -=1;
				if (fade<=300) {
					fade = 255;
				}
				fade_in_text = Color.WHITE;
			} else {
				// if normal alpha, fade out.
				Color fade_in = new Color(0,0,0,fade);
				g2.setColor(fade_in);
				fade -= (270-fade)/2;
				if (fade<0) {
					fade = 0;
				}
				fade_in_text = new Color(255,255,255,fade);
			}
			g2.fillRect(0, 0, 2000, 1500);
			g2.setColor(fade_in_text);
			g2.drawString(fade_text, Main.screenSize.width / 3, Main.screenSize.height / 2);
		}
		
	}

	public void drawBG(Graphics2D g2) {
		// we need to do a bigger think for this one
		// System.out.println(bgscrollx+", "+(-2*bgDimensions[0])+",
		// "+((bgDimensions[0]*-2)+Main.screenSize.width));
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
		//System.out.println("Background culling: " + bgscrollx + ", " + Main.gameSize.width + ", "
		//		+ (bgscrollx + bgDimensions[0]) + ", " + Main.gameSize.height);
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
			for (Interactable tile : interactables) {
				tile.checkIsVisible();
			}

			// Player moves; calculating collision is also in here
			player.calcMove(left + right, isShift, isJump, isAttack);
		}
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
			if (tile.isVisible()) {
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
			if (tile.isVisible()) {
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
		return highest;
	}

	// returns height
	public int checkCeilingCollision(Point2D.Double left, Point2D.Double right) {
		int highest = -1000001;
		// For every tile
		for (Tile tile : area) {
			if (tile.isVisible()) {
				if (tile.isInside(left) || tile.isInside(right)) {
					// player.setStandingOn(tile);
					// If it is, save the lowest Y value
					if (tile.y > highest) {
						highest = tile.getHeight(right.getX()) + tile.height;
					}
				}
			}
		}
		for (Interactable tile : interactables) {
			if (tile.isVisible()) {
				if (tile.isInside(left) || tile.isInside(right)) {
					// player.setStandingOn(tile);
					// If it is, save the lowest Y value
					if (tile.y > highest) {
						highest = tile.getHeight(right.getX()) + tile.height;
					}
				}
			}
		}
		return highest;
	}

	public int checkHorizontalCollision(Point2D.Double leftTop, Point2D.Double leftBot, Point2D.Double rightTop,
			Point2D.Double rightBot) {
		// For every tile
		for (Tile tile : area) {
			if (tile.isVisible() && tile.id < 100) {
				// See if either foot is inside something
				if (tile.isInside(leftTop)) {
					return tile.x + tile.width + 15 + 2;
				} else if (tile.isInside(leftBot)) {
					return tile.x + tile.width + 15 + 2;
				} else if (tile.isInside(rightTop)) {
					return tile.x - 75 - 2;
				} else if (tile.isInside(rightBot)) {
					return tile.x - 75 - 2;
				}
			}
		}
		for (Interactable tile : interactables) {
			if (tile.isVisible()) {
				// See if either foot is inside something
				if (tile.isInside(leftTop)) {
					return tile.x + tile.width + 15 + 2;
				} else if (tile.isInside(leftBot)) {
					return tile.x + tile.width + 15 + 2;
				} else if (tile.isInside(rightTop)) {
					return tile.x - 75 - 2;
				} else if (tile.isInside(rightBot)) {
					return tile.x - 75 - 2;
				}
			}
		}
		return -1000001;
	}

	public boolean checkHitboxCollision(ArrayList<Hitbox> hitboxes) {
		int dx = player.x - Screen.scrollx;
		int dy = player.y - Screen.scrolly;
		int point[];
		for (Hitbox box : hitboxes) {
			if (box.isActive()) {
				for (Interactable tile : interactables) {
					if (tile.isVisible()) {
						for (int i = 1; i < 5; i++) {
							point = box.getRelativePoint(i);
							point[0] += dx;
							point[1] += dy;
							// if (i == 4) {
							// System.out.println(
							// point[0] + ", " + point[1] + " | " + tile.x + ", " + tile.y + " " + scrolly);
							// }
							if (tile.isInside(point)) {
								// System.out.println("MADE CONTACT");
								if (tile.interact()) {
									return true;
								} else {
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
									interactables.remove(tile);
									return false;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	public boolean checkDescendingStairs(Point2D.Double leftFoot, Point2D.Double rightFoot) {
		for (Tile tile : area) {
			if (tile.isVisible() && tile.getId() >= 100) {
				if (tile.slopeState == SlopeState.RIGHT) {
					// System.out.println("Checking for object");
					if (tile.isInside(rightFoot)) {
						// System.out.println("Successfully found object)");
						return true;
					}
				} else {
					if (tile.isInside(leftFoot)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void loadLevel(String which) {

		if (state == GameState.LEVEL) {

			FileInputStream in = null;
			File file = null;
			String raw_stage = null;

			fade_text = which;
			try {
				file = new File("stages/" + which + ".txt");
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

			area = new ArrayList<Tile>();
			String[] lines = raw_stage.split("\n");
			for (String line : lines) {
				String[] elements = line.split(",");
				if (elements[0].equals("Tile")) {
					try {
						if (!elements[5].equals("")) {
							area.add(new Tile(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]),
									Integer.parseInt(elements[3]), Integer.parseInt(elements[4]),
									Integer.parseInt(elements[5])));
						} else {
							area.add(new Tile(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]),
									Integer.parseInt(elements[3]), Integer.parseInt(elements[4])));
						}
					} catch (Exception e) {
						System.out.println("Error reading stage element: " + line);
					}
				} else {
					try {
						if (!elements[5].equals("")) {
							interactables.add(new Interactable(Integer.parseInt(elements[1]),
									Integer.parseInt(elements[2]), Integer.parseInt(elements[3]),
									Integer.parseInt(elements[4]), Integer.parseInt(elements[5])));
						} else {
							interactables
									.add(new Interactable(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]),
											Integer.parseInt(elements[3]), Integer.parseInt(elements[4])));
						}
					} catch (Exception e) {
						System.out.println(e + "Error reading stage element: " + line);
					}
				}
				System.out.println(elements[5]);
			}
		} else if (state == GameState.TITLE) {
			area = new ArrayList<Tile>();
			area.add(new Graphic(100, 100, 10));
			fade_text = "";
		} else if (state == GameState.SELECT) {
			File file = new File("stages");
			String[] files = file.list();
			area = new ArrayList<Tile>();
			int i = 0;
			for (String name : files) {
				area.add(new Graphic(800, 150 * i + 50, 500, 100, 11));
				area.get(area.size() - 1).setText(name.substring(0, name.length() - 4));
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
				}
				break;
			case 16:
				isShift = true;
				break;
			case KeyEvent.VK_R:
				scrollx = 0;
				scrolly = 100;
				player.litx = 0;
				player.lity = 0;
				break;
			case KeyEvent.VK_A:
				left = -1;
				break;
			case KeyEvent.VK_D:
				right = 1;
				break;
			case KeyEvent.VK_Q:
				if (player.debug) {
					player.debug = false;
				} else {
					player.debug = true;
				}
				break;
			case KeyEvent.VK_UP:
				if (state == GameState.LEVEL) {
					zoom += .5;
					if (zoom > 1) {
						zoom = 1;
					}
					Main.gameSize.width = (int) ((double) Main.screenSize.width / zoom);
					Main.gameSize.height = (int) ((double) Main.screenSize.height / zoom);
					Main.scrollPos[0] = Main.gameSize.width / 5;
					Main.scrollPos[1] = Main.gameSize.width * 2 / 3;
					Main.scrollPos[2] = Main.gameSize.width / 5;
					Main.scrollPos[3] = Main.gameSize.width * 2 / 5;
				}
				break;
			case KeyEvent.VK_DOWN:
				if (state == GameState.LEVEL) {
					zoom -= .5;
					if (zoom < .5) {
						zoom = .5;
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
