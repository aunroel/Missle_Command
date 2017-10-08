import processing.core.PApplet;
import processing.core.PVector;

public class Particle {

    private static final float DAMPING = 0.995f;
    private static final PVector GRAVITY = new PVector(0f, 4.3f);

    public final PApplet parent;
    public final PVector position, velocity;
    public float invMass;
    public boolean cease_existance = false;

    public Particle(PApplet p, PVector pos, PVector vel, float invM) {
        parent = p;
        position = pos;
        velocity = vel;
        invMass = invM;
    }

    public float getMass() { return 1/invMass; }

    public void integrate() {
        if (invMass <= 0f) return;

        position.add(velocity);
        PVector acceleration = GRAVITY.copy();
        acceleration.mult(invMass);

        velocity.add(acceleration);
        velocity.mult(DAMPING);
        if (position.x < 0 || position.x > parent.width) {
            cease_existance = !cease_existance;
        }

    }
}
