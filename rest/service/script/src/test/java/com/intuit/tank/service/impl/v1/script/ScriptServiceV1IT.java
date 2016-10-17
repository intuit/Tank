/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.script;

/*
 * #%L
 * Script Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptDescriptionContainer;
import com.intuit.tank.test.TestGroups;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * DataFileServiceV1Test
 * 
 * @author dangleton
 * 
 */
public class ScriptServiceV1IT {

    /**
     * 
     */
    private static final String SERVICE_BASE_URL = "http://localhost:8088//rest/v1/script-service";
    private Client client = null;

    @BeforeClass
    public void setup() {

    }

    // @Test(groups = {"manual"})
    public void testPing() {
        WebResource webResource = client.resource(SERVICE_BASE_URL + "/ping");
        String response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
        Assert.assertEquals("PONG", response);
    }

    // @Test(groups = TestGroups.MANUAL)
    public void testPostDataFile() {
        client = Client.create();
        client.setFollowRedirects(true);
        WebResource webResource = client.resource(SERVICE_BASE_URL + "/script/description");

        webResource.accept(MediaType.APPLICATION_JSON_TYPE);
        ClientResponse response = webResource.get(ClientResponse.class);

        ScriptDescriptionContainer entity = response.getEntity(ScriptDescriptionContainer.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getScripts());

        List<ScriptDescription> scripts = entity.getScripts();
        for (ScriptDescription sd : scripts) {
            Assert.assertNotNull(sd.getId());
        }

    }
}
