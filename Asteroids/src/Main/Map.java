package Main;

import Element.Alien;
import Element.Asteroid;
import Element.Bonus;
import Element.SpaceShip;
import Element.Sprites;
import Other.Pointt;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.JComponent;

public class Map extends JComponent {

    public static int DEFAULT_PLAYER_LIFE = 5;
    private static int MAX_DIFFICULTY = 10;
    private Image background;
    public static final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    protected int currentPlayer = 0; //Either 0 or 1
    protected GameScreen gameScreen;
    protected ArrayList<Asteroid> asteroids;
    protected SpaceShip player;
    protected ArrayList<Alien> aliens;
    protected ArrayList<Sprites> sprites;
    protected ArrayList<Bonus> bonuses;
    protected GamePhysics gamePhysics;
    protected Player[] players = new Player[2];
    protected boolean endGame;
    private GameGraphics gameGraphics;
    private static final String BACK_GROUND_IMAGE = "Background.jpg";
    public static final String SHOOT = "file:./src/shoot2.aiff";
    public static final String EXPLODE = "file:./src/explode.aiff";
    public static final String POWER_UP = "file:./src/power.wav";

    /**
     * Create the map having every elements.
     */
    public Map(GameScreen container) {
        this.gameScreen = container;
        players[0] = new Player();
        players[1] = new Player();

        setupListener();
        try {
            background = ImageIO.read(new File(BACK_GROUND_IMAGE));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Cannot load background image!");
        }
    }

    private void setupListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int inputKey = e.getKeyCode();

