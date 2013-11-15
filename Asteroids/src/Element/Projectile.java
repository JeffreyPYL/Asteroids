package Element;

import Main.GameScreen;
import Main.Map;
import Other.Pointt;

import java.awt.Image;
import java.io.*;

import javax.imageio.ImageIO;

public class Projectile extends Element {

    private static final int SIZE = 20;
    private static final double SPEED = 0.25;
    private static final double RADIUS = SIZE / 2;
    private static Image image;
    protected double damage;

    /**
     * Create a projectile, See the super class for more information
     * damage: not applicable
     */
    public Projectile(Pointt position, double damage, double movingAngle) {
        super(null, position, movingAngle, SPEED, 0);
        graphics.graphicsSize = SIZE;
        this.physics.radius = RADIUS;
        if (image == null) {
            try {
                image = ImageIO.read(new File(GameScreen.projectile));
                image = image.getScaledInstance(graphics.graphicsSize, graphics.graphicsSize, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("FAiled to load image");
                System.exit(0);
            }
        }
        this.damage = damage;
    }

    /**
     * Process movement of the projectile
     * time: processing time.
     */
    @Override
    public void move(int time) {
        physics.position.setX(physics.position.getX() + time * physics.xVel);
        physics.position.setY(physics.position.getY() + time * physics.yVel);
    }

    /**
     * Return an image representing the projectile on screen.
     */
    @Override
    public Image getRep() {
        return image;
    }
}