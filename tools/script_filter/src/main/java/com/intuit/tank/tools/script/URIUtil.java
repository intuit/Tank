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

/**
 * URIUtil
 * 
 * @author dangleton
 * 
 */
public class URIUtil {
    /**
     * Encode a URI path.
     * 
     * @param path
     *            The path the encode
     * @param buf
     *            StringBuffer to encode path into (or null)
     * @return The StringBuffer or null if no substitutions required.
     */
    public static StringBuffer encodePath(StringBuffer buf, String path) {
        if (buf == null) {
            loop: for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                switch (c) {
                case '%':
                case '?':
                case ';':
                case '#':
                case ' ':
                    buf = new StringBuffer(path.length() << 1);
                    break loop;
                }
            }
            if (buf == null)
                return null;
        }

        synchronized (buf) {
            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                switch (c) {
                case '%':
                    buf.append("%25");
                    continue;
                case '?':
                    buf.append("%3F");
                    continue;
                case ';':
                    buf.append("%3B");
                    continue;
                case '#':
                    buf.append("%23");
                    continue;
                case ' ':
                    buf.append("%20");
                    continue;
                default:
                    buf.append(c);
                    continue;
                }
            }
        }

        return buf;
    }
}
