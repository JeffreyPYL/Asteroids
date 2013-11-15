package Element;

import Main.Map;
import Other.Pointt;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class PhysicsElement {

    protected Pointt position;
    protected double speed;
    protected double xVel;
    protected double yVel;
    protected double movingAngle;
    protected double acceleration;
    protected double health;
    protected Element container;
    protected double radius;
    private static final double MAX_SPEED = 0.2;
    protected double xAcc, yAcc;

    /**
     * Create a PhysicsElement that will take care of all physics aspect of the
     * element.
     */
    public PhysicsElement(Pointt position, double movingAngle, double speed,
            double health, Element container) {
        this.position = position;
        this.movingAngle = movingAngle;
        this.speed = speed;
        this.health = health;
        this.container = container;
        updateVelocity();
    }

    /**
     * process physics aspects of the element: movement processing
     *
     */
    protected void move(int time) {
        this.position.setX(this.position.getX() + time * xVel);
        this.position.setY(this.position.getY() + time * yVel);

        if (position.getX() <= 0) {
            position.setX(Map.screen.width);
        } else if (position.getX() >= Map.screen.width) {
            position.setX(0);
        }

        if (position.getY() <= 0) {
            position.setY(Map.screen.height);
        } else if (position.getY() >= Map.screen.height) {
            position.setY(0);
        }
    }

    /**
     * update the velocity of the element according to its moving angle and
     * speed.
     */
    protected void updateVelocity() {
        xVel = speed * Math.cos(movingAngle);
        yVel = speed * Math.sin(movingAngle);
    }

    /**
     * increase speed of the element according to its acceleration.
     */
    protected void increaseSpeed() {
        xAcc = acceleration * Math.cos(movingAngle);
        yAcc = acceleration * Math.sin(movingAngle);

        if (calculateSpeed(xVel + xAcc, yVel + yAcc) < MAX_SPEED) {
            xVel += xAcc;
            yVel += yAcc;
        }

    }

    /**
     * decrease speed of the element according to its acceleration.
     */
    protected void decreaseSpeed() {
        xAcc = acceleration * Math.cos(movingAngle);
        yAcc = acceleration * Math.sin(movingAngle);

        if (calculateSpeed(xVel + xAcc, yVel + yAcc) > 0) {
            xVel += xAcc;
            yVel += yAcc;
        }

    }

    
    /**
     * return: a double representing the speed length.
     */
    public double calculateSpeed(double x, double y) {
        return Math.sqrt((x * x) + (y * y));
    }

    /**
     * change the speed of the element according to the specified increment speed. Negative increment means decelerating.
     */
    protected void naturalAccelerate(double increment) {
        // Note the increment is negative... reason for funny +/- situation
        if (xVel > -increment) {
            xVel += increment;
        } else {
            if (xVel < increment) {
                xVel -= increment;
            } else {
                xVel = 0;// if within increment speed
            }

        }

        if (yVel > -increment) {
            yVel += increment;
        } else {
            if (yVel < increment) {
                yVel -= increment;
            } else {
                yVel = 0;// if within increment speed
            }

        }

    }

    /**
     *
     * @return real Radius of the element
     */
    public double getRadius() {
        return radius;
    }

    /**
     *
     * @return position of the element on screen
     */
    public Pointt getPosition() {
        return position;
    }

    /**
     *
     * @param position change position of the element to the specified element
     */
    public void setPosition(Pointt position) {
        this.position = position;
    }

    /**
     *
     * @return the speed of the element
     */
    public double getSpeed() {
        return speed;
    }

    public double getMovingAngle() {
        return movingAngle;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getHealth() {
        return health;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setMovingAngle(double movingAngle) {
        this.movingAngle = movingAngle;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setxVel(double xVel) {
        this.xVel = xVel;
    }

    public void setyVel(double yVel) {
        this.yVel = yVel;
    }

    public double getxVel() {
        return xVel;
    }

    public double getyVel() {
        return yVel;
    }
}
