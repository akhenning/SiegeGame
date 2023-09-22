package siegeGame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

public class Mob extends Interactable {
	public static Image basicdrone = Toolkit.getDefaultToolkit().getImage("assets/smalldrone.png"); // ID 0

    private double effx = 0;
    private double effy = 0;

    private int startx;
    private int starty;
    private int endx;
    private int endy;
    private double xmovement = 0;
    private double ymovement = 0;
    private double orientation = 1;

    private double hoveroffset = 0;
    private double hoverdy = 0;
    private double hoverd2y = 0;
    private double hoverd2ycap = 0;

    private int ticks_per_cycle;
    private int tick_in_cycle = 0;
    private boolean pause_at_end_of_cycle;

    // turn red when hit for a certain number of frames
    // most opaque at 10
    private int turn_red = 0;


    public Mob(int x, int y, int id) {
        super(x, y,-1,-1);
        effx = x;
        effy = y;
        this.id = 0;
        switch (id) {
            case 0:
                // placeholder test drone that does nothing but move
                ticks_per_cycle = 300;
                width = 188;
                height = 106;
                startx=x;
                starty=y;
                endx = startx + 1000;
                endy = starty + 100;
                xmovement = (endx-startx)/ticks_per_cycle;
                ymovement = (endy-starty)/ticks_per_cycle;
                // specifying endpoint is gonna be a pain in the ass, probably gonna need to add new features to BuilderScreen :/

                // hover rate of change (sine wave period)
                hoverd2y = .04;
                // hover range cap (sine wave magnitude, not in px)
                hoverd2ycap = .4;

                pause_at_end_of_cycle = true;
                break;
        }
        clipType = -1;
    }

    public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
		switch (id) {
            case 0:
                if (orientation == -1) {
                    g2.drawImage(basicdrone, scrollx + x, scrolly + y + (int)hoveroffset, width, height, null);
                } else {
                    g2.drawImage(basicdrone, scrollx + x + width, scrolly + y + (int)hoveroffset, -width, height, null);
                }
                break;
		}

        if (turn_red > 0) {
            Color fade_red = new Color(200, 0, 0, turn_red*25);
			g2.setColor(fade_red);

			g2.fillRect(scrollx + x, scrolly + y, width, height);
			g2.setColor(Color.black);
        }

		if (Main.debug) {
			g2.setColor(Color.yellow);
			drawCollision(g2, scrollx, scrolly);
			g2.setColor(Color.black);
		}
	}
    
	public void nextFrame() {
        tick_in_cycle += 1;

        if (!pause_at_end_of_cycle || tick_in_cycle < ticks_per_cycle - 40) {
            effx += xmovement;
            effy += ymovement;
        }
        // Track externally whether it is at end of cycle, and if it is, reverse movement.
        if (tick_in_cycle >= ticks_per_cycle) {
            tick_in_cycle=0;
            xmovement *= -1;
            ymovement *= -1;
            orientation *= -1;
        }
        
        hoverdy += hoverd2y;
        hoveroffset += hoverdy;
        if (Math.abs(hoverdy)>=hoverd2ycap) {
            hoverd2y *= -1;
        }

        if (turn_red > 0) {
            turn_red -= 1;
        }
		
        x = (int)effx;
        y = (int)effy;
	}

	public boolean isTangible() {
		return false;
	}

	public boolean hasInteraction() {
		return false; //not certain this is false
	}

    // return 1 if effects Siege, 2 if changes text, 0 if should register as hitting something
	public int interact() {
        if (turn_red != 0) {
            return -1;
        }
        turn_red = 10;
		return -1;
	}
    
	public String toString() {
		switch (id) {
		case 0:
			type = "Basic Drone";
			break;
		}
		return "Mob,\t\t" + startx + ",\t" + starty + ",\t" + width + ",\t" + height + ",\t" + clipType + ",\t" + id
				+ ",\t\t" + type + ",\t" + endx + ",\t" + endy + "\n";
	}
}
