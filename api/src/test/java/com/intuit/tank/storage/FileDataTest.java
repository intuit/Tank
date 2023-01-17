package com.intuit.tank.storage;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileDataTest {

    @Test
    void testDefaultFileData() {
        FileData fd = new FileData();
        assertEquals(fd.getPath(), "");
        assertNull(fd.getFileName());
    }

    @Test
    void testSetPath() {
        FileData fd = new FileData();
        fd.setPath("target/storage");
        assertEquals(fd.getPath(), "target/storage");
    }

    @Test
    void testSetFileName() {
        FileData fd = new FileData();
        fd.setFileName("test.txt");
        assertEquals(fd.getFileName(), "test.txt");
    }

    @Test
    void testAddAttribute() {
        FileData fd = new FileData();
        fd.addAttribute("test_key", "test_value");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("test_key", "test_value");
        assertEquals(fd.getAttributes(), attributes);
    }

    @Test
    void testToString() {
        FileData fd = new FileData("target/storage", "test.txt");
        assertEquals(fd.toString(), "target/storage/test.txt");
    }

    @Test
    void testEquals() {
        FileData fd1 = new FileData("target/storage", "test.txt");
        FileData fd2 = new FileData("target/storage", "test.txt");
        assertTrue(fd1.equals(fd2));
    }

    @Test
    void testEqualsIncorrectObject() {
        FileData fd1 = new FileData("target/storage", "test.txt");
        String test = "test";
        assertFalse(fd1.equals(test));
    }

    @Test
    void testHashCode() {
        FileData fd = new FileData("target/storage", "test.txt");
        assertEquals(fd.hashCode(), -520765042);
    }
}
