package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static com.intuit.tank.tools.debugger.DialogTestSupport.waitFor;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InfiniteProgressPanel is a lightweight component, so these tests run headless.
 */
public class InfiniteProgressPanelTest {

    private static final long TIMEOUT = 5000;

    private InfiniteProgressPanel panel;

    @AfterEach
    public void stopAnimation() throws InterruptedException {
        if (panel != null) {
            Thread animation = panel.animation;
            panel.interrupt();
            if (animation != null) {
                animation.join(TIMEOUT);
                assertFalse(animation.isAlive());
            }
        }
    }

    @Test
    public void testDefaults() {
        panel = new InfiniteProgressPanel();
        assertEquals("", panel.getText());
        assertEquals(14, panel.barsCount);
        assertEquals(0.70f, panel.shield);
        assertEquals(15.0f, panel.fps);
        assertEquals(300, panel.rampDelay);
        assertNotNull(panel.hints);
        assertFalse(panel.started);
    }

    @Test
    public void testOutOfRangeSettingsFallBackToSaneValues() {
        panel = new InfiniteProgressPanel("Loading", -1, -0.5f, 0.0f, -10);
        assertEquals("Loading", panel.getText());
        assertEquals(14, panel.barsCount);
        assertEquals(0.0f, panel.shield);
        assertEquals(15.0f, panel.fps);
        assertEquals(0, panel.rampDelay);

        panel = new InfiniteProgressPanel("Loading", 8, 0.5f, 30.0f, 100);
        assertEquals(8, panel.barsCount);
        assertEquals(0.5f, panel.shield);
        assertEquals(30.0f, panel.fps);
        assertEquals(100, panel.rampDelay);
    }

    @Test
    public void testSetText() {
        panel = new InfiniteProgressPanel("one", 10);
        panel.setText("two");
        assertEquals("two", panel.getText());
    }

    @Test
    public void testStartRampsUpAndStopRampsDownAndHides() throws InterruptedException {
        panel = new InfiniteProgressPanel("Working", 6, 0.7f, 100.0f, 50);
        panel.setSize(200, 200);
        panel.start();
        assertTrue(panel.isVisible());
        assertEquals(1, panel.getMouseListeners().length);
        assertEquals(6, panel.ticker.length);

        waitFor(() -> panel.started && panel.alphaLevel == 255, TIMEOUT, "the ramp up to finish");
        // once fully shown, the ticker turns
        double startX = panel.ticker[0].getBounds2D().getCenterX();
        waitFor(() -> panel.ticker[0].getBounds2D().getCenterX() != startX, TIMEOUT, "the ticker to turn");

        Thread rampUp = panel.animation;
        panel.stop();
        assertNotSame(rampUp, panel.animation);
        // the animator clears started, then hides the panel, then removes its listener: wait for all three
        waitFor(() -> !panel.started && !panel.isVisible() && panel.getMouseListeners().length == 0, TIMEOUT,
                "the ramp down to finish");
        assertEquals(0, panel.alphaLevel);
        rampUp.join(TIMEOUT);
        assertFalse(rampUp.isAlive());
    }

    @Test
    public void testZeroRampDelayShowsAtFullOpacityAtOnceAndStopsOnItsOwn() throws InterruptedException {
        panel = new InfiniteProgressPanel("", 4, 0.7f, 100.0f, 0);
        panel.setSize(100, 100);
        panel.start();
        waitFor(() -> panel.started, TIMEOUT, "the animation to start");
        assertEquals(255, panel.alphaLevel);
        // with nothing to ramp, the ticker turns straight away
        double startX = panel.ticker[0].getBounds2D().getCenterX();
        waitFor(() -> panel.ticker[0].getBounds2D().getCenterX() != startX, TIMEOUT, "the ticker to turn");

        Thread rampUp = panel.animation;
        panel.stop();
        // the ramp down ends by itself, without an interrupt
        // the animator clears started, then hides the panel, then removes its listener: wait for all three
        waitFor(() -> !panel.started && !panel.isVisible() && panel.getMouseListeners().length == 0, TIMEOUT,
                "the ramp down to finish");
        assertEquals(0, panel.alphaLevel);
        panel.animation.join(TIMEOUT);
        assertFalse(panel.animation.isAlive());
        rampUp.join(TIMEOUT);
    }

