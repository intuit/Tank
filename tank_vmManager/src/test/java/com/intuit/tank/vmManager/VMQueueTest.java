package com.intuit.tank.vmManager;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class VMQueueTest {

    @Test
    public void execute_runsTheRunnable() throws Exception {
        VMQueue vmQueue = new VMQueue();
        CountDownLatch ran = new CountDownLatch(1);

        vmQueue.execute(ran::countDown);

        assertTrue(ran.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void clone_isNotSupported() {
        assertThrows(CloneNotSupportedException.class, new VMQueue()::clone);
    }
}
