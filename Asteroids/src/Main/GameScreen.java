package Main;

import Element.Alien;
import Other.HighScore;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

public class GameScreen extends JFrame implements Runnable {

    protected static final int SINGLE_PLAYER_MODE = 1;
    protected static final int DUAL_PLAYER_MODE = 2;
    private static final int PROCESS_RATE = 100;
    private static final int PLOT_RATE = 50;
    public static final String ship = "sp1.png";
    public static final String rock = "a1.png";
    public static final String projectile = "projectile.png";
    public static final String alien = "Republic Gunship.png";
    public static final String explosion = "explode.png";
    public static final int TIME_FRAME = 100;//Milliseconds
    protected ScheduledThreadPoolExecutor graphicsTimer;
    protected ScheduledThreadPoolExecutor physicsTimer;
    protected WelcomeScreen welcome;
    private Map map;
    private boolean paused = false;
    private static int numberOfPlayers;

    public GameScreen(WelcomeScreen welcomeScreen, int playMode, JTable tbHighScore) {
        if (playMode == 1 || playMode == 2) {
            numberOfPlayers = playMode;
            this.welcome = welcomeScreen;
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            this.setSize(screen);
            setResizable(false);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            setupGraphics();
            //Background Color
            getContentPane().setBackground(Color.BLACK);

            map.setupMap();
            map.setFocusable(true);
            this.setVisible(true);
        } else {
            this.welcome = welcomeScreen;
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            this.setSize(screen);
            setResizable(false);
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            setupGraphics();
            //Background Color
            getContentPane().setBackground(Color.BLACK);

            map.setupMap();
            map.setFocusable(true);
            this.setVisible(true);
        }
    }

    public static int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    protected void load(File file) {
        pauseGame(true);
        map.load(file);
    }

    protected void runGame() {
        physicsTimer = new ScheduledThreadPoolExecutor(1);
        PhysicsProcessor physics = new PhysicsProcessor();
        physicsTimer.scheduleAtFixedRate(physics, 0, PROCESS_RATE, TimeUnit.MILLISECONDS);
        graphicsTimer = new ScheduledThreadPoolExecutor(1);
        graphicsTimer.scheduleAtFixedRate(this, 0, PLOT_RATE, TimeUnit.MILLISECONDS);
    }

    protected void endGame() {
        map.endGame = true;
        map.repaint();
        welcome.difficulty = 1;
        welcome.updateHighScore(new HighScore(welcome.tfPlayer1Name.getText(), map.players[0].score));
        welcome.updateHighScore(new HighScore(welcome.tfPlayer2Name.getText(), map.players[1].score));
    }

    private void setupGraphics() {
        Box gameScreen = Box.createHorizontalBox();

        map = new Map(this);
        gameScreen.add(map);

        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);

        JMenu file = new JMenu("File");
        menu.add(file);

        JMenuItem load = new JMenuItem("Load");
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame(true);
                JFileChooser chooser = new JFileChooser();
                int choose;
                chooser.setApproveButtonText("Load Game");
                choose = chooser.showOpenDialog(null);
                if (choose == JFileChooser.APPROVE_OPTION) {
                    map.load(chooser.getSelectedFile());
                }
                map.repaint();
            }
        });
        file.add(load);

        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame(true);
                JFileChooser chooser = new JFileChooser();
                int choose;
                chooser.setApproveButtonText("Save Game");
                choose = chooser.showOpenDialog(null);
                if (choose == JFileChooser.APPROVE_OPTION) {
                    map.save(chooser.getSelectedFile());
                }
            }
        });
        file.add(save);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame(!paused);
                GameScreen.this.setVisible(false);
                GameScreen.this.dispose();
                welcome.setVisible(true);
            }
        });
        file.add(exit);


        JMenu option = new JMenu("Option");
        JMenuItem opt = new JMenuItem("Option Configuration");
        opt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame(true);
                OptionBoard tam = new OptionBoard(welcome.difficulty, welcome.soundOn);
                tam.setWelcomeScreen(welcome);
                tam.setCaller(GameScreen.this);
                tam.setVisible(true);
            }
        });

        option.add(opt);
        menu.add(option);

        pause = new JMenu("Pause");
        pau = new JMenuItem("Pause Game");
        pau.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK));
        pau.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseGame(!paused);
            }
        });
        pause.add(pau);
        menu.add(pause);

        add(gameScreen);
    }
    JMenu pause;
    JMenuItem pau;

    public int getDifficulty() {
        return welcome.difficulty;
    }

    private void pauseGame(boolean isPaused) {
        if (isPaused) {
            pause.setText("Resume");
            pau.setText("Resume");
            if (GameScreen.this.physicsTimer != null && GameScreen.this.graphicsTimer != null) {
                GameScreen.this.physicsTimer.shutdown();
                GameScreen.this.graphicsTimer.shutdown();
                for (int i = 0; i < GameScreen.this.map.aliens.size(); i++) {
                    Alien current = GameScreen.this.map.aliens.get(i);
                    current.clearTask();
                }
            }
            paused = true;
        } else {
            pause.setText("Pause game");
            pau.setText("Pause game");
            PhysicsProcessor physics = new PhysicsProcessor();

            physicsTimer = new ScheduledThreadPoolExecutor(1);
            physicsTimer.scheduleAtFixedRate(physics, 0, PROCESS_RATE, TimeUnit.MILLISECONDS);

            graphicsTimer = new ScheduledThreadPoolExecutor(1);
            graphicsTimer.scheduleAtFixedRate(this, 0, PLOT_RATE, TimeUnit.MILLISECONDS);

            for (int i = 0; i < GameScreen.this.map.aliens.size(); i++) {
                Alien current = GameScreen.this.map.aliens.get(i);
                current.getAttack().schedule();
            }

            paused = false;
        }
    }

    private class PhysicsProcessor implements Runnable {

        public void run() {
            map.process();
        }
    }

    @Override
    public void run() {
        map.repaint();
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public static void setNumberOfPlayers(int numberOfPlayers) {
        GameScreen.numberOfPlayers = numberOfPlayers;
    }
}
