package com.techmate.techmate.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Enum and Constants Tests")
class EnumAndConstantsTest {

    enum TestStatus {
        ACTIVE, INACTIVE, PENDING
    }

    enum Priority {
        LOW(1), MEDIUM(2), HIGH(3);

        private final int level;

        Priority(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    @Test
    @DisplayName("Enum values should be retrievable")
    void enumValuesShouldBeRetrievable() {
        TestStatus status = TestStatus.ACTIVE;
        assertNotNull(status);
        assertEquals(TestStatus.ACTIVE, status);
    }

    @Test
    @DisplayName("Enum should support all values")
    void enumShouldSupportAllValues() {
        TestStatus[] values = TestStatus.values();
        assertEquals(3, values.length);
    }

    @Test
    @DisplayName("Enum values should be comparable")
    void enumValuesShouldBeComparable() {
        TestStatus status1 = TestStatus.ACTIVE;
        TestStatus status2 = TestStatus.ACTIVE;
        TestStatus status3 = TestStatus.INACTIVE;

        assertEquals(status1, status2);
        assertNotEquals(status1, status3);
    }

    @Test
    @DisplayName("Enum names should be retrievable")
    void enumNamesShouldBeRetrievable() {
        TestStatus status = TestStatus.PENDING;
        assertEquals("PENDING", status.name());
    }

    @Test
    @DisplayName("Enum should support valueOf")
    void enumShouldSupportValueOf() {
        TestStatus status = TestStatus.valueOf("ACTIVE");
        assertEquals(TestStatus.ACTIVE, status);
    }

    @Test
    @DisplayName("Enum with constructor should work")
    void enumWithConstructorShouldWork() {
        assertEquals(1, Priority.LOW.getLevel());
        assertEquals(2, Priority.MEDIUM.getLevel());
        assertEquals(3, Priority.HIGH.getLevel());
    }

    @Test
    @DisplayName("Enum ordinal should be consistent")
    void enumOrdinalShouldBeConsistent() {
        assertEquals(0, TestStatus.ACTIVE.ordinal());
        assertEquals(1, TestStatus.INACTIVE.ordinal());
        assertEquals(2, TestStatus.PENDING.ordinal());
    }

    @Test
    @DisplayName("Enum comparison should work with ordinal")
    void enumComparisonShouldWorkWithOrdinal() {
        assertTrue(TestStatus.ACTIVE.ordinal() < TestStatus.PENDING.ordinal());
        assertTrue(Priority.HIGH.getLevel() > Priority.LOW.getLevel());
    }

    @Test
    @DisplayName("Constants should be accessible")
    void constantsShouldBeAccessible() {
        final String CONSTANT_VALUE = "TEST_CONSTANT";
        assertEquals("TEST_CONSTANT", CONSTANT_VALUE);
    }

    @Test
    @DisplayName("Multiple enum instances should be independent")
    void multipleEnumInstancesShouldBeIndependent() {
        TestStatus status1 = TestStatus.ACTIVE;
        TestStatus status2 = TestStatus.INACTIVE;
        
        assertNotSame(status1, status2);
        assertNotEquals(status1, status2);
    }
}

