package stageBuilder;

import javax.swing.JPanel;

import siegeGame.Tile;
import siegeGame.Interactable;

import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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

public class BuilderScreen extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static double zoom = .5;

	public static int scrollx = 0;
	public static int scrolly = 0;

	private ArrayList<Tile> area = new ArrayList<Tile>();
	//private ArrayList<Interactable> interactables = new ArrayList<Interactable>();
	// RectObj finish=new RectObj(new Point2D.Double(3000,100),50,1000,Color.BLACK);
	private boolean isShift = false;
	private Tile lastActiveTile = null;
	boolean dragMode = true;// true is drag, false is resize
	boolean canDrag = false;// true if currently dragging
	boolean nowResize;// true is resize mode
	private Point2D.Double lastPoint = null;
	private double[] selectOffset = new double[2];
	private int selectSide = 0;

	private Font font = new Font("Serif", Font.PLAIN, 150);
	public static Image E = Toolkit.getDefaultToolkit().getImage("assets/E key.png");
	public static Image R = Toolkit.getDefaultToolkit().getImage("assets/R key.png");

	static int windowWidth = 0;
	//private int currentLevel = 1;

	public BuilderScreen() {
		setBackground(Color.WHITE);
		addMouseListener(new ClickListener());
		addMouseMotionListener(new MovementListener());
		setFocusable(true);
		addKeyListener(new KeysListener());
		loadLevel("2");
		//saveLevel("2");
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
		// g2.translate(-Main.screenSize.width/2+Main.screenSize.width/zoom/2,
		// -Main.screenSize.height/2+Main.screenSize.height/zoom/2);

		for (Tile tile : area) {
			tile.draw(g2, scrollx, scrolly);
		}
		//for (Interactable tile : interactables) {
		//	tile.draw(g2, scrollx, scrolly);
		//}

		if (!dragMode && lastActiveTile != null) {
			lastActiveTile.drawSide(g2, scrollx, scrolly, selectSide);
		}
		// todo lets just wrap this up and figure out why interactables arent ... oh.
		// its because it's only checking the one.

		int fontX = (int) (BuilderMain.gameSize.width * .65);
		int fontY = (int) (BuilderMain.gameSize.height * .85);
		g2.drawImage(E, (int) (BuilderMain.gameSize.width * .58), (int) (BuilderMain.gameSize.height * .775), null);
		g2.setColor(Color.white);
		g2.fillRect(fontX - 10, (int) (BuilderMain.gameSize.height * .75), 900, 200);
		g2.setColor(Color.black);
		g2.setFont(font);
		if (dragMode) {
			g2.drawString("Mode: Drag", fontX, fontY);
		} else {
			g2.drawString("Mode: Resize", fontX, fontY);
			g2.drawString("Select a Side", (int) (BuilderMain.gameSize.width * .07), fontY);
			g2.drawImage(R, 0, (int) (BuilderMain.gameSize.height * .775), null);
		}
	}

	public void nextFrame() {
		// Make sure we don't waste time checking tiles out of frame

		// Draw everything
		repaint();
		requestFocusInWindow();

	}

	public void loadLevel(String which) {

		FileInputStream in = null;
		File file = null;
		String raw_stage = null;

		try {
			file = new File("stages/"+which+".txt");
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
				System.out.println("Error when closing stage file when reading");
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
						area.add(new Interactable(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]),
								Integer.parseInt(elements[3]), Integer.parseInt(elements[4]),
								Integer.parseInt(elements[5])));
					} else {
						area.add(new Interactable(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]),
								Integer.parseInt(elements[3]), Integer.parseInt(elements[4])));
					}
				} catch (Exception e) {
					System.out.println(e + "Error reading stage element: " + line);
				}
			}
			// System.out.println(elements[5]);
		}

	}

	public void saveLevel(String name) {
		FileOutputStream out = null;
		File file = null;

		try {
			file = new File("stages/" + name + ".txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			out = new FileOutputStream(file);
			// out = new FileOutputStream("stages/1.txt");
			for (Tile tile : area) {
				out.write(tile.toString().getBytes());
			}
			//for (Interactable inter : interactables) {
			//	out.write(inter.toString().getBytes());
			//}

			// byte[] data = new byte[(int) file.length()];

			out.flush();

		} catch (Exception e) {
			System.out.println("Error when writing stage file");
		}
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				System.out.println("Error when closing stage file after writing");
			}
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
				break;
			case 16:
				isShift = true;
				// scrollx = 0;
				// scrolly = 100;
				break;
			case KeyEvent.VK_A:
				scrollx += 10;
				if (isShift) {
					scrollx += 30;
				}
				break;
			case KeyEvent.VK_D:
				scrollx -= 10;
				if (isShift) {
					scrollx -= 30;
				}
				break;
			case KeyEvent.VK_W:
				scrolly += 10;
				if (isShift) {
					scrolly += 30;
				}
				break;
			case KeyEvent.VK_S:
				scrolly -= 10;
				if (isShift) {
					scrolly -= 30;
				}
				break;
			case KeyEvent.VK_E:
				if (dragMode) {
					dragMode = false;
				} else {
					dragMode = true;
				}
				break;
			case KeyEvent.VK_R:
				selectSide += 1;
				if (selectSide > 3) {
					selectSide = 0;
				}
				break;
			case KeyEvent.VK_UP:
				zoom += .1;
				if (zoom > 4) {
					zoom = 4;
				}
				BuilderMain.gameSize.width = (int) ((double) BuilderMain.screenSize.width / zoom);
				BuilderMain.gameSize.height = (int) ((double) BuilderMain.screenSize.height / zoom);
				break;
			case KeyEvent.VK_DOWN:
				zoom -= .1;
				if (zoom < .2) {
					zoom = .2;
				}
				BuilderMain.gameSize.width = (int) ((double) BuilderMain.screenSize.width / zoom);
				BuilderMain.gameSize.height = (int) ((double) BuilderMain.screenSize.height / zoom);
				break;
			case KeyEvent.VK_ENTER:
				saveLevel("2");
				break;
			default:

			}
			repaint();
			requestFocusInWindow();
		}

		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_SPACE:
				break;
			case 16:
				isShift = false;
				break;
			case KeyEvent.VK_A:
				break;
			case KeyEvent.VK_D:
				break;
			default:

			}
		}

		public void keyTyped(KeyEvent e) {

		}
	}

	public class ClickListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {

			Point2D.Double point = new Point2D.Double(e.getPoint().getX() * 2 - scrollx,
					e.getPoint().getY() * 2 - scrolly);
			if (lastActiveTile != null && lastActiveTile.isInside(point)) {
				canDrag = true;
				selectOffset = lastActiveTile.getDifference(point);
				lastPoint = point;
			} else {
				lastActiveTile = null;// new Circle(new Point2D.Double(0, 0), 0, Color.WHITE);
				for (Tile shape : area) {
					System.out.println(shape.isInside(point));
					if (shape.isInside(point)) {
						canDrag = true;
						lastActiveTile = shape;
						selectOffset = shape.getDifference(point);
						lastPoint = point;
					}
				}
			}
			// System.out.println(point.getX());
			// lastActiveShape.goTo(point.getX(),point.getY());
			// if (lastActiveTile.isInside(point)) {
			// dragMode = false;

			// }
			repaint();
			requestFocusInWindow();
		}

		public void mouseReleased(MouseEvent e) {
			lastPoint = null;
			canDrag = false;
			nowResize = false;
			requestFocusInWindow();
		}
	}

	public class MovementListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			//System.out.println("Trying: "+canDrag+" " +dragMode);
			Point2D.Double point = new Point2D.Double(e.getPoint().getX()*2-scrollx, e.getPoint().getY()*2-scrolly);
			if (dragMode) {
				if (canDrag) {
					lastActiveTile.goTo(point.getX()-selectOffset[0], point.getY()-selectOffset[1]);
					repaint();
				}
			} else {
				if (canDrag && lastPoint != null) {
					switch(selectSide) {
						case 0:
							// increase or decrease height only
							lastActiveTile.resize(0, 0,0,point.getY()-lastPoint.getY());
							break;
						case 1:
							// increase or decrease width and X
							lastActiveTile.resize(-point.getX()+lastPoint.getX(), 0,-point.getX()+lastPoint.getX(),0);
							break;
						case 2:
							// increase or decrease height and Y
							lastActiveTile.resize(0,-point.getY()+lastPoint.getY(), 0,-point.getY()+lastPoint.getY());
							break;
						case 3:
							// increase or decrease width only
							lastActiveTile.resize(0, 0,point.getX()-lastPoint.getX(),0);
							break;
					}
					//lastActiveTile.resize(-point.getX()+lastPoint.getX(), -point.getY()+lastPoint.getY());
					//lastActiveTile.setRadius(lastActiveTile.getCenter().distance(point));
					repaint();
				}
			}
			lastPoint = point;
			requestFocusInWindow();
		}

		public void mouseMoved(MouseEvent e) {
		}
	}
}
