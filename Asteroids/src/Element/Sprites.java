package Element;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import Main.GameScreen;
import Other.Pointt;

public class Sprites extends Element {

    public static int EXPLOSION_DISPLAY_TIME = 100;
    private static int SIZE = 150;
    private static Image image;
    private long startTime;

    /**
     * Create Sprite representing the explosion
     * position: position where the explosion occurs.
     * startTime: starting time of the explosion.
     */
    public Sprites(Pointt position, long startTime) {
        super(null, position, 0, 0, 0);
        graphics.graphicsSize = SIZE;
        this.startTime = startTime;

        if (image == null) {
            try {
                image = ImageIO.read(new File(GameScreen.explosion));
                image = image.getScaledInstance((int) graphics.graphicsSize, (int) graphics.graphicsSize, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load image");
                System.exit(0);
            }
        }
    }

    @Override
    public Image getRep() {
        return image;
    }

    @Override
    public void plot(Graphics2D a, AffineTransform transform) {
        a.translate(physics.position.getX(), physics.position.getY());
        super.plot(a, transform);
    }

    /**
     *
     * return the starting time of the explosion.
     */
    public long getStartTime() {
        return this.startTime;
    }
}
