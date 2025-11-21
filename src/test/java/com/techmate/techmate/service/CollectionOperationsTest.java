package com.techmate.techmate.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Map and Collection Operations Tests")
class CollectionOperationsTest {

    @Test
    @DisplayName("HashMap should support basic operations")
    void hashMapShouldSupportBasicOperations() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        assertEquals("value1", map.get("key1"));
        assertEquals(2, map.size());
    }

    @Test
    @DisplayName("HashMap should handle null values")
    void hashMapShouldHandleNullValues() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", null);

        assertTrue(map.containsKey("key1"));
        assertNull(map.get("key1"));
    }

    @Test
    @DisplayName("HashMap should support removal")
    void hashMapShouldSupportRemoval() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.remove("key1");

        assertFalse(map.containsKey("key1"));
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("HashMap should support containsKey")
    void hashMapShouldSupportContainsKey() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        assertTrue(map.containsKey("key1"));
        assertFalse(map.containsKey("key2"));
    }

    @Test
    @DisplayName("HashMap should support iteration")
    void hashMapShouldSupportIteration() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        int count = 0;
        for (String key : map.keySet()) {
            count++;
            assertNotNull(map.get(key));
        }
        assertEquals(2, count);
    }

    @Test
    @DisplayName("HashMap should clear all entries")
    void hashMapShouldClearAllEntries() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.clear();

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("HashMap should handle duplicate keys")
    void hashMapShouldHandleDuplicateKeys() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key1", "value2");

        assertEquals("value2", map.get("key1"));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("HashMap values method should work")
    void hashMapValuesMethodShouldWork() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");

        assertEquals(2, map.values().size());
    }

    @Test
    @DisplayName("HashMap entrySet should be accessible")
    void hashMapEntrySetShouldBeAccessible() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        assertEquals(1, map.entrySet().size());
    }

    @Test
    @DisplayName("HashMap should support getOrDefault")
    void hashMapShouldSupportGetOrDefault() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");

        assertEquals("value1", map.getOrDefault("key1", "default"));
        assertEquals("default", map.getOrDefault("key2", "default"));
    }
}

