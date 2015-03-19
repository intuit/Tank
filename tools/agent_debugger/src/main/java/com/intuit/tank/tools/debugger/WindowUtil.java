/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * FrameUtils
 * 
 * @author dangleton
 * 
 */
public class WindowUtil {

    private WindowUtil() {
    }

    /**
     * 
     * @param toCenter
     */
    public static void centerOnScreen(Container toCenter) {
        Dimension dim = toCenter.getToolkit().getScreenSize();
        Rectangle abounds = toCenter.getBounds();
        toCenter.setLocation((dim.width - abounds.width) / 2,
                (dim.height - abounds.height) / 2);
    }

    /**
     * 
     * @param toCenter
     *            the container to center
     * @param parent
     *            the parent to center on.
     */
    public static void centerOnParent(Container toCenter) {
        centerOn(toCenter, toCenter.getParent());
    }

    /**
     * 
     * @param toCenter
     *            the container to center
     * @param centerOn
     *            the container to center on.
     */
    public static void centerOn(Container toCenter, Container centerOn) {
        int x;
        int y;

        if (centerOn == null) {
            centerOnScreen(toCenter);
        } else {
            Point topLeft = centerOn.getLocationOnScreen();
            Dimension parentSize = centerOn.getSize();

            Dimension mySize = toCenter.getSize();

            if (parentSize.width > mySize.width) {
                x = ((parentSize.width - mySize.width) / 2) + topLeft.x;
            } else {
                x = topLeft.x;
            }
            if (parentSize.height > mySize.height) {
                y = ((parentSize.height - mySize.height) / 2) + topLeft.y;
            } else {
                y = topLeft.y;
            }
            toCenter.setLocation(x, y);
        }
    }
}
