import processing.core.PApplet;
import processing.core.PVector;

public class Missile {

    public final PApplet parent;
    public PVector position, velocity, gun_position;


    public Missile(PApplet p, PVector pos, PVector vel, PVector gun_pos) {
        parent = p;
        position = pos;
        velocity = vel;
        gun_position = gun_pos;
    }


    public void shoot() {
        position.add(velocity);
        parent.fill(parent.color(255, 0, 0));
        parent.ellipse(gun_position.x, gun_position.y, 20, 20);
        parent.noFill();
    }

}
