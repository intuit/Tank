package com.intuit.tank.rest.mvc.rest.controllers.services;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.models.jobs.CreateJobRequest;
import com.intuit.tank.rest.mvc.rest.services.jobs.JobServiceV2Impl;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobContainer;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobTO;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;

import static org.mockito.Mockito.*;
import com.intuit.tank.rest.mvc.rest.util.JobServiceUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.List;

public class JobServiceV2ImplTest {

    @Test
    public void testPing() {
        JobServiceV2Impl service = new JobServiceV2Impl();
        assertTrue(service.ping().contains("PONG JobServiceV2"));
    }

    @Test
    public void testGetJob() {
        JobInstanceDao mockDao = mock(JobInstanceDao.class);
        JobInstance mockJobInstance = mock(JobInstance.class);


        when(mockDao.findById(6)).thenReturn(mockJobInstance);


        // Mock JobServiceUtil
        try (MockedStatic<JobServiceUtil> mockedJobServiceUtil = Mockito.mockStatic(JobServiceUtil.class)) {
            JobTO mockJobTO = mock(JobTO.class);
            mockedJobServiceUtil.when(() -> JobServiceUtil.jobToTO(mockJobInstance)).thenReturn(mockJobTO);

            JobServiceV2Impl service = new JobServiceV2Impl() {
                @Override
                protected JobInstanceDao createJobInstanceDao() {
                    return mockDao;
                }
            };

            JobTO result = service.getJob(6);
            assertNotNull(result);
        }
    }

