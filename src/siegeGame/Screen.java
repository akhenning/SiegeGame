package siegeGame;

import javax.swing.JPanel;

import siegeGame.Tile.SlopeState;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Point2D;

public class Screen extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static double zoom = 1;

	public static int scrollx = 0;
	public static int scrolly = 0;
	private boolean first = true;

	private ArrayList<Tile> area = new ArrayList<Tile>();
	private ArrayList<Interactable> interactables = new ArrayList<Interactable>();
	private Player player = new Player(this);
	// RectObj finish=new RectObj(new Point2D.Double(3000,100),50,1000,Color.BLACK);
	private boolean isShift = false;
	private boolean isJump = false;
	// private int direction = 1;

	static int windowWidth = 0;
	// private int linearDirection = 0;
	private int currentLevel = 1;
	private double left = 0;
	private double right = 0;

	public Screen() {
		setBackground(Color.WHITE);
		// addMouseListener(new ClickListener());
		// addMouseMotionListener(new MovementListener());
		setFocusable(true);
		addKeyListener(new KeysListener());
		loadLevel(currentLevel);
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
		g2.scale(zoom, zoom);
		//g2.translate(-Main.screenSize.width/2+Main.screenSize.width/zoom/2, -Main.screenSize.height/2+Main.screenSize.height/zoom/2);

		if (first) {
			first = false;
			player.load(g2);
		}

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
	}

	public void nextFrame() {
		// Make sure we don't waste time checking tiles out of frame
		for (Tile tile : area) {
			tile.checkIsVisible();
		}
		for (Interactable tile : interactables) {
			tile.checkIsVisible();
		}

		// Player moves; calculating collision is also in here
		player.calcMove(left + right, isShift, isJump);

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
		return -1000001;
	}

	public boolean checkHitboxCollision(ArrayList<Hitbox> hitboxes) {
		int point[];
		for (Hitbox box : hitboxes) {
			if (box.isActive()) {
				for (Interactable tile : interactables) {
					if (tile.isVisible()) {
						for (int i = 1; i < 5; i++) {
							point = box.getRelativePoint(i);
							point[0] += player.x - Screen.scrollx;
							point[1] += player.y - Screen.scrolly;
							// if (i == 4) {
							// System.out.println(
							// point[0] + ", " + point[1] + " | " + tile.x + ", " + tile.y + " " + scrolly);
							// }
							if (tile.isInside(point)) {
								// System.out.println("MADE CONTACT");
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public void loadLevel(int which) {
		area = new ArrayList<Tile>();
		area.add(new Tile(-200, 600, 2000, 400));
		area.add(new Tile(200, 200, 200, 200));
		// area.add(new Tile(600, 300, 200, 100));
		area.add(new Tile(900, 0, 200, 100));
		area.add(new Tile(0, 150, 200, 100));
		area.add(new Tile(2200, 600, 200, 100));
		interactables.add(new Interactable(1200, -2000, 200, 2200));
		interactables.add(new Interactable(2000, -2000, 200, 2200));
		area.add(new Tile(1000, -1000, 200, 1200));
		area.add(new Tile(2200, -1000, 200, 1200));
		area.add(new Tile(1000, 400, 200, 200, 101));
		area.add(new Tile(1200, 400, 200, 200));
		area.add(new Tile(1400, 200, 400, 200, 101));
		area.add(new Tile(-200, 400, 200, 200, 102));
		interactables.add(new Interactable(0, 400, 200, 200));
		

		area.add(new Tile(2000, 200, 200, 200,10));
		area.add(new Tile(3000, 200, 200, 200,10));
		area.add(new Tile(4000, 200, 200, 200,10));
	}

	public Color randomColor() {
		return new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	}

	public class KeysListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_SPACE:
				isJump = true;
				break;
			case 16:
				isShift = true;
				scrollx = 0;
				scrolly = 100;
				player.litx=0;
				player.lity=0;
				break;
			case KeyEvent.VK_A:
				left = -1;
				break;
			case KeyEvent.VK_D:
				right = 1;
				break;
			case KeyEvent.VK_E:
				if (player.debug) {
					player.debug = false;
				} else {
					player.debug = true;
				}
				break;
			case KeyEvent.VK_UP:
				zoom += .1;
				if (zoom > 4) {
					zoom = 4;
				}
				Main.gameSize.width=(int)((double)Main.screenSize.width/zoom);
				Main.gameSize.height=(int)((double)Main.screenSize.height/zoom);
				Main.scrollPos[0] = Main.gameSize.width/5;
				Main.scrollPos[1] = Main.gameSize.width*2/3;
				Main.scrollPos[2] = Main.gameSize.width/5;
				Main.scrollPos[3] = Main.gameSize.width*2/5;
				break;
			case KeyEvent.VK_DOWN:
				zoom -= .1;
				if (zoom < .2) {
					zoom = .2;
				}
				Main.gameSize.width=(int)((double)Main.screenSize.width/zoom);
				Main.gameSize.height=(int)((double)Main.screenSize.height/zoom);
				Main.scrollPos[0] = Main.gameSize.width/5;
				Main.scrollPos[1] = Main.gameSize.width*2/3;
				Main.scrollPos[2] = Main.gameSize.width/5;
				Main.scrollPos[3] = Main.gameSize.width*2/5;
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
