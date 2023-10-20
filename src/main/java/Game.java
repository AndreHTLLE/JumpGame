import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Game extends JPanel implements ActionListener {
    private final Timer timer;

    public static GameObject exitButton;
    public static GameObject startButton;
    public static int frameWidth=500;
    public static int frameHeight=100;
    public static JFrame frame;
    public static GameState currentState;

                    //GameState Enum
    //State hinzufügen für neue Bereiche des Spieles
    //Siehe protected void paintComponent(Graphics g) und public void actionPerformed(ActionEvent e) um zu erfahren wie neue States im Spiel aufgebaut sind.

    public Game() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (int) ((screenSize.getWidth()));
        int y = (int) ((screenSize.getHeight()));
        this.setPreferredSize(new Dimension(x, y));
        this.timer = new Timer(16, this);  // 60 FPS

        currentState = GameState.MENU;


        exitButton = new GameObject(200, 200, 400, 400, "src/main/java/Images/ExitBUTTON.png", true,true);
        startButton = new GameObject(1100, 200, 400, 400, "src/main/java/Images/StartBUTTON.png", true,true);
        exitButton.setClickable(true);
        exitButton.setGamestate(GameState.MENU);
        startButton.setClickable(true);
        startButton.setGamestate(GameState.MENU);



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

            /*for (GameObject obj : GameObject.getAllObjects()) {
                if (obj.isPlayable()  && obj.getGamestate() == currentState) {
                    obj.updatePosition();
                }
            }*/
        }

        if(currentState == GameState.IN_GAME) {

            for (GameObject obj : GameObject.getAllObjects()) {
                if (obj.isPlayable()  && obj.getGamestate() == currentState) {
                    obj.updatePosition();
                }
            }
        }

        if(currentState == GameState.GAME_OVER) {

        }

        repaint();
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
        frame = new JFrame("Menü");
        frame.setBackground(Color.CYAN);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(frameWidth,frameHeight);
        frame.setContentPane(new Game());
        frame.pack();
        frame.setVisible(true);
    }
}



