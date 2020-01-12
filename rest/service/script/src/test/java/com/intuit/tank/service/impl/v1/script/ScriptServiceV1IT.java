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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.intuit.tank.test.TestGroups;

import com.intuit.tank.api.model.v1.script.ScriptDescription;
import com.intuit.tank.api.model.v1.script.ScriptDescriptionContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    public void setup() {

    }

    @Test
    @Tag(TestGroups.MANUAL)
    @Disabled
    public void testPing() {
    	WebTarget webTarget = client.target(SERVICE_BASE_URL + "/ping");
        String response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get(String.class);
        assertEquals("PONG", response);
    }

    @Test
    @Tag(TestGroups.MANUAL)
    @Disabled
    public void testPostDataFile() {
        client = ClientBuilder.newClient();
        //client.setFollowRedirects(true);
        WebTarget webTarget = client.target(SERVICE_BASE_URL + "/script/description");

        webTarget.request(MediaType.APPLICATION_JSON_TYPE);
        Response response = webTarget.request().get();

        ScriptDescriptionContainer entity = response.readEntity(ScriptDescriptionContainer.class);
        assertNotNull(entity);
        assertNotNull(entity.getScripts());

        List<ScriptDescription> scripts = entity.getScripts();
        for (ScriptDescription sd : scripts) {
            assertNotNull(sd.getId());
        }

    }
}
