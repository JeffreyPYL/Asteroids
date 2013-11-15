package Main;

import Element.Alien;
import Element.Asteroid;
import Element.Bonus;
import Element.Sprites;
import Other.Pointt;
import java.util.Random;

public class GamePhysics {

    private static final double BONUS_RATE = 0.01;
    private static final int DEFAULT_SCORE_BOUND = 2000;
    private Map map;
    private long scoreBound;

    /**
     * Control all the physics aspect of the game.
     */
    public GamePhysics(Map map) {
        this.map = map;
        scoreBound = DEFAULT_SCORE_BOUND;
    }

    /**
     * Process everything about physics, collision of the game.
     */
    protected void processPhysics() {
        // Process getPlayer
        map.player.process();

        // Process Asteroids
        for (int i = 0; i < map.asteroids.size(); i++) {
            map.asteroids.get(i).move(GameScreen.TIME_FRAME);
        }
        // Process Alien
        for (int i = 0; i < map.aliens.size(); i++) {
            map.aliens.get(i).process();
        }

        //Process bonuses
        for (int i = 0; i < map.bonuses.size(); i++) {
            map.bonuses.get(i).move(GameScreen.TIME_FRAME);
        }

        //Process Sprites
        for (int i = 0; i < map.sprites.size(); i++) {
            if (System.currentTimeMillis() - map.sprites.get(i).getStartTime() > Sprites.EXPLOSION_DISPLAY_TIME) {
                map.sprites.remove(i);
                i--;
            }
        }
        testOfCollision();
    }

