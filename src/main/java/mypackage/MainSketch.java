package mypackage;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;
import processing.core.PVector;



import java.util.ArrayList;

public class MainSketch extends PApplet {

    private final static int MY_WIDTH = 800;
    private final static int MY_HEIGHT = 800;
    private final static int SURFACE_YPOS = new Double(MY_HEIGHT * 0.8).intValue();
    private final static int CITIES_AMOUNT = 6;
    private final static int CITY_SIZE = 50;
    private final static int STARTING_AMOUNT_PARTICLES = 10;
    private static final int PARTICLE_SCORE = 100;
    private static final int MISSILE_SCORE = 25;
    private static final int CITY_SCORE = 250;
    private final float restitution = 1;

    private ArrayList<Particle> particles;
    private ArrayList<City> existing_cities;
    private ArrayList<City> destroyed_cities;
    private ArrayList<Missile> missiles;
    private ContactResolver contactResolver;
    private ArrayList missile_contacts;
    private PFont font;
    private PShape gun;
    private PVector gravity;

    private int draw_counter;
    private int player_score;
    private int wave_particles_amount;
    private int wave_particles_counter;
    private int n_missiles;
    private int city_rebuild_price;
    private int particle_frequency;
    private int wave_counter;
    private int bonus_score;

    private boolean playing = true;
    private boolean end_game = false;
    private boolean rebuilt_city = false;

    public static void main(String[] args) {
        PApplet.main("mypackage.MainSketch");
    }

    public void settings() {
        size(MY_WIDTH, MY_HEIGHT);
    }

    public void setup() {
        newGame();
    }

    public void draw() {
        draw_counter++;
        background(240,255,255);
        stroke(204, 102, 0);
        strokeWeight(3);
        line(0, SURFACE_YPOS, MY_WIDTH, SURFACE_YPOS);
        noStroke();

        if (draw_counter % particle_frequency == 0)
            createParticle();

        existing_cities.forEach(City::displayCity);
        for (int i = 0; i < existing_cities.size(); i++) {
            if (existing_cities.get(i).isDestroyed()) {
                destroyed_cities.add(existing_cities.get(i));
                existing_cities.remove(existing_cities.get(i));
            }
        }

        shape(gun);
        processObjectBehaviour();
    }

    /**
     * Creates a particle
     */
    private void createParticle() {
        if (particles.size() < wave_particles_counter) {
            particles.add(new Particle(this,
                    new PVector((int) random(0, MY_WIDTH), 0),
                    new PVector(random(-.5f, .5f), random(-.5f, .5f)),
                    random(0.001f, 0.005f),
                    SURFACE_YPOS
            ));
        }
    }

