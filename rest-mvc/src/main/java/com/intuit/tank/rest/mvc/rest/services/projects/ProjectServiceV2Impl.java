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
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.Workload;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.models.projects.*;
import com.intuit.tank.rest.mvc.rest.util.ProjectServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.util.ScriptServiceUtil;
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
import javax.persistence.NoResultException;

@Service
public class ProjectServiceV2Impl implements ProjectServiceV2 {

    @Autowired
    private ServletContext servletContext;

    private static final Logger LOGGER = LogManager.getLogger(ProjectServiceV2Impl.class);

    protected ProjectDao createProjectDao() {
        return new ProjectDao();
    }

    protected JobRegionDao createJobRegionDao() {
        return new JobRegionDao();
    }

    protected ServletContext getServletContext() {
        return servletContext;
    }

    protected ServletInjector<MessageEventSender> getServletInjector() {
        return new ServletInjector<>();
    }

    @Override
    public String ping() {
        return "PONG " + getClass().getInterfaces()[0].getSimpleName();
    }

    @Override
    public ProjectContainer getAllProjects(){
        try {
            List<Project> all = createProjectDao().findAll();
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
            List<Project> all = createProjectDao().findAllFast();
            return all.stream().sorted(Comparator.comparing(Project::getModified).reversed())
                               .collect(Collectors.toMap(Project::getId, Project::getName, (e1, e2) -> e1, LinkedHashMap::new));
        } catch (Exception e) {
            LOGGER.error("Error returning all project names: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("projects", "all project names", e);
        }
    }

    @Override
    public ProjectTO getProject(Integer projectId){
        try {
            Project prj = createProjectDao().findByIdEager(projectId);
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
        ProjectDao projectDao = createProjectDao();
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
            ProjectDao projectDao = createProjectDao();
            ModificationType type = ModificationType.UPDATE;
            Project project = new Project();

            if(projectId != null) {
                project  = projectDao.findByIdEager(projectId);
                project.setName(request.getName());
                project.setProductName(request.getProductName());
                project.setComments(request.getComments());
                if(!request.getTestPlans().isEmpty()){
                    project.getWorkloads().get(0).getTestPlans().clear(); // overwrite existing test plans
                    addTestPlans(project.getWorkloads().get(0), request);
                }
            } else {
                checkProjectName(request.getName());
                Workload workload = new Workload();
                List<Workload> workloads = new ArrayList<>();
                project.setName(request.getName());
                project.setProductName(request.getProductName());
                project.setComments(request.getComments());
                project.setCreator(TankConstants.TANK_USER_SYSTEM);
                workload.setName(request.getName());
                if(!request.getTestPlans().isEmpty()){
                    addTestPlans(workload, request);
                } else {
                    TestPlan testPlan = TestPlan.builder().name("Main").usersPercentage(100).build();
                    workload.addTestPlan(testPlan);
                }
                workloads.add(workload);
                workload.setParent(project);
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
            JobRegionDao jrd = createJobRegionDao();
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
            throw new GenericServiceCreateOrUpdateException("projects", e.getMessage(), e);
        }
    }

    private void sendMsg(BaseEntity entity, ModificationType type) {
        MessageEventSender sender = getServletInjector().getManagedBean(getServletContext(), MessageEventSender.class);
        sender.sendEvent(new ModifiedEntityMessage(entity.getClass(), entity.getId(), type));
    }

    private void addTestPlans(Workload workload, AutomationRequest request){
        for (AutomationTestPlan tp : request.getTestPlans()) {
            List<ScriptGroup> scriptGroups = new ArrayList<ScriptGroup>();
            for(AutomationScriptGroup sg : tp.getScriptGroups()){
                List<ScriptGroupStep> scripts = new ArrayList<ScriptGroupStep>();
                for(AutomationScriptGroupStep sgs: sg.getScripts()){
                    Integer scriptId = sgs.getScriptId();
                    ScriptDao dao = new ScriptDao();
                    Script script = dao.findById(scriptId);
                    ScriptGroupStep entry = new ScriptGroupStep();
                    if (script != null) {
                        entry.setScript(script);
                        entry.setLoop(sgs.getLoop());
                        scripts.add(entry); // add scripts in order they appear in scripts payload
                    } else {
                        LOGGER.error("Script with script id " + scriptId + " does not exist and cannot be added to Test Plan " + tp.getName());
                        throw new GenericServiceBadRequestException("projects", "updating project",
                                "project - Script with script id " + scriptId + " does not exist and cannot be added to Test Plan " + tp.getName());
                    }
                }
                ScriptGroup entry = new ScriptGroup();
                entry.setName(sg.getName());
                entry.setLoop(sg.getLoop());
                entry.setScriptGroupSteps(scripts);
                scriptGroups.add(entry); // add script group in order they appear in script group payload
            }

            TestPlan testPlan = TestPlan.builder()
                    .name(tp.getName())
                    .usersPercentage(tp.getUserPercentage())
                    .withScriptGroups(scriptGroups)
                    .build();

            workload.addTestPlan(testPlan); // add test plans in order they appear in test plans payload
        }
    }

    private void checkProjectName(String name){
        try {
            ProjectDao projectDao = new ProjectDao();
            if(projectDao.findByName(name) != null){
                LOGGER.error("project - Cannot change the name of the existing project " + name);
                throw new GenericServiceBadRequestException("projects", "updating project",
                        "project - Cannot change the name of the existing project " + name);
            }
        } catch (GenericServiceBadRequestException e) {
            throw e;
        } catch (Exception e) {
            // ignore javax.persistence.NoResultException: project name does not exist
        }
    }

    @Override
    public Map<String, StreamingResponseBody> downloadTestScriptForProject(Integer projectId) {
        try {
            StreamingResponseBody streamingResponse;
            Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
            Project p = createProjectDao().loadScripts(projectId);
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

    @Override
    public String deleteProject(Integer projectId) {
        try {
            ProjectDao dao = createProjectDao();
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
