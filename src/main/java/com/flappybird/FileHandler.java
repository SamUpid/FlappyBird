package com.flappybird;

import java.io.*; // For file handling classes like ObjectInputStream, ObjectOutputStream, etc.
import java.util.*; // For utility classes, though not used in this code.

public class FileHandler {
    // Path to the file where the high score will be saved and loaded from.
    private static final String FILE_PATH = "gameData.ser";

    /**
     * Saves the high score to a file.
     * @param highScore The high score value to be saved.
     * 
     * The method uses ObjectOutputStream to serialize the high score and store it
     * in a file. If the file doesn't exist, it will be created.
     */
    public static void saveHighScore(double highScore) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(highScore); // Write the high score as a serialized object.
            System.out.println("High score saved successfully.");
        } catch (IOException e) {
            // Handles any errors that occur during file operations.
            System.out.println("Error saving high score: " + e.getMessage());
        }
    }

    /**
     * Loads the high score from a file.
     * @return The high score value, or 0 if no score is saved or an error occurs.
     * 
     * The method checks if the file exists. If it does, it reads the high score
     * using ObjectInputStream. If the file doesn't exist or an error occurs during
     * reading, it returns 0.
     */
    public static double loadHighScore() {
        File file = new File(FILE_PATH); // Create a File object for the high score file.
        
        // Check if the file exists. If not, return 0 as no high score is saved yet.
        if (!file.exists()) {
            return 0;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (double) ois.readObject(); // Read and return the high score from the file.
        } catch (IOException | ClassNotFoundException e) {
            // Handles errors that occur during file reading or deserialization.
            System.err.println("Error loading high score: " + e.getMessage());
            return 0; // Return 0 if an error occurs.
        }
    }
}

