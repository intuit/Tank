package com.intuit.tank.harness;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandListenerTest {

    @Test
    public void testApplyCommandUnknownThrows() {
        assertThrows(UnsupportedOperationException.class, () -> CommandListener.applyCommand("not-a-real-command"));
    }
}
