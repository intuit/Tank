package com.intuit.tank.harness;

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

import java.util.Date;

import com.intuit.tank.http.BaseRequest;

public class MockRequest extends BaseRequest {

    private Date date = new Date();

    public MockRequest() {
        super(null, null);

    }

    @Override
    public void setKey(String key, String value) {

    }

    @Override
    public String getKey(String key) {
        return null;
    }

    @Override
    public void setNamespace(String name, String value) {
    }

    @Override
    public Date getTimeStamp() {
        return date;
    }

}
