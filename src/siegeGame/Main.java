package siegeGame;

import java.awt.Dimension;

import javax.swing.JFrame;
import java.awt.Toolkit;

/**
 * Class that contains the main method for the program and creates the frame
 * containing the component.
 * 
 * @author @henning
 * @version 8/7/20
 */

public class Main {
	
	public static Dimension screenSize;
	public static Dimension gameSize;
	// This variable marks the locations at which the game starts to scroll
	public static int scrollPos[] = {0,0,0,0};
	public static boolean debug = false;
	/**
	 * main method for the program which creates and configures the frame for the
	 * program
	 *
	 */
	public static void main(String[] args) {
		System.out.println("Test");
		JFrame frame = new JFrame();

		// new thing I'm testing
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// frame.setUndecorated(true);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		gameSize = new Dimension();
		Main.gameSize.width=Main.screenSize.width;
		Main.gameSize.height=Main.screenSize.height;
		Main.scrollPos[0] = gameSize.width/5;
		Main.scrollPos[1] = gameSize.width*2/3;
		Main.scrollPos[2] = gameSize.width/3;
		Main.scrollPos[3] = gameSize.width*2/5;


		frame.setTitle("Siege Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Screen canvas = new Screen();
		frame.add(canvas);

		// make the frame visible which will result in the paintComponent method being
		// invoked on the
		// component.
		frame.setVisible(true);


		long timeElapsed = 0; 
		long timeTook = 0;
		
		while (true) {
			//System.out.println("Time since last frame:" + (System.nanoTime() - timeElapsed) / 1000000);
			
			timeElapsed = System.nanoTime(); // starts counting here...
			
			canvas.nextFrame();
			
			timeTook = (System.nanoTime() - timeElapsed) / 1000000;
			if (timeTook  > 2) {
				System.out.println("Lag Spike: " + timeTook);
				if (timeTook > 21) {
					System.out.println("WARNING: LAG SPIKE WAS PRETTY HIGH");
				}
				// Currently rarely has any issues, and I don't see it above 5
			}
			//System.out.println("Time Took: " + timeTook);

			try {
				if (timeTook<33.3) {
					Thread.sleep((long) (33.3-timeTook));
				}
			} catch (InterruptedException e) {
				System.out.println("Error while sleeping\n");
				e.printStackTrace();
			}
		}
	}

}
