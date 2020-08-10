package siegeGame;

import javax.swing.JPanel;
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
	
	public static int scrollx = 0;
	public static int scrolly = 0;

	private long timeElapsed = System.nanoTime();

	private ArrayList<Tile> area = new ArrayList<Tile>();
	private ArrayList<Interactable> interactables = new ArrayList<Interactable>();
	private Player player = new Player(this);
	// RectObj finish=new RectObj(new Point2D.Double(3000,100),50,1000,Color.BLACK);
	private boolean isShift = false;
	private boolean isJump = false;
	//private int direction = 1;

	static int windowWidth = 0;
	//private int linearDirection = 0;
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

		// System.out.println(offset*animationFrame);

		for (Tile tile : area) {
			tile.draw(g2);
		}for (Interactable tile : interactables) {
			tile.draw(g2);
		}
		player.draw(g2);
	}

	public void nextFrame() {
		if (((float) (System.nanoTime() - timeElapsed) / 1000000) > 35) {
			System.out.println("Time Elapsed: " + ((float) (System.nanoTime() - timeElapsed) / 1000000));
		}
		timeElapsed = System.nanoTime();
		// for (Shape shape:groundBlocks)
		// {
		// if(player.isHitNextFrame(shape))
		// {
		// player.hitWall(shape.getCenter().getX(),shape.getCenter().getY(),shape.getXL(),shape.getYL(),shape);
		//
		// }
		// boolean b=player.isOnTopOfNextFrame(shape);
		// int top=(int)(shape.getCenter().getY()-shape.getYL());
		// player.whenTouchingGround(b,top);
		// }

		player.calcMove(left + right, isShift, isJump);

		repaint();
		requestFocusInWindow();

	}
	
	// returns height
	public int checkLandingCollision(Point2D.Double leftFoot, Point2D.Double rightFoot) {
		int highest = 1000001;
		// For every tile
		for (Tile tile:area) {
			//TODO check that tile is in viable area
			// See if either foot is inside something
			if(tile.isInside(leftFoot) || tile.isInside(rightFoot)) {
				// If it is, save the lowest Y value
				if(tile.y<highest) {
					highest = tile.y;
				}
			}
		}
		return highest;
	}
	public int checkHorizontalCollision(Point2D.Double left, Point2D.Double right) {
		//TODO check that tile is in viable area
		// For every tile
		for (Tile tile:area) {
			//TODO check that tile is in viable area
			// See if either foot is inside something
			if(tile.isInside(left)) {
				return tile.x+tile.width+15 + 2;
			} else if (tile.isInside(right)) {
				return tile.x-75 - 2;
			}
		}
		return -1000001;
	}
	public boolean checkHitboxCollision(ArrayList<Hitbox> hitboxes) {
		// TODO optimize checking if valid hitbox or something
		int point[];
		for (Hitbox box: hitboxes) {
			if (box.isActive()) {
				for (Interactable tile:interactables) {
					//TODO check that tile is in viable area
					for (int i=1;i<5;i++) {
						point = box.getRelativePoint(i);
						point[0] += player.x - Screen.scrollx;
						point[1] += player.y - Screen.scrolly;
						if(i==4) {
							System.out.println(point[0]+", "+point[1]+" | "+tile.x+", "+tile.y + " " + scrolly);
						}
						if(tile.isInside(point)) {
							System.out.println("MADE CONTACT");
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public void loadLevel(int which) {
		area = new ArrayList<Tile>();
		area.add(new Tile(-200,600,2000,400));
		area.add(new Tile(200,200,200,200));
		area.add(new Tile(600,300,200,100));
		area.add(new Tile(900,0,200,100));
		area.add(new Tile(0,150,200,100));
		area.add(new Tile(2200,600,200,100));
		interactables.add(new Interactable(1200,-2000,200,2600));
		interactables.add(new Interactable(2000,-2000,200,2600));
		area.add(new Tile(1000,-1000,200,1200));
		area.add(new Tile(2200,-1000,200,1200));
	}

	public Color randomColor() {
		return new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
	}

	public class KeysListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				isJump = true;

			} else if (e.getKeyCode() == 16) {
				isShift = true;
				scrollx=0;
				scrolly=0;
			} else if (e.getKeyCode() == KeyEvent.VK_A) {
				left = -1;
			} else if (e.getKeyCode() == KeyEvent.VK_D) {
				right = 1;
			}
			requestFocusInWindow();
		}

		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				isJump = false;

			} else if (e.getKeyCode() == 16) {
				isShift = false;
			} else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				// isJumping=false;
			} else if (e.getKeyCode() == KeyEvent.VK_A) {
				left = 0;
			} else if (e.getKeyCode() == KeyEvent.VK_D) {
				right = 0;
			}
		}

		public void keyTyped(KeyEvent e) {

		}
	}
}
