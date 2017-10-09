package mypackage;

import processing.core.PApplet;

public class City {

    PApplet parent;
    int color;
    float xpos;
    float ypos;
    final int SIZE;
    private boolean destroyed;


    City(PApplet p, float x, float y, int size, int color) {
        parent = p;
        xpos = x;
        ypos = y;
        SIZE = size;
        this.color = color;
        destroyed = false;
    }

    void displayCity() {
        if (!destroyed) {
            parent.fill(color);
            parent.rect(xpos, ypos, SIZE, SIZE, 4);
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
