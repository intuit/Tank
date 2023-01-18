package com.intuit.tank.project;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.PreferencesBean;
import com.intuit.tank.ProjectBean;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.common.util.ReportUtil;
import com.intuit.tank.vm.event.JobEvent;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertFalse;

@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class JobMakerTest {

    @Mock
    private ProjectBean projectBean;
    @Mock
    private UsersAndTimes usersAndTimes;
    @Mock
    private PreferencesBean preferences;
    @Mock
    private TankSecurityContext securityContext;
    @Mock
    private JobQueueDao jobQueueDao;
    @Mock
    private Event<JobQueue> jobQueueEvent;
    @Mock
    private Messages messages;
    @Mock
    private Event<JobEvent> jobEventProducer;

    @InjectMocks
    private JobMaker jobMaker;

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
    public void test_createJobInstance() {
        // Arrange
        Project project = Project.builder().name("DUMMYPROJECT").build();
        Workload workload = new Workload();
        workload.setProject(project);
        Mockito.when(projectBean.doSave()).thenReturn(Boolean.TRUE);
        Mockito.when(usersAndTimes.getTotalUsers()).thenReturn(10);
        Mockito.when(preferences.getTimestampFormat()).thenReturn(FastDateFormat.getInstance(ReportUtil.DATE_FORMAT));
        Mockito.when(projectBean.getWorkload()).thenReturn(workload);
        Mockito.when(projectBean.getJobConfiguration()).thenReturn(workload.getJobConfiguration());
        Principal principal = () -> "TESTDUMMY";
        Mockito.when(securityContext.getCallerPrincipal()).thenReturn(principal);
        // Act
        jobMaker.createJobInstance();
        boolean result = jobMaker.isValid();

        // Assert
        assertFalse(result);

        // Arrange
        Mockito.when(projectBean.getProject()).thenReturn(project);
        Mockito.when(jobQueueDao.findOrCreateForProjectId(Mockito.anyInt())).thenReturn(new JobQueue());
        Mockito.doNothing().when(jobQueueEvent).fire(Mockito.any(JobQueue.class));
        Mockito.doNothing().when(messages).info(Mockito.anyString());
        Mockito.doNothing().when(jobEventProducer).fire(Mockito.any(JobEvent.class));
        // Act
        AWSXRay.beginSegment("test");
        jobMaker.addJobToQueue();
        AWSXRay.clearTraceEntity();
    }
}