    @Test
    public void testGetJobException() {
        JobInstanceDao mockDao = mock(JobInstanceDao.class);
        when(mockDao.findById(8)).thenThrow(new RuntimeException("trigger exception"));

        JobServiceV2Impl service = new JobServiceV2Impl();
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getJob(8));
    }

    @Test
    public void testGetJobsByProject() {
        ProjectDao mockProjectDao = mock(ProjectDao.class);
        JobQueueDao mockJobQueueDao = mock(JobQueueDao.class);
        Project mockProject = mock(Project.class);
        JobQueue mockJobQueue = mock(JobQueue.class);

        when(mockProjectDao.findByIdEager(2)).thenReturn(mockProject);
        when(mockJobQueueDao.findOrCreateForProjectId(2)).thenReturn(mockJobQueue);

        try (MockedStatic<JobServiceUtil> mockedJobServiceUtil = Mockito.mockStatic(JobServiceUtil.class)) {
            JobTO mockJobTO = mock(JobTO.class);
            mockedJobServiceUtil.when(() -> JobServiceUtil.jobToTO(any())).thenReturn(mockJobTO);

            JobServiceV2Impl service = new JobServiceV2Impl() {
                @Override
                protected ProjectDao createProjectDao() {
                    return mockProjectDao;
                }

                @Override
                protected JobQueueDao createJobQueueDao() {
                    return mockJobQueueDao;
                }
            };

            JobContainer result = service.getJobsByProject(2);
            assertNotNull(result);
        }
    }

    @Test
    public void testGetJobsByProjectException() {
        ProjectDao mockProjectDao = mock(ProjectDao.class);

        when(mockProjectDao.findByIdEager(1)).thenThrow(new RuntimeException("trigger exception"));

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            protected ProjectDao createProjectDao() {
                return mockProjectDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getJobsByProject(1));
    }


    @Test
    public void testGetAllJobs() {
        JobInstanceDao mockDao = mock(JobInstanceDao.class);
        List<JobInstance> jobs = Collections.singletonList(mock(JobInstance.class));

        when(mockDao.findAll()).thenReturn(jobs);

        try (MockedStatic<JobServiceUtil> mockedJobServiceUtil = Mockito.mockStatic(JobServiceUtil.class)) {
            JobTO mockJobTO = mock(JobTO.class);
            mockedJobServiceUtil.when(() -> JobServiceUtil.jobToTO(any())).thenReturn(mockJobTO);

            JobServiceV2Impl service = new JobServiceV2Impl() {
                @Override
                protected JobInstanceDao createJobInstanceDao() {
                    return mockDao;
                }
            };

            JobContainer result = service.getAllJobs();
            assertNotNull(result);
        }
    }

    @Test
    public void testGetAllJobsException() {
        JobInstanceDao mockDao = mock(JobInstanceDao.class);

        when(mockDao.findAll()).thenThrow(new RuntimeException("trigger exception"));

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            protected JobInstanceDao createJobInstanceDao() {
                return mockDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, service::getAllJobs);
    }

    @Test
    public void testGetJobStatus() {
        JobInstanceDao mockJobInstanceDao = mock(JobInstanceDao.class);
        JobInstance mockJobInstance = mock(JobInstance.class);
        when(mockJobInstanceDao.findById(anyInt())).thenReturn(mockJobInstance);
        when(mockJobInstance.getStatus()).thenReturn(JobQueueStatus.Running);

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            protected JobInstanceDao createJobInstanceDao() {
                return mockJobInstanceDao;
            }
        };

        String status = service.getJobStatus(1);
        assertEquals("Running", status);
    }

    @Test
    public void testGetJobStatusException() {
        JobInstanceDao mockJobInstanceDao = mock(JobInstanceDao.class);
        when(mockJobInstanceDao.findById(anyInt())).thenThrow(new RuntimeException("trigger exception"));

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            protected JobInstanceDao createJobInstanceDao() {
                return mockJobInstanceDao;
            }
        };

        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getJobStatus(1));
    }

    @Test
    public void testGetJobVMStatus() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        CloudVmStatusContainer mockStatusContainer = mock(CloudVmStatusContainer.class);
        when(mockJobEventSender.getVmStatusForJob("1")).thenReturn(mockStatusContainer);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            protected ServletContext getServletContext() {
                return mockServletContext;
            }
            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }
        };

        CloudVmStatusContainer status = service.getJobVMStatus("1");
        assertNotNull(status);
    }


    @Test
    public void testGetJobVMStatusException() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        CloudVmStatusContainer mockStatusContainer = mock(CloudVmStatusContainer.class);
        when(mockJobEventSender.getVmStatusForJob("1")).thenReturn(mockStatusContainer);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            protected ServletContext getServletContext() {
                return mockServletContext;
            }
            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }
        };

        when(mockJobEventSender.getVmStatusForJob("2")).thenThrow(new RuntimeException("Error"));
        assertThrows(GenericServiceResourceNotFoundException.class, () -> service.getJobVMStatus("2"));
    }

    @Test
    public void testStartJob() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            public String getJobStatus(Integer jobId) {
                return "Runnning";
            }
            @Override
            protected ServletContext getServletContext() {
                return mockServletContext;
            }
            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }
            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };

        String result = service.startJob(15);
        assertEquals("Runnning", result);
    }

    @Test
    public void testStartJobException() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {

            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }

            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };
        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).startJob(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.startJob(11));
    }

    @Test
    public void testStopJob() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            public String getJobStatus(Integer jobId) {
                return "Stopped";
            }
            @Override
            protected ServletContext getServletContext() {
                return mockServletContext;
            }
            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }
            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };

        String result = service.stopJob(19);
        assertEquals("Stopped", result);
    }

    @Test
    public void testStopJobException() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {

            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }

            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };
        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).stopJob(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.stopJob(14));
    }

    @Test
    public void testPauseJob() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            public String getJobStatus(Integer jobId) {
                return "RampPaused";
            }
            @Override
            protected ServletContext getServletContext() {
                return mockServletContext;
            }
            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }
            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };

        String result = service.pauseJob(20);
        assertEquals("RampPaused", result);
    }

    @Test
    public void testPauseJobException() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {

            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }

            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };
        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).pauseRampJob(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.pauseJob(21));
    }

    @Test
    public void testResumeJob() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            public String getJobStatus(Integer jobId) {
                return "Running";
            }
            @Override
            protected ServletContext getServletContext() {
                return mockServletContext;
            }
            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }
            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };

        String result = service.resumeJob(23);
        assertEquals("Running", result);
    }

    @Test
    public void testResumeJobException() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {

            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }

            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };
        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).resumeRampJob(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.resumeJob(29));
    }

    @Test
    public void testKillJob() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletContext mockServletContext = mock(ServletContext.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {
            @Override
            public String getJobStatus(Integer jobId) {
                return "Completed";
            }
            @Override
            protected ServletContext getServletContext() {
                return mockServletContext;
            }
            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }
            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };

        String result = service.killJob(28);
        assertEquals("Completed", result);
    }

    @Test
    public void testKillJobException() {
        JobEventSender mockJobEventSender = mock(JobEventSender.class);
        ServletInjector<JobEventSender> mockServletInjector = mock(ServletInjector.class);
        when(mockServletInjector.getManagedBean(any(ServletContext.class), eq(JobEventSender.class))).thenReturn(mockJobEventSender);

        JobServiceV2Impl service = new JobServiceV2Impl() {

            @Override
            protected ServletInjector<JobEventSender> getServletInjector() {
                return mockServletInjector;
            }

            @Override
            protected void annotateXRaySegment(String key, Number value) {
                // do nothing
            }
        };
        doThrow(new RuntimeException("trigger exception")).when(mockJobEventSender).killJob(anyString());
        assertThrows(GenericServiceCreateOrUpdateException.class, () -> service.killJob(39));
    }
}
