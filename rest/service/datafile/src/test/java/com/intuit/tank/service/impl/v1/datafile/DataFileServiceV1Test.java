/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.datafile;

/*
 * #%L
 * Datafile Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.junit.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.api.model.v1.datafile.DataFileDescriptor;
import com.intuit.tank.datafile.util.DataFileServiceUtil;

/**
 * DataFileServiceV1Test
 * 
 * @author dangleton
 * 
 */
public class DataFileServiceV1Test {

    /**
     * 
     */
    private static final String SERVICE_BASE_URL = "http://localhost:8080//rest/v1/data-file-service";
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
    	WebTarget webTarget = client.target(SERVICE_BASE_URL + "/data-file");
    	webTarget.property(ClientProperties.FOLLOW_REDIRECTS, true);
        File f = new File("src/test/resources/users.csv");
        MultiPart multiPart = new MultiPart();
        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", f);
        multiPart.bodyPart(fileDataBodyPart);
        DataFileDescriptor df = new DataFileDescriptor();
        df.setName(f.getName());
        df.setCreator("Denis");
        df.setComments("comments");
        multiPart.bodyPart(new FormDataBodyPart("dataFile", df, MediaType.APPLICATION_XML_TYPE));
        webTarget.request(MediaType.APPLICATION_XML_TYPE);
        ClientResponse response = webTarget.request().post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE), ClientResponse.class);

        DataFileDescriptor entity = response.readEntity(DataFileDescriptor.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getId());
        Assert.assertNotNull(entity.getCreated());
        Assert.assertNotNull(entity.getModified());
        Assert.assertEquals(df.getComments(), entity.getComments());
        Assert.assertEquals(df.getCreator(), entity.getCreator());
        Assert.assertEquals(df.getName(), entity.getName());

        df = DataFileServiceUtil.dataFileToDescriptor(DataFileServiceUtil.descriptorToDataFile(entity));
        // update the dataFile data
        entity.setComments("New Comments");
        entity.setName("new_name.csv");
        multiPart = new MultiPart();
        multiPart.bodyPart(new FormDataBodyPart("dataFile", df, MediaType.APPLICATION_XML_TYPE));
        webTarget.request(MediaType.APPLICATION_XML_TYPE);
        response = webTarget.request().post(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA_TYPE), ClientResponse.class);

        entity = response.readEntity(DataFileDescriptor.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getId());
        Assert.assertNotNull(entity.getCreated());
        Assert.assertNotNull(entity.getModified());
        Assert.assertEquals(df.getComments(), entity.getComments());
        Assert.assertEquals(df.getCreator(), entity.getCreator());
        Assert.assertEquals(df.getName(), entity.getName());

        webTarget = client.target(SERVICE_BASE_URL + "/data-file/" + entity.getId());
        response = webTarget.request().get(ClientResponse.class);

        entity = response.readEntity(DataFileDescriptor.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getId());
        Assert.assertNotNull(entity.getCreated());
        Assert.assertNotNull(entity.getModified());
        Assert.assertEquals(df.getComments(), entity.getComments());
        Assert.assertEquals(df.getCreator(), entity.getCreator());
        Assert.assertEquals(df.getName(), entity.getName());

        response = webTarget.request().delete(ClientResponse.class);
        Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());

    }
}
