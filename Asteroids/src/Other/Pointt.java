package Other;

import Main.Map;
import java.util.Random;

public class Pointt {

    private static Random random = new Random(System.nanoTime());
    private double x;
    private double y;

    /**
     * Provide better support class than Point class as default
     */
    public Pointt(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(Pointt other) {
        return Math.sqrt(Math.pow(x - other.x,2) + Math.pow(y - other.y,2));
    }
    
    public static Pointt generateRandom() {
        return new Pointt(random.nextDouble() * Map.screen.width,random.nextDouble() * Map.screen.height);
    }
    
    @Override
    public String toString() {
        return "x = " + x + "; y = " + y;
    }
    
    @Override
    public Pointt clone() {
        return new Pointt(x,y);
    }
    
    public double getX() {
        return this.x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
