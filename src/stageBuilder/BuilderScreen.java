package stageBuilder;

import javax.swing.JPanel;

import siegeGame.Tile;
import siegeGame.ConnectedTile;
import siegeGame.Graphic;
import siegeGame.Interactable;
import siegeGame.Player;

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

	public class TileType {
		public int id;
		public boolean isInteractable;
		public String description;

		public TileType(int id, boolean isInteractable, String description) {
			this.id = id;
			this.isInteractable = isInteractable;
			this.description = description;
		}
	}

	public ArrayList<TileType> TileTypes = new ArrayList<TileType>();
	public int current_type = 0;
	public ArrayList<TileType> ComplexTypes = new ArrayList<TileType>();
	public int current_complex_type = 0;

	public static int scrollx = 0;
	public static int scrolly = 0;

	private ArrayList<Tile> area = new ArrayList<Tile>();
	// private ArrayList<Interactable> interactables = new
	// ArrayList<Interactable>();
	// RectObj finish=new RectObj(new Point2D.Double(3000,100),50,1000,Color.BLACK);
	private boolean isShift = false;
	private Tile lastActiveTile = null;
	private int mode = 0;// 0 is drag, 1 is resize, 2 is change, 3 is add, 4 is change collision type, 5 is add complex object
	boolean canDrag = false;// true if currently dragging
	boolean nowResize;// true is resize mode
	private Point2D.Double lastPoint = null;
	private double[] selectOffset = new double[2];
	private int selectSide = 0;
	private String currentLevel = "";
	private int numSelected = 0;
	private static Image[] saveIcons = { Toolkit.getDefaultToolkit().getImage("assets/saved.png"),
			Toolkit.getDefaultToolkit().getImage("assets/save.png") };
	private boolean hasChanges = false;

	private Font font = new Font("Serif", Font.PLAIN, 150);
	private Font font2 = new Font("Serif", Font.PLAIN, 100);
	private Font font3 = new Font("Serif", Font.PLAIN, 50);
	public static Image E = Toolkit.getDefaultToolkit().getImage("assets/E key.png");
	public static Image R = Toolkit.getDefaultToolkit().getImage("assets/R key.png");
	private static Image selectbg = Toolkit.getDefaultToolkit().getImage("assets/selectbg1.png");

	private enum GameState {
		TITLE, LEVEL, SELECT
	}

	public GameState state = GameState.SELECT;

	static int windowWidth = 0;
	// private int currentLevel = 1;

	public BuilderScreen() {
		TileTypes.add(new TileType(0, false, "Basic black block"));
		TileTypes.add(new TileType(2, false, "Invisible block"));
		TileTypes.add(new TileType(101, false, "Right-facing slope"));
		TileTypes.add(new TileType(102, false, "Left-facing slope"));
		TileTypes.add(new TileType(50, true, "Basic bounce pad"));
		TileTypes.add(new TileType(60, true, "Basic destructable block"));
		TileTypes.add(new TileType(61, true, "Destructable W element"));
		TileTypes.add(new TileType(70, true, "Basic text prompter"));
		TileTypes.add(new TileType(71, true, "Basic activated text prompter"));
		TileTypes.add(new TileType(99, true, "Basic finish element"));
		ComplexTypes.add(new TileType(-1, false, "Respawn Paired Element"));
		ComplexTypes.add(new TileType(0, false, "Blank Graphic"));

		setBackground(Color.WHITE);
		addMouseListener(new ClickListener());
		addMouseMotionListener(new MovementListener());
		setFocusable(true);
		addKeyListener(new KeysListener());
		loadLevel("2");
		// saveLevel("2");
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

			for (Tile tile : area) {
				tile.draw(g2, scrollx, scrolly);
			}
			// for (Interactable tile : interactables) {
			// tile.draw(g2, scrollx, scrolly);
			// }
			g2.drawImage(Player.IDLE, 450 + scrollx, 375 + scrolly, 200 + 450 + scrollx, 375 + 250 + scrolly, 0, 0, 200,
					250, null);

			if (lastActiveTile != null) {
				g2.setColor(Color.blue);
				g2.drawRect(lastActiveTile.getX() + scrollx, scrolly + lastActiveTile.getY(), lastActiveTile.getWidth(),
						lastActiveTile.getHeight());
			}
			g2.setColor(Color.black);
			if (mode == 1 && lastActiveTile != null) {
				g2.setFont(font3);
				lastActiveTile.drawSide(g2, scrollx, scrolly, selectSide);
			}

			if (hasChanges) {
				g2.drawImage(saveIcons[1], BuilderMain.gameSize.width - 200, 0, null);
			} else {
				g2.drawImage(saveIcons[0], BuilderMain.gameSize.width - 200, 0, null);
			}

			g2.setFont(font2);
			int fontX = (int) (BuilderMain.gameSize.width * .65);
			int fontY = (int) (BuilderMain.gameSize.height * .85);
			g2.drawString(currentLevel, (int) (BuilderMain.gameSize.width * .8), 100);
			if (mode == 3) {
				g2.drawString("Adding: " + TileTypes.get(current_type).description,
						(int) (BuilderMain.gameSize.width * .07), fontY);
			} else if (mode == 5) {
				g2.drawString("Adding: " + ComplexTypes.get(current_complex_type).description,
						(int) (BuilderMain.gameSize.width * .07), fontY);
				
			}

			g2.setFont(font);
			g2.drawImage(E, (int) (BuilderMain.gameSize.width * .58), (int) (BuilderMain.gameSize.height * .775), null);
			g2.setColor(Color.white);
			g2.fillRect(fontX - 10, (int) (BuilderMain.gameSize.height * .75), 900, 200);
			g2.setColor(Color.black);
			if (mode == 0) {
				g2.drawString("Mode: Drag", fontX, fontY);
			} else if (mode == 1) {
				g2.drawString("Mode: Resize", fontX, fontY);
				g2.drawString("Select a Side", (int) (BuilderMain.gameSize.width * .07), fontY);
				g2.drawImage(R, 0, (int) (BuilderMain.gameSize.height * .775), null);
			} else if (mode == 2) {
				g2.drawString("Mode: Change", fontX, fontY);
				g2.drawString("Change Type", (int) (BuilderMain.gameSize.width * .07), fontY);
				g2.drawImage(R, 0, (int) (BuilderMain.gameSize.height * .775), null);
			} else if (mode == 3) {
				g2.drawString("Mode: Add", fontX, fontY);
				g2.drawImage(R, 0, (int) (BuilderMain.gameSize.height * .775), null);
			} else if (mode == 4) {
				g2.drawString("Mode: Collision", fontX, fontY);
				g2.drawString("Change Type", (int) (BuilderMain.gameSize.width * .07), fontY);
				g2.drawImage(R, 0, (int) (BuilderMain.gameSize.height * .775), null);
			} else if (mode == 5) {
				g2.drawString("Mode: Add Complex", fontX, fontY);
				g2.drawImage(R, 0, (int) (BuilderMain.gameSize.height * .775), null);
			} 
		} else if (state == GameState.SELECT) {
			g2.setFont(font2);
			g2.fillRect(0, 1000, 2000, 9999);
			g2.drawImage(selectbg, scrollx, scrolly, null);
			int i = 0;
			g2.setColor(Color.BLACK);
			for (Tile tile : area) {
				// if (tile.isVisible()) {
				tile.draw(g2, scrollx, scrolly);
				if (i == numSelected) {
					tile.drawBoxAround(g2, scrollx, scrolly);
				}
				i++;
				// }
			}
		}
	}

	public void nextFrame() {
		// Make sure we don't waste time checking tiles out of frame

		// Draw everything
		repaint();
		requestFocusInWindow();

	}

	public void loadLevel(String which) {

		if (state == GameState.LEVEL) {

			FileInputStream in = null;
			File file = null;
			String raw_stage = null;

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
					System.out.println("Error when closing stage file when reading");
				}
			}

			int first = 2;
			area = new ArrayList<Tile>();
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
						System.out.println("Error reading stage element: " + line);
					}
				} else if (elements[0].trim().equals("Graphic")) {
					try {
						area.add(new Graphic(Integer.parseInt(elements[1].trim()), Integer.parseInt(elements[2].trim()),
								Integer.parseInt(elements[3].trim()), Integer.parseInt(elements[4].trim()),
								Integer.parseInt(elements[5].trim()), Integer.parseInt(elements[6].trim())));
					} catch (Exception e) {
						System.out.println(e + "Error reading stage element: " + line);
					}
				} else if (elements[0].trim().equals("ConnectedTile")) {
					Tile tied = new Tile(Integer.parseInt(elements[8].trim()), Integer.parseInt(elements[9].trim()),
							Integer.parseInt(elements[10].trim()), Integer.parseInt(elements[11].trim()),
							Integer.parseInt(elements[12].trim()), Integer.parseInt(elements[13].trim()));
					area.add(new ConnectedTile(Integer.parseInt(elements[1].trim()),
								Integer.parseInt(elements[2].trim()), Integer.parseInt(elements[3].trim()),
								Integer.parseInt(elements[4].trim()), Integer.parseInt(elements[5].trim()),
								Integer.parseInt(elements[6].trim()),tied));
				} else {
					try {
						area.add(new Interactable(Integer.parseInt(elements[1].trim()),
								Integer.parseInt(elements[2].trim()), Integer.parseInt(elements[3].trim()),
								Integer.parseInt(elements[4].trim()), Integer.parseInt(elements[5].trim()),
								Integer.parseInt(elements[6].trim()), Integer.parseInt(elements[7].trim())));
					} catch (Exception e) {
						System.out.println(e + "Error reading stage element: " + line);
					}
				}
				// System.out.println(elements[5]);
			}
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
		}

	}

	public void saveLevel(String name) {
		System.out.println("Saved.");
		FileOutputStream out = null;
		File file = null;

		try {
			file = new File("stages/" + name + "/level.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			out = new FileOutputStream(file);
			// out = new FileOutputStream("stages/1.txt");
			out.write("Element Type\tX\tY\tWidth\tHeight\tClip\tID\tOther\tDescription\n".getBytes());
			out.write("-------------------------------------------------------------------------------------------\n".getBytes());
			for (Tile tile : area) {
				if (tile.should_be_saved) {
					out.write(tile.toString().getBytes());
				}
			}
			// for (Interactable inter : interactables) {
			// out.write(inter.toString().getBytes());
			// }

			// byte[] data = new byte[(int) file.length()];

			out.flush();
			hasChanges = false;

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
				if (state != GameState.SELECT) {
					scrollx += 20;
					if (isShift) {
						scrollx += 60;
					}
				}
				break;
			case KeyEvent.VK_D:

				if (state != GameState.SELECT) {
					scrollx -= 20;
					if (isShift) {
						scrollx -= 60;
					}
				}
				break;
			case KeyEvent.VK_W:
				if (state == GameState.SELECT) {
					numSelected -= 1;
					if (numSelected < 0) {
						numSelected = area.size() - 1;
						scrolly = area.size() * -150 + 600;
					}
					if (area.get(numSelected).getScreenY(scrolly) < 0) {
						scrolly += 300;
					}
				} else {
					scrolly += 20;
					if (isShift) {
						scrolly += 60;
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
					if (area.get(numSelected).getScreenY(scrolly) + 200 > BuilderMain.screenSize.height) {
						scrolly -= 300;
					}
				} else {
					scrolly -= 20;
					if (isShift) {
						scrolly -= 60;
					}
				}
				break;
			case KeyEvent.VK_E:
				mode += 1;
				if (mode > 5) {
					mode = 0;
				}
				break;
			case KeyEvent.VK_Q:
				switch (mode) {
				case 1:
					selectSide += 1;
					if (selectSide > 3) {
						selectSide = 0;
					}
					break;
				case 2:
					current_type += 1;
					if (current_type >= TileTypes.size()) {
						current_type = 0;
					}
					System.out.println(TileTypes.get(current_type).id);
					if (TileTypes.get(current_type).isInteractable == lastActiveTile.isInteractable()) {
						lastActiveTile.setId(TileTypes.get(current_type).id);
					} else if (TileTypes.get(current_type).isInteractable) {
						// if is not Interactable, but becoming one.
						Interactable temp = new Interactable(lastActiveTile.getX(), lastActiveTile.getY(),
								lastActiveTile.getWidth(), lastActiveTile.getHeight(), TileTypes.get(current_type).id);
						area.set(area.indexOf(lastActiveTile), temp);
						lastActiveTile = temp;
					} else {
						// if is currently Interactable, but becoming Tile.
						Tile temp = new Tile(lastActiveTile.getX(), lastActiveTile.getY(), lastActiveTile.getWidth(),
								lastActiveTile.getHeight(), 0, TileTypes.get(current_type).id);
						area.set(area.indexOf(lastActiveTile), temp);
						lastActiveTile = temp;
					}
					hasChanges = true;
					break;
				case 3:
					current_type += 1;
					if (current_type >= TileTypes.size()) {
						current_type = 0;
					}
					break;
				case 4:
					if (lastActiveTile != null) {
						lastActiveTile.cycleCollision();
					}
					break;
				case 5:
					current_complex_type += 1;
					if (current_complex_type >= ComplexTypes.size()) {
						current_complex_type = 0;
					}
					break;
				}
				break;
			case KeyEvent.VK_UP:
				if (state == GameState.LEVEL) {
					zoom += .1;
					if (zoom > 4) {
						zoom = 4;
					}
					BuilderMain.gameSize.width = (int) ((double) BuilderMain.screenSize.width / zoom);
					BuilderMain.gameSize.height = (int) ((double) BuilderMain.screenSize.height / zoom);
				}
				break;
			case KeyEvent.VK_DOWN:
				if (state == GameState.LEVEL) {
					zoom -= .1;
					if (zoom < .2) {
						zoom = .2;
					}
					BuilderMain.gameSize.width = (int) ((double) BuilderMain.screenSize.width / zoom);
					BuilderMain.gameSize.height = (int) ((double) BuilderMain.screenSize.height / zoom);
				}
				break;
			case KeyEvent.VK_ENTER:
				if (state == GameState.SELECT) {
					currentLevel = area.get(numSelected).getText();
					System.out.println(currentLevel);
					state = GameState.LEVEL;
					loadLevel(currentLevel);
					// loadLevel();
				} else {
					saveLevel(currentLevel);
				}
				break;
			case KeyEvent.VK_DELETE:
				lastActiveTile.cleanup();
				area.remove(lastActiveTile);
				lastActiveTile = null;
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

			Point2D.Double point = new Point2D.Double(e.getPoint().getX() / zoom - scrollx,
					e.getPoint().getY() / zoom - scrolly);
			if (lastActiveTile != null && lastActiveTile.isInside(point)) {
				canDrag = true;
				selectOffset = lastActiveTile.getDifference(point);
				lastPoint = point;
				hasChanges = true;
			} else {
				lastActiveTile = null;// new Circle(new Point2D.Double(0, 0), 0, Color.WHITE);
				for (Tile shape : area) {
					// System.out.println(shape.isInside(point));
					if (shape.isInside(point)) {
						canDrag = true;
						lastActiveTile = shape;
						selectOffset = shape.getDifference(point);
						lastPoint = point;
						hasChanges = true;
						current_type = 0;
					}
				}
				if (lastActiveTile == null && mode == 3) {
					if (TileTypes.get(current_type).isInteractable) {
						lastActiveTile = new Interactable(0, 0, 200, 200, TileTypes.get(current_type).id);
					} else {
						lastActiveTile = new Tile(0, 0, 200, 200, 0, TileTypes.get(current_type).id);
					}
					lastActiveTile.goTo(point.getX() - 100, point.getY() - 100);
					canDrag = true;
					area.add(lastActiveTile);
					selectOffset = lastActiveTile.getDifference(point);
					lastPoint = point;
					hasChanges = true;
				}
				if (lastActiveTile == null && mode == 5) {
					if (ComplexTypes.get(current_complex_type).id == -1) {
						lastActiveTile = new ConnectedTile(0, 0, 200, 200, 0, TileTypes.get(current_type).id);
						lastActiveTile.goToTied(point.getX(), point.getY() - 100);
						area.add(lastActiveTile.getTied());
					} else {
						lastActiveTile = new Graphic(0,0,ComplexTypes.get(current_complex_type).id);
					}
					if (lastActiveTile != null) {
						lastActiveTile.goTo(point.getX() - 100, point.getY() - 100);
						canDrag = true;
						area.add(lastActiveTile);
						selectOffset = lastActiveTile.getDifference(point);
						lastPoint = point;
						hasChanges = true;
					} else {
						System.out.println("Error: adding unrecognized type with id: " + ComplexTypes.get(current_complex_type).id);
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
			if (lastActiveTile != null && !(lastActiveTile instanceof Graphic)) {
				lastActiveTile.snap();
			}
			lastPoint = null;
			canDrag = false;
			nowResize = false;
			repaint();
			requestFocusInWindow();
		}
	}

	public class MovementListener implements MouseMotionListener {
		public void mouseDragged(MouseEvent e) {
			// System.out.println("Trying: "+canDrag+" " +dragMode);
			Point2D.Double point = new Point2D.Double(e.getPoint().getX() / zoom - scrollx,
					e.getPoint().getY() / zoom - scrolly);
			if (mode != 1) {
				if (canDrag) {
					lastActiveTile.goTo(point.getX() - selectOffset[0], point.getY() - selectOffset[1]);
					hasChanges = true;
					repaint();
				}
			} else {
				if (canDrag && lastPoint != null) {
					switch (selectSide) {
					case 0:
						// increase or decrease height only
						lastActiveTile.resize(0, 0, 0, point.getY() - lastPoint.getY());
						break;
					case 1:
						// increase or decrease width and X
						lastActiveTile.resize(-point.getX() + lastPoint.getX(), 0, -point.getX() + lastPoint.getX(), 0);
						break;
					case 2:
						// increase or decrease height and Y
						lastActiveTile.resize(0, -point.getY() + lastPoint.getY(), 0, -point.getY() + lastPoint.getY());
						break;
					case 3:
						// increase or decrease width only
						lastActiveTile.resize(0, 0, point.getX() - lastPoint.getX(), 0);
						break;
					}
					// lastActiveTile.resize(-point.getX()+lastPoint.getX(),
					// -point.getY()+lastPoint.getY());
					// lastActiveTile.setRadius(lastActiveTile.getCenter().distance(point));
					hasChanges = true;
					repaint();
				}
			}
			lastPoint = point;
			requestFocusInWindow();
		}

		public void mouseMoved(MouseEvent e) {
			lastPoint = new Point2D.Double(e.getPoint().getX() * 2 - scrollx, e.getPoint().getY() * 2 - scrolly);
			requestFocusInWindow();
		}
	}
}
