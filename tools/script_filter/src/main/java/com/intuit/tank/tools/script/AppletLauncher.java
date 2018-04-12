/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.script;

/*
 * #%L
 * script-filter
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

/**
 * AppletLauncher
 * 
 * @author dangleton
 * 
 */
public class AppletLauncher extends JApplet {

    private static final long serialVersionUID = 1L;
    private ScriptFilterRunner frame;

    // Called when this applet is loaded into the browser.
    @Override
    public void init() {
        // Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait( () -> {
                createGUI();
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
        }
    }

    @Override
    public void start() {
        frame.setVisible(true);
        frame.requestFocus();
    }

    @Override
    public void stop() {
        frame.setVisible(false);
    }

    private void createGUI() {
        try {
            System.out.println("Starting... codebase = " + getCodeBase());
            String codeBase = getCodeBase().toString();
            String baseUrl = codeBase.substring(0, codeBase.indexOf('/', 10));
            String context = getParameter("rootContext");
            frame = new ScriptFilterRunner(false, baseUrl + context);
        } catch (Exception t) {
            System.out.println("ERROR: " + t.toString());
            t.printStackTrace();
        }
    }
}
