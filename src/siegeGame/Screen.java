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

public class Screen extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	long timeElapsed;

	ArrayList<Tile> area = new ArrayList<Tile>();
	Player player = new Player();
	// RectObj finish=new RectObj(new Point2D.Double(3000,100),50,1000,Color.BLACK);
	boolean isShift = false;
	boolean isJump = false;
	int direction = 1;

	static int windowWidth = 0;
	int linearDirection = 0;
	int currentLevel = 1;
	double left = 0;
	double right = 0;

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

	public void loadLevel(int which) {
		area = new ArrayList<Tile>();
		area.add(new Tile(200,200,200,200));
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
