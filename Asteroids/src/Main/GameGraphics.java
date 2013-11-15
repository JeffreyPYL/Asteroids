package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class GameGraphics {

    private Map map;

    /**
     * Create GameGraphics that will take care of all graphics aspect.
     */
    public GameGraphics(Map map) {
        this.map = map;
    }

    /**
     * plot everything on screen.
     */
    protected void plotGraphics(Graphics2D a, AffineTransform transform) {
        //Plot Spaceship
        map.player.plot(a, transform);

        //Plot Bonus
        for (int i = 0; i < map.bonuses.size(); i++) {
            map.bonuses.get(i).plot(a, transform);
        }
        
        //Plot Asteroid
        for (int i = 0; i < map.asteroids.size(); i++) {
            map.asteroids.get(i).plot(a, transform);
        }

        //Plot Alien
        for (int i = 0; i < map.aliens.size(); i++) {
            map.aliens.get(i).plot(a, transform);
        }

        //Plot sprites
        for (int i = 0; i < map.sprites.size(); i++) {
            map.sprites.get(i).plot(a, transform);
        }

        a.setTransform(transform);

        Font font = new Font("Times New Roman", Font.ROMAN_BASELINE, 14);
        a.setFont(font);
        a.setPaint(Color.RED);
        a.drawString("SCORE: ", Map.screen.width - 100, 20);
        String score = Integer.toString(map.getScore());
        a.drawString("PLAYER: " + (map.currentPlayer + 1), Map.screen.width - 200, 20);
        String diff = Integer.toString(map.getDifficulty());
        a.drawString("DIFFICULTY: " + (diff), Map.screen.width - 450, 20);
        String lives = Integer.toString(map.getLifeLeft());
        a.drawString("LIVES: " + (lives), Map.screen.width - 300, 20);


        a.drawString(score, Map.screen.width - 50, 20);

    }
}
