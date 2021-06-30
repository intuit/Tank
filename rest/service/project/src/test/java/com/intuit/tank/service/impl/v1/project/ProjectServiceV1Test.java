package com.intuit.tank.service.impl.v1.project;

import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.service.util.ResponseUtil;
import com.intuit.tank.vm.api.enumerated.ScriptDriver;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import javax.ws.rs.core.Response;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectServiceV1Test {

    @InjectMocks
    ProjectServiceV1 projectServiceV1;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void testPing() {
        assertEquals("PONG ProjectServiceV1", projectServiceV1.ping());
    }

    @Test
    public void testDeleteProjectEmtpy() {
        Response response = projectServiceV1.deleteProject(0);

        assertNotNull(response);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteProjectPost() {
        Project project = new Project();
        project.setId(1);
        project.setName("ProjectName");
        project.setCreator("System");
        project.setScriptDriver(ScriptDriver.Tank);
        project.setComments("Comments");
        project.setProductName("Product Name");
        Workload workload = new Workload();
        workload.setName("WorkloadName");
        project.addWorkload(workload);
        project = new ProjectDao().saveOrUpdate(project);

        Response response = projectServiceV1.deleteProjectPost(project.getId());

        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testRunProjectEmtpy() {
        Response response = projectServiceV1.runProject(0);

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDownloadTestScriptForProject() {
        Response response = projectServiceV1.downloadTestScriptForProject(0);

        assertNotNull(response);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAddJobToQueue() {
        Project project = new Project();
        project.setId(1);
        project.setName("ProjectName");
        project.setCreator("System");
        project.setScriptDriver(ScriptDriver.Tank);
        project.setComments("Comments");
        project.setProductName("Product Name");
        Workload workload = new Workload();
        workload.setName("WorkloadName");
        project.addWorkload(workload);
        project = new ProjectDao().saveOrUpdate(project);

        try (MockedStatic<ResponseUtil> responseUtilMockedStatic = Mockito.mockStatic(ResponseUtil.class)) {
            responseUtilMockedStatic.when(() -> ResponseUtil
                    .storeScript(Mockito.anyString(), Mockito.any(Workload.class), Mockito.any(JobInstance.class)))
                    .thenAnswer((Answer<Void>) invocation -> null);
            JobInstance jobInstance = projectServiceV1.addJobToQueue(project);

            assertNotNull(jobInstance);
        }

    }
}
