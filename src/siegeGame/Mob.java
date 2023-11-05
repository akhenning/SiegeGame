package siegeGame;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

public class Mob extends Interactable {
	public static Image basicdrone = Toolkit.getDefaultToolkit().getImage("assets/smalldrone.png"); // ID 0
	public static Image shootydrone = Toolkit.getDefaultToolkit().getImage("assets/shootydrone.png"); // ID 0

    protected double effx = 0;
    protected double effy = 0;

    private int startx;
    private int starty;
    private Tile[] checkpoints;
    private double xmovement = 0;
    private double ymovement = 0;
    private int orientation = 1;

    private int sub_id = 0;

    private double hoveroffset = 0;
    private double hoverdy = 0;
    private double hoverd2y = 0;
    private double hoverd2ycap = 0;

    private int ticks_per_cycle;
    private int tick_in_cycle = 0;

    private boolean loop = false;
    
    private int place_in_cycle = 1; // represents which checkpoint it is going to
    private int cycle_direction = 1;
    private int pause_at_end_of_cycle = 0;
    private int pause_at_checkpoint = 0;

    private int fire_rate = -1;
    private int fire_cooldown = 100;
    private int search_rate = 25;
    private int attack_animation_timer = -1;
    private enum FireState {
		COOLDOWN, REQUESTING_ANGLE, ANGLE_GRANTED, ATTACKING
	}
    FireState state = FireState.COOLDOWN;
    //private int request_distance_check = 0; // 0 is no request, 1 is requesting, 2 is request granted
    private int projectile_type = 0;

    private double aiming_angle = 0;


    // turn red when hit for a certain number of frames
    // most opaque at 10
    private int turn_red = 0;
    private int hp = 1;

    public Mob(int x, int y, int id, int[] endposx, int[] endposy) {
        super(x, y,-1,-1);
        //System.out.println("Making mob with inputs: "+x+", "+y+", "+id+", "+endposx+", "+endposy+".");
        if (id == -98) {
            // This is a projectile (I see why people don't like inheretence)
            this.id = id;
            return;
        }

        this.id = id;
        effx = x;
        effy = y;
        if (id >-99) {
            System.err.println("ERROR: MOBS HAVE ID <-99, BUT MOB IS CREATED WITH ID"+id);
            System.out.println("ERROR: MOBS HAVE ID <-99, BUT MOB IS CREATED WITH ID"+id);
        }
        //System.out.println(endposx.length);
        //System.out.println("Making drone with ID: "+id);

        startx=x;
        starty=y;
        checkpoints = new Tile[endposx.length+1];
        checkpoints[0] = new Tile(startx, starty, 20, 20, -1,-99);
        for (int i=1;i<checkpoints.length;i++) {
            checkpoints[i] = new Tile(endposx[i-1], endposy[i-1], 20, 20, -1,-99);
        }
        width = 188;
        height = 106;

        // hover rate of change (sine wave period)
        hoverd2y = .05;
        // hover range cap (sine wave magnitude, not in px)
        hoverd2ycap = .45;
        ticks_per_cycle = 0;
        sub_id = id;

        switch (this.id) {
            // Basic drone
            case -100:
                ticks_per_cycle = 250;
                loop = false;
                pause_at_checkpoint = 0;
                pause_at_end_of_cycle = 1;
                hp = 2;
                break;
            // looping drone
            case -101:
                ticks_per_cycle = 250;
                loop = true;
                pause_at_checkpoint = 1;
                pause_at_end_of_cycle = 0;
                this.sub_id = -100;
                hp = 2;
                break;
            // fast sentry drone
            case -102:
                ticks_per_cycle = 100;
                loop = false;
                pause_at_checkpoint = 0;
                pause_at_end_of_cycle = 1;
                this.sub_id = -100;
                hp = 2;
                break;
            // zoomy drone
            case -103:
                ticks_per_cycle = 50;
                loop = true;
                pause_at_checkpoint = 0;
                pause_at_end_of_cycle = 0;
                this.sub_id = -100;
                hp = 2;
                break;
            // super zoomy drone
            case -104:
                ticks_per_cycle = 10;
                loop = true;
                pause_at_checkpoint = 0;
                pause_at_end_of_cycle = 0;
                this.sub_id = -100;
                hp = 2;
                break;
            // basic shooty drone
            case -110:
                ticks_per_cycle = 200;
                loop = false;
                pause_at_checkpoint = 0;
                pause_at_end_of_cycle = 1;
                fire_rate = 50;
                projectile_type = 1;
                hp = 3;
                break;
        }
        clipType = -1;
        moveTowardsCheckpoint(1);
        System.out.println(checkpoints.length);
    }

