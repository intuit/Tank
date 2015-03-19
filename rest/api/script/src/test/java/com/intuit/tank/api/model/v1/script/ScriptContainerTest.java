/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.script;

/*
 * #%L
 * Script Rest API
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.api.model.v1.script.ScriptStepContainer;
import com.intuit.tank.api.model.v1.script.ScriptStepTO;
import com.intuit.tank.api.model.v1.script.StepDataTO;

/**
 * DataFileDescriptorTest
 * 
 * @author dangleton
 * 
 */
public class ScriptContainerTest {

    @Test(groups = { "manual" })
    public void generateSample() throws Exception {

        JAXBContext ctx = JAXBContext.newInstance(ScriptStepContainer.class.getPackage().getName());
        Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        List<ScriptStepTO> steps = new ArrayList<ScriptStepTO>();
        for (int i = 0; i < 3; i++) {
            steps.add(createStep(i));
        }
        ScriptStepContainer jaxbObject = ScriptStepContainer.builder().withNumRemaining(10).withNumRequsted(100)
                .withNumReturned(2).withStartIndex(20).withSteps(steps).build();
        File parent = new File("target/jaxb-sample-xml");
        parent.mkdirs();
        Assert.assertTrue(parent.exists());
        File file = new File(parent, jaxbObject.getClass().getSimpleName() + ".xml");
        marshaller.marshal(jaxbObject, file);
        Assert.assertTrue(file.length() > 0);
        ScriptStepContainer unmarshalled = (ScriptStepContainer) ctx.createUnmarshaller().unmarshal(file);
        Assert.assertEquals(unmarshalled.getNumRemaining(), jaxbObject.getNumRemaining());
        Assert.assertEquals(unmarshalled.getNumRequsted(), jaxbObject.getNumRequsted());
        Assert.assertEquals(unmarshalled.getNumReturned(), jaxbObject.getNumReturned());
    }

    public ScriptStepTO createStep(int index) {
        ScriptStepTO ret = new ScriptStepTO();
        ret.setComments("comments");
        ret.setCreated(new Date());
        ret.setModified(new Date());
        ret.setHostname("www.google.com");
        ret.setLabel("label");
        ret.setMethod("GET");
        ret.setMimetype("text/html");
        ret.setName("Name");
        ret.setOnFail("abort");
        ret.setProtocol("http");
        ret.setReqFormat("reqFormat");
        ret.setRespFormat("respFormat");
        ret.setResult("success");
        ret.setScriptGroupName("scriptGroupName");
        ret.setSimplePath("/simplePath");
        ret.setStepIndex(index);
        ret.setType("type");
        ret.setUrl("http://www.google.com");
        ret.setUuid(UUID.randomUUID().toString());

        ret.setData(createData("requestData"));
        ret.setPostDatas(createData("requestPostData"));
        ret.setQueryStrings(createData("queryString"));
        ret.setRequestCookies(createData("requestCookie"));
        ret.setRequestheaders(createData("requestheader"));
        ret.setResponseCookies(createData("responseCookie"));
        ret.setResponseheaders(createData("responseHeader"));

        return ret;
    }

    /**
     * @param string
     * @return
     */
    private Set<StepDataTO> createData(String type) {
        Set<StepDataTO> ret = new HashSet<StepDataTO>();
        for (int i = 0; i < 3; i++) {
            StepDataTO data = new StepDataTO();
            data.setKey("key " + i);
            data.setValue("value " + i);
            data.setType(type);
            ret.add(data);
        }
        return ret;
    }
}
