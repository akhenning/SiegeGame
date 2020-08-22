package stageBuilder;

import javax.swing.JPanel;

import siegeGame.Tile;
import siegeGame.Interactable;

import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
	private ArrayList<Interactable> interactables = new ArrayList<Interactable>();
	// RectObj finish=new RectObj(new Point2D.Double(3000,100),50,1000,Color.BLACK);
	private boolean isShift = false;

	static int windowWidth = 0;
	private int currentLevel = 1;

	public BuilderScreen() {
		setBackground(Color.WHITE);
		// addMouseListener(new ClickListener());
		// addMouseMotionListener(new MovementListener());
		setFocusable(true);
		addKeyListener(new KeysListener());
		loadLevel(currentLevel);
		saveLevel("2");
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
		for (Interactable tile : interactables) {
			tile.draw(g2, scrollx, scrolly);
		}
	}

	public void nextFrame() {
		// Make sure we don't waste time checking tiles out of frame

		// Draw everything
		repaint();
		requestFocusInWindow();

	}

	public void loadLevel(int which) {

		FileInputStream in = null;
		File file = null;
		String raw_stage = null;

		try {
			file = new File("stages/1.txt");
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
		for (String line:lines) {
			String[] elements = line.split(",");
			if (elements[0].equals("Tile")) {
				try {
					if(!elements[5].equals("")) {
						area.add(new Tile(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]), Integer.parseInt(elements[3]), Integer.parseInt(elements[4]), Integer.parseInt(elements[5])));
					} else {
						area.add(new Tile(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]), Integer.parseInt(elements[3]), Integer.parseInt(elements[4])));
					}
				}catch(Exception e) {
					System.out.println("Error reading stage element: "+line);
				}
			} else {
				try {
					if(!elements[5].equals("")) {
						interactables.add(new Interactable(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]), Integer.parseInt(elements[3]), Integer.parseInt(elements[4]), Integer.parseInt(elements[5])));
					} else {
						interactables.add(new Interactable(Integer.parseInt(elements[1]), Integer.parseInt(elements[2]), Integer.parseInt(elements[3]), Integer.parseInt(elements[4])));
					}
				}catch(Exception e) {
					System.out.println(e+"Error reading stage element: "+line);
				}
			}
			System.out.println(elements[5]);
		}
		
	}
	
	public void saveLevel(String name) {
		FileOutputStream out = null;
		File file = null;

		try {
			file = new File("stages/"+name+".txt");
			
			if (!file.exists()) {
			     file.createNewFile();
			  }
			
			out = new FileOutputStream(file);
			// out = new FileOutputStream("stages/1.txt");
			for (Tile tile:area) {
				out.write(tile.toString().getBytes());
			}
			for(Interactable inter:interactables) {
				out.write(inter.toString().getBytes());
			}

			byte[] data = new byte[(int) file.length()];

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
				scrollx = 0;
				scrolly = 100;
				break;
			case KeyEvent.VK_A:
				scrollx += 10;
				break;
			case KeyEvent.VK_D:
				scrollx -= 10;
				break;
			case KeyEvent.VK_W:
				scrolly += 10;
				break;
			case KeyEvent.VK_S:
				scrolly -= 10;
				break;
			case KeyEvent.VK_E:
				break;
			case KeyEvent.VK_UP:
				zoom += .1;
				if (zoom > 4) {
					zoom = 4;
				}
				break;
			case KeyEvent.VK_DOWN:
				zoom -= .1;
				if (zoom < .2) {
					zoom = .2;
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
}
