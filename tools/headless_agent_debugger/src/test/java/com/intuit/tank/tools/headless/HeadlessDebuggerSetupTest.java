//package com.intuit.tank.tools.debugger;
//
//import com.intuit.tank.harness.data.HDTestPlan;
//import com.intuit.tank.rest.mvc.rest.clients.ProjectClient;
//import com.intuit.tank.rest.mvc.rest.models.projects.ProjectTO;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class HeadlessDebuggerSetupTest {
//
//    private static HeadlessDebuggerSetup headlessDebuggerSetup;
//    private static ProjectClient mockProjectClient;
//
//    @BeforeAll
//    public static void setup() {
//        mockProjectClient = Mockito.mock(ProjectClient.class);
//        Mockito.when(mockProjectClient.getProject(anyInt())).thenReturn(new ProjectTO());
//        Mockito.when(mockProjectClient.downloadTestScriptForProject(anyInt())).thenReturn("someStringXml");
//        headlessDebuggerSetup = new HeadlessDebuggerSetup("http://localhost:8008/", 39);
//        assertNotNull(headlessDebuggerSetup);
//    }
//
//    @Test
//    public void testCurrentTestPlan() {
//        HDTestPlan hdTestPlan = new HDTestPlan();
//        headlessDebuggerSetup.setCurrentTestPlan(hdTestPlan);
//        assertEquals(hdTestPlan, headlessDebuggerSetup.getCurrentTestPlan());
//        assertEquals(-1, headlessDebuggerSetup.getCurrentRunningStep());
//    }
//}
