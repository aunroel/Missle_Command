import processing.core.PApplet;

public class Building{

    PApplet parent;
    int color;
    float xpos;
    float ypos;
    final int SIZE;

    Building(PApplet p, float x, float y, int size, int color) {
        parent = p;
        xpos = x;
        ypos = y;
        SIZE = size;
        this.color = color;
    }

    void displayCity() {
        parent.fill(color);
        parent.rect(xpos, ypos, SIZE, SIZE);
    }

}