    private void testOfCollision() {

        // Player test
        boolean crashA = false;

        //Player picks up bonus
        for (int i = 0; i < map.bonuses.size(); i++) {
            if (map.player.collide(map.bonuses.get(i))) {
                try {
                    map.bonuses.get(i).applyEffect(map.player);
                    map.playSoundEffect(Map.POWER_UP);
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }

        //Player collision with asteroids
        for (int i = 0; i < map.asteroids.size(); i++) {
            if (map.player.collide(map.asteroids.get(i))) {
                try {
                    Sprites sprite = new Sprites(map.player.getPosition().clone(), System.currentTimeMillis());
                    map.sprites.add(sprite);
                    map.playSoundEffect(Map.EXPLODE);
                    if (!map.player.isInvulnerable()) {
                        lifeLoss();
                    }
                    crashA = true;
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                }
            }

            //projectile and asteroid collision
            for (int j = 0; j < map.player.getProjectile().size(); j++) {
                try {
                    if (map.asteroids.get(i).collide(map.player.getProjectile().get(j))) {
                        map.playSoundEffect(Map.EXPLODE);
                        map.setScore(map.getScore() + 100);

                        crashA = true;
                        map.player.getProjectile().remove(j);
                        j--;
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                }
            }

            if (crashA) {
                map.asteroids.get(i).split(map.asteroids);
                map.asteroids.remove(i);
                i--;
                crashA = false;
            }
        }

        // Asteroid test
        for (int i = 0; i < map.asteroids.size(); i++) {
            for (int j = i; j < map.asteroids.size(); j++) {
                if (map.asteroids.get(i).collide(map.asteroids.get(j))) {
                    // Swap xVel
                    double tam = map.asteroids.get(i).getPhysics().getxVel();
                    map.asteroids.get(i).getPhysics().setxVel(map.asteroids.get(j).getPhysics().getxVel());
                    map.asteroids.get(j).getPhysics().setxVel(tam);

                    // Swap yVel
                    tam = map.asteroids.get(i).getPhysics().getyVel();
                    map.asteroids.get(i).getPhysics().setyVel(map.asteroids.get(j).getPhysics().getyVel());
                    map.asteroids.get(j).getPhysics().setyVel(tam);
                }
            }
        }

        // Alien
        for (int i = 0; i < map.aliens.size(); i++) {
            //Internal test
            for (int j = i + 1; j < map.aliens.size(); j++) {
                try {
                    map.playSoundEffect(Map.EXPLODE);
                    map.aliens.get(i).clearTask();
                    map.aliens.get(j).clearTask();
                    map.aliens.remove(i);
                    map.aliens.remove(j);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                }
            }

            //Alien Kills Player
            for (int j = 0; j < map.aliens.get(i).getProjectile().size(); j++) {
                try {
                    if (map.aliens.get(i).getProjectile().get(j).collide(map.player)) {
                        if (!map.player.isInvulnerable()) {
                            Sprites sprite = new Sprites(map.player.getPosition().clone(), System.currentTimeMillis());
                            map.sprites.add(sprite);
                            map.playSoundEffect(Map.EXPLODE);
                            lifeLoss();
                        }

                        map.aliens.get(i).getProjectile().remove(j);
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            }

            //Player Kills alien
            for (int j = 0; j < map.player.getProjectile().size(); j++) {
                try {
                    if (map.aliens.get(i).collide(map.player.getProjectile().get(j))) {
                        if (!map.aliens.get(i).isInvulnerable()) {
                            map.playSoundEffect(Map.EXPLODE);
                            map.setScore(map.getScore() + 100);

                            Sprites sprite = new Sprites(map.aliens.get(i).getPosition().clone(), System.currentTimeMillis());
                            map.sprites.add(sprite);
                            map.aliens.get(i).clearTask();
                            map.aliens.remove(i);
                        }
                        map.player.getProjectile().remove(j);
                    }
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                }
            }

            //Alien picks bonus
            for (int j = 0; j < map.bonuses.size(); j++) {
                try {
                    if (map.aliens.get(i).collide(map.bonuses.get(j))) {
                        map.bonuses.get(j).applyEffect(map.aliens.get(i));
                    }
                } catch (Exception e) {
                }
            }

            // Test with asteroid : no need since assume that alien passes through asteroid
//                for (int i = 0; i < getMap.asteroids.size(); i++) {
//                }
        }

        //Check if bonus should appear
        if (Math.random() < BONUS_RATE) {
            Random random = new Random(System.nanoTime());
            map.bonuses.add(new Bonus(map, Pointt.generateRandom(),
                    2 * Math.random() * Math.PI, map.bonuses, random.nextInt(Bonus.BONUS.length)));
        }

        //Check if new alien should appear
        if (map.getScore() > scoreBound) {
            scoreBound += DEFAULT_SCORE_BOUND;
            do {
                Pointt alienSpawn = Pointt.generateRandom();
                if (alienSpawn.distance(map.player.getPosition()) >= map.player.getRadius() * Alien.SAFE_ALIEN) {
                    map.aliens.add(new Alien(map, alienSpawn, 0.0, 0.0, map.player));
                    break;
                }
            } while (true);
        }

        //Check if should renew the map
        if (map.asteroids.isEmpty() && map.aliens.isEmpty()) {
            map.renew();
        }
    }

    private void lifeLoss() {
        int numberOfPlayers = GameScreen.getNumberOfPlayers();
        map.players[map.currentPlayer].lifeLeft--;

        if (numberOfPlayers == 2) {
            map.changePlayer();
            if (map.players[map.currentPlayer].lifeLeft == 0) {
                map.changePlayer();
            }
        }
        map.gameScreen.welcome.difficulty = map.players[map.currentPlayer].difficulty;

        if (map.players[0].lifeLeft == 0) {// when all getPlayer finish their turn
            if (numberOfPlayers == 2) {
                if (map.players[1].lifeLeft == 0) {
                    map.gameScreen.physicsTimer.shutdown();
                    map.gameScreen.graphicsTimer.shutdown();
                    map.gameScreen.endGame();
                }
            } else {
                map.gameScreen.physicsTimer.shutdown();
                map.gameScreen.graphicsTimer.shutdown();
                map.gameScreen.endGame();
            }
        } else {
            map.player.setPosition(new Pointt(300, 200));
            map.player.getPhysics().setPosition(map.player.getPosition());
            map.player.getPhysics().setxVel(0);
            map.player.getPhysics().setyVel(0);
            try {
                for (int i = 0; i < map.player.getStatus().size(); i++) {
                    map.player.getStatus().get(i).remove();
                }
            } catch (IndexOutOfBoundsException | NullPointerException ex) {
                ex.printStackTrace();
                System.exit(0);
            }
            Bonus invulnerable = new Bonus(map, new Pointt(20, 20), 0, map.player.getStatus(), Bonus.SHIELD);
            invulnerable.applyEffect(map.player);
        }
    }
}