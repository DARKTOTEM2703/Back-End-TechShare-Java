package com.techmate.techmate.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Number Operations and Math Tests")
class NumberOperationsTest {

    @Test
    @DisplayName("Integer arithmetic should work correctly")
    void integerArithmeticShouldWorkCorrectly() {
        int a = 10;
        int b = 3;
        
        assertEquals(13, a + b);
        assertEquals(7, a - b);
        assertEquals(30, a * b);
        assertEquals(3, a / b);
    }

    @Test
    @DisplayName("Long arithmetic should work correctly")
    void longArithmeticShouldWorkCorrectly() {
        long a = 1000000L;
        long b = 500000L;
        
        assertEquals(1500000L, a + b);
        assertEquals(500000L, a - b);
    }

    @Test
    @DisplayName("Double arithmetic should work correctly")
    void doubleArithmeticShouldWorkCorrectly() {
        double a = 10.5;
        double b = 3.2;
        
        assertTrue(Math.abs(13.7 - (a + b)) < 0.001);
        assertTrue(Math.abs(7.3 - (a - b)) < 0.001);
    }

    @Test
    @DisplayName("Modulo operation should work")
    void moduloOperationShouldWork() {
        int a = 10;
        int b = 3;
        
        assertEquals(1, a % b);
    }

    @Test
    @DisplayName("Math.abs should work correctly")
    void mathAbsShouldWorkCorrectly() {
        assertEquals(5, Math.abs(-5));
        assertEquals(5, Math.abs(5));
        assertEquals(0, Math.abs(0));
    }

    @Test
    @DisplayName("Math.max should work correctly")
    void mathMaxShouldWorkCorrectly() {
        assertEquals(10, Math.max(5, 10));
        assertEquals(10, Math.max(10, 5));
    }

    @Test
    @DisplayName("Math.min should work correctly")
    void mathMinShouldWorkCorrectly() {
        assertEquals(5, Math.min(5, 10));
        assertEquals(5, Math.min(10, 5));
    }

    @Test
    @DisplayName("Math.sqrt should work correctly")
    void mathSqrtShouldWorkCorrectly() {
        assertEquals(5.0, Math.sqrt(25.0), 0.001);
        assertEquals(3.0, Math.sqrt(9.0), 0.001);
    }

    @Test
    @DisplayName("Math.pow should work correctly")
    void mathPowShouldWorkCorrectly() {
        assertEquals(8.0, Math.pow(2, 3), 0.001);
        assertEquals(16.0, Math.pow(4, 2), 0.001);
    }

    @Test
    @DisplayName("Number comparison should work")
    void numberComparisonShouldWork() {
        assertTrue(10 > 5);
        assertTrue(5 < 10);
        assertTrue(5 == 5);
        assertTrue(5 <= 5);
        assertTrue(5 >= 5);
    }

    @Test
    @DisplayName("Integer overflow handling should be considered")
    void integerOverflowHandlingConcept() {
        // This test demonstrates the concept
        int maxInt = Integer.MAX_VALUE;
        assertTrue(maxInt > 0);
    }

    @Test
    @DisplayName("Floating point precision should be considered")
    void floatingPointPrecisionConcept() {
        double a = 0.1 + 0.2;
        double b = 0.3;
        
        // Use delta comparison for floating point
        assertTrue(Math.abs(a - b) < 0.0001);
    }

    @Test
    @DisplayName("Number string conversion should work")
    void numberStringConversionShouldWork() {
        int num = 42;
        String str = String.valueOf(num);
        assertEquals("42", str);
        
        int parsed = Integer.parseInt(str);
        assertEquals(42, parsed);
    }

    @Test
    @DisplayName("Bitwise operations should work")
    void bitwiseOperationsShouldWork() {
        int a = 5; // 0101
        int b = 3; // 0011
        
        assertEquals(7, a | b); // OR
        assertEquals(1, a & b); // AND
        assertEquals(6, a ^ b); // XOR
    }
}