    @Test
    public void testPaintsTextWithoutAFontOfItsOwn() throws InterruptedException {
        panel = new InfiniteProgressPanel("Working", 6, 0.7f, 100.0f, 0);
        panel.setSize(200, 200);
        panel.setFont(null);
        panel.start();
        waitFor(() -> panel.started, TIMEOUT, "the animation to start");
        Thread animation = panel.animation;
        panel.interrupt();
        animation.join(TIMEOUT);

        // standalone, with no frame to inherit a font from, the text is drawn with the graphics' font
        assertFalse(isBlank(paint(panel)));
    }

    @Test
    public void testInterruptHidesImmediately() throws InterruptedException {
        panel = new InfiniteProgressPanel("Working", 6, 0.7f, 100.0f, 50);
        panel.setSize(200, 200);
        panel.start();
        Thread animation = panel.animation;
        panel.interrupt();
        assertNull(panel.animation);
        assertFalse(panel.isVisible());
        assertEquals(0, panel.getMouseListeners().length);
        animation.join(TIMEOUT);
        assertFalse(animation.isAlive());
    }

    @Test
    public void testStopAndInterruptDoNothingWhenNotStarted() {
        panel = new InfiniteProgressPanel();
        panel.stop();
        panel.interrupt();
        assertNull(panel.animation);
        assertTrue(panel.isVisible());
    }

    @Test
    public void testPaintsNothingUntilStarted() {
        panel = new InfiniteProgressPanel("Working");
        panel.setSize(200, 200);
        assertTrue(isBlank(paint(panel)));
    }

    @Test
    public void testPaintsTheShieldTickerAndText() throws InterruptedException {
        panel = new InfiniteProgressPanel("Working", 6, 0.7f, 100.0f, 0);
        panel.setSize(200, 200);
        // the font normally comes from the frame whose glass pane this is
        panel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        panel.setForeground(Color.BLACK);
        panel.start();
        waitFor(() -> panel.started, TIMEOUT, "the animation to start");
        // stop the ticker turning so that successive paints are comparable
        Thread animation = panel.animation;
        panel.interrupt();
        animation.join(TIMEOUT);
        assertTrue(panel.started);

        BufferedImage withText = paint(panel);
        assertFalse(isBlank(withText));
        panel.setText("");
        BufferedImage withoutText = paint(panel);
        assertFalse(isBlank(withoutText));
        assertFalse(Arrays.equals(pixels(withText), pixels(withoutText)), "the text should be drawn");
        panel.setText(null);
        assertArrayEquals(pixels(withoutText), pixels(paint(panel)));
    }

    @Test
    public void testMouseEventsAreSwallowed() {
        panel = new InfiniteProgressPanel();
        MouseEvent e = new MouseEvent(panel, MouseEvent.MOUSE_CLICKED, 0, 0, 1, 1, 1, false);
        panel.mouseClicked(e);
        panel.mousePressed(e);
        panel.mouseReleased(e);
        panel.mouseEntered(e);
        panel.mouseExited(e);
        assertFalse(e.isConsumed());
        assertFalse(panel.started);
    }

    private static BufferedImage paint(InfiniteProgressPanel panel) {
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            panel.paintComponent(g);
        } finally {
            g.dispose();
        }
        return image;
    }

    private static int[] pixels(BufferedImage image) {
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    private static boolean isBlank(BufferedImage image) {
        return Arrays.stream(pixels(image)).allMatch(p -> p == 0);
    }
}
