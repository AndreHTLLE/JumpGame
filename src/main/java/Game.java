import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class Game extends JPanel implements ActionListener {
    private final Timer timer;


    public Game() {
        this.setPreferredSize(new Dimension(800, 600));
        this.timer = new Timer(16, this);  // 60 FPS




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


        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GameObject Test");
        frame.setBackground(Color.CYAN);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(800,500);
        frame.setContentPane(new Game());
        frame.pack();
        frame.setVisible(true);
    }

    public static void startScreen() {



    }
}


