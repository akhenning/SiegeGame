package stageBuilder;

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

public class BuilderMain {

	public static Dimension screenSize;

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

		frame.setTitle("Siege Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BuilderScreen canvas = new BuilderScreen();
		frame.add(canvas);

		// make the frame visible which will result in the paintComponent method being
		// invoked on the
		// component.
		frame.setVisible(true);

		while (true) {

			canvas.nextFrame();

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
