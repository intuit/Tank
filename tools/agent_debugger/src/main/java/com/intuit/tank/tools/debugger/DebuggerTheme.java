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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;

/**
 * Installs the FlatLaf look and feel and keeps the RSyntaxTextArea editors in step with it. The light/dark choice is
 * remembered between runs.
 */
public final class DebuggerTheme {

    private static final Logger LOG = LogManager.getLogger(DebuggerTheme.class);
    private static final Preferences PREFS = Preferences.userNodeForPackage(DebuggerTheme.class);
    private static final String PREF_DARK = "theme.dark";
    private static final String CUSTOM_DEFAULTS_PACKAGE = "com.intuit.tank.tools.debugger.themes";
    private static final String LIGHT_EDITOR_THEME = "/org/fife/ui/rsyntaxtextarea/themes/idea.xml";
    private static final String DARK_EDITOR_THEME = "/org/fife/ui/rsyntaxtextarea/themes/dark.xml";

    /** Client property for a text area that paints selections in its current line color, as the script editor does */
    static final String SELECTION_AS_CURRENT_LINE = "tank.selectionAsCurrentLine";

    private DebuggerTheme() {
    }

    /** Sets up the look and feel. Call before any Swing component is created. */
    public static void install() {
        FlatLaf.registerCustomDefaultsSource(CUSTOM_DEFAULTS_PACKAGE, DebuggerTheme.class.getClassLoader());
        setup(isDark());
    }

    public static boolean isDark() {
        return PREFS.getBoolean(PREF_DARK, false);
    }

    /** Switches every open window between the light and dark themes */
    public static void setDark(boolean dark) {
        PREFS.putBoolean(PREF_DARK, dark);
        FlatAnimatedLafChange.showSnapshot();
        setup(dark);
        FlatLaf.updateUI();
        for (Window window : Window.getWindows()) {
            applyToTree(window);
        }
        FlatAnimatedLafChange.hideSnapshotWithAnimation();
    }

    private static void setup(boolean dark) {
        if (dark) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
    }

    /** Applies the editor color scheme that matches the current look and feel */
    public static void apply(RSyntaxTextArea textArea) {
        String path = FlatLaf.isLafDark() ? DARK_EDITOR_THEME : LIGHT_EDITOR_THEME;
        try (InputStream in = DebuggerTheme.class.getResourceAsStream(path)) {
            Theme.load(in).apply(textArea);
        } catch (IOException | RuntimeException e) {
            LOG.warn("Error loading editor theme " + path + ": " + e);
        }
        // the script editor is disabled to stop editing, so keep its text readable
        textArea.setDisabledTextColor(textArea.getForeground());
        if (Boolean.TRUE.equals(textArea.getClientProperty(SELECTION_AS_CURRENT_LINE))) {
            textArea.setSelectionColor(textArea.getCurrentLineHighlightColor());
        }
    }

    /** Keeps a component that isn't in any window's tree, such as a popup menu, in step with theme changes */
    public static void updateOnThemeChange(Component component) {
        UIManager.addPropertyChangeListener(e -> {
            if ("lookAndFeel".equals(e.getPropertyName())) {
                SwingUtilities.updateComponentTreeUI(component);
            }
        });
    }

    /**
     * @return a one pixel line in the theme's border color along the bottom, with padding inside it. Set it from
     *         updateUI() so the color follows theme changes.
     */
    public static Border bottomLine(int top, int left, int bottom, int right) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, color("Component.borderColor", Color.LIGHT_GRAY)),
                BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    /** @return the look and feel color for key, or fallback when the look and feel doesn't define it */
    public static Color color(String key, Color fallback) {
        Color c = UIManager.getColor(key);
        return c != null ? c : fallback;
    }

    private static void applyToTree(Component component) {
        if (component instanceof RSyntaxTextArea textArea) {
            apply(textArea);
        } else if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyToTree(child);
            }
        }
    }
}