                if (inputKey == KeyEvent.VK_W) {//Accelerate
                    Map.this.player.setAccelerate(true);                 
                } else if (inputKey == KeyEvent.VK_S) {//Decelerate
                    Map.this.player.setDeccelerate(true);
                } else if (inputKey == KeyEvent.VK_A) {//Increase movement angle
                    Map.this.player.setTurningLeft(true);
                } else if (inputKey == KeyEvent.VK_D) {//Decrease movement angle
                    Map.this.player.setTurningRight(true);
                } else if (inputKey == KeyEvent.VK_ENTER) {//Shoot
                    Map.this.player.setShooting(true);
                    playSoundEffect(SHOOT);
                } else {
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

                int inputKey = e.getKeyCode();
                if (inputKey == KeyEvent.VK_W) {//Accelerate
                    Map.this.player.setAccelerate(false);
                } else if (inputKey == KeyEvent.VK_S) {//Decelerate
                    Map.this.player.setDeccelerate(false);
                } else if (inputKey == KeyEvent.VK_A) {//Increase movement angle
                    Map.this.player.setTurningLeft(false);
                } else if (inputKey == KeyEvent.VK_D) {//Decrease movement angle
                    Map.this.player.setTurningRight(false);
                } else if (inputKey == KeyEvent.VK_ENTER) {//Shoot
                    Map.this.player.setShooting(false);
                } else {
                }
            }
        });

    }

    /**
     * Setup initial arrangement of the map
     */
    protected void setupMap() {
        gamePhysics = new GamePhysics(this);
        gameGraphics = new GameGraphics(this);

        asteroids = new ArrayList<>();
        aliens = new ArrayList<>();
        sprites = new ArrayList<>();
        bonuses = new ArrayList<>();

        player = new SpaceShip(this, new Pointt(300, 200), 0, 100, SpaceShip.WEAPON_TYPE_DEFAULT);

        do {
            Pointt alienSpawn = Pointt.generateRandom();
            if (alienSpawn.distance(player.getPosition()) >= player.getRadius() * Alien.SAFE_ALIEN) {
                aliens.add(new Alien(this, alienSpawn, 0.0, 0.0, player));
                break;
            }
        } while (true);

        for (int i = 0; i < gameScreen.welcome.difficulty * 4; i++) {
            do {
                Pointt asteroidSpawn = Pointt.generateRandom();
                if (asteroidSpawn.distance(player.getPosition()) >= player.getRadius() * Asteroid.SAFE_DISTANCE) {
                    asteroids.add(new Asteroid(this, Asteroid.STARTING_SIZE, asteroidSpawn, Math.random() * 2 * Math.PI, Math.random() * Asteroid.SPEED_DEFAULT));
                    break;
                }
            } while (true);
        }
    }

    /**
     * renew stuffs on the map according to the current level
     */
    protected void renew() {
        players[currentPlayer].difficulty = Math.min(players[currentPlayer].difficulty + 1, Map.MAX_DIFFICULTY);
        gameScreen.welcome.difficulty = players[currentPlayer].difficulty;
        for (int i = 0; i < gameScreen.welcome.difficulty * 4; i++) {
            Asteroid toAdd = new Asteroid(this, Asteroid.STARTING_SIZE, Pointt.generateRandom(), Math.random() * 2 * Math.PI, Math.random() * Asteroid.SPEED_DEFAULT);
            if (toAdd.getPhysics().getPosition().distance(player.getPosition()) >= player.getRadius() * 2) {
                asteroids.add(toAdd);
            }
        }

        player.upgradeWeapon();
    }

    /**
     *
     * @return current player's difficulty
     */
    public int getDifficulty() {
        return players[currentPlayer].difficulty;
    }

    /**
     *
     * @param soundToPlay
     */
    public void playSoundEffect(String soundToPlay) {
        if (!gameScreen.welcome.soundOn) {
            return;
        }
        URL soundLocation;
        try {
            soundLocation = new URL(soundToPlay);
            Clip clip;
            clip = AudioSystem.getClip();
            AudioInputStream inputStream;
            inputStream = AudioSystem.getAudioInputStream(soundLocation);
            clip.open(inputStream);
            clip.loop(0);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * process physics related aspect
     */
    protected void process() {
        gamePhysics.processPhysics();
    }
    int ind = 0;

    @Override
    public void paint(Graphics g) {
        try {
            Graphics2D a = (Graphics2D) g;
            a.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            AffineTransform defaultTransform = new AffineTransform();

            if (background != null) {
                a.setTransform(defaultTransform);
                a.drawImage(background, 0, 0, gameScreen);
            }

            if (endGame) {
                Font font = new Font("Times New Roman", Font.ROMAN_BASELINE, 200);
                a.setTransform(defaultTransform);
                a.setFont(font);
                a.setPaint(Color.RED);
                a.drawString("Game Over", screen.width / 2 - 490, screen.height / 2 + 50);
                return;
            }

            final Color[] color = {Color.BLACK, Color.BLUE};
            a.setPaint(Color.BLACK);
            a.setTransform(defaultTransform);
            gameGraphics.plotGraphics(a, defaultTransform);
        } catch (NullPointerException e) {
        }
    }

    protected void save(File file) {
        FileWriter fw = null;
        try {
            file.setWritable(true);
            fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            String d = "\n";
            bw.write(currentPlayer + d);
            bw.write(gameScreen.isPaused() + d);
            bw.write(GameScreen.getNumberOfPlayers() + d);

            bw.write(gameScreen.welcome.soundOn + d);
            bw.write(gameScreen.welcome.difficulty + d);

            bw.write("startAsteroid" + d);
            for (int i = 0; i < asteroids.size(); i++) {
                Asteroid current = asteroids.get(i);
                bw.write(current.getSize() + d);
                bw.write(current.getPhysics().getPosition().getX() + d);
                bw.write(current.getPhysics().getPosition().getY() + d);
                bw.write(current.getPhysics().getSpeed() + d);
                bw.write(current.getPhysics().getMovingAngle() + d);
            }
            bw.write("endAsteroid" + d);

            bw.write(player.getPosition().getX() + d);
            bw.write(player.getPosition().getY() + d);
            bw.write(player.getPhysics().getMovingAngle() + d);
            bw.write(player.getPhysics().getHealth() + d);
            bw.write(player.getWeaponType() + d);


            bw.write("startAlien" + d);
            for (int i = 0; i < aliens.size(); i++) {
                Alien current = aliens.get(i);
                bw.write(current.getPhysics().getPosition().getX() + d);
                bw.write(current.getPhysics().getPosition().getY() + d);
                bw.write(current.getPhysics().getMovingAngle() + d);
                bw.write(current.getPhysics().getHealth() + d);
                bw.write(current.getWeaponType() + d);
            }
            bw.write("endAlien" + d);

            bw.write("startBonus" + d);
            for (int i = 0; i < bonuses.size(); i++) {
                Bonus current = bonuses.get(i);
                bw.write(current.getPhysics().getPosition().getX() + d);
                bw.write(current.getPhysics().getPosition().getY() + d);
                bw.write(current.getPhysics().getMovingAngle() + d);
                bw.write(current.getType() + d);
            }
            bw.write("endBonus" + d);

            bw.write(players[0].lifeLeft + d);
            bw.write(players[0].score + d);
            bw.write(players[0].difficulty + d);
            bw.write(players[1].lifeLeft + d);
            bw.write(players[1].score + d);
            bw.write(players[1].difficulty + d);


            bw.write("endFile");

            bw.close();
        } catch (IOException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Cannot save game!");
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Cannot save game!");
            }
        }
    }

    protected void load(File file) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            this.currentPlayer = Integer.parseInt(line);


            line = br.readLine();
            this.gameScreen.setPaused(true);
            line = br.readLine();
            GameScreen.setNumberOfPlayers(Integer.parseInt(line));
            line = br.readLine();
            this.gameScreen.welcome.soundOn = line.equals("true");
            line = br.readLine();
            this.gameScreen.welcome.difficulty = Integer.parseInt(line);


            line = br.readLine();
            asteroids = new ArrayList<>();
            do {
                line = br.readLine();
                if (line.equals("endAsteroid")) {
                    break;
                }

                int size = Integer.parseInt(line);
                line = br.readLine();
                double posX = Double.parseDouble(line);
                line = br.readLine();
                double posY = Double.parseDouble(line);
                line = br.readLine();
                double speed = Double.parseDouble(line);
                line = br.readLine();
                double movingAngle = Double.parseDouble(line);

                asteroids.add(new Asteroid(this, size, new Pointt(posX, posY), movingAngle, speed));
            } while (true);

            {
                line = br.readLine();
                double posX = Double.parseDouble(line);
                line = br.readLine();
                double posY = Double.parseDouble(line);
                line = br.readLine();
                double movingAngle = Double.parseDouble(line);
                line = br.readLine();
                double health = Double.parseDouble(line);
                line = br.readLine();
                int weaponType = Integer.parseInt(line);

                player = new SpaceShip(this, new Pointt(posX, posY), movingAngle, health, weaponType);
            }

            line = br.readLine();
            aliens = new ArrayList<>();
            do {
                line = br.readLine();
                if (line.equals("endAlien")) {
                    break;
                }
                double posX = Double.parseDouble(line);
                line = br.readLine();
                double posY = Double.parseDouble(line);
                line = br.readLine();
                double movingAngle = Double.parseDouble(line);
                line = br.readLine();
                double health = Double.parseDouble(line);
                line = br.readLine();
                int weaponType = Integer.parseInt(line);
                aliens.add(new Alien(this, new Pointt(posX, posY), movingAngle, health, player));
            } while (true);

            line = br.readLine();
            bonuses = new ArrayList<>();
            do {
                line = br.readLine();
                if (line.equals("endBonus")) {
                    break;
                }
                double posX = Double.parseDouble(line);
                line = br.readLine();
                double posY = Double.parseDouble(line);
                line = br.readLine();
                double movingAngle = Double.parseDouble(line);
                line = br.readLine();
                int type = Integer.parseInt(line);
                bonuses.add(new Bonus(this, new Pointt(posX, posY), movingAngle, bonuses, type));
            } while (true);

            line = br.readLine();
            players[0].lifeLeft = Integer.parseInt(line);
            line = br.readLine();
            players[0].score = Integer.parseInt(line);
            line = br.readLine();
            players[0].difficulty = Integer.parseInt(line);

            line = br.readLine();
            players[1].lifeLeft = Integer.parseInt(line);
            line = br.readLine();
            players[1].score = Integer.parseInt(line);
            line = br.readLine();
            players[1].difficulty = Integer.parseInt(line);

            line = br.readLine();
            if (!line.equals("endFile")) {
                throw new RuntimeException("Invalid save file");
            }

        } catch (IOException ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("Invalid save file");
        }
    }

    public class Player {

        protected int lifeLeft;
        protected int score;
        protected int difficulty;

        private Player() {
            this.difficulty = Map.this.gameScreen.getDifficulty();
            lifeLeft = DEFAULT_PLAYER_LIFE;
            score = 0;
        }

        public void increaseLifeLeft() {
            lifeLeft++;
        }

        public void decreaseLifeLeft() {
            lifeLeft--;
        }

        public void setDifficulty(int difficulty) {
            this.difficulty = difficulty;
        }
    }

    protected void changePlayer() {
        currentPlayer = (currentPlayer + 1) % 2;
    }

    protected void setScore(int score) {
        players[currentPlayer].score = score;
    }

    protected int getScore() {
        return players[currentPlayer].score;
    }

    public SpaceShip getPlayer() {
        return player;
    }

    public Player[] getPlayers() {
        return players;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getLifeLeft() {
        return players[currentPlayer].lifeLeft;
    }

    public ArrayList<Bonus> getBonuses() {
        return bonuses;
    }
}
