package com.intuit.tank.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TankCookieTest {

    @Test
    public void testTankCookieDomain() {
        TankCookie.Builder instance = TankCookie.builder();
        instance.withDomain("testDomain");
        TankCookie cookie = instance.build();
        String actual = cookie.getDomain();
        assertEquals("testDomain", actual);
    }

    @Test
    public void testTankCookieName() {
        TankCookie.Builder instance = TankCookie.builder();
        instance.withName("testName");
        TankCookie cookie = instance.build();
        String actual = cookie.getName();
        assertEquals("testName", actual);
    }

    @Test
    public void testTankCookieValue() {
        TankCookie.Builder instance = TankCookie.builder();
        instance.withValue("testValue");
        TankCookie cookie = instance.build();
        String actual = cookie.getValue();
        assertEquals("testValue", actual);
    }

    @Test
    public void testTankCookiePath() {
        TankCookie.Builder instance = TankCookie.builder();
        instance.withPath("testPath");
        TankCookie cookie = instance.build();
        String actual = cookie.getPath();
        assertEquals("testPath", actual);
    }
}
