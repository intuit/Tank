/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.util;

/*
 * #%L
 * Rest Service Common Classes
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Workload;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;

/**
 * ResponseUtil
 * 
 * @author dangleton
 * 
 */
public class ResponseUtil {
    private static final Logger LOG = LogManager.getLogger(ResponseUtil.class);

    private ResponseUtil() {
        // empty private constructor to implement util pattern
    }

    public static StreamingOutput getXMLStream(Object toMarshall) {
        return (OutputStream outputStream) -> {
            AWSXRay.beginSubsegment("JAXB.Marshal." + toMarshall.getClass().getSimpleName());
            try {
                JAXBContext ctx = JAXBContext.newInstance(toMarshall.getClass().getPackage().getName());
                Marshaller marshaller = ctx.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(toMarshall, outputStream);
            } catch (JAXBException e) {
                LOG.error("Error Marshalling XML to Stream: " + e.toString(), e);
                throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
            } finally {
                AWSXRay.endSubsegment();
            }
        };
    }

    public static void storeScript(String jobId, Workload workload, JobInstance job) {
        new WorkloadDao().loadScriptsForWorkload(workload);
        HDWorkload hdWorkload = ConverterUtil.convertWorkload(workload, job);
        String scriptString = ConverterUtil.getWorkloadXML(hdWorkload);
        ProjectDaoUtil.storeScriptFile(jobId, scriptString);
    }

    public static final CacheControl getNoStoreCacheControl() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);
        cc.setNoStore(true);
        cc.setMaxAge(0);
        cc.setSMaxAge(0);
        cc.setPrivate(true);
        return cc;
    }
}
