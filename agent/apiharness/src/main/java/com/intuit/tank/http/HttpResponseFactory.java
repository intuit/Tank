package com.intuit.tank.http;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.http.binary.BinaryResponse;
import com.intuit.tank.http.json.JsonResponse;
import com.intuit.tank.http.xml.XMLResponse;

public class HttpResponseFactory {

    /**
     * If the user has specified a particular resposne type, new it up.
     * 
     * @return The specified response type; null if not specified.
     */
    public static BaseResponse getHttpResponse(String responseType) {
        if (responseType == null) {
            responseType = "XML";
        }
        if (responseType.equalsIgnoreCase("JSON")) {
            return new JsonResponse();
        } else if (responseType.equalsIgnoreCase("XML")) {
            return new XMLResponse();
        } else {
            return new BinaryResponse();
        }
    }
}
