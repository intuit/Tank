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

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name="DebuggerAppender", category="Core", elementType="appender", printObject=true)
public class DebuggerAppender extends AbstractAppender {

    private static volatile ArrayList<JTextArea> jTextAreaList = new ArrayList<JTextArea>();

    protected DebuggerAppender(String name, Layout<? extends Serializable> layout, Filter filter, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static DebuggerAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginElement("Layout") Layout<?> layout,
                                              @PluginElement("Filters") Filter filter,
                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {

        if (name == null) {
            LOGGER.error("No name provided for JTextAreaAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new DebuggerAppender(name, layout, filter, ignoreExceptions);
    }

    // Add the target JTextArea to be populated and updated by the logging information.
    public static void addTextArea(final JTextArea textArea) {
    	DebuggerAppender.jTextAreaList.add(textArea);
    }

    @Override
    public void append(LogEvent event) {
        final String message = new String(this.getLayout().toByteArray(event));

        // Append formatted message to text area using the Thread.
        try {
            SwingUtilities.invokeLater( () -> {
                for (JTextArea jTA : jTextAreaList){
                    try {
                        if (jTA != null) {
                            if (jTA.getText().length() == 0) {
                                jTA.setText(message);
                            } else {
                                jTA.append(message);
                            }
                        }
                    } catch (final Throwable t) {
                        System.out.println("Unable to append log to text area: "
                                + t.getMessage());
                    }
                }
            });
        } catch (final IllegalStateException e) {
            // ignore case when the platform hasn't yet been iniitialized
        }
    }
}
