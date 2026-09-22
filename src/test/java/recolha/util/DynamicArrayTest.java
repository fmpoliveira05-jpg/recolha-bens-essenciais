package recolha.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DynamicArrayTest {

    @Test
    void growsBeyondInitialCapacity() {
        DynamicArray<Integer> list = new DynamicArray<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        assertEquals(100, list.size());
        assertEquals(99, list.get(99));
    }

    @Test
    void insertAndRemoveKeepOrder() {
        DynamicArray<String> list = new DynamicArray<>();
        list.add("a");
        list.add("c");
        list.insertAt(1, "b");
        assertArrayEquals(new String[] {"a", "b", "c"}, list.toArray(String[]::new));

        assertEquals("a", list.removeAt(0));
        assertArrayEquals(new String[] {"b", "c"}, list.toArray(String[]::new));
    }

    @Test
    void searchesUseEquals() {
        DynamicArray<String> list = new DynamicArray<>();
        list.add(new String("x"));
        assertTrue(list.contains("x"));
        assertEquals(0, list.indexOf("x"));
        assertEquals(-1, list.indexOf("y"));
        assertEquals("x", list.find(s -> s.startsWith("x")));
        assertNull(list.find(s -> s.startsWith("z")));
    }

    @Test
    void toArrayReturnsIndependentCopy() {
        DynamicArray<String> list = new DynamicArray<>();
        list.add("a");
        String[] copy = list.toArray(String[]::new);
        copy[0] = "changed";
        assertEquals("a", list.get(0));
    }

    @Test
    void invalidPositionsAreRejected() {
        DynamicArray<String> list = new DynamicArray<>();
        assertTrue(list.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.insertAt(1, "a"));
        list.add("a");
        assertFalse(list.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> list.removeAt(1));
    }
}