    /**
     * Responsible for missile/particle interaction, as well as missile and particle rendering.
     */
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
            particles.get(i).setCities(existing_cities);
            particles.get(i).integrate(gravity);
            missile_contacts.clear();
            particles.get(i).setSize((int) (particles.get(i).getMass() * 0.01f) + 15);
            particles.get(i).displayParticle();
            if (particles.get(i).cease_existence) {
                player_score += PARTICLE_SCORE;
                particles.remove(i);
                wave_particles_counter--;

            }
        }
        for (int i = 0; i < missiles.size(); i++) {
            if (missiles.get(i).vanish) {
                missiles.remove(i);
                n_missiles--;
                continue;
            }
            missiles.get(i).shoot();
            missiles.get(i).displayMissile();
        }

        displayText();
        checkEndWave();
        checkEndGame();
    }

    /**
     * Displays game information below the "surface"
     */
    private void displayText() {
        fill(0);
        textFont(font);
        text("Meteorites left: " + (wave_particles_amount - (wave_particles_amount - wave_particles_counter)),
                MY_WIDTH * 0.7f, SURFACE_YPOS + 50);
        text("Player score: " + player_score, MY_WIDTH * 0.7f, SURFACE_YPOS + 75);
        text("Missiles left: " + (n_missiles - missiles.size()), MY_WIDTH * 0.05f, SURFACE_YPOS + 50);
        text("Cities left: " + (CITIES_AMOUNT - destroyed_cities.size()), MY_WIDTH * 0.05f, SURFACE_YPOS + 75);
        text("Wave " + wave_counter, MY_WIDTH * 0.7f, SURFACE_YPOS + 100);
        text("City rebuild price: " + city_rebuild_price, MY_WIDTH * 0.05f, SURFACE_YPOS + 100);

    }

    public void mousePressed() {
         if (playing)
            launchMissile();

    }

    /**
     * Launches a missile towards clicked region
     */
    private void launchMissile() {
        if (mouseY > SURFACE_YPOS - (CITY_SIZE * 2)) {
            return;
        }
        if (missiles.size() == n_missiles) return;

        PVector origin = new PVector(MY_WIDTH/2, SURFACE_YPOS - 30);
        PVector destination = new PVector(mouseX, mouseY);
        PVector acceleration = destination.sub(origin);
        acceleration.normalize();
        acceleration.mult(6);
        missiles.add(new Missile(this, origin, acceleration, origin, new PVector(mouseX, mouseY)));
    }

    /**
     * Check end of wave conditions - all particles destroyed/at least one city stands?
     */
    private void checkEndWave() {
        if (destroyed_cities.size() < CITIES_AMOUNT && wave_particles_counter == 0) {
            playing = false;
            text("Wave " + wave_counter + " finished", MY_WIDTH/3f, MY_HEIGHT/2.5f - 150);
            bonus_score = n_missiles * MISSILE_SCORE + existing_cities.size() * CITY_SCORE;
            text("Bonus score: " + bonus_score, MY_WIDTH/3f, MY_HEIGHT/2.5f - 100);
            text("Press \"R\" to rebuild city (if applicable)", MY_WIDTH/3f, MY_HEIGHT/2.5f - 50);
            text("Press \"Enter\" to start next wave", MY_WIDTH/3f, MY_HEIGHT/2.5f);
        }
    }

    /**
     * Displays end of game information
     */
    private void checkEndGame() {
        if (destroyed_cities.size() == CITIES_AMOUNT) {
            end_game = true;
            noLoop();
            textFont(font, 24);
            text("Sadly, the last city has fallen.", MY_WIDTH/3f, MY_HEIGHT/2.5f);
            text("Your final score is: " + player_score, MY_WIDTH/3f, MY_HEIGHT/2.3f);
            text("Press \"Enter\" to start a new game or \"Q\" to exit. ", MY_WIDTH /6f, MY_HEIGHT/2f);
        }
    }

    public void keyPressed() {
        if(end_game) {
            playing = false;
            if (key == 'Q' || key == 'q') {
                System.exit(0);
            }
            if (key == keyCode && keyCode == ENTER) {
                newGame();
                loop();
            }
        } else {
            if(!playing) {
                if (key == keyCode && keyCode == ENTER) {
                    generateNextWave();
                }
                if (key == 'R' || key == 'r') {
                    if (destroyed_cities.size() > 0 && player_score+bonus_score >= city_rebuild_price) {
                        player_score += bonus_score;
                        rebuilt_city = true;
                        player_score -= city_rebuild_price;
                        city_rebuild_price += 2500;
                        City c = destroyed_cities.get(destroyed_cities.size() - 1);
                        destroyed_cities.remove(c);
                        c.setDestroyed(false);
                        existing_cities.add(c);
                    }
                }
            }
        }
    }

    /**
     * Generates new wave with respect to the characteristics of a previous wave
     */
    private void generateNextWave() {
        if (!rebuilt_city)
            player_score += bonus_score;
        wave_particles_amount += 5;
        wave_particles_counter = wave_particles_amount;
        wave_counter++;
        particle_frequency = (particle_frequency > 30) ? particle_frequency - 10 : particle_frequency;
        n_missiles = new Double(wave_particles_counter * 1.5f).intValue();
        particles = new ArrayList<>();
        missiles = new ArrayList<>();
        gravity = (gravity.y < 6.5f) ? new PVector(0f, gravity.y + 0.3f) : gravity;
        playing = true;
        rebuilt_city = false;
    }

    /**
     * Resets all the game variables to initial state
     */
    private void newGame() {
        draw_counter = 0;
        player_score = 0;
        wave_particles_amount = STARTING_AMOUNT_PARTICLES;
        wave_particles_counter = wave_particles_amount;
        city_rebuild_price = 5000;
        particle_frequency = 90;
        wave_counter = 1;
        n_missiles = new Double(wave_particles_counter * 1.5f).intValue();
        particles = new ArrayList<>();
        existing_cities = new ArrayList<>();
        destroyed_cities = new ArrayList<>();
        missiles = new ArrayList<>();
        gravity = new PVector(0f, 1.3f);
        contactResolver = new ContactResolver();
        missile_contacts = new ArrayList<>();
        int building_xstep = 25;
        for (int i = 0; i < CITIES_AMOUNT; i++) {
            existing_cities.add(new City(this,
                    building_xstep,
                    SURFACE_YPOS - CITY_SIZE,
                    CITY_SIZE,
                    color(153,50,204)
            ));
            if (i == 2) building_xstep += 200;
            building_xstep += 100;
        }
        font = createFont("Arial", 20, true);
        cursor(CROSS);
        createGun();

        end_game = false;
        playing = true;
    }

    /**
     * Draws a missile launcher
     */
    private void createGun() {
        gun = createShape(GROUP);
        PShape base = createShape(RECT,MY_WIDTH/2 - 50, SURFACE_YPOS - 30, 100, 30, 4);
        base.setFill(color(244,164,96));
        base.setStroke(false);
        PShape tower = createShape(ARC, MY_WIDTH/2, SURFACE_YPOS - 30, 50, 50, PI, 2*PI);
        tower.setFill(color(244,164,96));
        tower.setStroke(false);
        PShape silo = createShape(ELLIPSE, MY_WIDTH/2, SURFACE_YPOS - 30, 20, 20);
        silo.setFill(color(255, 255, 255));
        silo.setStroke(false);

        gun.addChild(base);
        gun.addChild(tower);
        gun.addChild(silo);
    }

    private Contact detectCollision(Particle p, Missile m) {
        PVector distance = p.position.copy() ;
        distance.sub(m.position);

        // Collision?
        if (distance.mag() < m.expl_size - m.DEFAULT_SIZE) {
            distance.normalize() ;
            return new Contact(this, p, m, restitution, distance) ;
        }
        return null ;
    }

}
