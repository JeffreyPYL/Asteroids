package Element;

import java.util.ArrayList;

public class Weapon {

    private static double[] DAMAGE = {0, 1, 1, 1, 1, 1};
    protected static int MAX_WEAPON_TYPE = 4;
    private SpaceShip ship;
    private Asteroid asteroid;
    private int type;

    /**
     *
     * ship: the ship that has this weapon equipped.
     * type: the type of this weapon.
     */
    public Weapon(SpaceShip ship, int type) {
        this.ship = ship;
        this.type = type;
    }
    private static final double[][] SHOOT = {{}, {Math.PI / 4, -Math.PI / 4},
        {Math.PI / 8, -Math.PI / 8}, {Math.PI / 16, -Math.PI / 16}, {Math.PI / 32, -Math.PI / 32}};

    /**
     * Add the projectile to the spaceship's projectile container.
     * projectiles: the projectile container of the spaceship
     */
    public void shoot(ArrayList<Projectile> projectiles) {
        projectiles.add(new Projectile(ship.getHead(), DAMAGE[type], ship.physics.movingAngle));
        for (int i = 1; i <= type; i++) {
            projectiles.add(new Projectile(ship.getHead(), DAMAGE[type], ship.physics.movingAngle + SHOOT[i][0]));
            projectiles.add(new Projectile(ship.getHead(), DAMAGE[type], ship.physics.movingAngle + SHOOT[i][1]));
        }
    }

    /**
     * upgrade weapon.
     */
    protected void upgrade() {
        type = Math.min(type + 1, MAX_WEAPON_TYPE);
    }
    
    /**
     * degrade weapon.
     */
    protected void degrade() {
        System.out.println(type);
        type = Math.max(type - 1, 0);
        System.out.println(type + "  After");
    }
    
    public double getX() {
        return ship.physics.getPosition().getX();
    }

    public double getY() {
        return ship.physics.getPosition().getY();
    }
}
