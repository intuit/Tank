/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.agent;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinition;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.service.impl.v1.cloud.JobController;
import com.intuit.tank.service.impl.v1.cloud.CloudController;
import com.intuit.tank.service.util.ServletInjector;
import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.agent.messages.Header;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.Subsegment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class AgentServiceV2Impl implements AgentServiceV2 {

    @Autowired
    private ServletContext servletContext;

    private static final Logger LOGGER = LogManager.getLogger(AgentServiceV2Impl.class);
    private static final String HARNESS_JAR = "apiharness-1.0-all.jar";
    @Override
    public String ping() {
        return "PONG " + getClass().getInterfaces()[0].getSimpleName();
    }

    @Override
    public String getSettings() {
        String settings;
        try {
            File configFile = new TankConfig().getSourceConfigFile();
            File agentConfigFile = new File(configFile.getParentFile(), "agent-settings.xml");
            if (agentConfigFile.exists() && agentConfigFile.isFile()) {
                configFile = agentConfigFile;
            }
            settings = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Error reading agent settings file: " + e);
            throw new GenericServiceResourceNotFoundException("agent", "settings file", e);
        }
        return settings;
    }

    @Override
    public File getSupportFiles() {
        String filename = "agent-support-files.zip";
        File supportFiles = new File(new TankConfig().getTmpDir(), filename);
        if (!supportFiles.exists()) {
            final FileStorage fileStorage = FileStorageFactory.getFileStorage(new TankConfig().getJarDir(), false);
            final File harnessJar = new File(servletContext.getRealPath("/tools/" + HARNESS_JAR));
            LOGGER.info("harnessJar = " + harnessJar.getAbsolutePath());
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
                            LOGGER.info("Not adding harness because we found it in the war.");
                        } else {
                            addFileToZip(fileData.getFileName(), fileStorage.readFileData(fileData), zip);
                        }
                    }
                    zip.flush();
                } catch (IOException e) {
                    LOGGER.error("Error reading support files: " + e);
                    throw new GenericServiceResourceNotFoundException("agent", "support files", e);
                }
            }
        }
        return supportFiles;
    }

    private void addFileToZip(String name, InputStream in, ZipOutputStream zip) {
        Subsegment subsegment = AWSXRay.beginSubsegment("Zip.File." + name);
        try {
            zip.putNextEntry(new ZipEntry(name));
            IOUtils.copy(in, zip);
            zip.closeEntry();
        } catch (IOException e) {
            LOGGER.error(e.toString());
            subsegment.addException(e);
        } finally {
            AWSXRay.endSubsegment();
            if (in != null) {
                try { in.close(); } catch (IOException e) {}
            }
        }
    }

    @Nonnull
    @Override
    public AgentTestStartData agentReady(AgentData data) {
        AgentTestStartData response;
        LOGGER.info("Agent ready: " + data);
        AWSXRay.getCurrentSegment().putAnnotation("JobId", data.getJobId());
        AWSXRay.getCurrentSegment().putAnnotation("InstanceId", data.getInstanceId());
        try {
            JobManager jobManager = new ServletInjector<JobManager>().getManagedBean(servletContext, JobManager.class);
            response = jobManager.registerAgentForJob(data);
        } catch (Exception e) {
            LOGGER.error("Error registering for a job: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("agent", "and registering agent", e);
        }
        return response;
    }

    @Override
    public Headers getHeaders() {
        Headers headers = new Headers();
        try {
            Map<String, String> requestHeaderMap = new TankConfig().getAgentConfig().getRequestHeaderMap();
            if (requestHeaderMap != null) {
                for (Entry<String, String> entry : requestHeaderMap.entrySet()) {
                    headers.getHeaders().add(new Header(entry.getKey(), entry.getValue()));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error returning agent headers: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("agent", "agent headers", e);
        }
        return headers;
    }

    @Override
    public TankHttpClientDefinitionContainer getClients() {
        try {
            AgentConfig config = new TankConfig().getAgentConfig();
            Map<String, String> map = config.getTankClientMap();
            List<TankHttpClientDefinition> definitions = map.entrySet().stream().map(entry -> new TankHttpClientDefinition(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            String defaultDefinition = config.getTankClientDefault();
            if (defaultDefinition == null && definitions.size() > 0) {
                defaultDefinition = definitions.get(0).getName();
            }
            return new TankHttpClientDefinitionContainer(definitions, defaultDefinition);
        } catch (Exception e) {
            LOGGER.error("Error returning agent clients: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("agent", "agent clients", e);
        }
    }

    @Override
    public void setStandaloneAgentAvailability(AgentAvailability availability) {
        try {
            StandaloneAgentTracker tracker = new ServletInjector<StandaloneAgentTracker>().getManagedBean(servletContext, StandaloneAgentTracker.class);
            LOGGER.info("Adding agent availability: " + availability);
            tracker.addAvailability(availability);
        } catch (Exception e) {
            LOGGER.error("Error setting agent availability: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("agent", "agent availability", e);
        }
    }

    @Override
    public CloudVmStatus getInstanceStatus(String instanceId) {
        AWSXRay.getCurrentSegment().putAnnotation("instanceId", instanceId);
        try {
            CloudController controller = new ServletInjector<CloudController>().getManagedBean(
                    servletContext, CloudController.class);
            return controller.getVmStatus(instanceId);
        } catch (Exception e) {
            LOGGER.error("Error returning instance status: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("agent", "instance status", e);
        }
    }

    @Override
    public void setInstanceStatus(String instanceId, CloudVmStatus status) {
        Segment segment = AWSXRay.getCurrentSegment();
        segment.putAnnotation("instanceId", instanceId);
        segment.putAnnotation("jobId", status.getJobId());
        segment.putAnnotation("currentUsers", status.getCurrentUsers());
        segment.putAnnotation("TotalUsers", status.getTotalUsers());
        segment.putAnnotation("totalTps", status.getTotalTps());
        try {
            CloudController controller = new ServletInjector<CloudController>().getManagedBean(
                    servletContext, CloudController.class);
            controller.setVmStatus(instanceId, status);
        } catch (Exception e) {
            LOGGER.error("Error updating instance status: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("agent", "instance status", e);
        }
    }

    @Override
    public String stopInstance(String instanceId) {
        AWSXRay.getCurrentSegment().putAnnotation("instanceId", instanceId);
        try {
            JobController controller = new ServletInjector<JobController>().getManagedBean(
                    servletContext, JobController.class);
            controller.stopAgent(instanceId);
            return getInstanceStatus(instanceId).getVmStatus().name();
        } catch (Exception e) {
            LOGGER.error("Error stopping instance: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("agent", "instance status to stop", e);
        }
    }

    @Override
    public String pauseInstance(String instanceId) {
        AWSXRay.getCurrentSegment().putAnnotation("instanceId", instanceId);
        try {
            JobController controller = new ServletInjector<JobController>().getManagedBean(
                    servletContext, JobController.class);
            controller.pauseRampInstance(instanceId);
            return getInstanceStatus(instanceId).getVmStatus().name();
        } catch (Exception e) {
            LOGGER.error("Error pausing running instance: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("agent", "instance status to pause", e);
        }
    }

    @Override
    public String resumeInstance(String instanceId) {
        AWSXRay.getCurrentSegment().putAnnotation("instanceId", instanceId);
        try {
            JobController controller = new ServletInjector<JobController>().getManagedBean(
                    servletContext, JobController.class);
            controller.resumeRampInstance(instanceId);
            return getInstanceStatus(instanceId).getVmStatus().name();
        } catch (Exception e) {
            LOGGER.error("Error resuming instance: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("agent", "instance status to resume", e);
        }
    }

    @Override
    public String killInstance(String instanceId) {
        AWSXRay.getCurrentSegment().putAnnotation("instanceId", instanceId);
        try {
            JobController controller = new ServletInjector<JobController>().getManagedBean(
                    servletContext, JobController.class);
            controller.killInstance(instanceId);
            return getInstanceStatus(instanceId).getVmStatus().name();
        } catch (Exception e) {
            LOGGER.error("Error killing instance: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("agent", "instance status to terminate", e);
        }
    }

}
