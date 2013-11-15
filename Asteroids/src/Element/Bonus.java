package Element;

import Main.Map;
import Other.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class Bonus extends Element {

    public static final String[] BONUS = {"Shield.png", "Life.png", "Weapon.png"};
    private static final long DURATION[] = {5000, 0, 10000};
    public static final int SHIELD = 0;
    public static final int LIFE = 1;
    public static final int WEAPON = 2;
    private static final double SPEED_DEFAULT = 0.01;
    private static final double RADIUS = 15;
    private static final long DEFAULT_START_TIME = Long.MAX_VALUE;
    private static Image[] rep;
    private ArrayList<Bonus> container;
    private int type;
    private long startTime;

    /**
     * Create a bonus with specified type
     * see the super class for other variables.
     */
    public Bonus(Map map, Pointt position, double movingAngle, ArrayList<Bonus> container, int type) {
        super(map, position, movingAngle, SPEED_DEFAULT, 0);
        physics.radius = RADIUS;
        graphics.graphicsSize = (int) (physics.radius * GRAPHICS_REAL_SCALE);

        if (rep == null) {
            rep = new Image[BONUS.length];
            for (int i = 0; i < BONUS.length; i++) {
                try {
                    rep[i] = ImageIO.read(new File(BONUS[i]));
                    rep[i] = rep[i].getScaledInstance(graphics.graphicsSize, graphics.graphicsSize, Image.SCALE_SMOOTH);
                } catch (Exception ex) {
                    System.out.println("Cannot load bonus image");
                    Logger.getLogger(Bonus.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(0);
                }
            }
        }

        this.container = container;
        this.type = type;
        this.startTime = DEFAULT_START_TIME;
    }

    /**
     * the method will remove the current bonus from the game bonus container and add itself to the spaceship status. Depoends on the type of the bonus, the effects will vary
     */
    public void applyEffect(SpaceShip ship) {
        ship.status.add(this);
        map.getBonuses().remove(this);
        this.container = ship.status;
        physics.radius = ship.physics.radius;
        if (type == SHIELD) {
            ship.setInvulnerable(true);
        } else if (type == WEAPON) {
            ship.upgradeWeapon();
        } else if (type == LIFE) {
            if (ship.getClass() != Alien.class) {
                map.getPlayers()[map.getCurrentPlayer()].increaseLifeLeft();
            }
        }
        this.startTime = System.currentTimeMillis();
    }

    /**
     *
     * Effect: Remove (if
     * applicable)
     */
    @Override
    public void move(int time) {
        if (hasApplied()) {
            if (System.currentTimeMillis() - startTime > DURATION[type]) {
                remove();
            }
        } else {
            super.move(time);
        }
    }

    /**
     * the method will plot the current bonus on the game screen.
     */
    @Override
    public void plot(Graphics2D a, AffineTransform defaultTransform) {
        if (!hasApplied()) {
            super.plot(a, defaultTransform);
        } else {
            double radius = physics.radius;
            double radiusInner = radius - 5;
            Area outer = new Area(new Ellipse2D.Double(-radius, -radius, 2 * radius, 2 * radius));
            Area inner = new Area(new Ellipse2D.Double(-radiusInner, -radiusInner, 2 * radiusInner, 2 * radiusInner));
            outer.subtract(inner);
            if (type == SHIELD) {
                a.setPaint(Color.BLUE);
            } else if (type == WEAPON) {
                a.setPaint(Color.RED);
            }
            a.fill(outer);
        }
    }

    /**
     * Remove the bonus from the spaceship's status, and also revert the effects that have been applied.
     */
    public void remove() {
        if (type == SHIELD) {
            map.getPlayer().setInvulnerable(false);
        } else if (type == WEAPON) {
            map.getPlayer().degradeWeapon();
        }
        container.remove(this);
    }

    /**
     *
     * return an image representing the Bonus, if an only if the bonus is not taken by any spaceship..
     */
    @Override
    public Image getRep() {
        if (!hasApplied()) {
            return rep[type];
        } else {
            throw new RuntimeException("Not suppose to be here");
        }
    }

    /**
     *
     * return if the bonus has been taken by any spaceship.
     */
    private boolean hasApplied() {
        return startTime != DEFAULT_START_TIME;
    }
    
    /**
     *
     * return the bonus type of the current bonus.
     */
    public int getType() {
        return type;
    }
}