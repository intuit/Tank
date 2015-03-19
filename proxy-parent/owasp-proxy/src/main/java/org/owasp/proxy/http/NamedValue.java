/*
 * This file is part of the OWASP Proxy, a free intercepting proxy library.
 * Copyright (C) 2008-2010 Rogan Dawes <rogan@dawes.za.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to:
 * The Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.owasp.proxy.http;

public class NamedValue {

    private String name;

    private String separator;

    private String value;

    public NamedValue(String name, String separator, String value) {
        this.name = name;
        this.separator = separator;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getSeparator() {
        return separator;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return (name == null ? "" : name)
                + (separator == null ? "" : separator)
                + (value == null ? "" : value);
    }

    /**
     * parses a string into a NamedValue
     * 
     * @param string
     *            the string to parse
     * @param separator
     *            a regular expression specifying the separator between the name and the value
     * @return a NamedValue containing the parsed name, separator, and value
     * @throws MessageFormatException
     *             if the separator couldn't be found, or if the name and value could not be parsed
     */
    public static NamedValue parse(String string, String separator)
            throws MessageFormatException {
        String[] parts = string.split(separator, 2);
        if (parts.length == 2) {
            String sep = string.substring(parts[0].length(), string.length()
                    - parts[1].length());
            return new NamedValue(parts[0], sep, parts[1]);
        } else if (parts.length == 1) {
            if (parts[0].length() < string.length()) {
                String sep = string.substring(parts[0].length(), string
                        .length());
                return new NamedValue(parts[0], sep, "");
            } else {
                return new NamedValue(parts[0], null, null);
            }
        }
        throw new MessageFormatException("Error parsing '" + string
                + "' into a NamedValue using '" + separator + "'");
    }

    public static NamedValue[] parse(String string, String delimiter,
            String separator) throws MessageFormatException {
        if (string == null)
            return null;
        String[] nvs = string.split(delimiter);
        NamedValue[] values = new NamedValue[nvs.length];
        for (int i = 0; i < values.length; i++)
            values[i] = parse(nvs[i], separator);
        return values;
    }

    /**
     * Joins multiple NamedValues into a single String
     * 
     * @param values
     *            the values to join
     * @param separator
     *            the separator between the values
     * @return a string containing the joined values
     */
    public static String join(NamedValue[] values, String separator) {
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            buff.append(values[i]);
            if (i < values.length - 1)
                buff.append(separator);
        }
        return buff.toString();
    }

    public static String findValue(NamedValue[] values, String name) {
        if (values == null || values.length == 0)
            return null;
        for (int i = 0; i < values.length; i++)
            if (name.equals(values[i].getName()))
                return values[i].getValue();
        return null;
    }
}
