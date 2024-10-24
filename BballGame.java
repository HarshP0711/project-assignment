import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BballGame extends JFrame {
    private int score = 0;
    private int timeLeft = 45; // 45 seconds
    private int netX = 150;     // Initial net position
    private int netY = 100;     // Y position of the hoop
    private boolean ballThrown = false;
    private int ballX = 200, ballY = 400;
    private int ballDiameter = 30;
    private Timer netTimer, gameTimer;
    private JPanel gamePanel;

    public BballGame() {
        setTitle("Basketball Game");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new GamePanel();
        add(gamePanel);

        // Timer for moving the net
        netTimer = new Timer(100, new ActionListener() {
            int direction = 1;

            @Override
            public void actionPerformed(ActionEvent e) {
                netX += direction * 5;
                if (netX > getWidth() - 100 || netX < 0) { // Adjust to backboard width
                    direction *= -1;
                }
                gamePanel.repaint();
            }
        });
        netTimer.start();

        // Timer for game countdown
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                if (timeLeft <= 0) {
                    gameOver();
                }
                gamePanel.repaint();
            }
        });
        gameTimer.start();

        // Mouse listener to simulate throwing the ball
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!ballThrown) {
                    ballThrown = true;
                    throwBall();
                }
            }
        });

        setVisible(true);
    }

    // Simulate throwing the ball
    private void throwBall() {
        new Thread(() -> {
            while (ballThrown && ballY > netY - 30) {
                ballY -= 5;
                gamePanel.repaint();

                // Check if the ball passes through the hoop
                if (ballX > netX + 40 && ballX < netX + 80 && ballY <= netY + 30) {
                    score++;
                    ballThrown = false;
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            resetBall();
        }).start();
    }

    private void resetBall() {
        ballX = 200;
        ballY = 400;
        ballThrown = false;
    }

    // Game over logic
    private void gameOver() {
        netTimer.stop();
        gameTimer.stop();
        JOptionPane.showMessageDialog(null, "Time's up! Your score: " + score);
        System.exit(0);
    }

    // GamePanel to draw components
    class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw the backboard
            g.setColor(Color.WHITE);
            g.fillRect(netX, netY - 40, 100, 40); // Backboard

            // Draw the rim
            g.setColor(Color.RED);
            g.drawOval(netX + 40, netY, 40, 10); // Rim

            // Draw the net with lines
            g.setColor(Color.WHITE);
            for (int i = 0; i < 5; i++) {
                int x1 = netX + 45 + i * 10;  // Start at different points along the rim
                int y1 = netY + 5;            // From the bottom of the rim
                int x2 = netX + 50 + (i - 2) * 10;  // Move lines toward the center at the bottom
                int y2 = netY + 30;           // Extend downwards to simulate net
                g.drawLine(x1, y1, x2, y2);   // Draw each line
            }

            // Draw the ball
            g.setColor(Color.ORANGE);
            g.fillOval(ballX, ballY, ballDiameter, ballDiameter);

            // Draw score and time
            g.setColor(Color.BLACK);
            g.drawString("Score: " + score, 10, 20);
            g.drawString("Time Left: " + timeLeft, 300, 20);
        }
    }

    public static void main(String[] args) {
        new BballGame();
    }
}





