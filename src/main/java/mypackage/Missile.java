package mypackage;

import processing.core.PApplet;
import processing.core.PVector;

public class Missile {

    public final PApplet parent;
    public PVector position, velocity, gun_position, destination, impulse;
    public final float INV_MASS = 0.01f;
    public final int DEFAULT_SIZE = 10;
    public final int EXPLOSION_COEF = 5;
    public int expl_size = DEFAULT_SIZE;

    // variable controls exploding initialisation and processing
    public boolean explode = false;
    // Variable indicates when the explosion reached its' maximum size and is due to be destroyed
    public boolean vanish = false;

    public Missile(PApplet p, PVector pos, PVector vel, PVector gun_pos, PVector destination) {
        parent = p;
        position = pos;
        velocity = vel;
        gun_position = gun_pos;
        this.destination = destination;
    }


    public void shoot() {
        if (expand()) {
            impulse = velocity.copy();
        } else
            position.add(velocity);
    }

    public void displayMissile() {
        if (explode)
            parent.fill(parent.color(255,140,0));
        else
            parent.fill(parent.color(128,128,0));
        parent.ellipse(gun_position.x, gun_position.y, expl_size, expl_size);
        parent.noFill();
    }

    //
    private boolean expand() {
        if (expl_size == EXPLOSION_COEF * DEFAULT_SIZE) {
            vanish = true;
            return true;
        }

        if (position.x > destination.x - DEFAULT_SIZE/2 && position.x < destination.x + DEFAULT_SIZE/2) {
            if (position.y > destination.y - DEFAULT_SIZE/2 && position.y < destination.y + DEFAULT_SIZE/2) {
                explode = !explode;
                expl_size += DEFAULT_SIZE/2;
                return true;
            }
        }
        return false;
    }

}
