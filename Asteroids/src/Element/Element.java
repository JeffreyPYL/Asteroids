package Element;

import Main.Map;
import Other.Pointt;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public abstract class Element {

    public static final double GRAPHICS_REAL_SCALE = 3.25;
    protected PhysicsElement physics;
    protected GraphicsElement graphics;
    protected Map map;

    /**
     * Create a new element in the game Map
     */
    public Element(Map map, Pointt position, double movingAngle, double speed, double health) {
        this.map = map;
        physics = new PhysicsElement(position, movingAngle, speed, health, this);
        graphics = new GraphicsElement(this);
    }
 

    /**
     * move the element accordingly to its speed and moving angle
     */
    public void move(int time) {
        physics.move(time);
    }

    /**
     * draw the element on screen.
     */
    public void plot(Graphics2D a, AffineTransform transform) {
    	graphics.plot(a, transform);
    }

    /**
     * return if the two element collide.
     */
    public boolean collide(Element second) {
    	boolean out = physics.position.distance(second.physics.position) < this.getRadius() + second.getRadius();
    	return out;
    }

    /**
     *
     * return an image representing the Bonus, if an only if the bonus is not taken by any spaceship.
     */
    public Image getRep() {
    	return null;
    }

    /**
     *
     * @return PhysicsElement of the current element
     */
    public PhysicsElement getPhysics() {
        return physics;
    }

    /**
     *
     * return GraphicsElement of the current element.
     */
    public GraphicsElement getGraphics() {
        return graphics;
    }
    
    /**
     *
     * return real radius of the element.
     */
    public double getRadius() {
    	return physics.radius;
    }
    
    /**
     *
     * return the Map containing this element.
     */
    public Map getMap() {
        return map;
    }
}
