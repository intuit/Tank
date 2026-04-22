package com.intuit.tank.rest.mvc.rest.services.projects;

import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.ScriptDao;
import com.intuit.tank.project.*;
import com.intuit.tank.projects.models.*;
import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.util.ProjectServiceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import jakarta.servlet.ServletContext;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProjectServiceV2ImplTest {

    @InjectMocks
    private ProjectServiceV2Impl service;

    @Mock
    private ServletContext servletContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void ping_returnsPong() {
        assertTrue(service.ping().contains("PONG"));
    }

    // =====================================================================
    // getAllProjects
    // =====================================================================

    @Test
    void getAllProjects_returnsProjects() {
        Project p = createProject(1, "TestProject");

        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findAllFast()).thenReturn(List.of(p)))) {

            ProjectContainer result = service.getAllProjects();
            assertNotNull(result);
            assertEquals(1, result.getProjects().size());
            assertEquals("TestProject", result.getProjects().get(0).getName());
        }
    }

    @Test
    void getAllProjects_throwsOnError() {
        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findAllFast()).thenThrow(new RuntimeException("DB error")))) {

            assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getAllProjects());
        }
    }

    // =====================================================================
    // getAllProjectNames
    // =====================================================================

    @Test
    void getAllProjectNames_returnsNameMap() {
        Project p1 = createProject(1, "ProjectA");
        Project p2 = createProject(2, "ProjectB");

        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findAllFast()).thenReturn(List.of(p1, p2)))) {

            Map<Integer, String> result = service.getAllProjectNames();
            assertEquals(2, result.size());
            assertEquals("ProjectA", result.get(1));
        }
    }

    // =====================================================================
    // getProject
    // =====================================================================

    @Test
    void getProject_returnsProject() {
        Project p = createProject(1, "Found");
        ProjectTO to = ProjectTO.builder().withId(1).withName("Found").build();

        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findByIdEager(1)).thenReturn(p));
             MockedStatic<ProjectServiceUtil> utilMock = Mockito.mockStatic(ProjectServiceUtil.class)) {
            utilMock.when(() -> ProjectServiceUtil.projectToTransferObject(p)).thenReturn(to);

            ProjectTO result = service.getProject(1);
            assertNotNull(result);
            assertEquals("Found", result.getName());
        }
    }

    // =====================================================================
    // createProject
    // =====================================================================

    @Test
    void createProject_createsNewProject() {
        Project savedProject = createProject(42, "NewProject");
        AutomationRequest request = AutomationRequest.builder()
                .withName("NewProject")
                .withProductName("MyProduct")
                .withComments("test")
                .build();

        MessageEventSender mockSender = mock(MessageEventSender.class);

        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> {
                    when(mock.findByName("NewProject")).thenReturn(null);
                    when(mock.saveOrUpdateProject(any(Project.class))).thenReturn(savedProject);
                });
             MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(MessageEventSender.class)))
                        .thenReturn(mockSender))) {

            Map<String, String> result = service.createProject(request);
            assertEquals("42", result.get("ProjectId"));
            assertEquals("Created", result.get("status"));
        }
    }

    // =====================================================================
    // updateProject
    // =====================================================================

    @Test
    void updateProject_updatesExistingProject() {
        Project existingProject = createProject(1, "Existing");
        AutomationRequest request = AutomationRequest.builder()
                .withName("Updated")
                .build();

        MessageEventSender mockSender = mock(MessageEventSender.class);

        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> {
                    when(mock.findByIdEager(1)).thenReturn(existingProject);
                    when(mock.saveOrUpdateProject(any(Project.class))).thenReturn(existingProject);
                });
             MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(MessageEventSender.class)))
                        .thenReturn(mockSender))) {

            Map<String, String> result = service.updateProject(1, request);
            assertEquals("Updated", result.get("status"));
        }
    }

    @Test
    void updateProject_returnsErrorWhenNotFound() {
        AutomationRequest request = AutomationRequest.builder().withName("X").build();

        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findByIdEager(999)).thenReturn(null))) {

            Map<String, String> result = service.updateProject(999, request);
            assertTrue(result.containsKey("error"));
        }
    }

    // =====================================================================
    // deleteProject
    // =====================================================================

    @Test
    void deleteProject_deletesExisting() {
        Project p = createProject(1, "ToDelete");

        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findByIdEager(1)).thenReturn(p))) {

            String result = service.deleteProject(1);
            assertEquals("", result);

            ProjectDao dao = daoMock.constructed().get(0);
            verify(dao).delete(p);
        }
    }

    @Test
    void deleteProject_returnsMessageWhenNotFound() {
        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findByIdEager(999)).thenReturn(null))) {

            String result = service.deleteProject(999);
            assertTrue(result.contains("does not exist"));
        }
    }

    @Test
    void deleteProject_throwsOnError() {
        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.findByIdEager(anyInt())).thenThrow(new RuntimeException("error")))) {

            assertThrows(GenericServiceDeleteException.class, () -> service.deleteProject(1));
        }
    }

    // =====================================================================
    // createOrUpdateProject with test plans
    // =====================================================================

    @Test
    void createProject_withTestPlans_assemblesCorrectly() {
        Script script = new Script();
        script.setId(10);
        script.setName("TestScript");

        Project savedProject = createProject(42, "WithPlans");

        AutomationScriptGroupStep step = AutomationScriptGroupStep.builder()
                .withScriptId(10).withLoop(2).build();
        AutomationScriptGroup sg = AutomationScriptGroup.builder()
                .withName("SG1").withLoop(1).withScripts(List.of(step)).build();
        AutomationTestPlan tp = AutomationTestPlan.builder()
                .withName("Plan1").withUserPercentage(100).withScriptGroups(List.of(sg)).build();

        AutomationRequest request = AutomationRequest.builder()
                .withName("WithPlans")
                .withTestPlans(List.of(tp))
                .build();

        MessageEventSender mockSender = mock(MessageEventSender.class);

        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> {
                    when(mock.findByName("WithPlans")).thenReturn(null);
                    when(mock.saveOrUpdateProject(any(Project.class))).thenReturn(savedProject);
                });
             MockedConstruction<ScriptDao> scriptDaoMock = Mockito.mockConstruction(ScriptDao.class,
                (mock, ctx) -> when(mock.findById(10)).thenReturn(script));
             MockedConstruction<ServletInjector> injectorMock = Mockito.mockConstruction(ServletInjector.class,
                (mock, ctx) -> when(mock.getManagedBean(eq(servletContext), eq(MessageEventSender.class)))
                        .thenReturn(mockSender))) {

            Map<String, String> result = service.createProject(request);
            assertEquals("42", result.get("ProjectId"));
        }
    }

    // =====================================================================
    // downloadTestScriptForProject
    // =====================================================================

    @Test
    void downloadTestScriptForProject_returnsNullWhenNotFound() {
        try (MockedConstruction<ProjectDao> daoMock = Mockito.mockConstruction(ProjectDao.class,
                (mock, ctx) -> when(mock.loadScripts(999)).thenReturn(null))) {

            assertNull(service.downloadTestScriptForProject(999));
        }
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private static Project createProject(int id, String name) {
        Project p = new Project();
        p.setId(id);
        p.setName(name);
        p.setCreated(new Date());
        p.setModified(new Date());
        p.setCreator("System");

        Workload workload = new Workload();
        workload.setName(name);
        JobConfiguration jc = new JobConfiguration();
        workload.setJobConfiguration(jc);
        jc.setParent(workload);
        TestPlan testPlan = TestPlan.builder().name("Main").usersPercentage(100).build();
        workload.addTestPlan(testPlan);

        List<Workload> workloads = new ArrayList<>();
        workloads.add(workload);
        p.setWorkloads(workloads);
        workload.setParent(p);

        return p;
    }
}
