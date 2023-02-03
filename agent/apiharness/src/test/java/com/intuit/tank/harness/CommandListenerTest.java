package com.intuit.tank.harness;

import org.junit.jupiter.api.Test;

public class CommandListenerTest {
    @Test
    public void testCommandListenerTest_1() {
        CommandListener.main(new String[]{});
        CommandListener.startTest();
    }
}
