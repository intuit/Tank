package com.intuit.tank.rest.mvc.rest.controllers.services;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.rest.mvc.rest.cloud.MessageEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceDeleteException;
import com.intuit.tank.rest.mvc.rest.models.projects.AutomationJobRegion;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.rest.mvc.rest.models.projects.AutomationRequest;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.rest.mvc.rest.models.datafiles.DataFileDescriptor;
import com.intuit.tank.vm.api.enumerated.Location;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.project.Workload;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;
import com.intuit.tank.rest.mvc.rest.models.projects.ProjectContainer;
import com.intuit.tank.rest.mvc.rest.services.projects.ProjectServiceV2Impl;
import com.intuit.tank.rest.mvc.rest.util.ProjectServiceUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;


import javax.servlet.ServletContext;
import java.util.*;

public class ProjectServiceV2ImplTest {

    @Test
    public void testPing() {
        ProjectServiceV2Impl service = new ProjectServiceV2Impl();
        assertTrue(service.ping().contains("PONG ProjectServiceV2"));
    }

    @Test
    public void testGetAllProjects() {
        ProjectDao mockDao = mock(ProjectDao.class);
        Project mockProject = mock(Project.class);
        ProjectTO mockProjectTO = mock(ProjectTO.class);
        when(mockDao.findAll()).thenReturn(Arrays.asList(mockProject));

        try (MockedStatic<ProjectServiceUtil> mockedProjectServiceUtil = Mockito.mockStatic(ProjectServiceUtil.class)) {
            mockedProjectServiceUtil.when(() -> ProjectServiceUtil.projectToTransferObject(mockProject))
                    .thenReturn(mockProjectTO);

            ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
                @Override
                protected ProjectDao createProjectDao() {
                    return mockDao;
                }
            };

            ProjectContainer result = service.getAllProjects();

            assertNotNull(result);
            assertEquals(1, result.getProjects().size());
        }
    }

    @Test
    public void testGetAllProjectsException() {

        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                throw new RuntimeException("trigger exception");
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class,
                service::getAllProjects);
    }

    @Test
    public void testGetAllProjectNames() {
        ProjectDao mockDao = mock(ProjectDao.class);
        Project mockProject = mock(Project.class);
        when(mockDao.findAllFast()).thenReturn(Arrays.asList(mockProject));
        when(mockProject.getId()).thenReturn(1);
        when(mockProject.getName()).thenReturn("testProject");
        when(mockProject.getModified()).thenReturn(null);

        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                return mockDao;
            }
        };

        Map<Integer, String> result = service.getAllProjectNames();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(1));
        assertEquals("testProject", result.get(1));
    }

    @Test
    public void testGetAllProjectNamesException() {

        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                throw new RuntimeException("trigger exception");
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class,
                service::getAllProjectNames);
    }

    @Test
    public void testGetProject() {
        ProjectDao mockDao = mock(ProjectDao.class);
        Project mockProject = mock(Project.class);
        ProjectTO mockProjectTO = mock(ProjectTO.class);

        when(mockDao.findByIdEager(anyInt())).thenReturn(mockProject);

        try (MockedStatic<ProjectServiceUtil> mockedProjectServiceUtil = Mockito.mockStatic(ProjectServiceUtil.class)) {
            mockedProjectServiceUtil.when(() -> ProjectServiceUtil.projectToTransferObject(mockProject))
                    .thenReturn(mockProjectTO);

            ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
                @Override
                protected ProjectDao createProjectDao() {
                    return mockDao;
                }
            };

            ProjectTO result = service.getProject(1);
            assertNotNull(result);
        }
    }

    @Test
    public void testGetProjectException() {
        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                throw new RuntimeException("trigger exception");
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getProject(2));
    }

    @Test
    public void testCreateOrUpdateProject() {
        ProjectDao mockDao = mock(ProjectDao.class);
        Project mockProject = mock(Project.class);
        Workload mockWorkload = mock(Workload.class);
        JobConfiguration mockJobConfiguration = mock(JobConfiguration.class);
        AutomationRequest mockRequest = mock(AutomationRequest.class);
        JobRegionDao mockJobRegionDao = mock(JobRegionDao.class);
        JobRegion mockJobRegion = mock(JobRegion.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        MessageEventSender mockMessageEventSender = mock(MessageEventSender.class);
        ServletInjector<MessageEventSender> mockServletInjector = mock(ServletInjector.class);

        when(mockProject.getWorkloads()).thenReturn(Collections.singletonList(mockWorkload));
        when(mockWorkload.getJobConfiguration()).thenReturn(mockJobConfiguration);
        when(mockJobConfiguration.getJobRegions()).thenReturn(new HashSet<>());
        when(mockJobConfiguration.getVariables()).thenReturn(new HashMap<>());
        when(mockProject.getId()).thenReturn(7);
        when(mockDao.saveOrUpdateProject(any(Project.class))).thenReturn(mockProject);

        when(mockRequest.getName()).thenReturn("Test Project");
        when(mockRequest.getProductName()).thenReturn("Test Product");
        when(mockRequest.getComments()).thenReturn("Test Comments");
        when(mockRequest.getJobRegions()).thenReturn(new HashSet<>());
        when(mockRequest.getVariables()).thenReturn(new HashMap<>());
        when(mockRequest.getDataFileIds()).thenReturn(new ArrayList<>());
        when(mockRequest.getRampTime()).thenReturn("1");
        when(mockRequest.getSimulationTime()).thenReturn("1");
        when(mockRequest.getUserIntervalIncrement()).thenReturn(1);
        when(mockRequest.getLocation()).thenReturn(Location.unspecified);

        when(mockJobRegionDao.saveOrUpdate(any(JobRegion.class))).thenReturn(mockJobRegion);

        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(MessageEventSender.class))).thenReturn(mockMessageEventSender);

        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                return mockDao;
            }

            @Override
            protected JobRegionDao createJobRegionDao() {
                return mockJobRegionDao;
            }

            @Override
            protected ServletContext getServletContext() {
                return mockServletContext;
            }

            @Override
            protected ServletInjector<MessageEventSender> getServletInjector() {
                return mockServletInjector;
            }
        };

        // Create
        Map<String, String> result = service.createProject(mockRequest);

        assertNotNull(result);
        assertEquals("7", result.get("ProjectId"));
        assertEquals("Created", result.get("status"));


        // Update
        when(mockDao.findByIdEager(7)).thenReturn(mockProject);
        result = service.updateProject(7, mockRequest);

        assertNotNull(result);
        assertEquals("7", result.get("ProjectId"));
        assertEquals("Updated", result.get("status"));

        // Update but doesn't exist
        result = service.updateProject(9999, mockRequest);

        assertNotNull(result);
        assertEquals("project with that project Id does not exist", result.get("error"));

    }

    @Test
    public void testDownloadTestScriptForProject() {
        ProjectDao mockDao = mock(ProjectDao.class);
        Project mockProject = mock(Project.class);
        Workload mockWorkload = mock(Workload.class);
        JobConfiguration mockJobConfig = mock(JobConfiguration.class);
        HDWorkload mockHDWorkload = mock(HDWorkload.class);
        StreamingResponseBody mockStream = mock(StreamingResponseBody.class);

        when(mockDao.loadScripts(3)).thenReturn(mockProject);
        when(mockProject.getWorkloads()).thenReturn(Collections.singletonList(mockWorkload));
        when(mockWorkload.getJobConfiguration()).thenReturn(mockJobConfig);

        try (MockedStatic<ConverterUtil> mockedConverterUtil = Mockito.mockStatic(ConverterUtil.class);
             MockedStatic<ResponseUtil> mockedResponseUtil = Mockito.mockStatic(ResponseUtil.class)) {

            mockedConverterUtil.when(() -> ConverterUtil.convertWorkload(mockWorkload, mockJobConfig))
                    .thenReturn(mockHDWorkload);
            mockedResponseUtil.when(() -> ResponseUtil.getXMLStream(mockHDWorkload))
                    .thenReturn(mockStream);

            ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
                @Override
                protected ProjectDao createProjectDao() {
                    return mockDao;
                }
            };

            Map<String, StreamingResponseBody> result = service.downloadTestScriptForProject(3);

            assertNotNull(result);
            assertEquals(1, result.size());

        }

        when(mockDao.loadScripts(9999)).thenReturn(null);
        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                return mockDao;
            }
        };
        Map<String, StreamingResponseBody> invalidResult = service.downloadTestScriptForProject(9999);
        assertNull(invalidResult);
    }

    @Test
    public void testDownloadTestScriptForProjectException() {
        ProjectDao mockDao = mock(ProjectDao.class);

        when(mockDao.loadScripts(1)).thenThrow(new RuntimeException("trigger exception"));

        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                return mockDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.downloadTestScriptForProject(1));
    }



    @Test
    public void testDeleteProject() {
        ProjectDao mockDao = mock(ProjectDao.class);
        Project mockProject = mock(Project.class);
        when(mockDao.findByIdEager(1)).thenReturn(mockProject);

        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                return mockDao;
            }
        };

        // Exists
        String deleteResult = service.deleteProject(1);
        assertEquals("", deleteResult);

        // Doesn't exist
        when(mockDao.findByIdEager(999)).thenReturn(null);
        String invalidDeleteResult = service.deleteProject(999);
        assertEquals("Project with id 999 does not exist", invalidDeleteResult);
    }

    @Test
    public void testDeleteProjectException() {
        ProjectDao mockDao = mock(ProjectDao.class);
        when(mockDao.findByIdEager(1)).thenThrow(new RuntimeException("trigger exception"));

        ProjectServiceV2Impl service = new ProjectServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                return mockDao;
            }
        };

        assertThrows(GenericServiceDeleteException.class, () -> service.deleteProject(1));
    }
}
