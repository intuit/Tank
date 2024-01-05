/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.script.models.ScriptTO;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

import com.amazonaws.xray.AWSXRay;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.ws.rs.core.CacheControl;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;

public class ResponseUtil {
    private static final Logger LOG = LogManager.getLogger(ResponseUtil.class);

    private ResponseUtil() {
        // empty private constructor to implement util pattern
    }

    public static StreamingResponseBody getXMLStream(Object toMarshall) {
        return (OutputStream outputStream) -> {
            AWSXRay.beginSubsegment("JAXB.Marshal." + toMarshall.getClass().getSimpleName());
            try {
                JAXBContext ctx;
                if(toMarshall instanceof ScriptTO) {
                    ctx = JAXBContext.newInstance(toMarshall.getClass());
                } else {
                    ctx = JAXBContext.newInstance(toMarshall.getClass().getPackage().getName());
                }
                if (ctx != null) {
                    Marshaller marshaller = ctx.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                    marshaller.marshal(toMarshall, outputStream);
                }
            } catch (JAXBException e) {
                LOG.error("Error Marshalling XML to Stream: " + e.toString(), e);
                throw new GenericServiceResourceNotFoundException("ResponseUtil", "harness or tank script file", e);
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
