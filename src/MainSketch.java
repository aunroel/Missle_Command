import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.util.ArrayList;

public class MainSketch extends PApplet {

    PFont font;

    private final static int MY_WIDTH = 800;
    private final static int MY_HEIGTH = 800;
    private final int NO_MISSLES = 100;
    private final static int SURFACE_YPOS = new Double(MY_HEIGTH * 0.8).intValue();
    private final static int MISSLE_FREQUENCY = 30;
    private final static int BUILDINGS = 6;

    private ArrayList<Missle> missles;
    private ArrayList<Building> buildings;
    private int draw_counter = 0;


    public static void main(String[] args) {
        PApplet.main("MainSketch");
    }

    public void settings() {
        size(MY_WIDTH, MY_HEIGTH);
    }

    public void setup() {
        background(255);
        missles = new ArrayList<>();
        buildings = new ArrayList<>();
        int building_xstep = 25;
        for (int i = 0; i < BUILDINGS; i++) {
            buildings.add(new Building(this,
                    building_xstep,
                    SURFACE_YPOS - 50,
                    50,
                    color(66, 220, 244)
            ));
            if (i == 2) building_xstep += 200;
            building_xstep += 100;
        }
        font = createFont("Arial", 36, true);
        cursor(CROSS);
    }

    public void draw() {
        draw_counter++;
        background(255);
        text("Missles created: " + missles.size(), MY_WIDTH * 0.8f, SURFACE_YPOS + 50);
        stroke(204, 102, 0);
        strokeWeight(3);
        line(0, SURFACE_YPOS, MY_WIDTH, SURFACE_YPOS);
        noStroke();
        fill(123,32,65);
        if (draw_counter % MISSLE_FREQUENCY == 0)
           createMissle();

        for (int i = 0; i < missles.size() - 1; i++) {
            missles.get(i).integrate();
            PVector position = missles.get(i).position;
            ellipse(position.x, position.y, 10, 10);
        }

        buildings.stream().forEach(x -> x.displayCity());

        stroke(175);
        line(width/2,0,width/2,height);
        noStroke();
        rect(MY_WIDTH/2 - 50, SURFACE_YPOS - 30, 100, 30);
        arc(MY_WIDTH/2, SURFACE_YPOS - 30, 50, 50, PI, 2*PI);
        drawGun();
        if (mousePressed) {

        }

    }

    private void createMissle() {
        if (missles.size() < NO_MISSLES) {
            missles.add(new Missle(this,
                    new PVector((int) random(0, MY_WIDTH), 0),
                    new PVector(random(-.5f, .5f), random(-.5f, .5f)),
                    random(0.001f, 0.005f)
            ));
        }
    }


    private void drawGun() {
        pushMatrix();
        translate(MY_WIDTH/2, SURFACE_YPOS - 25);
        rect(MY_WIDTH/2 - 3, SURFACE_YPOS - 75, 6, 50);
        popMatrix();
    }
}
