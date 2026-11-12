package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2026 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JSplitPane;

/**
 * Remembers the main window's bounds and split pane divider positions between runs.
 */
public final class WindowState {

    private static final Preferences PREFS = Preferences.userNodeForPackage(WindowState.class);
    private static final String PREF_BOUNDS = "window.bounds";
    private static final String PREF_MAXIMIZED = "window.maximized";
    private static final String PREF_DIVIDER = "divider.";

    private static final Map<String, JSplitPane> splitPanes = new LinkedHashMap<>();

    private WindowState() {
    }

    /** Restores the pane's saved divider location, or uses defaultLocation, and remembers it on {@link #save} */
    public static void track(String key, JSplitPane pane, int defaultLocation) {
        pane.setDividerLocation(PREFS.getInt(PREF_DIVIDER + key, defaultLocation));
        splitPanes.put(key, pane);
    }

    /** Restores the frame's saved bounds if they are still on a connected screen */
    public static void restore(Frame frame) {
        String[] parts = PREFS.get(PREF_BOUNDS, "").split(",");
        if (parts.length == 4) {
            try {
                Rectangle bounds = new Rectangle(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                if (isOnScreen(bounds)) {
                    frame.setBounds(bounds);
                }
            } catch (NumberFormatException e) {
                // ignore bad saved bounds and keep the defaults
            }
        }
        if (PREFS.getBoolean(PREF_MAXIMIZED, false)) {
            frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
        }
    }

    public static void save(Frame frame) {
        boolean maximized = (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
        PREFS.putBoolean(PREF_MAXIMIZED, maximized);
        if (!maximized) {
            Rectangle b = frame.getBounds();
            PREFS.put(PREF_BOUNDS, b.x + "," + b.y + "," + b.width + "," + b.height);
        }
        splitPanes.forEach((key, pane) -> PREFS.putInt(PREF_DIVIDER + key, pane.getDividerLocation()));
    }

    private static boolean isOnScreen(Rectangle bounds) {
        if (GraphicsEnvironment.isHeadless()) {
            return false;
        }
        return Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
                .anyMatch(d -> d.getDefaultConfiguration().getBounds().intersects(bounds));
    }
}
