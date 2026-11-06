package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

public class WindowUtilTest {

    @Test
    public void centerOnScreenUsesTheScreenSize() {
        Toolkit toolkit = mock(Toolkit.class);
        when(toolkit.getScreenSize()).thenReturn(new Dimension(1000, 800));
        JPanel toCenter = new JPanel() {
            @Override
            public Toolkit getToolkit() {
                return toolkit;
            }
        };
        toCenter.setSize(200, 100);

        WindowUtil.centerOnScreen(toCenter);

        assertEquals(new Point(400, 350), toCenter.getLocation());

        // with nothing to center on it centers on the screen too
        toCenter.setLocation(0, 0);
        WindowUtil.centerOn(toCenter, null);
        assertEquals(new Point(400, 350), toCenter.getLocation());
    }

    @Test
    public void centerOnALargerContainer() {
        JPanel toCenter = new JPanel();
        toCenter.setSize(200, 100);

        WindowUtil.centerOn(toCenter, onScreenAt(50, 60, 600, 400));

        assertEquals(new Point(50 + 200, 60 + 150), toCenter.getLocation());
    }

    @Test
    public void aContainerSmallerThanTheOneToCenterGetsItsTopLeftCorner() {
        JPanel toCenter = new JPanel();
        toCenter.setSize(800, 600);

        WindowUtil.centerOn(toCenter, onScreenAt(50, 60, 600, 400));

        assertEquals(new Point(50, 60), toCenter.getLocation());
    }

    @Test
    public void centerOnParent() {
        JPanel parent = onScreenAt(10, 20, 300, 300);
        JPanel toCenter = new JPanel();
        parent.add(toCenter);
        toCenter.setSize(100, 100);

        WindowUtil.centerOnParent(toCenter);

        assertEquals(new Point(10 + 100, 20 + 100), toCenter.getLocation());
    }

    /** A container that reports a screen location without being shown */
    private static JPanel onScreenAt(int x, int y, int width, int height) {
        JPanel panel = new JPanel(null) {
            @Override
            public Point getLocationOnScreen() {
                return new Point(x, y);
            }
        };
        panel.setSize(width, height);
        return panel;
    }
}
