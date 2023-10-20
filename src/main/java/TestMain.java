import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TestMain extends JPanel implements ActionListener {
    private final Timer timer;
    private final GameObject goblinJumping;
    private final GameObject goblinFalling;

    public TestMain() {
        this.setPreferredSize(new Dimension(800, 600));
        this.timer = new Timer(16, this);  // 60 FPS

        // Erstelle GameObjects
        goblinJumping = new GameObject(100, 100, 50, 50, "src/main/java/Images/goblin_jumping.png", true, true);
        goblinJumping.setHitboxDimensions(23,23);
        goblinFalling = new GameObject(200, 200, 50, 50, "src/main/java/Images/wolke.png", false, true);

        goblinFalling.setClickable(true);
        goblinJumping.setPlayable(true);
        goblinJumping.setSpeed(1);


        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                goblinJumping.setMoveDirection(e.getKeyCode(), true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                goblinJumping.setMoveDirection(e.getKeyCode(), false);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (GameObject obj : GameObject.getAllObjects()) {
                    if (obj.checkIfClicked(e) && obj.isClicked()) {
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
        goblinJumping.updatePosition();

        if(goblinFalling.isClicked()) {
            goblinFalling.setClicked(false);
            goblinFalling.move(-1,1);
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Zeichne die GameObjects
        goblinJumping.draw(g, this);
        goblinFalling.draw(g, this);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GameObject Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(800,500);
        frame.setContentPane(new TestMain());
        frame.pack();
        frame.setVisible(true);
    }
}


