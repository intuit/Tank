package com.intuit.tank.harness.functions;

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
        instance.getNextLine(false);
    }
}
