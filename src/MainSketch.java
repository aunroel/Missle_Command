import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;
import processing.core.PVector;

import java.util.ArrayList;

public class MainSketch extends PApplet {

    private PFont font;
    private PShape gun;

    private final static int MY_WIDTH = 800;
    private final static int MY_HEIGHT = 800;
    private final static int SURFACE_YPOS = new Double(MY_HEIGHT * 0.8).intValue();
    private final static int PARTICLE_FREQUENCY = 30;
    private final static int BUILDINGS = 6;
    private final static int BUILDING_SIZE = 50;
    private final float restitution = 1;
    private int n_particles = 100;
    private int n_missles = new Double(n_particles * 1.5f).intValue();

    private int player_score = 0;
    private ArrayList<Particle> particles;
    private ArrayList<Building> buildings;
    private ArrayList<Missile> missiles;
    private ContactResolver contactResolver;
    private ArrayList missile_contacts;
    private int draw_counter = 0;
    private boolean playing = true;


    public static void main(String[] args) {
        PApplet.main("MainSketch");
    }

    public void settings() {
        size(MY_WIDTH, MY_HEIGHT);
    }

    public void setup() {
        particles = new ArrayList<>();
        buildings = new ArrayList<>();
        missiles = new ArrayList<>();
        contactResolver = new ContactResolver();
        missile_contacts = new ArrayList<>();
        int building_xstep = 25;
        for (int i = 0; i < BUILDINGS; i++) {
            buildings.add(new Building(this,
                    building_xstep,
                    SURFACE_YPOS - BUILDING_SIZE,
                    BUILDING_SIZE,
                    color(66, 220, 244)
            ));
            if (i == 2) building_xstep += 200;
            building_xstep += 100;
        }
        font = createFont("Arial", 20, true);
        cursor(CROSS);

        gun = createShape(GROUP);
        PShape base = createShape(RECT,MY_WIDTH/2 - 50, SURFACE_YPOS - 30, 100, 30, 4);
        base.setFill(color(21, 223, 12));
        base.setStroke(false);
        PShape tower = createShape(ARC, MY_WIDTH/2, SURFACE_YPOS - 30, 50, 50, PI, 2*PI);
        tower.setFill(color(21, 223, 12));
        tower.setStroke(false);
        PShape silo = createShape(ELLIPSE, MY_WIDTH/2, SURFACE_YPOS - 30, 20, 20);
        silo.setFill(color(255, 255, 255));
        silo.setStroke(false);

        gun.addChild(base);
        gun.addChild(tower);
        gun.addChild(silo);
    }

    public void draw() {
        draw_counter++;
        background(240,255,255);
        stroke(204, 102, 0);
        strokeWeight(3);
        line(0, SURFACE_YPOS, MY_WIDTH, SURFACE_YPOS);
        noStroke();


        if (draw_counter % PARTICLE_FREQUENCY == 0)
            createParticle();

        buildings.forEach(Building::displayCity);

        shape(gun);

        displayText();

//        if (!missiles.isEmpty()) {
//            missiles.forEach(Missile::shoot);
//            for (int i = 0; i < particles.size(); i++) {
//                for (int j = 0; j < missiles.size(); j++) {
//                    Contact contact = detectCollision(particles.get(i), missiles.get(j));
//                    if (contact != null) missile_contacts.add(contact);
//                }
//            }
//            contactResolver.resolveContacts(missile_contacts);
//        processParticle();
//        missile_contacts.clear();
        processObjectBehaviour();
//        }

    }

    private void createParticle() {
        if (particles.size() < n_particles) {
            particles.add(new Particle(this,
                    new PVector((int) random(0, MY_WIDTH), 0),
                    new PVector(random(-.5f, .5f), random(-.5f, .5f)),
                    random(0.001f, 0.005f)
            ));
        }
    }

    private void processObjectBehaviour() {
        for (int i = 0; i < particles.size(); i++) {
            for (int j = 0; j < missiles.size(); j++) {
                if (missiles.get(j).explode) {
                    Contact contact = detectCollision(particles.get(i), missiles.get(j));
                    if (contact != null) {
                        missile_contacts.add(contact);
                    }
                }
            }
            contactResolver.resolveContacts(missile_contacts);
            particles.get(i).integrate();
//            missiles.forEach(Missile::shoot);
            missile_contacts.clear();
            particles.get(i).setSize((int) (particles.get(i).getMass() * 0.01f) + 15);
            particles.get(i).displayParticle();
//            missiles.forEach(Missile::displayMissile);
            if (particles.get(i).cease_existance) {
                player_score += 100;
                particles.remove(i);
                n_particles--;
            }
        }
        for (int i = 0; i < missiles.size(); i++) {
            if (missiles.get(i).vanish) {
                missiles.remove(i);
                continue;
            }
            missiles.get(i).shoot();
            missiles.get(i).displayMissile();
        }
    }

    private void displayText() {
        textFont(font);
        text("Meteorites left: " + particles.size(), MY_WIDTH * 0.7f, SURFACE_YPOS + 50);
        text("Player score: " + player_score, MY_WIDTH * 0.7f, SURFACE_YPOS + 75);
        text("Missles left: " + (n_missles - missiles.size()), MY_WIDTH * 0.05f, SURFACE_YPOS + 50);
        text("Cities left: " + buildings.size(), MY_WIDTH * 0.05f, SURFACE_YPOS + 75);
    }

     public void mousePressed() {
         if (playing)
            launchMissile();
         else
             System.out.println("huehuehue");
    }

    private void launchMissile() {
        if (mouseY > SURFACE_YPOS - (BUILDING_SIZE * 2)) {
            return;
        }
        if (missiles.size() == n_missles) return;

        PVector origin = new PVector(MY_WIDTH/2, SURFACE_YPOS - 30);
        PVector destination = new PVector(mouseX, mouseY);
        PVector acceleration = destination.sub(origin);
        acceleration.normalize();
        acceleration.mult(6);
        missiles.add(new Missile(this, origin, acceleration, origin, new PVector(mouseX, mouseY)));
    }

    private Contact detectCollision(Particle p, Missile m) {
        PVector distance = p.position.copy() ;
        distance.sub(m.position);

        // Collision?
        if (distance.mag() < m.expl_size) {
            distance.normalize() ;
            return new Contact(this, p, m, restitution, distance) ;
        }
        return null ;
    }

}
