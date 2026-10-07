/**
 *  Copyright 2015-2026 Intuit Inc.
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
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.BulkDeleteResult;
import com.intuit.tank.rest.mvc.rest.models.PageResponse;
import com.intuit.tank.rest.mvc.rest.models.ProjectCopyRequest;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail;
import com.intuit.tank.rest.mvc.rest.models.ProjectSummary;
import com.intuit.tank.rest.mvc.rest.util.ProjectFixtures;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.ModificationType;
import com.intuit.tank.vm.settings.ModifiedEntityMessage;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProjectServiceV2ImplEditorTest {

    @InjectMocks
    private ProjectServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    private AutoCloseable mocks;
    private final List<MockedConstruction<?>> constructions = new ArrayList<>();
    private final MessageEventSender sender = mock(MessageEventSender.class);

    /** what the database holds, by project id */
    private final Map<Integer, Project> stored = new HashMap<>();
    private final List<Project> saved = new ArrayList<>();
    private final List<Integer> deletedIds = new ArrayList<>();
    private PagedQuery lastQuery;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        useConfig(true, Map.of(AccessRight.CREATE_PROJECT, List.of("creators")));
        actAs(user("alice"));
        stored.put(1, ProjectFixtures.project(1, "Load", "alice"));
        stored.put(2, ProjectFixtures.project(2, "Other", "carol"));

        constructions.add(Mockito.mockConstruction(ProjectDao.class, (dao, context) -> {
            when(dao.findByIdEager(anyInt())).thenAnswer(i -> fresh(i.getArgument(0)));
            when(dao.findById(anyInt())).thenAnswer(i -> fresh(i.getArgument(0)));
            when(dao.findByName(anyString())).thenAnswer(i -> stored.values().stream()
                    .filter(p -> p.getName().equals(i.getArgument(0))).findFirst().orElse(null));
            when(dao.saveOrUpdateProject(any())).thenAnswer(i -> {
                Project p = i.getArgument(0);
                if (p.getId() == 0) {
                    p.setId(99);
                }
                p.setModified(new Date(ProjectFixtures.MODIFIED.getTime() + 60_000));
                saved.add(p);
                stored.put(p.getId(), p);
                return p;
            });
            doAnswer(i -> deletedIds.add(i.getArgument(0))).when(dao).delete(anyInt());
            when(dao.findPaged(any())).thenAnswer(i -> {
                lastQuery = i.getArgument(0);
                return new PagedResult<>(List.of(stored.get(1)), 41);
            });
        }));
        constructions.add(Mockito.mockConstruction(ScriptDao.class, (dao, context) ->
                when(dao.findForIds(anyList())).thenAnswer(i -> ((List<Integer>) i.getArgument(0)).stream()
                        .filter(id -> id == 42 || id == 43).map(id -> ProjectFixtures.script(id, "s" + id)).toList())));
        constructions.add(Mockito.mockConstruction(DataFileDao.class, (dao, context) ->
                when(dao.findForIds(anyList())).thenAnswer(i -> ((List<Integer>) i.getArgument(0)).stream()
                        .filter(id -> id < 50).map(id -> {
                            DataFile f = new DataFile();
                            f.setId(id);
                            return f;
                        }).toList())));
        constructions.add(Mockito.mockConstruction(UserDao.class, (dao, context) ->
                when(dao.findByUserName(anyString())).thenAnswer(i -> "nobody".equals(i.getArgument(0)) ? null
                        : User.builder().name(i.getArgument(0)).build())));
        constructions.add(Mockito.mockConstruction(TankConfig.class, (config, context) -> {
            VmManagerConfig vm = mock(VmManagerConfig.class);
            when(vm.getConfiguredRegions()).thenReturn(List.of(VMRegion.US_EAST));
            when(config.getVmManagerConfig()).thenReturn(vm);
        }));
        constructions.add(Mockito.mockConstruction(ServletInjector.class, (injector, context) ->
                when(injector.getManagedBean(eq(servletContext), eq(MessageEventSender.class))).thenReturn(sender)));
    }

    @AfterEach
    void tearDown() throws Exception {
        constructions.forEach(MockedConstruction::close);
        mocks.close();
        reset();
    }

    /** a detached copy of what is stored, as each DAO lookup would return */
    private Project fresh(Integer id) {
        Project p = stored.get(id);
        if (p == null) {
            return null;
        }
        Project copy = ProjectFixtures.project(p.getId(), p.getName(), p.getCreator());
        copy.setModified(p.getModified());
        return copy;
    }

    private static ProjectDetail withName(ProjectDetail d, String name, String owner, Date modified) {
        return new ProjectDetail(d.id(), name, d.productName(), d.comments(), owner, null, modified, d.settings(),
                d.regions(), d.testPlans(), d.variables(), d.dataFileIds(), null);
    }

    private static ProjectDetail withData(ProjectDetail d, List<ProjectDetail.TestPlanDetail> plans, List<Integer> dataFiles) {
        return new ProjectDetail(d.id(), d.name(), d.productName(), d.comments(), d.owner(), null, d.modified(),
                d.settings(), d.regions(), plans, d.variables(), dataFiles, null);
    }

    private ModificationType lastEvent() {
        ArgumentCaptor<ModifiedEntityMessage> msg = ArgumentCaptor.forClass(ModifiedEntityMessage.class);
        verify(sender, atLeastOnce()).sendEvent(msg.capture());
        return msg.getValue().getType();
    }

    // list

    @Test
    void listProjects_buildsQueryAndPage() {
        PageResponse<ProjectSummary> page = service.listProjects(2, 20, "name,asc", "alice", " load ");
        assertEquals(41, page.total());
        assertEquals(2, page.page());
        assertEquals(20, page.size());
        assertEquals("Load", page.items().get(0).name());
        assertEquals("alice", page.items().get(0).owner());
        assertEquals("name", lastQuery.sortProperty());
        assertTrue(lastQuery.ascending());
        assertEquals(Map.of("creator", "alice"), lastQuery.equalTo());
        assertEquals("load", lastQuery.search());
        assertEquals(List.of("name", "productName", "comments"), lastQuery.searchProperties());
    }

    @Test
    void listProjects_defaultsAndValidation() {
        service.listProjects(0, null, null, null, null);
        assertEquals("modified", lastQuery.sortProperty());
        assertFalse(lastQuery.ascending());
        assertEquals(Map.of(), lastQuery.equalTo());
        assertThrows(GenericServiceBadRequestException.class, () -> service.listProjects(0, 10, "creator", null, null));
    }

    @Test
    void newEndpointsNeedAUser() {
        reset();
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.listProjects(0, 10, null, null, null));
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.getProjectDetail(1));
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.validateProject(1));
    }

    // get

    @Test
    void getProjectDetail_includesPermissions() {
        ProjectDetail mine = service.getProjectDetail(1);
        assertEquals("Load", mine.name());
        assertEquals(new ProjectDetail.Permissions(true, true, true), mine.permissions());
        ProjectDetail theirs = service.getProjectDetail(2);
        assertEquals(new ProjectDetail.Permissions(false, false, false), theirs.permissions());
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getProjectDetail(404));
    }

    // update

    @Test
    void update_savesAndReturnsStoredProject() {
        ProjectDetail result = service.updateProjectDetail(1, withName(ProjectFixtures.detail("Load", "alice"),
                "Load v2", "alice", ProjectFixtures.MODIFIED));
        assertEquals(1, saved.size());
        assertEquals("Load v2", saved.get(0).getName());
        assertEquals("Load v2", result.name());
        assertEquals(ProjectFixtures.MODIFIED.getTime() + 60_000, result.modified().getTime());
        assertEquals(ModificationType.UPDATE, lastEvent());
    }

    @Test
    void update_modifiedIsComparedToTheSecond() {
        Date sameSecond = new Date(ProjectFixtures.MODIFIED.getTime() + 999);
        assertDoesNotThrow(() -> service.updateProjectDetail(1,
                withName(ProjectFixtures.detail("Load", "alice"), "Load", "alice", sameSecond)));
    }

    @Test
    void update_staleModifiedIsAConflict() {
        Date earlier = new Date(ProjectFixtures.MODIFIED.getTime() - 5_000);
        assertThrows(GenericServiceConflictException.class, () -> service.updateProjectDetail(1,
                withName(ProjectFixtures.detail("Load", "alice"), "Load", "alice", earlier)));
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateProjectDetail(1,
                withName(ProjectFixtures.detail("Load", "alice"), "Load", "alice", null)));
        assertEquals(List.of(), saved);
    }

    @Test
    void update_nameTakenByAnotherProjectIsAConflict() {
        assertThrows(GenericServiceConflictException.class, () -> service.updateProjectDetail(1,
                withName(ProjectFixtures.detail("Load", "alice"), "Other", "alice", ProjectFixtures.MODIFIED)));
        assertEquals(List.of(), saved);
    }

    @Test
    void update_needsEditRightOrOwnership() {
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.updateProjectDetail(2,
                withName(ProjectFixtures.detail("Other", "carol"), "Other", "carol", ProjectFixtures.MODIFIED)));
        actAs(user("erin", "admin"));
        assertDoesNotThrow(() -> service.updateProjectDetail(2,
                withName(ProjectFixtures.detail("Other", "carol"), "Other", "carol", ProjectFixtures.MODIFIED)));
    }

    @Test
    void update_ownerChange() {
        // owner gives the project away
        service.updateProjectDetail(1, withName(ProjectFixtures.detail("Load", "alice"), "Load", "bob", ProjectFixtures.MODIFIED));
        assertEquals("bob", saved.get(0).getCreator());
        // an editor who is not the owner may not
        useConfig(true, Map.of(AccessRight.EDIT_PROJECT, List.of("editors")));
        actAs(user("dave", "editors"));
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.updateProjectDetail(2,
                withName(ProjectFixtures.detail("Other", "carol"), "Other", "dave", ProjectFixtures.MODIFIED)));
        // unknown users are rejected
        actAs(user("alice"));
        stored.put(1, ProjectFixtures.project(1, "Load", "alice"));
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateProjectDetail(1,
                withName(ProjectFixtures.detail("Load", "alice"), "Load", "nobody", ProjectFixtures.MODIFIED)));
    }

    @Test
    void update_rejectsInvalidDetailAndUnknownReferences() {
        ProjectDetail base = ProjectFixtures.detail("Load", "alice");
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateProjectDetail(1,
                withName(base, " ", "alice", ProjectFixtures.MODIFIED)));
        List<ProjectDetail.TestPlanDetail> unknownScript = List.of(new ProjectDetail.TestPlanDetail("Main", 100,
                List.of(new ProjectDetail.ScriptGroupDetail("G", 1, List.of(new ProjectDetail.ScriptRef(77, null, 1))))));
        GenericServiceBadRequestException e = assertThrows(GenericServiceBadRequestException.class,
                () -> service.updateProjectDetail(1, withData(base, unknownScript, List.of())));
        assertTrue(e.getMessage().contains("[77]"), e.getMessage());
        e = assertThrows(GenericServiceBadRequestException.class,
                () -> service.updateProjectDetail(1, withData(base, base.testPlans(), List.of(7, 60))));
        assertTrue(e.getMessage().contains("[60]"), e.getMessage());
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateProjectDetail(1, null));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.updateProjectDetail(404, base));
        assertEquals(List.of(), saved);
    }

    // copy

    @Test
    void copy_createsProjectOwnedByCaller() {
        actAs(user("bob", "creators"));
        ProjectDetail copy = service.copyProject(1, new ProjectCopyRequest(" Load copy "));
        assertEquals(99, copy.id());
        assertEquals("Load copy", saved.get(0).getName());
        assertEquals("bob", saved.get(0).getCreator());
        assertEquals(1, saved.get(0).getWorkloads().get(0).getTestPlans().size());
        assertEquals(ModificationType.ADD, lastEvent());
    }

    @Test
    void copy_checks() {
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.copyProject(1, new ProjectCopyRequest("x")));
        actAs(user("bob", "creators"));
        assertThrows(GenericServiceBadRequestException.class, () -> service.copyProject(1, new ProjectCopyRequest(" ")));
        assertThrows(GenericServiceBadRequestException.class, () -> service.copyProject(1, null));
        assertThrows(GenericServiceConflictException.class, () -> service.copyProject(1, new ProjectCopyRequest("Other")));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.copyProject(404, new ProjectCopyRequest("y")));
        assertEquals(List.of(), saved);
    }

    // bulk delete

    @Test
    void deleteProjects_deletesAndReportsMissing() {
        BulkDeleteResult result = service.deleteProjects(List.of(1, 404, 1));
        assertEquals(List.of(1), result.deleted());
        assertEquals(List.of(404), result.notFound());
        assertEquals(List.of(1), deletedIds);
        assertEquals(ModificationType.DELETE, lastEvent());
    }

    @Test
    void deleteProjects_forbiddenOneDeletesNone() {
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.deleteProjects(List.of(1, 2)));
        assertEquals(List.of(), deletedIds);
    }

    @Test
    void deleteProjects_limits() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.deleteProjects(List.of()));
        assertThrows(GenericServiceBadRequestException.class, () -> service.deleteProjects(null));
        List<Integer> tooMany = java.util.stream.IntStream.rangeClosed(1, ProjectServiceV2Impl.MAX_BULK_DELETE + 1)
                .boxed().toList();
        assertThrows(GenericServiceBadRequestException.class, () -> service.deleteProjects(tooMany));
    }

    @Test
    void sameSecond() {
        assertTrue(ProjectServiceV2Impl.sameSecond(new Date(1000), new Date(1999)));
        assertFalse(ProjectServiceV2Impl.sameSecond(new Date(1000), new Date(2000)));
        assertFalse(ProjectServiceV2Impl.sameSecond(null, new Date(1000)));
    }
}
