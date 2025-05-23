package com.intuit.tank.integration_tests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleIT {

    @Test
    public void testAddition() {
        // Arrange
        int a = 5;
        int b = 3;
        
        // Act
        int result = a + b;
        
        // Assert
        assertEquals(8, result, "5 + 3 should equal 8");
    }
    
    @Test
    public void testSubtraction() {
        // Arrange
        int a = 10;
        int b = 4;
        
        // Act
        int result = a - b;
        
        // Assert
        assertEquals(6, result, "10 - 4 should equal 6");
    }
    
}
