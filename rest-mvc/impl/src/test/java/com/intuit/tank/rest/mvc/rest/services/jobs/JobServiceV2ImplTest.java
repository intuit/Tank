package com.intuit.tank.rest.mvc.rest.services.jobs;

import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.jobs.models.CreateJobRequest;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.util.JobDetailFormatter;
import com.intuit.tank.rest.mvc.rest.util.JobValidator;
import com.intuit.tank.rest.mvc.rest.util.ResponseUtil;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.util.TestParameterContainer;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class JobServiceV2ImplTest {

    @Test
    void addJobToQueue_clearsLoadedScriptSteps_evenWhenStoreScriptThrows() throws Exception {
        Script loadedScript = createScript("loaded-script", 11);
        Script savedScript = createScript("saved-script", 22);
        Workload loadedWorkload = createWorkload("loaded-workload", 111, loadedScript);
        Workload savedWorkload = createWorkload("saved-workload", 222, savedScript);
        Project project = createProject(loadedWorkload);
        CreateJobRequest request = createRequest();
        JobQueue queue = new JobQueue(project.getId());

        try (MockedStatic<ResponseUtil> responseUtil = Mockito.mockStatic(ResponseUtil.class);
             MockedStatic<JobDetailFormatter> jobDetailFormatter = Mockito.mockStatic(JobDetailFormatter.class);
             MockedStatic<TestParamUtil> testParamUtil = Mockito.mockStatic(TestParamUtil.class);
             MockedConstruction<JobValidator> ignoredValidator = Mockito.mockConstruction(JobValidator.class,
                     (mock, context) -> when(mock.getDurationMs(anyString())).thenReturn(0L));
             MockedConstruction<JobQueueDao> ignoredJobQueueDao = Mockito.mockConstruction(JobQueueDao.class,
                     (mock, context) -> {
                         when(mock.findOrCreateForProjectId(anyInt())).thenReturn(queue);
                         when(mock.saveOrUpdate(any(JobQueue.class))).thenReturn(queue);
                     });
             MockedConstruction<WorkloadDao> ignoredWorkloadDao = Mockito.mockConstruction(WorkloadDao.class,
                     (mock, context) -> when(mock.saveOrUpdate(any(Workload.class))).thenReturn(savedWorkload));
             MockedConstruction<JobInstanceDao> ignoredJobInstanceDao = Mockito.mockConstruction(JobInstanceDao.class,
                     (mock, context) -> when(mock.saveOrUpdate(any(JobInstance.class))).thenAnswer(invocation -> {
                         JobInstance savedJob = invocation.getArgument(0);
                         savedJob.setId(123);
                         return savedJob;
                     }));
             MockedConstruction<DataFileDao> ignoredDataFileDao = Mockito.mockConstruction(DataFileDao.class);
             MockedConstruction<JobNotificationDao> ignoredJobNotificationDao = Mockito.mockConstruction(JobNotificationDao.class)) {

            jobDetailFormatter.when(() -> JobDetailFormatter.createJobDetails(any(JobValidator.class), any(Workload.class), any(JobInstance.class)))
                    .thenReturn("job-details");
            testParamUtil.when(() -> TestParamUtil.evaluateTestTimes(anyLong(), anyString(), anyString()))
                    .thenReturn(TestParameterContainer.builder().withRampTime(0L).withSimulationTime(0L).build());
            responseUtil.when(() -> ResponseUtil.storeScript(anyString(), any(Workload.class), any(JobInstance.class)))
                    .thenThrow(new RuntimeException("boom"));

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> JobServiceV2Impl.addJobToQueue(project, request));

            assertEquals("boom", thrown.getMessage());
            assertTrue(loadedScript.getScriptSteps().isEmpty());
            assertTrue(savedScript.getScriptSteps().isEmpty());
        }
    }

    private static Project createProject(Workload workload) {
        Project project = new Project();
        project.setId(7);
        project.setName("project-name");
        project.addWorkload(workload);
        return project;
    }

    private static Workload createWorkload(String name, int id, Script script) {
        JobConfiguration jobConfiguration = new JobConfiguration();
        jobConfiguration.setLocation("us-west-2");
        jobConfiguration.setLoggingProfile("standard");
        jobConfiguration.setStopBehavior("stop");
        jobConfiguration.setVmInstanceType("m5.large");
        jobConfiguration.setNumUsersPerAgent(1);
        jobConfiguration.setReportingMode("standard");
        jobConfiguration.setRampTimeExpression("0");
        jobConfiguration.setSimulationTimeExpression("0");

        ScriptGroupStep groupStep = new ScriptGroupStep();
        groupStep.setScript(script);

        ScriptGroup group = new ScriptGroup();
        group.setName(name + "-group");
        group.addScriptGroupStep(groupStep);

        TestPlan testPlan = new TestPlan();
        testPlan.setName(name + "-plan");
        testPlan.addScriptGroup(group);

        Workload workload = new Workload();
        workload.setId(id);
        workload.setName(name);
        workload.setJobConfiguration(jobConfiguration);
        jobConfiguration.setParent(workload);
        workload.addTestPlan(testPlan);
        return workload;
    }

    private static Script createScript(String name, int id) {
        Script script = new Script();
        script.setId(id);
        script.setName(name);
        script.setScriptSteps(new ArrayList<>(List.of(new ScriptStep(), new ScriptStep())));
        return script;
    }

    private static CreateJobRequest createRequest() throws Exception {
        CreateJobRequest request = new CreateJobRequest("project-name");
        setField(request, "workloadType", IncrementStrategy.standard);
        return request;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
