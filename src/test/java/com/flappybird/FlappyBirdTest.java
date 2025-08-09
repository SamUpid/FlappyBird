package com.flappybird;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FlappyBirdTest {
    
    private FlappyBird game;

    @BeforeEach
    void setUp() {
        // Initialize the game before each test
        game = new FlappyBird();
    }

    @Test
    void testInitialScore() {
        // Test that the initial score is 0
        assertEquals(0, game.getScore(), "Initial score should be 0");
    }

    @Test
    void testBirdJump() {
        // Test that when the bird jumps, its velocityY is set correctly
        game.jump();
        assertEquals(-9, game.velocityY, "Bird's vertical velocity should be -9 after jump");
    }

    @Test
    void testApplyGravity() {
        // Test that gravity is applied to the bird's vertical velocity
        int initialVelocityY = game.velocityY;
        game.applyGravity();
        assertTrue(game.velocityY > initialVelocityY, "Bird's velocityY should increase due to gravity");
    }


    @Test
    void testGameOverOnCollision() {
        // Create a pipe and place it at the bird's position to simulate a collision
        FlappyBird.Pipe pipe = game.new Pipe(game.topPipeImg);
        pipe.x = game.bird.x + game.bird.width;  // Position the pipe at bird's right
        pipe.y = game.bird.y;
    
        // Add pipe to the game
        game.pipes.add(pipe);
    
        // Simulate a few game loops to move the pipes and check for collision
        game.move(); // Simulate moving pipes and checking collisions
    
        // Test if the game is over after the collision
        assertTrue(game.isGameOver(), "Game should be over after bird collides with the pipe");
    }
   
    
}
