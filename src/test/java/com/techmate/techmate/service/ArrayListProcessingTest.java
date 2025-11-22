package com.techmate.techmate.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Array and List Processing Tests")
class ArrayListProcessingTest {

    @Test
    @DisplayName("Array creation and access should work")
    void arrayCreationAndAccessShouldWork() {
        int[] array = { 1, 2, 3, 4, 5 };
        assertEquals(5, array.length);
        assertEquals(1, array[0]);
        assertEquals(5, array[4]);
    }

    @Test
    @DisplayName("Array initialization with values")
    void arrayInitializationWithValues() {
        String[] names = { "Alice", "Bob", "Charlie" };
        assertEquals(3, names.length);
        assertEquals("Alice", names[0]);
    }

    @Test
    @DisplayName("List creation from array should work")
    void listCreationFromArrayShouldWork() {
        Integer[] array = { 1, 2, 3 };
        List<Integer> list = Arrays.asList(array);

        assertEquals(3, list.size());
        assertTrue(list.contains(2));
    }

    @Test
    @DisplayName("List operations should be correct")
    void listOperationsShouldBeCorrect() {
        List<String> list = new java.util.ArrayList<>();
        list.add("first");
        list.add("second");
        list.add("third");

        assertEquals(3, list.size());
        assertEquals("first", list.get(0));
        assertEquals("third", list.get(2));
    }

    @Test
    @DisplayName("List removal should work")
    void listRemovalShouldWork() {
        List<String> list = new java.util.ArrayList<>();
        list.add("item1");
        list.add("item2");
        list.add("item3");

        list.remove("item2");
        assertEquals(2, list.size());
        assertFalse(list.contains("item2"));
    }

    @Test
    @DisplayName("List iteration should work")
    void listIterationShouldWork() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        int sum = 0;

        for (Integer item : list) {
            sum += item;
        }

        assertEquals(15, sum);
    }

    @Test
    @DisplayName("Array sorting should work")
    void arraySortingShouldWork() {
        int[] array = { 5, 2, 8, 1, 9 };
        Arrays.sort(array);

        assertEquals(1, array[0]);
        assertEquals(9, array[4]);
    }

    @Test
    @DisplayName("Array binary search should work")
    void arrayBinarySearchShouldWork() {
        int[] array = { 1, 2, 3, 4, 5 };
        int index = Arrays.binarySearch(array, 3);

        assertEquals(2, index);
    }

    @Test
    @DisplayName("List stream filtering should work")
    void listStreamFilteringShouldWork() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> filtered = list.stream()
                .filter(n -> n > 2)
                .collect(java.util.stream.Collectors.toList());

        assertEquals(3, filtered.size());
    }

    @Test
    @DisplayName("List stream mapping should work")
    void listStreamMappingShouldWork() {
        List<String> list = Arrays.asList("hello", "world");
        List<String> uppercase = list.stream()
                .map(String::toUpperCase)
                .collect(java.util.stream.Collectors.toList());

        assertEquals("HELLO", uppercase.get(0));
        assertEquals("WORLD", uppercase.get(1));
    }

    @Test
    @DisplayName("Array equals should work")
    void arrayEqualsShouldWork() {
        int[] array1 = { 1, 2, 3 };
        int[] array2 = { 1, 2, 3 };
        int[] array3 = { 1, 2, 4 };

        assertTrue(Arrays.equals(array1, array2));
        assertFalse(Arrays.equals(array1, array3));
    }

    @Test
    @DisplayName("Array fill should work")
    void arrayFillShouldWork() {
        int[] array = new int[5];
        Arrays.fill(array, 10);

        for (int value : array) {
            assertEquals(10, value);
        }
    }

    @Test
    @DisplayName("List contains all should work")
    void listContainsAllShouldWork() {
        List<String> list1 = Arrays.asList("a", "b", "c");
        List<String> list2 = Arrays.asList("a", "b");

        assertTrue(list1.containsAll(list2));
        assertFalse(list2.containsAll(list1));
    }
}
