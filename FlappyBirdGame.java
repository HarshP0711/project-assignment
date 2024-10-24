import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class FlappyBirdGame extends JPanel implements ActionListener {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;
    private static final int BIRD_SIZE = 20;
    private static final int PIPE_WIDTH = 80;
    private static final int PIPE_GAP = 150;
    private static final int PIPE_SPEED = 5;
    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = 15;

    private int birdY = HEIGHT / 2;
    private int birdVelocity = 0;
    private int score = 0;
    private boolean gameOver = false;

    private ArrayList<Rectangle> pipes;
    private Random random;
    private Timer timer;

    public FlappyBirdGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.CYAN);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
                    jump();
                } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
                    resetGame();
                }
            }
        });

        pipes = new ArrayList<>();
        random = new Random();
        addPipe(); // Add the first pipe

        timer = new Timer(20, this);
        timer.start();
    }

    private void addPipe() {
        int pipeHeight = random.nextInt(HEIGHT - PIPE_GAP - 100) + 50; // Random height
        pipes.add(new Rectangle(WIDTH, 0, PIPE_WIDTH, pipeHeight)); // Top pipe
        pipes.add(new Rectangle(WIDTH, pipeHeight + PIPE_GAP, PIPE_WIDTH, HEIGHT - pipeHeight - PIPE_GAP)); // Bottom pipe
    }

    private void updateGame() {
        birdVelocity += GRAVITY; // Apply gravity
        birdY += birdVelocity;

        // Move pipes to the left
        for (int i = 0; i < pipes.size(); i++) {
            Rectangle pipe = pipes.get(i);
            pipe.x -= PIPE_SPEED;

            // Remove pipes that have gone off the screen
            if (pipe.x + PIPE_WIDTH < 0) {
                pipes.remove(i);
                i--; // Adjust index after removal
                score++; // Increment score
            }
        }

        // Add new pipes
        if (pipes.size() == 0 || pipes.get(pipes.size() - 1).x < WIDTH - 200) {
            addPipe();
        }

        // Check if the bird falls out of the window
        if (birdY > HEIGHT || birdY < 0) {
            gameOver = true;
        }
    }

    private void jump() {
        birdVelocity = -JUMP_STRENGTH; // Set upward velocity
    }

    private void checkCollisions() {
        Rectangle birdRect = new Rectangle(WIDTH / 2 - BIRD_SIZE / 2, birdY, BIRD_SIZE, BIRD_SIZE); // Bird rectangle
        for (Rectangle pipe : pipes) {
            if (birdRect.intersects(pipe)) {
                gameOver = true; // Collision detected
            }
        }
    }

    private void resetGame() {
        birdY = HEIGHT / 2; // Reset bird position
        birdVelocity = 0; // Reset bird velocity
        score = 0; // Reset score
        gameOver = false; // Reset game over state
        pipes.clear(); // Clear pipes
        addPipe(); // Add new pipe
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            updateGame();
            checkCollisions();
        }
        repaint(); // Repaint the game panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.YELLOW);
        g.fillRect(WIDTH / 2 - BIRD_SIZE / 2, birdY, BIRD_SIZE, BIRD_SIZE); // Draw the bird

        g.setColor(Color.GREEN);
        for (Rectangle pipe : pipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height); // Draw pipes
        }

        g.setColor(Color.BLACK);
        g.drawString("Score: " + score, 10, 20); // Draw score
        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 20)); // Set font for game over message
            g.drawString("Game Over! Press R to Restart", WIDTH / 8, HEIGHT / 2); // Game over message
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird Clone");
        FlappyBirdGame game = new FlappyBirdGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
