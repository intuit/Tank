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

package org.owasp.proxy.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * 
 * @author rogan
 */
public class TextFormatter extends Formatter {

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss ");

    /** Creates a new instance of TextFormatter */
    public TextFormatter() {
    }

    public String format(LogRecord record) {
        StringBuffer buff = new StringBuffer(100);
        buff.append(sdf.format(new Date(record.getMillis())));
        buff.append(Thread.currentThread().getName());
        String className = record.getSourceClassName();
        if (className.indexOf(".") > -1) {
            className = className.substring(className.lastIndexOf(".") + 1,
                    className.length());
        }
        buff.append("(").append(className).append(".");
        buff.append(record.getSourceMethodName()).append("): ");
        buff.append(record.getMessage());
        if (record.getParameters() != null) {
            Object[] params = record.getParameters();
            buff.append(" { ").append(params[0]);
            for (int i = 1; i < params.length; i++) {
                buff.append(", ").append(params[i]);
            }
            buff.append(" }");
        }
        buff.append("\n");
        return buff.toString();
    }

}
