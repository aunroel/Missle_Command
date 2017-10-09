package mypackage;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Particle {

    private static final float DAMPING = 0.995f;

    final PApplet parent;
    final PVector position, velocity;
    ArrayList<City> cities;

    float invMass;
    boolean cease_existence = false;
    int size = 0;
    final int ground;

    Particle(PApplet p, PVector pos, PVector vel, float invM, int ground) {
        parent = p;
        position = pos;
        velocity = vel;
        invMass = invM;
        this.ground = ground;
    }

    float getMass() { return 1/invMass; }

    void integrate(PVector gravity) {
        if (invMass <= 0f) return;

        position.add(velocity);
        PVector acceleration = gravity.copy();
        acceleration.mult(invMass);

        velocity.add(acceleration);
        velocity.mult(DAMPING);
        if (position.x < 0 || position.x > parent.width)
            cease_existence = !cease_existence;
        if (position.y < 0 - 50 || position.y > ground - size * 0.5)
            cease_existence = !cease_existence;
        detectCityCollision();
    }

    void displayParticle() {
        parent.fill(parent.color(123,32,65));
        parent.ellipse(position.x, position.y, size, size);
        parent.noFill();

    }

    void detectCityCollision() {
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            if (!city.isDestroyed()) {
                if ((position.x >= city.xpos - size/2 && position.x <= city.xpos + city.SIZE + size/2)) {
                    if (position.y + size/2 >= city.ypos && position.y+ size/2 <= city.ypos + city.SIZE) {
                        cease_existence = !cease_existence;
                        city.setDestroyed(true);
                    }
                }
            }
        }
    }

    void setSize(int size) {
        this.size = size;
    }

    void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }
}
