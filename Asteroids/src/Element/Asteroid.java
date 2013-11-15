package Element;

import Main.GameScreen;
import Main.Map;
import Other.Pointt;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Asteroid extends Element {

    private static final double[] RADIUS = {-99,20,30,40};//SIZE/2;
    private static Image image;
    private Image rep;
    public static double SPEED_DEFAULT = 0.1;
    public static int STARTING_SIZE = 3;
    public static int SAFE_DISTANCE = 3;
    private int size;

    /**
     * Create a new instance of asteroid 
     * see super class for other variable.
     */
    public Asteroid(Map map, int size, Pointt position, double movingAngle, double speed) {
        super(map, position, movingAngle, speed, 0);
        this.size = size;
        physics.radius = RADIUS[size];
        graphics.graphicsSize = (int)(physics.radius * GRAPHICS_REAL_SCALE);//SIZE * size;
        if (image == null) {
            try {
                image = ImageIO.read(new File(GameScreen.rock));
                image = image.getScaledInstance(graphics.graphicsSize, graphics.graphicsSize, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load image. Please check the directory where the images are stored.");
                System.exit(0);
            }
        }
        rep = image.getScaledInstance(graphics.graphicsSize, graphics.graphicsSize, Image.SCALE_SMOOTH);
        this.physics.health = 0;
    }

    private static final int[][] SPLIT = {{-1,-1},{-1,1},{1,1},{1,-1}};
    
    /**
     * the method should remove the current asteroid and add some new asteroids to the container with smaller size. If the asteroid size reaches 0, the method simply remove the asteroid from the container
     */
    public void split(ArrayList<Asteroid> container) {
        if (size == 1) return;
        for (int i = 0; i < 3; i++) {
            int newSize = size - 1;
            container.add(new Asteroid(map,newSize, 
                new Pointt(physics.position.getX() + SPLIT[i][0] * (newSize) * RADIUS[newSize] * Math.sqrt(2), 
                    physics.position.getY() + SPLIT[i][1] * (newSize) * RADIUS[newSize] * Math.sqrt(2))
                , Math.random() * 2 * Math.PI, Math.random() * SPEED_DEFAULT));
        }
    }

    /**
     *
     * @return an image representing the asteroid on the screen.
     */
    @Override
    public Image getRep() {
        return rep;
    }

    /**
     *
     * @return the size of the asteroid: ranging from 1 to 3.
     */
    public int getSize() {
        return this.size;
    }
}
