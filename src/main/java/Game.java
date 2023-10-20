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

    public Game() {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (int) ((screenSize.getWidth()));
        int y = (int) ((screenSize.getHeight()));
        this.setPreferredSize(new Dimension(x, y));
        this.timer = new Timer(16, this);  // 60 FPS


        exitButton = new GameObject(200, 200, 400, 400, "src/main/java/Images/ExitBUTTON.png", true);
        startButton = new GameObject(1100, 200, 400, 400, "src/main/java/Images/StartBUTTON.png", true);
        exitButton.setClickable(true);
        startButton.setClickable(true);


        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //goblinJumping.setMoveDirection(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
               // goblinJumping.setMoveDirection(e.getKeyCode(), false);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (GameObject obj : GameObject.getAllObjects()) { // Annahme, dass eine solche Methode existiert
                    if (obj.checkIfClicked(e) && obj.isClicked()) {
                        // Dein Code hier, z.B.
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

        if(exitButton.isClicked()) {
            exitButton.setClicked(false);
            System.exit(0);
        }


        if(startButton.isClicked()) {
            startButton.setClicked(false);

        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        startButton.draw(g,this);
        exitButton.draw(g,this);
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



