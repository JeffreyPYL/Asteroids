package Element;

import Main.GameScreen;
import Main.Map;
import Other.Pointt;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class SpaceShip extends Element {

    private static final int SIZE = 45;
    private static final int PROJECTILE_TYPE = 0;
    private static final double SPEED_DEFAULT = 0.005;
    private static final double ACCELERATION = 0.007;
    private static final double NATURAL_DECCELERATION = -0.005;
    private static final double ANGULAR_SPEED = Math.toRadians(9);
    public static final int WEAPON_TYPE_DEFAULT = 1;
    private static final int PROJECTILE_TYPE_DEFAULT = 0;
    private static final double RADIUS = SIZE / 2;
    private static Image image;
    private int weaponType;
    private int projectileType;
    private Weapon weapon;
    private ArrayList<Projectile> projectiles;
    private boolean invulnerable;
    private boolean accelerate;
    private boolean deccelerate;
    private boolean turningLeft;
    private boolean turningRight;
    private boolean shooting;
    protected ArrayList<Bonus> status;
    protected Pointt position;

    /**
     * Create a spaceship.
     */
    public SpaceShip(Map map, Pointt position, double movingAngle, double health, int weaponType) {
        super(map, position, movingAngle, SPEED_DEFAULT, health);
        this.position = position;
        this.physics.radius = RADIUS;
        graphics.graphicsSize = (int) (physics.radius * GRAPHICS_REAL_SCALE);
        if (image == null) {
            try {
                image = ImageIO.read(new File(GameScreen.ship));
                image = image.getScaledInstance((int) graphics.graphicsSize, (int) graphics.graphicsSize, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Failed to load image");
                System.exit(0);
            }
        }
        projectiles = new ArrayList<Projectile>();
        status = new ArrayList<>();
        this.weaponType = weaponType;
        projectileType = PROJECTILE_TYPE;
        weapon = new Weapon(this, weaponType);
    }

    /**
     * Process the movement of the spaceship and its projectiles movement.
     */
    public void process() {

        this.move(GameScreen.TIME_FRAME);

        // Process projectile
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile current = projectiles.get(i);
            current.move(GameScreen.TIME_FRAME);
        }
    }

    @Override
    public void move(int time) {
        if (accelerate) {
            increaseSpeed(ACCELERATION);
        }
        if (deccelerate) {
            decreaseSpeed(-ACCELERATION);
        }

        if (!accelerate && !deccelerate) {
            naturalDecreasing(NATURAL_DECCELERATION);
        }

        if (turningLeft) {// + turning angle
            increaseMovingAngle(-ANGULAR_SPEED);
        }

        if (turningRight) {
            increaseMovingAngle(ANGULAR_SPEED);
        }

        if (shooting) {
            weapon.shoot(projectiles);
            this.shooting = false;
        }

        try {
            for (int i = 0; i < status.size(); i++) {
                status.get(i).move(time);
            }
        } catch (IndexOutOfBoundsException ex) {
        }

        super.move(time);
    }

    public void upgradeWeapon() {
        weapon.upgrade();
    }

    public void degradeWeapon() {
        weapon.degrade();
    }

    /**
     *
     * bonus: bonus that will be applied to this spaceShip.
     */
    public void applyBonus(Bonus bonus) {//Instant apply
        status.add(bonus);
    }
    
    @Override
    public void plot(Graphics2D a, AffineTransform transform) {
        a.setPaint(Color.BLUE);
        super.plot(a, transform);

        a.setTransform(transform);
        a.translate(position.getX(), position.getY());
        for (int i = 0; i < status.size(); i++) {
            status.get(i).plot(a, transform);
        }
        
        for (int i = 0; i < projectiles.size(); i++) {
            try {
                projectiles.get(i).plot(a, transform);
            } catch (IndexOutOfBoundsException e) {
            }
        }
        
        
    }

    @Override
    public Image getRep() {
        return image;
    }

    /**
     * return the position where projectile will appear when shot by the
     * spaceship.
     */
    protected Pointt getHead() {
        return physics.position.clone();
    }

    private void increaseMovingAngle(double increment) {
        physics.movingAngle += increment;
    }

    private void increaseSpeed(double increment) {
        physics.acceleration = increment;
        physics.increaseSpeed();
    }

    private void decreaseSpeed(double increment) {
        physics.acceleration = increment;
        physics.decreaseSpeed();
    }

    private void naturalDecreasing(double increment) {
        physics.acceleration = increment;
        physics.naturalAccelerate(increment);

    }

    public ArrayList<Bonus> getStatus() {
        return status;
    }
    
    public boolean isAccelerate() {
        return accelerate;
    }

    public boolean isDeccelerate() {
        return deccelerate;
    }

    public boolean isTurningLeft() {
        return turningLeft;
    }

    public boolean isTurningRight() {
        return turningRight;
    }

    public boolean isShooting() {
        return shooting;
    }

    public void setAccelerate(boolean accelerate) {
        this.accelerate = accelerate;
    }

    public void setDeccelerate(boolean deccelerate) {
        this.deccelerate = deccelerate;
    }

    public void setTurningLeft(boolean turningLeft) {
        this.turningLeft = turningLeft;
    }

    public void setTurningRight(boolean turningRight) {
        this.turningRight = turningRight;
    }

    public void setShooting(boolean shooting) {
        this.shooting = shooting;
    }

    public ArrayList<Projectile> getProjectile() {
        return projectiles;
    }

    public Pointt getPosition() {
        return this.position;
    }

    public void setPosition(Pointt position) {
        this.position = position;
    }

    public int getWeaponType() {
        return weaponType;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }
}