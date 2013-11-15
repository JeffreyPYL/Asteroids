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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class Alien extends SpaceShip {

    private static final int SIZE = 30;
    private static final double RADIUS = SIZE / 2;
    private static final double SPEED_DEFAULT = 0.05;
    public static final int SAFE_ALIEN = 7;
    private static Image image;
    private SpaceShip preyShip;
    private Attack attack;

    /**
     * Generate a spaceship that will chase player.
     */
    public Alien(Map map, Pointt position, double movingAngle, double health, SpaceShip preyShip) {
        super(map, position, movingAngle, health, 0);
        this.position = position;
        this.physics.radius = RADIUS;
        graphics.graphicsSize = (int) (physics.radius * GRAPHICS_REAL_SCALE);
        if (image == null) {
            try {
                image = ImageIO.read(new File(GameScreen.alien));
                image = image.getScaledInstance(graphics.graphicsSize, graphics.graphicsSize, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("Failed to load image");
                System.exit(0);
            }
        }

        this.preyShip = preyShip;
        this.physics.speed = SPEED_DEFAULT;
        attack = new Attack();
        try {
            attack.schedule();
        } catch (Exception e) {
            throw new RuntimeException("ASDASD");
        }
    }

    /**
     * Update movement direction and process projectiles movement
     */
    @Override
    public void process() {
        if (this.getMap() != null) {
            preyShip = this.getMap().getPlayer();
        }
        updateMovement(preyShip.position);
        move(GameScreen.TIME_FRAME);
        for (int i = 0; i < this.getProjectile().size(); i++) {
            getProjectile().get(i).move(GameScreen.TIME_FRAME);
        }
    }

    /**
     * the method changes the moving speed (0 if very near player) and the moving angle of the alien (heading to the player on the screen)
     */
    public void updateMovement(Pointt destination) {
        if (physics.position.distance(destination) <= 2 * preyShip.getRadius()) {
            physics.xVel = 0;
            physics.yVel = 0;
            return;
        }

        physics.movingAngle = arcTan(destination.getY() - position.getY(),
                destination.getX() - position.getX(), destination.getX() - position.getX());
        physics.position = this.position.clone();
        physics.xVel = physics.speed * Math.cos(physics.movingAngle);
        physics.yVel = physics.speed * Math.sin(physics.movingAngle);
    }

    private static double arcTan(double y, double x, double xPosition) {
        if (x == 0) {
            if (y >= 0) {
                return Math.PI / 2;
            } else {
                return -Math.PI / 2;
            }
        } else {
            double result = Math.atan(y / x);
            if (xPosition < 0) {
                return result + Math.PI;
            } else {
                return result;
            }
        }
    }

    /**
     * stop the attack timer of the alien
     */
    public void clearTask() {
        attack.timer.shutdown();
        attack.timer = new ScheduledThreadPoolExecutor(1);
    }

    /**
     * the class controls the attacks of the alien: adding projectiles
     */
    public class Attack implements Runnable {

        private static final long BASE_ATTACK_TIME = 1000;
        ScheduledThreadPoolExecutor timer;

        /**
         * start shooting at the player at every BASE_ATTACK_TIME
         */
        public void schedule() {
            timer = new ScheduledThreadPoolExecutor(1);
            timer.scheduleAtFixedRate(this, 0, BASE_ATTACK_TIME, TimeUnit.MILLISECONDS);
        }

        @Override
        public void run() {
            Alien.this.getProjectile().add(new Projectile(position.clone(), 0, physics.movingAngle));
        }
    }

    /**
     * process movement of the alien.
     */
    @Override
    public void move(int time) {
        super.move(time);
        this.position = physics.position.clone();
    }

    /**
     * the method plots the alien on the game screen.
     */
    @Override
    public void plot(Graphics2D a, AffineTransform transform) {
        a.setPaint(Color.BLUE);
        super.plot(a, transform);

    }

    /**
     *
     * @return the image representing the alien on the game screen
     */
    @Override
    public Image getRep() {
        return image;
    }

    /**
     * @return the position where projectile will appear when shot by the
     * spaceship
     */
    protected Pointt getHead() {
        return physics.position.clone();
    }

    /**
     *
     * @return the attack processing unit of the alien
     */
    public Attack getAttack() {
        return attack;
    }
}
