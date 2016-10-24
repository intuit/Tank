/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.automation;

/*
 * #%L
 * Automation Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * DataFileServiceV1Test
 * 
 * @author dangleton
 * 
 */
@Path("/test-automation")
public class AutomationServiceV1Test {

    /**
     * 
     */
    private static final String SERVICE_BASE_URL = "http://localhost:8080//rest/v1/automation-service";

    private Client client = null;

    @BeforeClass
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @Test(groups = { "manual" })
    public void testPing() {
    	WebTarget webTarget = client.target(SERVICE_BASE_URL + "/ping");
    	webTarget.property(ClientProperties.FOLLOW_REDIRECTS, true);
        String response = webTarget.request(MediaType.TEXT_PLAIN_TYPE).get(String.class);
        Assert.assertEquals("PONG", response);
    }

    @Test(groups = { "manual" })
    public void testPostDataFile() {
        // WebResource webResource = client.resource(SERVICE_BASE_URL + "/script");
        // File f = new File("src/test/resources/medium.xml");
        // MultiPart multiPart = new MultiPart();
        // FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", f, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        // multiPart.bodyPart(fileDataBodyPart);
        // ScriptDescription sd = new ScriptDescription();
        // sd.setName("My Script");
        // sd.setCreator("Denis");
        // sd.setComments("comments");
        // sd.setRuntime(6000);
        // sd.setProductName("Test Product");
        // List<Integer> filters = Arrays.asList(new Integer[] { 1, 2 });
        //
        // ScriptUploadRequest scriptUploadRequest = new ScriptUploadRequest(sd, filters);
        // multiPart.bodyPart(new FormDataBodyPart("xmlString", scriptUploadRequest, MediaType.APPLICATION_XML_TYPE));
        // webResource.accept(MediaType.APPLICATION_XML_TYPE);
        // ClientResponse response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
        // .post(ClientResponse.class, multiPart);
        // Assert.assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        //
        // ScriptTO entity = response.getEntity(ScriptTO.class);
        // Assert.assertNotNull(entity);
        // Assert.assertNotNull(entity.getId());
        // Assert.assertNotNull(entity.getCreated());
        // Assert.assertNotNull(entity.getModified());
        // Assert.assertEquals(sd.getComments(), entity.getComments());
        // Assert.assertEquals(sd.getCreator(), entity.getCreator());
        // Assert.assertEquals(sd.getName(), entity.getName());
        // Integer id = entity.getId();
        //
        // webResource = client.resource(SERVICE_BASE_URL + "/script/" + entity.getId());
        // response = webResource.get(ClientResponse.class);
        // Assert.assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        //
        // ScriptTO to = response.getEntity(ScriptTO.class);
        // Assert.assertNotNull(to);
        // Assert.assertNotNull(to.getId());
        // Assert.assertNotNull(to.getCreated());
        // Assert.assertNotNull(to.getModified());
        // Assert.assertEquals(sd.getComments(), to.getComments());
        // Assert.assertEquals(sd.getCreator(), to.getCreator());
        // Assert.assertEquals(sd.getName(), to.getName());
        // Assert.assertNotNull(to.getSteps());
        // Assert.assertTrue(to.getSteps().size() > 0);
        //
        // to.setComments("New Comments");
        // to.setName("new Name");
        //
        // response = webResource.type(MediaType.APPLICATION_XML_TYPE)
        // .put(ClientResponse.class, to);
        // Assert.assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        //
        // ScriptTO updatedTo = response.getEntity(ScriptTO.class);
        // Assert.assertNotNull(updatedTo);
        // Assert.assertNotNull(updatedTo.getId());
        // Assert.assertNotNull(updatedTo.getCreated());
        // Assert.assertNotNull(updatedTo.getModified());
        // Assert.assertEquals(to.getComments(), updatedTo.getComments());
        // Assert.assertEquals(to.getCreator(), updatedTo.getCreator());
        // Assert.assertEquals(to.getName(), updatedTo.getName());
        // Assert.assertNotNull(updatedTo.getSteps());
        // Assert.assertEquals(to.getSteps().size(), updatedTo.getSteps().size());
        //
        // webResource = client.resource(SERVICE_BASE_URL + "/script/description/" + entity.getId());
        // response = webResource.get(ClientResponse.class);
        // Assert.assertEquals(ClientResponse.Status.OK.getStatusCode(), response.getStatus());
        //
        // ScriptDescription desc = response.getEntity(ScriptDescription.class);
        // Assert.assertNotNull(desc);
        // Assert.assertNotNull(desc.getId());
        // Assert.assertNotNull(desc.getCreated());
        // Assert.assertNotNull(desc.getModified());
        // Assert.assertEquals(updatedTo.getComments(), desc.getComments());
        // Assert.assertEquals(updatedTo.getCreator(), desc.getCreator());
        // Assert.assertEquals(updatedTo.getName(), desc.getName());
        //
        // webResource = client.resource(SERVICE_BASE_URL + "/script/" + id);
        // response = webResource.delete(ClientResponse.class);
        // Assert.assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

    }
}
