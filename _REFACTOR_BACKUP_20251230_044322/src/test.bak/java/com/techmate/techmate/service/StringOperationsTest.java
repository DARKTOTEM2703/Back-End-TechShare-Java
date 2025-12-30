package com.techmate.techmate.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("String Operations and Manipulation Tests")
class StringOperationsTest {

    @Test
    @DisplayName("String concatenation should work")
    void stringConcatenationShouldWork() {
        String str1 = "Hello";
        String str2 = "World";
        String result = str1 + " " + str2;

        assertEquals("Hello World", result);
    }

    @Test
    @DisplayName("String length should be calculated correctly")
    void stringLengthShouldBeCalculatedCorrectly() {
        String str = "Test";
        assertEquals(4, str.length());
    }

    @Test
    @DisplayName("String substring should work")
    void stringSubstringShouldWork() {
        String str = "HelloWorld";
        assertEquals("Hello", str.substring(0, 5));
        assertEquals("World", str.substring(5));
    }

    @Test
    @DisplayName("String should support contains")
    void stringShouldSupportContains() {
        String str = "HelloWorld";
        assertTrue(str.contains("Hello"));
        assertFalse(str.contains("Goodbye"));
    }

    @Test
    @DisplayName("String should support startsWith")
    void stringShouldSupportStartsWith() {
        String str = "HelloWorld";
        assertTrue(str.startsWith("Hello"));
        assertFalse(str.startsWith("World"));
    }

    @Test
    @DisplayName("String should support endsWith")
    void stringShouldSupportEndsWith() {
        String str = "HelloWorld";
        assertTrue(str.endsWith("World"));
        assertFalse(str.endsWith("Hello"));
    }

    @Test
    @DisplayName("String should support toUpperCase")
    void stringShouldSupportToUpperCase() {
        String str = "hello";
        assertEquals("HELLO", str.toUpperCase());
    }

    @Test
    @DisplayName("String should support toLowerCase")
    void stringShouldSupportToLowerCase() {
        String str = "HELLO";
        assertEquals("hello", str.toLowerCase());
    }

    @Test
    @DisplayName("String should support replace")
    void stringShouldSupportReplace() {
        String str = "Hello World";
        String replaced = str.replace("World", "Java");
        assertEquals("Hello Java", replaced);
    }

    @Test
    @DisplayName("String should support trim")
    void stringShouldSupportTrim() {
        String str = "  Hello World  ";
        assertEquals("Hello World", str.trim());
    }

    @Test
    @DisplayName("String should support split")
    void stringShouldSupportSplit() {
        String str = "Hello,World,Java";
        String[] parts = str.split(",");
        assertEquals(3, parts.length);
        assertEquals("Hello", parts[0]);
        assertEquals("World", parts[1]);
        assertEquals("Java", parts[2]);
    }

    @Test
    @DisplayName("String equality should work correctly")
    void stringEqualityShouldWorkCorrectly() {
        String str1 = "Test";
        String str2 = "Test";
        String str3 = "test";

        assertEquals(str1, str2);
        assertNotEquals(str1, str3);
    }

    @Test
    @DisplayName("String indexOf should work")
    void stringIndexOfShouldWork() {
        String str = "HelloWorld";
        assertEquals(0, str.indexOf("Hello"));
        assertEquals(5, str.indexOf("World"));
        assertEquals(-1, str.indexOf("NotFound"));
    }

    @Test
    @DisplayName("String format should work")
    void stringFormatShouldWork() {
        String formatted = String.format("Hello %s", "World");
        assertEquals("Hello World", formatted);
    }

    @Test
    @DisplayName("String join should work")
    void stringJoinShouldWork() {
        String joined = String.join(",", "Hello", "World", "Java");
        assertEquals("Hello,World,Java", joined);
    }
}
