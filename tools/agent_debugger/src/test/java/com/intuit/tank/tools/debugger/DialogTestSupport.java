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

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Helpers for driving the debugger's dialogs without ever putting them on screen.
 */
final class DialogTestSupport {

    private DialogTestSupport() {
    }

    /**
     * A frame to own dialogs that is never shown. The dialogs center themselves on their owner, which asks for the
     * owner's location on screen; an unshown frame reports the origin instead of failing.
     */
    static JFrame unshownFrame() throws Exception {
        return onEdt(() -> new JFrame() {
            private static final long serialVersionUID = 1L;

            @Override
            public Point getLocationOnScreen() {
                return new Point(0, 0);
            }
        });
    }

    /** A non-standalone debugger frame, with no service URL, that is never shown (see {@link #unshownFrame()}). */
    static AgentDebuggerFrame unshownDebuggerFrame() throws Exception {
        return onEdt(() -> new AgentDebuggerFrame(false, null, null) {
            private static final long serialVersionUID = 1L;

            @Override
            public Point getLocationOnScreen() {
                return new Point(0, 0);
            }
        });
    }

    interface EdtTask {
        void run() throws Exception;
    }

    /** Runs the task on the event dispatch thread and rethrows anything it throws, assertion failures included. */
    static void onEdt(EdtTask task) throws Exception {
        onEdt(() -> {
            task.run();
            return null;
        });
    }

    /** Computes a value on the event dispatch thread and rethrows anything it throws. */
    static <T> T onEdt(Callable<T> task) throws Exception {
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Throwable> error = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                result.set(task.call());
            } catch (Throwable t) {
                error.set(t);
            }
        });
        Throwable t = error.get();
        if (t instanceof Exception e) {
            throw e;
        } else if (t instanceof Error e) {
            throw e;
        }
        return result.get();
    }

    /** Every component of the given type under the container, in depth-first order. */
    static <T extends Component> List<T> findAll(Container root, Class<T> type) {
        List<T> found = new ArrayList<>();
        collect(root, type, found);
        return found;
    }

    private static <T extends Component> void collect(Container root, Class<T> type, List<T> found) {
        for (Component c : root.getComponents()) {
            if (type.isInstance(c)) {
                found.add(type.cast(c));
            }
            if (c instanceof Container child) {
                collect(child, type, found);
            }
        }
    }

    /** The first component of the given type under the container that matches the predicate. */
    static <T extends Component> T find(Container root, Class<T> type, Predicate<T> matcher) {
        return findAll(root, type).stream().filter(matcher).findFirst()
                .orElseThrow(() -> new AssertionError("No matching " + type.getSimpleName()));
    }

    /** The button (or check box) whose label is the given text. */
    static <T extends AbstractButton> T button(Container root, Class<T> type, String text) {
        return find(root, type, b -> text.equals(b.getText()));
    }

    /** Waits up to the timeout for the condition to hold, failing the test if it never does. */
    static void waitFor(BooleanSupplier condition, long timeoutMillis, String description) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMillis;
        while (!condition.getAsBoolean()) {
            if (System.currentTimeMillis() > deadline) {
                fail("Timed out waiting for " + description);
            }
            Thread.sleep(5);
        }
        assertTrue(condition.getAsBoolean());
    }
}
