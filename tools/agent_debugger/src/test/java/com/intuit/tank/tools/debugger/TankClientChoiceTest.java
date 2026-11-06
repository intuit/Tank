package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TankClientChoiceTest {

    @Test
    public void showsItsNameAndKeepsTheClientClass() {
        TankClientChoice choice = new TankClientChoice("JDK Http Client", "com.intuit.tank.httpclientjdk.TankHttpClientJDK");
        assertEquals("JDK Http Client", choice.getName());
        assertEquals("JDK Http Client", choice.toString());
        assertEquals("com.intuit.tank.httpclientjdk.TankHttpClientJDK", choice.getClientClass());
    }
}