    public void draw(Graphics2D g2) {
		draw(g2, Screen.scrollx, Screen.scrolly);
	}

	public void draw(Graphics2D g2, int scrollx, int scrolly) {
        
		switch (sub_id) {
            case -100:
                if (orientation == -1) {
                    g2.drawImage(basicdrone, scrollx + x, scrolly + y + (int)hoveroffset, width, height, null);
                } else {
                    g2.drawImage(basicdrone, scrollx + x + width, scrolly + y + (int)hoveroffset, -width, height, null);
                }
                break;
            case -110:
                if (orientation == -1) {
                    g2.drawImage(shootydrone, scrollx + x, scrolly + y + (int)hoveroffset, width, height, null);
                } else {
                    g2.drawImage(shootydrone, scrollx + x + width, scrolly + y + (int)hoveroffset, -width, height, null);
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
            for (Tile c:checkpoints) {
                c.draw(g2, scrollx, scrolly);
            }
			g2.setColor(Color.black);
		}
	}

    public void nextFrame() {
        nextFrameAndCheck();
    }

    public boolean nextFrameAndCheck() {
        //System.out.println(effx+", "+effy+", "+place_in_cycle+", "+tick_in_cycle+", "+xmovement);
        if (attack_animation_timer == -1) {
            tick_in_cycle += 1;

            // if not pausing, or ticks are not at the end of the cycle
            if (tick_in_cycle < ticks_per_cycle) {
                effx += xmovement;
                effy += ymovement;
            }
            // Track externally whether it is at end of cycle, and if it is, reverse movement.
            if (tick_in_cycle >= ticks_per_cycle + (pause_at_checkpoint * 40)) {// give extra 40 frames if pause at each checkpoint
                // proceed if either pause at end of cycle is 0, or it is 1 and it is greater than the number and it works
                boolean is_at_end_of_cycle = (place_in_cycle == checkpoints.length-1 && cycle_direction == 1) || (place_in_cycle == 0 && cycle_direction == -1);
                if (is_at_end_of_cycle && tick_in_cycle < ticks_per_cycle + (pause_at_end_of_cycle * 40)) { // or is in valid range
                } else {
                    effx = checkpoints[place_in_cycle].getX();
                    effy = checkpoints[place_in_cycle].getY();
                    if ((place_in_cycle == checkpoints.length-1 && cycle_direction == 1) || (place_in_cycle == 0 && cycle_direction == -1)) {
                        if (loop) {
                            //go to cp 1
                            if (cycle_direction == 1) {
                                place_in_cycle = 0;
                            } else {
                                place_in_cycle = checkpoints.length-1;
                            }
                            moveTowardsCheckpoint(0);
                        } else {
                            //reverse cycle and go backwards
                            cycle_direction *= -1;
                            place_in_cycle += cycle_direction;
                            moveTowardsCheckpoint(place_in_cycle);
                        }
                    } else {
                        place_in_cycle += cycle_direction;
                        if (place_in_cycle == -1) {
                            place_in_cycle = checkpoints.length-1;
                        }
                        moveTowardsCheckpoint(place_in_cycle);
                    }
                }
            }

            if (fire_rate != -1) {
                calculateAttack();
            }
        } else if (attack_animation_timer > -1) {
            attack_animation_timer -= 1;
        }

        calculateVisualEffects();
		
        x = (int)effx;
        y = (int)effy;

        // return if is going to do anything related to projectiles
        return (state == FireState.REQUESTING_ANGLE || state == FireState.ANGLE_GRANTED);
	}

    private void calculateAttack() {
        if (fire_cooldown > 0) {
            fire_cooldown -= 1;
        }
        if (fire_cooldown <= 0) {
            if (state == FireState.COOLDOWN) {
                state = FireState.REQUESTING_ANGLE;
            }
        }
    }

    public int[] getProjSpawnPoint() {
        int[] rtrn = new int[2];
        state = FireState.COOLDOWN;
        switch (sub_id) {
            case -100:
            case -110:
                rtrn[0] = x+50+(orientation*100);
                rtrn[1] = y+80;
                return rtrn;
		}
        rtrn[0] = x;
        rtrn[1] = y;
        return rtrn;
    }

    public int attemptingToShoot() {
        if (attack_animation_timer != -1) {
            if (attack_animation_timer == 7) {
                // ready to fire
                System.out.println("Attack animation is at fire frame: "+attack_animation_timer);
                return projectile_type;
            } else {
                // wait for frame 7
                return -1;
            }
        } else {
            // not aimed atm
            return 0;
        }
    }


    private void moveTowardsCheckpoint(int which) {
        tick_in_cycle = 0;
        xmovement = (checkpoints[which].getX()-effx)/ticks_per_cycle;
        ymovement = (checkpoints[which].getY()-effy)/ticks_per_cycle;
        if (x > checkpoints[which].getX()) {
            orientation = -1;
        } else {
            orientation = 1;
        }
    }

    private void calculateVisualEffects() {
        hoverdy += hoverd2y;
        hoveroffset += hoverdy;
        if (Math.abs(hoverdy)>=hoverd2ycap) {
            hoverd2y *= -1;
        }

        if (turn_red > 0) {
            turn_red -= 1;
        }
    }

	public void goTo(double x, double y) {
		this.x = (int) x;
		this.y = (int) y;
        startx = (int)x;
        starty = (int)y;
        checkpoints[0].goTo(x,y);
	}

    public void cleanup() {
        checkpoints = null;
    }

	public boolean isTangible() {
		return false;
	}

	public boolean hasInteraction() {
		return false; //not certain this is false
	}

    public boolean hasContactDamage() {
        return false;
    }

    public int[][] getContactPoints() {
        // needs scrollx and scrolly to be added before comparison is done
        int[][] rtrn = {{x,y},{x+width,y},{x,y+height},{x+width,y+height}};
        return rtrn;
    }

    // return 1 if effects Siege, 2 if changes text, 0 if should register as hitting something
	public int interact() {
        if (turn_red != 0) {
            return -1;
        }
        //System.out.println("Getting hit with HP "+hp+" and remaining iframes "+turn_red);
        turn_red = 10;
        hp -= 1;
        if (hp <= 0) {
            toRemove = true;
        }
		return 0;
	}

    public Tile[] fetchCheckpoints() {
        return checkpoints;
    }

    public void setAimingAngle(boolean success, double angle) {
        if (success) {
            aiming_angle = angle;
            state = FireState.ANGLE_GRANTED;
            fire_cooldown = fire_rate;
            attack_animation_timer = 15;
        } else {
            state = FireState.COOLDOWN;
            fire_cooldown = search_rate;
        }
        //System.out.println("Taking aim: "+success+", state "+state);
    }

    public double getAimingAngle() {
        return aiming_angle;
    }
    
	public String toString() {
        if (!should_be_saved) {
			return "";
		}
		switch (sub_id) {
		case -100:
			type = "Basic Drone";
			break;
		case -110:
			type = "Hostile Drone";
			break;
		}
        //System.out.println("Saving drone with ID: "+id);
        String cpts_str = "";
        for (int i=1;i<checkpoints.length;i++) {
            cpts_str += ",\t" + checkpoints[i].getX() + ",\t" + checkpoints[i].getY();
        }
		return "Mob,\t\t" + startx + ",\t" + starty + ",\t" + width + ",\t" + height + ",\t" + clipType + ",\t" + id
				+ ",\t\t" + type + cpts_str + "\n";
	}
}
