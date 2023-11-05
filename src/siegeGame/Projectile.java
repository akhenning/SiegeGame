package siegeGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Projectile extends Mob {

    private int lifetime = 200;
    private double angle = 0;

    private double dx = 0;
    private double dy = 0;
    private int proj_id = 0;

    // projectiles will have a different ID system, I guess, since they are temporary. -98 will be them 
    public Projectile(int x, int y, int id, double angle) {
        super(x, y, -98,null,null);
        this.angle = angle;
        effx = x;
        effy = y;
        double velocity = 8;

        proj_id = id;

        // Worth noting that vertical is flipped for some reason
        //System.out.println("Angle: "+angle+" gives dx "+Math.cos(angle)+" and dy "+Math.sin(angle));
        dx = velocity * Math.cos(angle);
        dy = velocity * Math.sin(angle);
    }


    public void nextFrame() {
        nextFrameAndCheck();
    }

    public boolean nextFrameAndCheck() {
        lifetime -= 1;
        effx += dx;
        effy += dy;

        x = (int)effx;
        y = (int)effy;

        if (lifetime <= 0) {
            toRemove = true;
        }

        return false;
    }

    public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
        /*
    AffineTransform trans = new AffineTransform();
    // trans rights
    if (direction == 1) {
        trans.translate((double) (x + 72), (double) (y));
        if (animationFrame < 8) {
            trans.rotate(Math.toRadians(Math.abs(4 - animationFrame) - 15));
        } else {
            trans.rotate(Math.toRadians(Math.abs(12 - animationFrame) - 15));
        }
        trans.translate(-130, 15 - 223); // 144
        g2.drawImage(skid[0], trans, null);
         */
		g2.setColor(Color.red);
        g2.drawLine(scrollx + x, scrolly + y, scrollx + x + (int)(15*dx), scrolly + y + (int)(15*dy));
        g2.fillOval(scrollx + x - 20, scrolly + y - 20, 40, 40);
		g2.setColor(Color.black);
    }

    public boolean hasContactDamage() {
        return true;
    }

    public int[][] getContactPoints() {
        // needs scrollx and scrolly to be added before comparison is done
        int[][] rtrn = {{x,y},{x + (int)(15*dx), y + (int)(15*dy)}};
        return rtrn;
    }

    public void cleanup() {
    }

    
	public String toString() {
		return "This Message Should Not Appear (Projectile savefile formatting method called)\n";
	}
}
