package com.flappybird;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList; // For storing all the pipes in the game.
import java.util.Random; // For placing the pipes at random positions.
import javax.swing.*; // For GUI components and game rendering.

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    // Game board dimensions
    int boardWidth = 360;
    int boardHeight = 640;
    double highScore = 0; // Stores the highest score across game sessions.

    // Images for the game elements
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird properties
    int birdX = boardWidth / 8; // Initial horizontal position of the bird.
    int birdY = boardHeight / 2; // Initial vertical position of the bird.
    int birdWidth = 34; // Bird's width in pixel.
    int birdHeight = 24; // Bird's height.

    // Bird class to manage bird-related properties
    class Bird {
        int x = birdX; // Horizontal position.
        int y = birdY; // Vertical position.
        int height = birdHeight; // Height of the bird.
        int width = birdWidth; // Width of the bird.
        Image img; // Image representing the bird.

        Bird(Image img) {
            this.img = img; // Assign the bird image.
        }
    }

    // Pipe properties
    int pipeX = boardWidth; // Initial horizontal position of pipes.
    int pipeY = 0; // Topmost vertical position of pipes.
    int pipeWidth = 64; // Width of each pipe.
    int pipeHeight = 512; // Height of each pipe.

    // Pipe class to manage pipe properties
    class Pipe {
        int x = pipeX; // Horizontal position.
        int y = pipeY; // Vertical position.
        int width = pipeWidth; // Width of the pipe.
        int height = pipeHeight; // Height of the pipe.
        Image img; // Image representing the pipe.
        boolean passed = false; // Tracks if the bird has passed this pipe.

        Pipe(Image img) {
            this.img = img; // Assign the pipe image.
        }
    }

    // Game logic variables
    Bird bird; // Instance of the Bird class.
    Timer speedIncreaseTimer; // Timer for increasing game speed over time.
    int velocityX = -4; // Initial speed of pipes moving leftward.
    int minVelocityX = -10; // Maximum speed for pipes.
    int velocityY = 0; // Vertical speed of the bird.
    int gravity = 1; // Gravity effect on the bird's vertical movement.

    ArrayList<Pipe> pipes; // List to store all pipes in the game.
    Random random = new Random(); // For generating random pipe positions.

    Timer gameLoop; // Main game loop timer.
    Timer placePipesTimer; // Timer to place pipes periodically.
    boolean gameOver = false; // Tracks the game state.
    double score = 0; // Player's current score.
    boolean paused = false; // Tracks if the game is paused.

    // Constructor to initialize the game
    FlappyBird() {
        highScore = FileHandler.loadHighScore(); // Load the saved high score.

        setPreferredSize(new Dimension(boardWidth, boardHeight)); // Set the game window size.
        setFocusable(true); // Enable key input focus for this panel.
        addKeyListener(this); // Attach key listener for user input.

        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Initialize the bird and pipe list
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        int baseInterval = 1500; // Base interval for pipe placement at the initial speed.
        int minInterval = 500; // Minimum time between pipe placements.

        // Timer to gradually increase game speed
        speedIncreaseTimer = new Timer(20000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (velocityX > minVelocityX) { // Increase speed if it hasn't reached the maximum.
                    velocityX -= 1; // Increase the speed by decreasing velocityX.
                    int adjustedInterval = Math.max(baseInterval * -4 / velocityX, minInterval);
                    placePipesTimer.setDelay(adjustedInterval); // Adjust pipe placement frequency.
                }
            }
        });
        speedIncreaseTimer.start();

        // Timer to place pipes at regular intervals
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes(); // Create and add new pipes.
            }
        });
        placePipesTimer.start();

        // Main game loop timer
        gameLoop = new Timer(1000 / 60, this); // Runs at 60 FPS.
        gameLoop.start();
    }

    // Create and place pipes on the screen
    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2)); // Randomize pipe position.
        int openingSpace = boardHeight / 4; // Space between top and bottom pipes for the bird to pass through.

        Pipe topPipe = new Pipe(topPipeImg); // Create top pipe.
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg); // Create bottom pipe.
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace; // Position below the top pipe.
        pipes.add(bottomPipe);
    }

    // Paint the game elements on the screen
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear the screen.
        draw(g); // Draw the game elements.
    }

    // Draw game elements
    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null); // Background image.
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null); // Bird.

        // Draw all pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Display score and high score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString(gameOver ? "Game Over: " + (int) score : String.valueOf((int) score), 10, 35);
        g.drawString("High Score: " + (int) highScore, 10, 70);
    }

    // Update game elements
    public void move() {
        if (paused) return; // Pause game logic if paused.

        velocityY += gravity; // Apply gravity to the bird.
        bird.y += velocityY; // Update bird's vertical position.
        bird.y = Math.max(bird.y, 0); // Prevent bird from going off the top of the screen.

        // Update pipe positions and check for collisions
        for (Pipe pipe : pipes) {
            pipe.x += velocityX; // Move pipes to the left.

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // Increment score when bird passes a pipe.
            }

            if (collision(bird, pipe)) {
                gameOver = true; // End game if bird collides with a pipe.
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true; // End game if bird falls below the screen.
        }
    }

    // Check for collision between bird and pipe
    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
               a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move(); // Update game elements.
            repaint(); // Redraw the screen.
        } else {
            if (score > highScore) {
                highScore = score;
                FileHandler.saveHighScore(highScore); // Save high score if it was beaten.
            }
            placePipesTimer.stop();
            gameLoop.stop();
            speedIncreaseTimer.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9; // Bird jumps upward.

            if (gameOver) {
                // Reset game state
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;

                // Restart timers
                gameLoop.start();
                placePipesTimer.start();
                speedIncreaseTimer.start();
            }
        } else if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused; // Toggle pause state.
            if (paused) {
                placePipesTimer.stop();
                speedIncreaseTimer.stop();
            } else {
                placePipesTimer.start();
                speedIncreaseTimer.start();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used in this implementation.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used in this implementation.
    }


    // New methods for FlappyBirdTest to use:
    public double getScore() { return score; }
    public void applyGravity() { 
        velocityY += gravity;
         bird.y += velocityY;
          bird.y = Math.max(bird.y, 0); }
    public boolean checkCollisionWithPipe(int x, int y) { return collision(bird, pipes.get(0)); }
    public void passPipe() { for (Pipe pipe : pipes) { if (!pipe.passed && bird.x > pipe.x + pipe.width) { pipe.passed = true; score += 0.5; } } }
    public boolean checkGameOver() { return gameOver; }
    public boolean isGameOver() { return gameOver; }
    public void jump() { velocityY = -9; }
    public void movePipes() { for (Pipe pipe : pipes) { pipe.x += velocityX; } }

}
