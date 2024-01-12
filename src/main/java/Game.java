import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;


public class Game extends JPanel implements ActionListener {
    private final Timer timer;

    public static GameObject exitButton;
    public static GameObject startButton;
    public static GameObject menuButton;
    public static GameObject ground;
    public static GameObject ground2;
    public static GameObject leftBorder;
    public static GameObject rightBorder;

    public static GameObject slime;
    public static int frameWidth=500;
    public static int frameHeight=100;
    public static JFrame frame;
    public static GameState currentState;
    public int counter = 0;
    public Random rand = new Random();
    public static ArrayList<GameObject> clouds = new ArrayList<>();
    private static HashMap<GameObject, GameObject> cloudMapping = new HashMap<>();
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public static int score = 0;
    public static int Highscore = 0;
    public static JLabel scoreLabel = new JLabel("Score: ");
    public static JLabel HighScoreLabel = new JLabel("HighScore: ");
    public static int x;
    public static int y;
    public static int speed = 1;
                    //GameState Enum
    //State hinzufügen für neue Bereiche des Spieles
    //Siehe protected void paintComponent(Graphics g) und public void actionPerformed(ActionEvent e) um zu erfahren wie neue States im Spiel aufgebaut sind.

    public Game() {

        int xCoord = (int) ((screenSize.getWidth()));
        x = xCoord;
        int yCoord = (int) ((screenSize.getHeight()));
        y = yCoord;
        this.setPreferredSize(new Dimension(x, y));
        this.timer = new Timer(16, this);  // 60 FPS

        this.add(scoreLabel);
        this.add(HighScoreLabel);

        currentState = GameState.MENU;

        System.out.println(y + " " + x);
        System.out.println(frame.getBounds().getHeight() +" "+ frame.getBounds().getWidth());

        //GameState.MENU
        exitButton = new GameObject(200, 200, 400, 400, "src/main/java/Images/ExitButton.png", true,true);
        startButton = new GameObject(1100, 200, 400, 400, "src/main/java/Images/StartButton.png", true,true);
        exitButton.setClickable(true);
        exitButton.setGamestate(GameState.MENU);
        startButton.setClickable(true);
        startButton.setGamestate(GameState.MENU);



        //GameState.IN_GAME
        ground = new GameObject(0,y-230, x, 100, "src/main/java/Images/ground.png", false, true);
        ground.setGamestate(GameState.IN_GAME);
        ground.setObjectName("Ground");
        ground2 = new GameObject(0,y-285, x, 100, "", false, true);
        ground2.setGamestate(GameState.IN_GAME);
        ground2.setObjectName("Ground");
        slime = new GameObject((x+100)/2, -100, 80,80,"src/main/java/Images/slime.png", true,true);
        slime.setHitboxDimensions(80,25);
        slime.setGamestate(GameState.IN_GAME);
        slime.setPlayable(true);
        slime.setSpeed(15);
        slime.setGravity(true);
        slime.setJumpTime(0.8f);
        slime.setObjectName("Slime");

        GameObject cloud = new GameObject((x+100)/2, 30, 150, 150, "src/main/Java/Images/wolke.png", false, true);
        cloud.setObjectName("Wolke");
        cloud.setGravity(true);
        cloud.setGamestate(GameState.IN_GAME);

        GameObject cloudBorder = new GameObject((x+100)/2, -29 + 150, 150, 20, "", false, true);
        cloudBorder.setObjectName("WolkeBorder");
        cloudBorder.setGravity(true);
        cloudBorder.setGamestate(GameState.IN_GAME);

        cloudMapping.put(cloudBorder, cloud);

        clouds.add(cloud);
        clouds.add(cloudBorder);

        leftBorder = new GameObject(-10,0,10,y, "src/main/java/Images/ground.png",false, true);
        leftBorder.setGamestate(GameState.IN_GAME);
        leftBorder.setObjectName("LeftBorder");
        rightBorder = new GameObject(x,0,10,y, "src/main/java/Images/ground.png",false, true);
        rightBorder.setGamestate(GameState.IN_GAME);
        rightBorder.setObjectName("RightBorder");

        menuButton = new GameObject(x-250, 50,150,75,"src/main/Java/Images/MenuButton.png",true,true);
        menuButton.setGamestate(GameState.IN_GAME);
        menuButton.setClickable(true);



                //Key and Mouse Listener
                addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        for (GameObject obj : GameObject.getAllObjects()) {
                            if (obj.isPlayable()  && obj.getGamestate() == currentState) {
                                obj.setMoveDirection(e.getKeyCode(),true);
                            }
                        }
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                        for (GameObject obj : GameObject.getAllObjects()) {
                            if (obj.isPlayable()  && obj.getGamestate() == currentState) {
                                obj.setMoveDirection(e.getKeyCode(),false);
                            }
                        }
                    }
                });

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        for (GameObject obj : GameObject.getAllObjects()) {
                            if (obj.checkIfClicked(e) && obj.isClicked() && obj.getGamestate() == currentState) {
                                System.out.println(obj + " wurde geklickt");
                            }
                        }
                    }
                });


        this.setFocusable(true);
        this.requestFocusInWindow();

        timer.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {

        slime.showAllHitBoxes(false);
        scoreLabel.setBounds(700,40,200,200);
        scoreLabel.setAlignmentX(2);
        HighScoreLabel.setBounds(900,40,200,200);
        if(currentState == GameState.MENU) {

            if(startButton.isClicked()) {
                startButton.setClicked(false);
                currentState = GameState.IN_GAME;
                startButton.setClickable(false);
                exitButton.setClickable(false);
            }
            if(exitButton.isClicked()) {
                exitButton.setClicked(false);
                System.exit(0);
            }

            for (GameObject obj : GameObject.getAllObjects()) {
                if (obj.isPlayable()  && obj.getGamestate() == currentState) {
                    obj.updatePosition();
                }
            }
        }

        if(currentState == GameState.IN_GAME) {
            this.remove(scoreLabel);
            score++;
            scoreLabel.setBounds(x-200, 60,200,200);
            scoreLabel.setText("Score: "+ score);
            this.add(scoreLabel);
            this.remove(HighScoreLabel);
            for (GameObject obj : GameObject.getAllObjects()) {
                if (obj.isPlayable() && obj.getGamestate() == currentState) {
                    obj.updatePosition();
                }
            }

            if(menuButton.isClicked()) {
                menuButton.setClicked(false);
                currentState = GameState.MENU;
                startButton.setClickable(true);
                exitButton.setClickable(true);
                this.add(scoreLabel);
                this.add(HighScoreLabel);
            }
            if(slime.getColldingObjectName().equals("Ground")) {
                currentState = GameState.GAME_OVER;
            }

            if(counter == 60) {
                createClouds();
            }
            if(counter == 120) {
                createClouds();
            }
            if(counter == 180) {
                createClouds();
                counter = 0;
            }
            counter++;
            Iterator<GameObject> iterator = clouds.iterator();
            while(iterator.hasNext()) {
                GameObject gameObject = iterator.next();
                gameObject.updatePosition();

                // Behandlung der Logik für Wolkenborder
                if (gameObject.getObjectName().equals("WolkeBorder")) {
                    GameObject cloud = cloudMapping.get(gameObject);

                    // Überprüfen, ob sich der Slime über der Wolkenborder befindet
                    if (slime.getY() + slime.getHeight() > gameObject.getY()-85) {
                        // Slime ist über der Wolke, Wolke wird passabel
                        if (cloud != null) {
                            cloud.setPassable(true);
                        }
                    } else {
                        // Slime ist unter der Wolke, Wolke wird nicht passabel
                        if (cloud != null) {
                            cloud.setPassable(false);
                        }
                    }
                }

                // Entfernen von Wolken, die den Boden berühren
                if(gameObject.getColldingObjectName().equals("Ground")) {
                    gameObject.remove();
                    iterator.remove();
                }
            }
        }


        if(currentState == GameState.GAME_OVER) {


            int x = (int) ((screenSize.getWidth()));
            this.remove(scoreLabel);
            slime.remove();
            slime = new GameObject((x+100)/2, -100, 80,80,"src/main/java/Images/slime.png", true,true);
            slime.setHitboxDimensions(80,25);
            slime.setGamestate(GameState.IN_GAME);
            slime.setPlayable(true);
            slime.setSpeed(15);
            slime.setGravity(true);
            slime.setJumpTime(0.8f);
            slime.setObjectName("Slime");
            menuButton.setClicked(false);
            currentState = GameState.MENU;
            startButton.setClickable(true);
            exitButton.setClickable(true);
            scoreLabel.setText("Score: " + score);
            this.add(scoreLabel);
            if(this.score > this.Highscore) {
                this.Highscore = this.score;
            }
            HighScoreLabel.setText("HighScore: " + Highscore);
            this.add(HighScoreLabel);
            score=0;
            Iterator<GameObject> iterator = clouds.iterator();
            while(iterator.hasNext()) {
                GameObject gameObject = iterator.next();
                gameObject.updatePosition();

                gameObject.remove();
                iterator.remove();

            }



            GameObject cloud = new GameObject((x+100)/2, 30, 150, 150, "src/main/Java/Images/wolke.png", false, true);
            cloud.setObjectName("Wolke");
            cloud.setGravity(true);
            cloud.setGamestate(GameState.IN_GAME);

            GameObject cloudBorder = new GameObject((x+100)/2, -29 + 150, 150, 20, "", false, true);
            cloudBorder.setObjectName("WolkeBorder");
            cloudBorder.setGravity(true);
            cloudBorder.setGamestate(GameState.IN_GAME);

            cloudMapping.put(cloudBorder, cloud);

            clouds.add(cloud);
            clouds.add(cloudBorder);

            System.out.println(startButton.getGamestate());


        }

        repaint();
    }
    public void createClouds() {
        int i = rand.nextInt(1000);
        GameObject cloud = new GameObject(i+100, -20, 150, 150, "src/main/Java/Images/wolke.png", false, false);
        cloud.setObjectName("Wolke");
        cloud.setGravity(true);

        cloud.setGamestate(GameState.IN_GAME);

        GameObject cloudBorder = new GameObject(i+100, -18 + 150, 150, 20, "", true, false);
        cloudBorder.setObjectName("WolkeBorder");
        cloudBorder.setGravity(true);

        cloudBorder.setGamestate(GameState.IN_GAME);
        System.out.println(speed);

        cloudMapping.put(cloudBorder, cloud); // Speichert die Verknüpfung in der HashMap

        clouds.add(cloud);
        clouds.add(cloudBorder);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (currentState == GameState.MENU) {
            for (GameObject obj : GameObject.getAllObjects()) {
                if(obj.getGamestate() == GameState.MENU)
                    obj.draw(g, this);
            }
        } else if (currentState == GameState.IN_GAME) {
            for (GameObject obj : GameObject.getAllObjects()) {
                if(obj.getGamestate() == GameState.IN_GAME)
                    obj.draw(g, this);
            }
        } else if (currentState == GameState.GAME_OVER) {
            for (GameObject obj : GameObject.getAllObjects()) {
                if(obj.getGamestate() == GameState.GAME_OVER)
                    obj.draw(g, this);
            }
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Game");
        frame.setBackground(Color.CYAN);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(frameWidth,frameHeight);
        frame.setContentPane(new Game());
        frame.pack();
        frame.setVisible(true);
    }
}



