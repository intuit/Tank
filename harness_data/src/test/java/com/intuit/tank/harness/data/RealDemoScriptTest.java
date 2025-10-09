package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.intuit.tank.test.JaxbUtil;
import com.intuit.tank.test.TestGroups;

/**
 * Test to verify the real websocket-demo-script.xml parses correctly.
 * This validates that our JAXB configuration works with the actual demo script.
 */
public class RealDemoScriptTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testLoadRealDemoScript() throws Exception {
        // Given: The real websocket-demo-script.xml file (in project root)
        File scriptFile = new File("../websocket-demo-script.xml");
        
        // Skip test if file doesn't exist (e.g., in CI environment)
        if (!scriptFile.exists()) {
            System.out.println("Skipping test - websocket-demo-script.xml not found at: " + scriptFile.getAbsolutePath());
            return;
        }

        // When: We load the workload
        String xml = Files.readString(scriptFile.toPath());
        HDWorkload workload = JaxbUtil.unmarshall(xml, HDWorkload.class);

        // Then: Workload should be parsed successfully
        assertNotNull(workload);
        assertEquals("Stock Investment Upgrade Flow Demo", workload.getName());
        assertEquals("TurboTax-style upgrade flow: Deluxe → Premium for stock transactions", 
                     workload.getDescription());

        // And: Should have the expected structure
        List<HDTestPlan> plans = workload.getPlans();
        assertNotNull(plans);
        assertEquals(1, plans.size());

        HDTestPlan plan = plans.get(0);
        assertEquals("Main", plan.getTestPlanName());
        assertEquals(100, plan.getUserPercentage());

        // And: Should have test groups
        List<HDScriptGroup> groups = plan.getGroup();
        assertNotNull(groups);
        assertTrue(groups.size() > 0);

        // And: Should have scripts with steps
        HDScriptGroup group = groups.get(0);
        List<HDScript> scripts = group.getGroupSteps();
        assertNotNull(scripts);
        assertTrue(scripts.size() > 0);

        HDScript script = scripts.get(0);
        List<HDScriptUseCase> useCases = script.getUseCase();
        assertNotNull(useCases);
        assertTrue(useCases.size() > 0);

        HDScriptUseCase useCase = useCases.get(0);
        List<TestStep> steps = useCase.getScriptSteps();
        assertNotNull(steps);
        
        // The demo script should have multiple steps
        assertTrue(steps.size() > 10, "Demo script should have multiple steps");

        System.out.println("\n=== Demo Script Analysis ===");
        System.out.println("Total steps: " + steps.size());
        System.out.println("\nStep breakdown:");

        int httpSteps = 0;
        int wsSteps = 0;

        for (int i = 0; i < steps.size(); i++) {
            TestStep step = steps.get(i);
            String stepType = step.getClass().getSimpleName();
            String stepInfo = step.getInfo();
            
            if (step instanceof WebSocketStep) {
                wsSteps++;
                WebSocketStep wsStep = (WebSocketStep) step;
                System.out.println(String.format("  [%d] %s - %s (action: %s, connectionId: %s)", 
                    i, stepType, wsStep.getName(), wsStep.getAction(), wsStep.getConnectionId()));
            } else if (step instanceof RequestStep) {
                httpSteps++;
                RequestStep reqStep = (RequestStep) step;
                System.out.println(String.format("  [%d] %s - %s", 
                    i, stepType, reqStep.getName()));
            } else {
                System.out.println(String.format("  [%d] %s - %s", 
                    i, stepType, stepInfo));
            }
        }

        System.out.println("\nSummary:");
        System.out.println("  HTTP/Request steps: " + httpSteps);
        System.out.println("  WebSocket steps: " + wsSteps);
        System.out.println("  Other steps: " + (steps.size() - httpSteps - wsSteps));

