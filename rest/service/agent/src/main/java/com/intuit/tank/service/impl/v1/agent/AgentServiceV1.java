/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.agent;

/*
 * #%L
 * Cloud Rest Service
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Subsegment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.model.v1.agent.TankHttpClientDefinition;
import com.intuit.tank.api.model.v1.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.api.service.v1.agent.AgentService;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.service.util.ResponseUtil;
import com.intuit.tank.service.util.ServletInjector;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.Header;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;

/**
 * CloudServiceV1
 * 
 * @author dangleton
 * 
 */
@Path(AgentService.SERVICE_RELATIVE_PATH)
public class AgentServiceV1 implements AgentService {

    private static final Logger LOG = LogManager.getLogger(AgentServiceV1.class);
    private static final String HARNESS_JAR = "apiharness-1.0-all.jar";
    private static final String START_SCRIPT = "startAgent.sh";

    @Context
    private ServletContext servletContext;

    /**
     * @inheritDoc
     */
    @Nonnull
    @Override
    public String ping() {
        return "PONG " + getClass().getSimpleName();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response agentReady(AgentData data) {
        LOG.info("Agent ready: " + data);
        AWSXRay.getCurrentSegment().putAnnotation("JobId", data.getJobId());
        AWSXRay.getCurrentSegment().putAnnotation("InstanceId", data.getInstanceId());
        ResponseBuilder responseBuilder = Response.ok();
        try {
            JobManager jobManager = new ServletInjector<JobManager>().getManagedBean(servletContext, JobManager.class);
            AgentTestStartData response = jobManager.registerAgentForJob(data);
            responseBuilder.entity(response);
        } catch (Exception e) {
            LOG.error("Error determining status: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    @Nonnull
    @Override
    public Response getSettings() {
        ResponseBuilder responseBuilder = Response.ok();
        try {
            File configFile = new TankConfig().getSourceConfigFile();
            File agentConfigFile = new File(configFile.getParentFile(), "agent-settings.xml");
            if (agentConfigFile.exists() && agentConfigFile.isFile()) {
                configFile = agentConfigFile;
            }
            responseBuilder.entity(FileUtils.readFileToString(configFile, StandardCharsets.UTF_8));
        } catch (Exception e) {
            LOG.error("Error reading settings: " + e.getMessage(), e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        return responseBuilder.build();
    }

    @Nonnull
    @Override
    public Response getSupportFiles() {
        ResponseBuilder responseBuilder = Response.ok();
        // AuthUtil.checkLoggedIn(servletContext);
        String filename = "agent-support-files.zip";
        File supportFiles = new File(new TankConfig().getTmpDir(), filename);
        if (!supportFiles.exists()) {
            final FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getJarDir(), false);
            final File harnessJar = new File(servletContext.getRealPath("/tools/" + HARNESS_JAR));
            LOG.info("harnessJar = " + harnessJar.getAbsolutePath());
            final List<FileData> files = fileStorage.listFileData("");

            synchronized ( supportFiles ) {
                supportFiles.getParentFile().mkdirs();
                // open the zip stream in a try resource block, no finally needed
                try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(supportFiles))) {

                    if (harnessJar.exists()) {
                        addFileToZip(HARNESS_JAR, new FileInputStream(harnessJar), zip);
                    }
                    for (FileData fileData : files) {
                        if (harnessJar.exists() && fileData.getFileName().equals(HARNESS_JAR)) {
                            LOG.info("Not adding harness because we found it in the war.");
                        } else {
                            addFileToZip(fileData.getFileName(), fileStorage.readFileData(fileData), zip);
                        }
                    }
                    zip.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        responseBuilder.entity(supportFiles);
        return responseBuilder.build();
    }

    private void addFileToZip(String name, InputStream in, ZipOutputStream zip) {
        Subsegment subsegment = AWSXRay.beginSubsegment("Zip.File." + name);
        try {
            zip.putNextEntry(new ZipEntry(name));
            IOUtils.copy(in, zip);
            zip.closeEntry();
        } catch (IOException e) {
            subsegment.addException(e);
        } finally {
            AWSXRay.endSubsegment();
            if (in != null) {
                try { in.close(); } catch (IOException e) {}
            }
        }
    }

    @Override
    public Response standaloneAgentAvailable(@Nonnull AgentAvailability availability) {
        StandaloneAgentTracker tracker = new ServletInjector<StandaloneAgentTracker>().getManagedBean(servletContext, StandaloneAgentTracker.class);
        LOG.info("Adding agent availability: " + availability);
        tracker.addAvailability(availability);
        return Response.noContent().build();
    }

    @Nonnull
    @Override
    public Response getHeaders() {
        ResponseBuilder responseBuilder = Response.ok();
        Headers headers = new Headers();
        Map<String, String> requestHeaderMap = new TankConfig().getAgentConfig().getRequestHeaderMap();
        if (requestHeaderMap != null) {
            for (Entry<String, String> entry : requestHeaderMap.entrySet()) {
                headers.getHeaders().add(new Header(entry.getKey(), entry.getValue()));
            }
        }
        responseBuilder.entity(headers);
        return responseBuilder.build();
    }

    @Nonnull
    @Override
    public Response getClients() {
        ResponseBuilder responseBuilder = Response.ok();
        AgentConfig config = new TankConfig().getAgentConfig();
        Map<String, String> map = config.getTankClientMap();
        List<TankHttpClientDefinition> definitions = map.entrySet().stream().map(entry -> new TankHttpClientDefinition(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        String defaultDefinition = config.getTankClientDefault();
        if (defaultDefinition == null && definitions.size() > 0) {
            defaultDefinition = definitions.get(0).getName();
        }
        TankHttpClientDefinitionContainer container = new TankHttpClientDefinitionContainer(definitions, defaultDefinition);
        responseBuilder.entity(container);
        return responseBuilder.build();
    }

}
