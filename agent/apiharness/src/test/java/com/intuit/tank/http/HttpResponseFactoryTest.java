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
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;


public class HttpResponseFactoryTest {

    @Test
    public void testGetHttpResponse_1() {
        BaseResponse result = HttpResponseFactory.getHttpResponse(null);
        assertEquals(XMLResponse.class, result.getClass());
    }

    @Test
    public void testGetHttpResponse_2() {
        BaseResponse result = HttpResponseFactory.getHttpResponse("JSON");
        assertEquals(JsonResponse.class, result.getClass());
    }

    @Test
    public void testGetHttpResponse_3() {
        BaseResponse result = HttpResponseFactory.getHttpResponse("XML");
        assertEquals(XMLResponse.class, result.getClass());
    }

    @Test
    public void testGetHttpResponse_4() {
        BaseResponse result = HttpResponseFactory.getHttpResponse("");
        assertEquals(BinaryResponse.class, result.getClass());
    }
}
