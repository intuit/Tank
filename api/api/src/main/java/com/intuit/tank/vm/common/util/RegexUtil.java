/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.common.util;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.UUID;

/**
 * RegexUtil
 * 
 * @author dangleton
 * 
 */
public final class RegexUtil {

    /**
     * 
     */
    private RegexUtil() {
        // privateconstructor to implement util pattern
    }

    /**
     * Converts wildcard expression to regular expression. In wildcard-format, = 0-N characters and ? = any one
     * character. Wildcards can be used easily with JDK 1.4 by converting them to regexps:
     * <p>
     * <code>
     * import java.util.regex.Pattern;
     * 
     * Pattern p = Pattern.compile(wildcardToRegexp("*.jpg"));
     * </code>
     * 
     * @param wildcard
     *            wildcard expression string
     * @return given wildcard expression as regular expression
     */
    public static String wildcardToRegexp(String wildcard) {
        String escapedSequence = UUID.randomUUID().toString();
        while (wildcard.contains(escapedSequence)) {
            escapedSequence = UUID.randomUUID().toString();
        }
        String escapedSequence1 = UUID.randomUUID().toString();
        while (wildcard.contains(escapedSequence1)) {
            escapedSequence1 = UUID.randomUUID().toString();
        }
        wildcard = wildcard.replace("\\\\", escapedSequence1);
        wildcard = wildcard.replace("\\*", escapedSequence);
        wildcard = wildcard.replace(escapedSequence1, "\\\\");

        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');

        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch (c) {
            case '*':
                s.append('.');
                s.append('*');
                break;

            // escape special regexp-characters
            case '?':
            case '(':
            case ')':
            case '[':
            case ']':
            case '$':
            case '^':
            case '.':
            case '{':
            case '}':
            case '|':
            case '\\':
                s.append('\\');
                if (c != '\\') {
                    s.append(c);
                }
                break;
            default:
                s.append(c);
                break;
            }
        }
        s.append('$');
        wildcard = s.toString().replace(escapedSequence, "\\*");
        return (wildcard);
    }

}
