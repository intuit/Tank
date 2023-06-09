/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.projects;

import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;
import com.intuit.tank.rest.mvc.rest.models.projects.AutomationJobRegion;
import com.intuit.tank.rest.mvc.rest.models.projects.AutomationRequest;
import com.intuit.tank.rest.mvc.rest.util.ProjectServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.vm.api.enumerated.ScriptDriver;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceV2Impl implements ProjectServiceV2 {

    @Autowired
    private ServletContext servletContext;

    private static final Logger LOGGER = LogManager.getLogger(ProjectServiceV2Impl.class);

    @Override
    public String ping() {
        return "PONG " + getClass().getInterfaces()[0].getSimpleName();
    }

    @Override
    public ProjectContainer getAllProjects(){
        try {
            List<Project> all = new ProjectDao().findAll();
            List<ProjectTO> to = all.stream().map(ProjectServiceUtil::projectToTransferObject).collect(Collectors.toList());
            return new ProjectContainer(to);
        } catch (Exception e) {
            LOGGER.error("Error returning all projects: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("projects", "all project", e);
        }
    }

    @Override
    public Map<Integer, String> getAllProjectNames(){
        try {
            List<Project> all = new ProjectDao().findAllFast();
            return all.stream().collect(Collectors.toMap(Project::getId, Project::getName));
        } catch (Exception e) {
            LOGGER.error("Error returning all project names: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("projects", "all project names", e);
        }
    }

    @Override
    public ProjectTO getProject(Integer projectId){
        try {
            Project prj = new ProjectDao().findByIdEager(projectId);
            return ProjectServiceUtil.projectToTransferObject(prj);
        } catch (Exception e) {
            LOGGER.error("Error returning the project: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("projects", "project", e);
        }
    }

    @Override
    public Map<String, String> createProject(AutomationRequest request){
        Map<String, String> response = new HashMap<>();
        Project project = createOrUpdateProject(null, request);
        response.put("ProjectId", Integer.toString(project.getId()));
        response.put("status", "Created");
        return response;
    }

    @Override
    public Map<String, String> updateProject(Integer projectId, AutomationRequest request){
        Map<String, String> response = new HashMap<>();
        ProjectDao projectDao = new ProjectDao();
        if(projectDao.findByIdEager(projectId) == null){
            response.put("error", "project with that project Id does not exist");
            return response;
        }
        Project project = createOrUpdateProject(projectId, request);
        response.put("ProjectId", Integer.toString(project.getId()));
        response.put("status", "Updated");
        return response;
    }

    private synchronized Project createOrUpdateProject(Integer projectId, AutomationRequest request) {
        try {
            ProjectDao projectDao = new ProjectDao();
            ModificationType type = ModificationType.UPDATE;
            Project project = new Project();

            if(projectId != null) {
                project  = projectDao.findByIdEager(projectId);
                project.setName(request.getName());
                project.setProductName(request.getProductName());
                project.setComments(request.getComments());
            } else {
                Workload workload = new Workload();
                List<Workload> workloads = new ArrayList<>();
                project.setName(request.getName());
                project.setProductName(request.getProductName());
                project.setComments(request.getComments());
                project.setCreator(TankConstants.TANK_USER_SYSTEM);
                workload.setName(request.getName());
                workloads.add(workload);
                workload.setParent(project);
                TestPlan testPlan = TestPlan.builder().name("Main").usersPercentage(100).build();
                workload.addTestPlan(testPlan);
                project.setWorkloads(workloads);
                project.setScriptDriver(ScriptDriver.Tank);
                project = projectDao.saveOrUpdateProject(project);
                type = ModificationType.ADD;
            }

            JobConfiguration jobConfiguration = project.getWorkloads().get(0).getJobConfiguration();
            jobConfiguration.setRampTimeExpression(request.getRampTime());
            jobConfiguration.setStopBehavior(request.getStopBehavior() != null ? request.getStopBehavior().name()
                    : StopBehavior.END_OF_SCRIPT_GROUP.name());
            jobConfiguration.setSimulationTimeExpression(request.getSimulationTime());
            jobConfiguration.setTerminationPolicy(request.getTerminationPolicy());
            jobConfiguration.setIncrementStrategy(request.getWorkloadType());
            jobConfiguration.setLocation(request.getLocation().name());
            jobConfiguration.setDataFileIds(Set.copyOf(request.getDataFileIds()));
            jobConfiguration.setUserIntervalIncrement(request.getUserIntervalIncrement());
            jobConfiguration.getJobRegions().clear();
            JobRegionDao jrd = new JobRegionDao();
            for (AutomationJobRegion r : request.getJobRegions()) {
                JobRegion jr = jrd.saveOrUpdate(new JobRegion(r.getRegion(), r.getUsers()));
                jobConfiguration.getJobRegions().add(jr);
            }

            Map<String, String> varMap = jobConfiguration.getVariables();
            varMap.putAll(request.getVariables());

            project = projectDao.saveOrUpdateProject(project);
            sendMsg(project, type);
            return project;
        } catch (Exception e) {
            LOGGER.error("Error creating project: " + e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("projects", "project", e);
        }
    }

    private void sendMsg(BaseEntity entity, ModificationType type) {
        MessageEventSender sender = new ServletInjector<MessageEventSender>().getManagedBean(servletContext, MessageEventSender.class);
        sender.sendEvent(new ModifiedEntityMessage(entity.getClass(), entity.getId(), type));
    }

    @Override
    public Map<String, StreamingResponseBody> downloadTestScriptForProject(Integer projectId) {
        try {
            StreamingResponseBody streamingResponse;
            Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
            Project p = new ProjectDao().loadScripts(projectId);
            if (p == null){
                return null;
            } else {
                String filename = "project_" + projectId + "_H.xml";
                final HDWorkload hdWorkload = ConverterUtil.convertWorkload(p.getWorkloads().get(0), p.getWorkloads().get(0).getJobConfiguration());
                streamingResponse = ResponseUtil.getXMLStream(hdWorkload);
                payload.put(filename, streamingResponse);
                return payload;
            }
        } catch (Exception e){
            LOGGER.error("Error downloading project harness file: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("projects", "project harness file", e);
        }
    }

    public String deleteProject(Integer projectId) {
        try {
            ProjectDao dao = new ProjectDao();
            Project project = dao.findByIdEager(projectId);
            if (project == null) {
                LOGGER.warn("Project with id " + projectId + " does not exist");
                return "Project with id " + projectId + " does not exist";
            } else {
                dao.delete(project);
                return "";
            }
        } catch (RuntimeException e) {
            LOGGER.error("Error deleting project: " + e, e);
            throw new GenericServiceDeleteException("project", "project", e);
        }
    }
}
