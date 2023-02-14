package com.intuit.tank.harness;

import com.intuit.tank.vm.api.enumerated.AgentCommand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommandListenerTest {
    @Test
    public void testCommandListenerTest_1() {
        CommandListener.main(new String[]{});
        AgentCommand command = APITestHarness.getInstance().getCmd();
        assertEquals(command.name(), "run");
    }
}
