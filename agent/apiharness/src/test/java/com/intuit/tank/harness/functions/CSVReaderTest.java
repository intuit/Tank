package com.intuit.tank.harness.functions;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class CSVReaderTest {

    @AfterEach
    public void init(){
        CSVReader.reset();
    }
    @Test
    public void testCSVReader_1() {
        CSVReader instance = CSVReader.getInstance("testFile");
        assertNull(instance.getNextLine(false));
    }

    @Test
    public void testGetNextLineReadsEachLineThenNull() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            CSVReader reader = CSVReader.getInstance(files.write("data.csv", "One,1,I\nTwo,2,II\n"));

            assertArrayEquals(new String[] { "One", "1", "I" }, reader.getNextLine(false));
            assertArrayEquals(new String[] { "Two", "2", "II" }, reader.getNextLine(false));
            assertNull(reader.getNextLine(false));
        }
    }

    @Test
    public void testGetNextLineLoopsBackToFirstLine() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            CSVReader reader = CSVReader.getInstance(files.write("data.csv", "One,1\nTwo,2\n"));

            reader.getNextLine(true);
            reader.getNextLine(true);
            assertArrayEquals(new String[] { "One", "1" }, reader.getNextLine(true));
        }
    }

    @Test
    public void testGetNextLineLoopingOverAnEmptyFileReturnsNull() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            CSVReader reader = CSVReader.getInstance(files.write("empty.csv", ""));

            assertNull(reader.getNextLine(true));
        }
    }

    @Test
    public void testGetNextLineReturnsCopy() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            CSVReader reader = CSVReader.getInstance(files.write("data.csv", "One,1\n"));

            // callers null out the columns they consume; that must not change the cached line
            reader.getNextLine(true)[0] = null;
            assertArrayEquals(new String[] { "One", "1" }, reader.getNextLine(true));
        }
    }

    @Test
    public void testGetInstanceCachesReaderPerFile() throws Exception {
        try (TestDataFiles files = new TestDataFiles()) {
            String fileName = files.write("data.csv", "One,1\nTwo,2\n");
            CSVReader reader = CSVReader.getInstance(fileName);
            reader.getNextLine(false);

            // the second lookup shares the reader, so it continues where the first left off
            assertSame(reader, CSVReader.getInstance(fileName));
            assertArrayEquals(new String[] { "Two", "2" }, CSVReader.getInstance(fileName).getNextLine(false));
        }
    }
}
