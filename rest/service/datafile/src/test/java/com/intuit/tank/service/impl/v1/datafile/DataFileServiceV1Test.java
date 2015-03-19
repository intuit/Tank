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

import javax.ws.rs.core.MediaType;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.api.model.v1.datafile.DataFileDescriptor;
import com.intuit.tank.datafile.util.DataFileServiceUtil;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

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
        client = Client.create();
        client.setFollowRedirects(true);

    }

    @Test(groups = { "manual" })
    public void testPing() {
        WebResource webResource = client.resource(SERVICE_BASE_URL + "/ping");
        String response = webResource.accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
        Assert.assertEquals("PONG", response);
    }

    @Test(groups = { "manual" })
    public void testPostDataFile() {
        WebResource webResource = client.resource(SERVICE_BASE_URL + "/data-file");
        File f = new File("src/test/resources/users.csv");
        MultiPart multiPart = new MultiPart();
        FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file", f);
        multiPart.bodyPart(fileDataBodyPart);
        DataFileDescriptor df = new DataFileDescriptor();
        df.setName(f.getName());
        df.setCreator("Denis");
        df.setComments("comments");
        multiPart.bodyPart(new FormDataBodyPart("dataFile", df, MediaType.APPLICATION_XML_TYPE));
        webResource.accept(MediaType.APPLICATION_XML_TYPE);
        ClientResponse response =
                webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
                        .post(ClientResponse.class, multiPart);

        DataFileDescriptor entity = response.getEntity(DataFileDescriptor.class);
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
        webResource.accept(MediaType.APPLICATION_XML_TYPE);
        response = webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE)
                .post(ClientResponse.class, multiPart);

        entity = response.getEntity(DataFileDescriptor.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getId());
        Assert.assertNotNull(entity.getCreated());
        Assert.assertNotNull(entity.getModified());
        Assert.assertEquals(df.getComments(), entity.getComments());
        Assert.assertEquals(df.getCreator(), entity.getCreator());
        Assert.assertEquals(df.getName(), entity.getName());

        webResource = client.resource(SERVICE_BASE_URL + "/data-file/" + entity.getId());
        response = webResource.get(ClientResponse.class);

        entity = response.getEntity(DataFileDescriptor.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getId());
        Assert.assertNotNull(entity.getCreated());
        Assert.assertNotNull(entity.getModified());
        Assert.assertEquals(df.getComments(), entity.getComments());
        Assert.assertEquals(df.getCreator(), entity.getCreator());
        Assert.assertEquals(df.getName(), entity.getName());

        response = webResource.delete(ClientResponse.class);
        Assert.assertEquals(Status.OK.getStatusCode(), response.getStatus());

    }
}
