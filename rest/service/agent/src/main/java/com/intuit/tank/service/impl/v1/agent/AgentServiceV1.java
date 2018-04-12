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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;
import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

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

    private static final String HARNESS_JAR = "apiharness-1.0-all.jar";
    private static final String START_SCRIPT = "startAgent.sh";

    private static final Logger LOG = LogManager.getLogger(AgentServiceV1.class);

    @Context
    private ServletContext servletContext;

    /**
     * @inheritDoc
     */
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

    @Override
    @Nonnull
    public Response getSupportFiles() {
        ResponseBuilder responseBuilder = Response.ok();
        // AuthUtil.checkLoggedIn(servletContext);
        final FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getJarDir(), false);
        final File harnessJar = new File(servletContext.getRealPath("/tools/" + HARNESS_JAR));
        LOG.info("harnessJar = " + harnessJar.getAbsolutePath());
        final List<FileData> files = fileStorage.listFileData("");

        StreamingOutput streamingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                ZipOutputStream zip = new ZipOutputStream(output);
                try {
                    if (harnessJar.exists()) {
                        addFileToZip(HARNESS_JAR, new FileInputStream(harnessJar), zip);
                        zip.flush();
                    }
                    for (FileData fileData : files) {
                        if (harnessJar.exists() && fileData.getFileName().equals(HARNESS_JAR)) {
                            LOG.info("Not adding harness because we found it in the war.");
                        } else {
                            addFileToZip(fileData.getFileName(), fileStorage.readFileData(fileData), zip);
                            zip.flush();
                        }
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    IOUtils.closeQuietly(zip);
                }
            }
        };
        String filename = "agent-support-files.zip";
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        responseBuilder.entity(streamingOutput);
        return responseBuilder.build();
    }

    private void addFileToZip(String name, InputStream in, ZipOutputStream zip) throws Exception {
        try {
            zip.putNextEntry(new ZipEntry(name));
            IOUtils.copy(in, zip);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public Response standaloneAgentAvailable(@Nonnull AgentAvailability availability) {
        StandaloneAgentTracker tracker = new ServletInjector<StandaloneAgentTracker>().getManagedBean(servletContext, StandaloneAgentTracker.class);
        LOG.info("Adding agent availability: " + availability);
        tracker.addAvailability(availability);
        return Response.noContent().build();
    }

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

    @Override
    public Response getClients() {
        ResponseBuilder responseBuilder = Response.ok();
        AgentConfig config = new TankConfig().getAgentConfig();
        Map<String, String> map = config.getTankClientMap();
        List<TankHttpClientDefinition> definitions = new ArrayList<TankHttpClientDefinition>();
        for (Entry<String, String> entry : map.entrySet()) {
            definitions.add(new TankHttpClientDefinition(entry.getKey(), entry.getValue()));
        }
        String defaultDefinition = config.getTankClientDefault();
        if (defaultDefinition == null && definitions.size() > 0) {
            defaultDefinition = definitions.get(0).getName();
        }
        TankHttpClientDefinitionContainer container = new TankHttpClientDefinitionContainer(definitions, defaultDefinition);
        responseBuilder.entity(container);
        return responseBuilder.build();
    }

}
