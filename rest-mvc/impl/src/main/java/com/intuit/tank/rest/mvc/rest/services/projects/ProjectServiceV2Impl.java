/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.projects;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.PagedQuery;
import com.intuit.tank.dao.PagedResult;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.UserDao;
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
import com.intuit.tank.projects.models.*;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.models.ProjectCopyRequest;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail;
import com.intuit.tank.rest.mvc.rest.models.ProjectSummary;
import com.intuit.tank.rest.mvc.rest.models.ProjectValidation;
import com.intuit.tank.rest.mvc.rest.util.PageRequests;
import com.intuit.tank.rest.mvc.rest.util.ProjectCopier;
import com.intuit.tank.rest.mvc.rest.util.ProjectDetailMapper;
import com.intuit.tank.rest.mvc.rest.util.ProjectValidator;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.rest.mvc.rest.util.ProjectServiceUtil;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.vm.api.enumerated.Location;
import com.intuit.tank.vm.api.enumerated.ScriptDriver;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import jakarta.servlet.ServletContext;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
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
            List<Project> all = new ProjectDao().findAllFast();
            List<ProjectTO> projects = all.stream().map(p ->
                    ProjectTO.builder()
                            .withId(p.getId())
                            .withName(p.getName())
                            .withProductName(p.getProductName())
                            .withComments(p.getComments())
                            .withCreator(p.getCreator())
                            .withCreated(p.getCreated())
                            .withModified(p.getModified())
                            .build())
                    .collect(Collectors.toList());
            return ProjectContainer.builder().withProjects(projects).build();
        } catch (Exception e) {
            LOGGER.error("Error returning all projects: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("projects", "all project", e);
        }
    }

    @Override
    public Map<Integer, String> getAllProjectNames(){
        try {
            List<Project> all = new ProjectDao().findAllFast();
            return all.stream().sorted(Comparator.comparing(Project::getModified).reversed())
                               .collect(Collectors.toMap(Project::getId, Project::getName, (e1, e2) -> e1, LinkedHashMap::new));
        } catch (Exception e) {
            LOGGER.error("Error returning all project names: {}", e.getMessage(), e);
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
        RestAuthorization.requireRight(AccessRight.CREATE_PROJECT, "projects");
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
        Project existing = projectDao.findByIdEager(projectId);
        if(existing == null){
            response.put("error", "project with that project Id does not exist");
            return response;
        }
        RestAuthorization.requireRightOrOwner(AccessRight.EDIT_PROJECT, existing, "projects");
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
                if(request.getName() != null) {
                    project.setName(request.getName());
                }
                if(request.getProductName() != null) {
                    project.setProductName(request.getProductName());
                }
                if(request.getComments() != null) {
                    project.setComments(request.getComments());
                }
                if(request.getTestPlans() != null && !request.getTestPlans().isEmpty()){
                    project.getWorkloads().get(0).getTestPlans().clear(); // overwrite existing test plans
                    addTestPlans(project.getWorkloads().get(0), request);
                }
            } else {
                if(request.getName() != null) {
                    checkProjectName(request.getName());
                }
                Workload workload = new Workload();
                List<Workload> workloads = new ArrayList<>();
                if(request.getName() != null) {
                    project.setName(request.getName());
                }
                if(request.getProductName() != null) {
                    project.setProductName(request.getProductName());
                }
                if(request.getComments() != null) {
                    project.setComments(request.getComments());
                }
                project.setCreator(RestAuthorization.currentUserName());
                if(request.getName() != null) {
                    workload.setName(request.getName());
                }
                if(request.getTestPlans() != null && !request.getTestPlans().isEmpty()){
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
            if(request.getRampTime() != null) {
                jobConfiguration.setRampTimeExpression(request.getRampTime());
            }
            jobConfiguration.setStopBehavior(request.getStopBehavior() != null ? request.getStopBehavior().name()
                    : StopBehavior.END_OF_SCRIPT_GROUP.name());
            if(request.getSimulationTime() != null) {
                jobConfiguration.setSimulationTimeExpression(request.getSimulationTime());
            }
            if(request.getTerminationPolicy() != null) {
                jobConfiguration.setTerminationPolicy(request.getTerminationPolicy());
            }
            if(request.getWorkloadType() != null) {
                jobConfiguration.setIncrementStrategy(request.getWorkloadType());
            }
            if(request.getLocation() != null) {
                String location = jobConfiguration.getLocation() != null ? jobConfiguration.getLocation() : Location.unspecified.name();
                jobConfiguration.setLocation(request.getLocation() != null ? request.getLocation().name() : location);
            }
            if(request.getDataFileIds() != null) {
                jobConfiguration.setDataFileIds(Set.copyOf(request.getDataFileIds()));
            }
            jobConfiguration.setUserIntervalIncrement(request.getUserIntervalIncrement());

            if(request.getJobRegions() != null) {
                jobConfiguration.getJobRegions().clear();
                JobRegionDao jrd = new JobRegionDao();
                for (AutomationJobRegion r : request.getJobRegions()) {
                    JobRegion jr = jrd.saveOrUpdate(new JobRegion(r.getRegion(), r.getUsers()));
                    jobConfiguration.getJobRegions().add(jr);
                }
            }

            Map<String, String> varMap = jobConfiguration.getVariables();
            if(request.getVariables() != null) {
                varMap.putAll(request.getVariables());
            }
            project = projectDao.saveOrUpdateProject(project);
            sendMsg(project, type);
            return project;
        } catch (Exception e) {
            LOGGER.error("Error creating project: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("projects", e.getMessage(), e);
        }
    }

    private void sendMsg(BaseEntity entity, ModificationType type) {
        MessageEventSender sender = new ServletInjector<MessageEventSender>().getManagedBean(servletContext, MessageEventSender.class);
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
                        LOGGER.error("Script with script id {} does not exist and cannot be added to Test Plan {}", scriptId, tp.getName());
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
                LOGGER.error("project - A project named {} already exists", name);
                throw new GenericServiceBadRequestException("projects", "creating project",
                        "A project named " + name + " already exists");
            }
        } catch (GenericServiceBadRequestException e) {
            throw e;
        } catch (Exception e) {
            // ignore jakarta.persistence.NoResultException: project name does not exist
        }
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
            LOGGER.error("Error downloading project harness file: {}", e.getMessage(), e);
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
                RestAuthorization.requireRightOrOwner(AccessRight.DELETE_PROJECT, project, "projects");
                dao.delete(project);
                return "";
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (RuntimeException e) {
            LOGGER.error("Error deleting project: {}", e, e);
            throw new GenericServiceDeleteException("project", "project", e);
        }
    }

    private static final String SERVICE = "projects";
    static final int MAX_BULK_DELETE = 100;

    private static final Map<String, String> SORTABLE_FIELDS = Map.of(
            "id", BaseEntity.PROPERTY_ID,
            "name", Project.PROPERTY_NAME,
            "productName", Project.PROPERTY_PRODUCT_NAME,
            "owner", Project.PROPERTY_CREATOR,
            "created", BaseEntity.PROPERTY_CREATE,
            "modified", BaseEntity.PROPERTY_MODIFIED);

    @Override
    public PageResponse<ProjectSummary> listProjects(Integer page, Integer size, String sort, String owner, String q) {
        RestAuthorization.requireUser(SERVICE);
        Map<String, Object> filters = new HashMap<>();
        filters.put(Project.PROPERTY_CREATOR, owner);
        PagedQuery query = PageRequests.toQuery(SERVICE, page, size, sort, SORTABLE_FIELDS, "modified,desc", filters, q,
                List.of(Project.PROPERTY_NAME, Project.PROPERTY_PRODUCT_NAME, Project.PROPERTY_COMMENTS));
        PagedResult<Project> result = new ProjectDao().findPaged(query);
        List<ProjectSummary> items = result.items().stream()
                .map(p -> new ProjectSummary(p.getId(), p.getName(), p.getProductName(), p.getComments(),
                        p.getCreator(), p.getCreated(), p.getModified()))
                .collect(Collectors.toList());
        return new PageResponse<>(items, result.total(), query.page(), query.size());
    }

    @Override
    public ProjectDetail getProjectDetail(Integer projectId) {
        RestAuthorization.requireUser(SERVICE);
        return toDetail(findProject(projectId));
    }

    @Override
    public synchronized ProjectDetail updateProjectDetail(Integer projectId, ProjectDetail detail) {
        RestAuthorization.requireUser(SERVICE);
        if (detail == null) {
            throw new GenericServiceBadRequestException(SERVICE, "project", "request body is required");
        }
        Project project = findProject(projectId);
        RestAuthorization.requireRightOrOwner(AccessRight.EDIT_PROJECT, project, SERVICE);
        if (detail.modified() == null) {
            throw new GenericServiceBadRequestException(SERVICE, "modified",
                    "modified is required; send the value from the last GET");
        }
        if (!sameSecond(detail.modified(), project.getModified())) {
            throw new GenericServiceConflictException(SERVICE,
                    "Project " + projectId + " was changed by someone else since it was loaded; reload it and try again");
        }
        List<String> errors = ProjectDetailMapper.validate(detail);
        if (!errors.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "project", String.join("; ", errors));
        }
        if (!detail.owner().equals(project.getCreator())) {
            // as in the web UI, only the owner or an admin may give a project away
            if (!RestAuthorization.isOwner(project)) {
                RestAuthorization.requireAdmin(SERVICE);
            }
            if (new UserDao().findByUserName(detail.owner()) == null) {
                throw new GenericServiceBadRequestException(SERVICE, "owner", "no user named " + detail.owner());
            }
        }
        requireNameAvailable(detail.name().trim(), projectId);
        Map<Integer, Script> scripts = findScripts(ProjectDetailMapper.scriptIds(detail));
        requireDataFiles(detail.dataFileIds());

        ProjectDetailMapper.apply(detail, project, scripts);
        try {
            project = new ProjectDao().saveOrUpdateProject(project);
        } catch (RuntimeException e) {
            LOGGER.error("Error saving project {}: {}", projectId, e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, "project", e);
        }
        sendMsg(project, ModificationType.UPDATE);
        LOGGER.info("{} saved project {}", RestAuthorization.currentUserName(), projectId);
        return toDetail(findProject(projectId));
    }

    @Override
    public synchronized ProjectDetail copyProject(Integer projectId, ProjectCopyRequest request) {
        RestAuthorization.requireUser(SERVICE);
        RestAuthorization.requireRight(AccessRight.CREATE_PROJECT, SERVICE);
        String name = request != null ? StringUtils.trimToNull(request.name()) : null;
        if (name == null) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name is required");
        }
        if (name.length() > 255) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name must be at most 255 characters");
        }
        Project source = findProject(projectId);
        requireNameAvailable(name, null);
        Project copy = ProjectCopier.copy(source, name, RestAuthorization.currentUserName());
        try {
            copy = new ProjectDao().saveOrUpdateProject(copy);
        } catch (RuntimeException e) {
            LOGGER.error("Error copying project {}: {}", projectId, e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, "project copy", e);
        }
        sendMsg(copy, ModificationType.ADD);
        LOGGER.info("{} copied project {} to {} ({})", RestAuthorization.currentUserName(), projectId, copy.getId(), name);
        return toDetail(findProject(copy.getId()));
    }

    @Override
    public BulkDeleteResult deleteProjects(List<Integer> projectIds) {
        RestAuthorization.requireUser(SERVICE);
        if (projectIds == null || projectIds.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "ids", "at least one id is required");
        }
        List<Integer> ids = projectIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.size() > MAX_BULK_DELETE) {
            throw new GenericServiceBadRequestException(SERVICE, "ids", "at most " + MAX_BULK_DELETE + " ids at a time");
        }
        ProjectDao dao = new ProjectDao();
        List<Project> found = new ArrayList<>();
        List<Integer> notFound = new ArrayList<>();
        for (Integer id : ids) {
            Project project = dao.findById(id);
            if (project == null) {
                notFound.add(id);
            } else {
                found.add(project);
            }
        }
        // check every project before deleting any, so a forbidden one leaves all of them in place
        for (Project project : found) {
            RestAuthorization.requireRightOrOwner(AccessRight.DELETE_PROJECT, project, SERVICE);
        }
        List<Integer> deleted = new ArrayList<>();
        for (Project project : found) {
            try {
                dao.delete(project.getId());
            } catch (RuntimeException e) {
                LOGGER.error("Error deleting project {}: {}", project.getId(), e.getMessage(), e);
                throw new GenericServiceDeleteException(SERVICE, "project " + project.getId()
                        + (deleted.isEmpty() ? "" : " (already deleted: " + deleted + ")"), e);
            }
            deleted.add(project.getId());
            sendMsg(project, ModificationType.DELETE);
        }
        LOGGER.info("{} deleted projects {}", RestAuthorization.currentUserName(), deleted);
        return new BulkDeleteResult(deleted, notFound);
    }

    @Override
    public ProjectValidation validateProject(Integer projectId) {
        RestAuthorization.requireUser(SERVICE);
        return ProjectValidator.validate(findProject(projectId));
    }

    private Project findProject(Integer projectId) {
        Project project = projectId != null ? new ProjectDao().findByIdEager(projectId) : null;
        if (project == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "project " + projectId, null);
        }
        return project;
    }

    private ProjectDetail toDetail(Project project) {
        boolean owner = RestAuthorization.isOwner(project);
        ProjectDetail.Permissions permissions = new ProjectDetail.Permissions(
                owner || RestAuthorization.hasRight(AccessRight.EDIT_PROJECT),
                owner || RestAuthorization.hasRight(AccessRight.DELETE_PROJECT),
                owner || RestAuthorization.isAdmin());
        TankConfig config = new TankConfig();
        return ProjectDetailMapper.toDetail(project, permissions, config.getVmManagerConfig().getConfiguredRegions(),
                config.getStandalone());
    }

    private static void requireNameAvailable(String name, Integer projectId) {
        Project existing = new ProjectDao().findByName(name);
        if (existing != null && (projectId == null || existing.getId() != projectId)) {
            throw new GenericServiceConflictException(SERVICE, "A project named " + name + " already exists");
        }
    }

    private static Map<Integer, Script> findScripts(Set<Integer> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        Map<Integer, Script> scripts = new ScriptDao().findForIds(new ArrayList<>(ids)).stream()
                .collect(Collectors.toMap(Script::getId, s -> s));
        List<Integer> missing = ids.stream().filter(id -> !scripts.containsKey(id)).sorted().toList();
        if (!missing.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "scripts", "no scripts with ids " + missing);
        }
        return scripts;
    }

    private static void requireDataFiles(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        Set<Integer> wanted = new HashSet<>(ids);
        Set<Integer> found = new DataFileDao().findForIds(new ArrayList<>(wanted)).stream()
                .map(DataFile::getId).collect(Collectors.toSet());
        List<Integer> missing = wanted.stream().filter(id -> !found.contains(id)).sorted().collect(Collectors.toList());
        if (!missing.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "dataFileIds", "no data files with ids " + missing);
        }
    }

    /**
     * Compares save times to the second: the database may not store milliseconds.
     */
    static boolean sameSecond(Date a, Date b) {
        return a != null && b != null && a.getTime() / 1000 == b.getTime() / 1000;
    }
}
