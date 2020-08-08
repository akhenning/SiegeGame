package siegeGame;

import javax.swing.JFrame;

/**
 * Class that contains the main method for the program and creates the frame
 * containing the component.
 * 
 * @author @henning
 * @version 8/7/20
 */

public class Main {
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

		frame.setTitle("Siege Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Screen canvas = new Screen();
		frame.add(canvas);

		// make the frame visible which will result in the paintComponent method being
		// invoked on the
		// component.
		frame.setVisible(true);

		while (true) {
			canvas.nextFrame();
			try {
				Thread.sleep((long) 33.3);
			} catch (InterruptedException e) {
				System.out.println("Error while sleeping\n");
				e.printStackTrace();
			}
		}
	}

}
