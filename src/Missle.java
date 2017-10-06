import processing.core.PApplet;
import processing.core.PVector;

public class Missle {

    private static final float DAMPING = 0.995f;
    private static final PVector GRAVITY = new PVector(0f, 3.3f);

    public final PApplet parent;
    public float invMass;
    public final PVector position, velocity;

    public Missle(PApplet p, PVector pos, PVector vel, float invM) {
        parent = p;
        position = pos;
        velocity = vel;
        invMass = invM;
    }

    public void integrate() {
        if (invMass <= 0f) return;

        position.add(velocity);
        PVector acceleration = GRAVITY.copy();
        acceleration.mult(invMass);

        velocity.add(acceleration);
//        velocity.mult(DAMPING);
        // Apply an impulse to bounce off the edge of the screen
//        if ((position.x < 0) || (position.x > parent.width)) velocity.x = -velocity.x ;
//        if ((position.y < 0) || (position.y > parent.height)) velocity.y = -velocity.y ;
    }
}
