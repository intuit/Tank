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

    private ProjectBean setupProjectBeanWithRealJobConfig() {
        ProjectBean mockProjectBean = mock(ProjectBean.class);
        JobConfiguration jobConfiguration = new JobConfiguration();
        Mockito.when(mockProjectBean.getJobConfiguration()).thenReturn(jobConfiguration);
        jobMaker.init(mockProjectBean);
        return mockProjectBean;
    }

    @Test
    public void testGetSetTankClientClass() {
        setupProjectBeanWithRealJobConfig();
        jobMaker.setTankClientClass("com.intuit.tank.client.DefaultTankClient");
        assertEquals("com.intuit.tank.client.DefaultTankClient", jobMaker.getTankClientClass());
    }

    @Test
    public void testGetSetLoggingProfile() {
        setupProjectBeanWithRealJobConfig();
        jobMaker.setLoggingProfile("standard");
        assertEquals("standard", jobMaker.getLoggingProfile());
    }

    @Test
    public void testGetSetStopBehavior() {
        setupProjectBeanWithRealJobConfig();
        jobMaker.setStopBehavior("stop");
        assertEquals("stop", jobMaker.getStopBehavior());
    }

    @Test
    public void testGetSetReportingMode() {
        setupProjectBeanWithRealJobConfig();
        jobMaker.setReportingMode("full");
        assertEquals("full", jobMaker.getReportingMode());
    }

    @Test
    public void testGetSetLocation() {
        setupProjectBeanWithRealJobConfig();
        jobMaker.setLocation("us-east-1");
        assertEquals("us-east-1", jobMaker.getLocation());
    }

    @Test
    public void testGetSetNumAgents() {
        setupProjectBeanWithRealJobConfig();
        jobMaker.setNumAgents(5);
        assertEquals(5, jobMaker.getNumAgents());
    }

    @Test
    public void testSetNumAgents_Zero_DoesNotChange() {
        setupProjectBeanWithRealJobConfig();
        int before = jobMaker.getNumAgents();
        jobMaker.setNumAgents(0);
        assertEquals(before, jobMaker.getNumAgents());
    }

    @Test
    public void testIsSetUseEips() {
        setupProjectBeanWithRealJobConfig();
        jobMaker.setUseEips(true);
        assertTrue(jobMaker.isUseEips());
        jobMaker.setUseEips(false);
        assertFalse(jobMaker.isUseEips());
    }

    @Test
    public void testIsSetUseTwoStep() {
        setupProjectBeanWithRealJobConfig();
        jobMaker.setUseTwoStep(true);
        assertTrue(jobMaker.isUseTwoStep());
        jobMaker.setUseTwoStep(false);
        assertFalse(jobMaker.isUseTwoStep());
    }

    @Test
    public void testGetSetSubmitInfo() {
        jobMaker.setSubmitInfo("some info");
        assertEquals("some info", jobMaker.getSubmitInfo());
    }

    @Test
    public void testSave_IsNoOp() {
        // save() is an empty method - just ensure it doesn't throw
        assertDoesNotThrow(() -> jobMaker.save());
    }

    @Test
    public void testCreateJobInstance_WhenDoSaveFalse_ProposedIsNull() {
        ProjectBean mockProjectBean = mock(ProjectBean.class);
        Mockito.when(mockProjectBean.doSave()).thenReturn(Boolean.FALSE);
        JobConfiguration mockJobConfiguration = mock(JobConfiguration.class);
        Mockito.when(mockProjectBean.getJobConfiguration()).thenReturn(mockJobConfiguration);
        jobMaker.init(mockProjectBean);
        jobMaker.createJobInstance();
        assertNull(jobMaker.getProposedJobInstance());
    }

    @Test
    public void testGetJobDetails_WhenNoProposedJobInstance_ReturnsIncomplete() {
        String result = jobMaker.getJobDetails();
        assertEquals("Job is incomplete.", result);
    }

    @Test
    public void testGetJobDetails_CachesResult() {
        String first = jobMaker.getJobDetails();
        String second = jobMaker.getJobDetails();
        assertSame(first, second);
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
