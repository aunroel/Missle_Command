import processing.core.PApplet;

public class MainSketch extends PApplet {

    private final static int MY_WIDTH = 800;
    private final static int MY_HEIGTH = 800;

    public static void main(String[] args) {
        PApplet.main("MainSketch");
    }

    public void settings() {
        size(MY_WIDTH, MY_HEIGTH);
    }

    public void setup() {

    }

    public void draw() {
        ellipse(MY_WIDTH/2, MY_HEIGTH/2, 400, 400);
    }
}
