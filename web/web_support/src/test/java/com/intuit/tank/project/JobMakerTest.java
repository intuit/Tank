package com.intuit.tank.project;

import com.intuit.tank.PreferencesBean;
import com.intuit.tank.ProjectBean;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.event.JobEvent;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.event.Event;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class JobMakerTest {

    @InjectMocks
    private JobMaker jobMaker;

    @Mock
    private Event<JobQueue> jobQueueEvent;

    @Mock
    private Event<JobEvent> jobEventProducer;

    @Mock
    private Messages messages;

    @Mock
    private PreferencesBean preferences;

    @Mock
    private UsersAndTimes usersAndTimes;

    @Mock
    private TankSecurityContext securityContext;

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
    public void testGetName() {
        ProjectBean mockProjectBean = mock(ProjectBean.class);
        JobConfiguration mockJobConfiguration = mock(JobConfiguration.class);
        Mockito.when(mockJobConfiguration.getIncrementStrategy()).thenReturn(IncrementStrategy.increasing);
        Mockito.when(mockJobConfiguration.getWorkload()).thenReturn(mock(Workload.class));
        Mockito.when(mockProjectBean.getJobConfiguration()).thenReturn(mockJobConfiguration);
        Mockito.when(usersAndTimes.getTotalUsers()).thenReturn(10);
        Mockito.when(preferences.getTimestampFormat()).thenReturn(FastDateFormat.getInstance("yyyy.MM.dd-HH:mm:ss.S z"));
        jobMaker.init(mockProjectBean);
        assertTrue(jobMaker.getName().contains("_10_users_"));

        Mockito.when(mockJobConfiguration.getIncrementStrategy()).thenReturn(IncrementStrategy.standard);
        jobMaker.init(mockProjectBean);
        assertTrue(jobMaker.getName().contains("_nonlinear_"));
    }

    @Test
    public void testSetVmInstanceType() {
        jobMaker.setVmInstanceType("");

        ProjectBean projectBean = mock(ProjectBean.class);
        JobConfiguration mockJobConfiguration = mock(JobConfiguration.class);
        Mockito.when(projectBean.getJobConfiguration()).thenReturn(mockJobConfiguration);
        jobMaker.init(projectBean);
        jobMaker.setVmInstanceType("m8g.2xlarge");
    }

    @Test void testTargetRatePerAgent() {
        ProjectBean mockProjectBean = mock(ProjectBean.class);
        JobConfiguration jobConfiguration = new JobConfiguration();
        Mockito.when(mockProjectBean.getJobConfiguration()).thenReturn(jobConfiguration);
        jobMaker.init(mockProjectBean);
        jobMaker.setTargetRatePerAgent(10.0);
        assertEquals(10.0, jobMaker.getTargetRatePerAgent());
    }

    @Test
    public void testCreateJobInstance() {
        ProjectBean mockProjectBean = mock(ProjectBean.class);
        JobConfiguration mockJobConfiguration = mock(JobConfiguration.class);
        Workload mockWorkload = mock(Workload.class);
        Mockito.when(mockWorkload.getJobConfiguration()).thenReturn(mockJobConfiguration);
        Mockito.when(mockJobConfiguration.getIncrementStrategy()).thenReturn(IncrementStrategy.increasing);
        Mockito.when(mockJobConfiguration.getWorkload()).thenReturn(mockWorkload);
        Mockito.when(mockJobConfiguration.getDataFileIds()).thenReturn(Set.of(1));
        Mockito.when(mockJobConfiguration.getNotifications()).thenReturn(Set.of(new JobNotification()));
        Mockito.when(mockProjectBean.getWorkload()).thenReturn(mockWorkload);
        Mockito.when(mockProjectBean.getJobConfiguration()).thenReturn(mockJobConfiguration);
        Mockito.when(mockProjectBean.doSave()).thenReturn(Boolean.TRUE);
        jobMaker.init(mockProjectBean);
        Mockito.when(preferences.getTimestampFormat()).thenReturn(FastDateFormat.getInstance("yyyy.MM.dd-HH:mm:ss.S z"));
        Mockito.when(securityContext.getCallerPrincipal()).thenReturn(() -> "TEST_USER");
        try (MockedStatic<JobDetailFormatter> mockedStatic = Mockito.mockStatic(JobDetailFormatter.class)) {
            mockedStatic.when(() -> JobDetailFormatter.createJobDetails(any(), any(), any())).thenReturn("TEST_JOB_DETAILS");
            jobMaker.createJobInstance();
            assertEquals("TEST_JOB_DETAILS", jobMaker.getJobDetails());
        }
    }

    @Test
    public void testAddJobToQueue() {
        jobMaker.proposedJobInstance = new JobInstance();
        ProjectBean mockProjectBean = mock(ProjectBean.class);
        JobConfiguration mockJobConfiguration = mock(JobConfiguration.class);
        Workload mockWorkload = mock(Workload.class);
        Project mockProject = mock(Project.class);
        Mockito.when(mockProjectBean.getProject()).thenReturn(mockProject);
        Mockito.when(mockJobConfiguration.getIncrementStrategy()).thenReturn(IncrementStrategy.increasing);
        Mockito.when(mockJobConfiguration.getWorkload()).thenReturn(mockWorkload);
        Mockito.when(mockProjectBean.getJobConfiguration()).thenReturn(mockJobConfiguration);
        Mockito.when(mockProjectBean.getWorkload()).thenReturn(mockWorkload);
        Mockito.when(mockWorkload.getProject()).thenReturn(mockProject);
        Mockito.when(usersAndTimes.getTotalUsers()).thenReturn(10);
        Mockito.when(preferences.getTimestampFormat()).thenReturn(FastDateFormat.getInstance("yyyy.MM.dd-HH:mm:ss.S z"));
        jobMaker.init(mockProjectBean);
        jobMaker.addJobToQueue();
        assertNull(jobMaker.getProposedJobInstance());
    }

    @Test
    public void testIsValid() {
        jobMaker.proposedJobInstance = null;
        assertFalse(jobMaker.isValid());

        jobMaker.proposedJobInstance = new JobInstance();
        assertFalse(jobMaker.isValid());

        jobMaker.setName("TEST");
        jobMaker.getProposedJobInstance().setIncrementStrategy(IncrementStrategy.increasing);
        assertFalse(jobMaker.isValid());

        jobMaker.getProposedJobInstance().setIncrementStrategy(IncrementStrategy.standard);
        jobMaker.getProposedJobInstance().setTerminationPolicy(TerminationPolicy.time);
        jobMaker.getProposedJobInstance().setSimulationTime(0);
        assertFalse(jobMaker.isValid());

        jobMaker.getProposedJobInstance().setSimulationTime(10);
        ProjectBean mockProjectBean = mock(ProjectBean.class);
        Workload mockWorkload = mock(Workload.class);
        Mockito.when(mockProjectBean.getWorkload()).thenReturn(mockWorkload);
        jobMaker.init(mockProjectBean);
        assertFalse(jobMaker.isValid());

        TestPlan testPlan = TestPlan.builder().build();
        ScriptGroup scriptGroup = ScriptGroup.builder().build();
        scriptGroup.addScriptGroupStep(ScriptGroupStep.builder().build());
        testPlan.addScriptGroup(scriptGroup);
        Mockito.when(mockWorkload.getTestPlans()).thenReturn(List.of(testPlan));
        JobConfiguration mockJobConfiguration = mock(JobConfiguration.class);
        Mockito.when(mockWorkload.getJobConfiguration()).thenReturn(mockJobConfiguration);
        assertFalse(jobMaker.isValid());

        Mockito.when(mockJobConfiguration.getJobRegions()).thenReturn(
                Set.of(JobRegion.builder().percentage("100").build()));
        assertTrue(jobMaker.isValid());
    }
}
