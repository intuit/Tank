/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.exception;

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

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXParseException;

/**
 * ParseExceptionHandler
 * 
 * @author dangleton
 * 
 */
public class ParseExceptionConverter {

    public static WatsParseException handleException(@Nonnull Throwable throwable) {
        if (throwable instanceof WatsParseException) {
            throw (WatsParseException) throwable;
        }
        int lineNumber = -1;
        int colNumber = -1;
        String msg = throwable.getMessage();
        if (throwable instanceof SAXParseException) {
            SAXParseException spe = (SAXParseException) throwable;
            lineNumber = spe.getLineNumber();
            colNumber = spe.getColumnNumber();
            msg = spe.getMessage();
        } else if (throwable instanceof XMLStreamException) {
            XMLStreamException spe = (XMLStreamException) throwable;
            lineNumber = spe.getLocation().getLineNumber();
            colNumber = spe.getLocation().getColumnNumber();
            msg = spe.getMessage();
        } else {
            msg = "Dont know what to do about Exception " + throwable.getClass().getCanonicalName();
        }
        return new WatsParseException(throwable, msg, lineNumber, colNumber);
    }

}
