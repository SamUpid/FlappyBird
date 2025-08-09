package com.flappybird;

import javax.swing.*; // Importing Swing library for GUI components.

public class App {
    public static void main(String[] args) throws Exception {
        // Dimensions of the game window in pixels.
        int boardWidth = 360; // Width of the game window.
        int boardHeight = 640; // Height of the game window.

        // Create a JFrame to serve as the main game window.
        JFrame frame = new JFrame("Flappy Bird"); // Set the title of the window to "Flappy Bird".
        
        // Set the dimensions of the game window.
        frame.setSize(boardWidth, boardHeight);
        
        // Center the game window on the screen.
        frame.setLocationRelativeTo(null);

        // Prevent the user from resizing the game window.
        frame.setResizable(false);

        // Close the application when the user clicks the 'X' button on the window.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create an instance of the FlappyBird class, which extends JPanel (presumably the game logic and rendering).
        FlappyBird flappyBird = new FlappyBird();

        // Add the FlappyBird panel to the JFrame.
        frame.add(flappyBird);

        // Adjust the size of the window so that the content fits the specified dimensions without additional padding.
        frame.pack(); 

        // Request focus for the FlappyBird panel to ensure it receives keyboard inputs.
        flappyBird.requestFocus();

        // Make the game window visible to the user.
        frame.setVisible(true);
    }
}