        // Verify specific WebSocket steps from the demo script
        verifyWebSocketSteps(steps);
    }

    private void verifyWebSocketSteps(List<TestStep> steps) {
        System.out.println("\n=== Verifying WebSocket Steps ===");

        // Step 1: WebSocket Connect
        assertTrue(steps.get(1) instanceof WebSocketStep, "Step 1 should be WebSocket Connect");
        WebSocketStep wsConnect = (WebSocketStep) steps.get(1);
        assertEquals("2. WebSocket Connect", wsConnect.getName());
        assertEquals(WebSocketAction.CONNECT, wsConnect.getAction());
        assertEquals("#{connectionId}", wsConnect.getConnectionId());
        assertNotNull(wsConnect.getRequest());
        assertTrue(wsConnect.getRequest().getUrl().startsWith("ws://localhost:8080/ws/events"));
        assertEquals(Integer.valueOf(5000), wsConnect.getRequest().getTimeoutMs());
        System.out.println("✓ Step 1: WebSocket Connect - VERIFIED");

        // Step 2: WebSocket Expect (Connection Info)
        assertTrue(steps.get(2) instanceof WebSocketStep, "Step 2 should be WebSocket Expect");
        WebSocketStep wsExpect1 = (WebSocketStep) steps.get(2);
        assertEquals("2.1 Expect Connection Info", wsExpect1.getName());
        assertEquals(WebSocketAction.EXPECT, wsExpect1.getAction());
        assertNotNull(wsExpect1.getResponse());
        assertEquals("\"connectionInfo\"", wsExpect1.getResponse().getExpectedContent());
        System.out.println("✓ Step 2: WebSocket Expect (Connection Info) - VERIFIED");

        // Step 3: WebSocket Send (Subscribe)
        assertTrue(steps.get(3) instanceof WebSocketStep, "Step 3 should be WebSocket Send");
        WebSocketStep wsSend = (WebSocketStep) steps.get(3);
        assertEquals("3. Subscribe to Cart Updates", wsSend.getName());
        assertEquals(WebSocketAction.SEND, wsSend.getAction());
        assertNotNull(wsSend.getRequest());
        assertTrue(wsSend.getRequest().getPayload().contains("addSubscription"));
        System.out.println("✓ Step 3: WebSocket Send (Subscribe) - VERIFIED");

        // Step 4: WebSocket Expect (Subscription Confirmation)
        assertTrue(steps.get(4) instanceof WebSocketStep, "Step 4 should be WebSocket Expect");
        WebSocketStep wsExpect2 = (WebSocketStep) steps.get(4);
        assertEquals("3.1 Expect Subscription Confirmation", wsExpect2.getName());
        assertEquals(WebSocketAction.EXPECT, wsExpect2.getAction());
        assertNotNull(wsExpect2.getResponse());
        assertEquals("\"subscribedTopics\"", wsExpect2.getResponse().getExpectedContent());
        assertEquals("subscription", wsExpect2.getResponse().getSaveVariable());
        System.out.println("✓ Step 4: WebSocket Expect (Subscription Confirmation) - VERIFIED");

        // Step 6: WebSocket Expect (Upgrade Prompt)
        assertTrue(steps.get(6) instanceof WebSocketStep, "Step 6 should be WebSocket Expect");
        WebSocketStep wsExpect3 = (WebSocketStep) steps.get(6);
        assertEquals("5. Trigger WebSocket Upgrade Prompt", wsExpect3.getName());
        assertEquals(WebSocketAction.EXPECT, wsExpect3.getAction());
        assertNotNull(wsExpect3.getResponse());
        assertEquals("\"UPGRADE_PROMPT\"", wsExpect3.getResponse().getExpectedContent());
        System.out.println("✓ Step 6: WebSocket Expect (Upgrade Prompt) - VERIFIED");

        // Step 9: WebSocket Expect (Cart Update Event)
        assertTrue(steps.get(9) instanceof WebSocketStep, "Step 9 should be WebSocket Expect");
        WebSocketStep wsExpect4 = (WebSocketStep) steps.get(9);
        assertEquals("8. Wait for WebSocket Cart Update Event", wsExpect4.getName());
        assertEquals(WebSocketAction.EXPECT, wsExpect4.getAction());
        assertNotNull(wsExpect4.getResponse());
        assertEquals("\"PLAN_UPGRADE_ADDED\"", wsExpect4.getResponse().getExpectedContent());
        System.out.println("✓ Step 9: WebSocket Expect (Cart Update) - VERIFIED");

        // Step 12: WebSocket Disconnect
        assertTrue(steps.get(12) instanceof WebSocketStep, "Step 12 should be WebSocket Disconnect");
        WebSocketStep wsDisconnect = (WebSocketStep) steps.get(12);
        assertEquals("11. WebSocket Disconnect", wsDisconnect.getName());
        assertEquals(WebSocketAction.DISCONNECT, wsDisconnect.getAction());
        assertEquals("#{connectionId}", wsDisconnect.getConnectionId());
        assertEquals("continue", wsDisconnect.getOnFail());
        System.out.println("✓ Step 12: WebSocket Disconnect - VERIFIED");

        System.out.println("\n✅ All WebSocket steps verified successfully!");
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testVariableSubstitutionInDemoScript() throws Exception {
        // Given: The real websocket-demo-script.xml file (in project root)
        File scriptFile = new File("../websocket-demo-script.xml");
        
        if (!scriptFile.exists()) {
            System.out.println("Skipping test - websocket-demo-script.xml not found");
            return;
        }

        // When: We load the workload
        String xml = Files.readString(scriptFile.toPath());
        HDWorkload workload = JaxbUtil.unmarshall(xml, HDWorkload.class);

        // Then: Variable placeholders should be preserved
        List<TestStep> steps = workload.getPlans().get(0)
            .getGroup().get(0)
            .getGroupSteps().get(0)
            .getUseCase().get(0)
            .getScriptSteps();

        WebSocketStep wsConnect = (WebSocketStep) steps.get(1);
        
        // Verify connectionId variable is preserved
        assertEquals("#{connectionId}", wsConnect.getConnectionId());
        
        // Verify URL contains variables
        String url = wsConnect.getRequest().getUrl();
        assertTrue(url.contains("#{connectionId}"), "URL should contain #{connectionId}");
        assertTrue(url.contains("#{target}"), "URL should contain #{target}");

        System.out.println("\n✅ Variable substitution preserved correctly!");
        System.out.println("   connectionId: " + wsConnect.getConnectionId());
        System.out.println("   URL: " + url);
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testStepOrderPreserved() throws Exception {
        // Given: The real websocket-demo-script.xml file (in project root)
        File scriptFile = new File("../websocket-demo-script.xml");
        
        if (!scriptFile.exists()) {
            System.out.println("Skipping test - websocket-demo-script.xml not found");
            return;
        }

        // When: We load the workload
        String xml = Files.readString(scriptFile.toPath());
        HDWorkload workload = JaxbUtil.unmarshall(xml, HDWorkload.class);

        List<TestStep> steps = workload.getPlans().get(0)
            .getGroup().get(0)
            .getGroupSteps().get(0)
            .getUseCase().get(0)
            .getScriptSteps();

        // Then: Step indices should be sequential
        for (int i = 0; i < steps.size(); i++) {
            assertEquals(i, steps.get(i).getStepIndex(), 
                "Step at position " + i + " should have stepIndex " + i);
        }

        System.out.println("\n✅ Step order preserved correctly!");
        System.out.println("   All " + steps.size() + " steps have correct sequential indices");
    }
}
