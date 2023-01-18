package com.intuit.tank.project;

import com.intuit.tank.ProjectBean;
import com.intuit.tank.util.Messages;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class WorkloadScriptsTest {

    @Mock
    private ProjectBean projectBean;
    @Mock
    private Messages messages;

    @InjectMocks
    private WorkloadScripts workloadScripts;

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
    public void test_copyTo() {
        // Arrange
        ScriptGroupStep scriptGroupStep = ScriptGroupStep.builder().build();
        ScriptGroup scriptGroup = ScriptGroup.builder().build();
        scriptGroup.addScriptGroupStep(scriptGroupStep);
        ArrayList<ScriptGroup> scriptGroups = new ArrayList<ScriptGroup>();
        scriptGroups.add(scriptGroup);
        TestPlan testPlan = TestPlan.builder().name("TESTPLAN").build();
        TestPlan testPlan2 = TestPlan.builder().name("TESTPLAN2").build();
        testPlan.setScriptGroups(scriptGroups);
        Workload workload = Workload.builder().addTestPlans(testPlan).addTestPlans(testPlan2).getInstance();
        Mockito.when(projectBean.getWorkload()).thenReturn(workload);
        // Act
        Workload copy = new Workload();
        workloadScripts.copyTo(copy);
        // Assert
        assertEquals(2, copy.getTestPlans().size());

        // Arrange
        Mockito.doNothing().when(messages).info(Mockito.anyString());
        //Act
        workloadScripts.deleteTestPlan(testPlan);
        workloadScripts.postConstruct();
    }
}
