package com.intuit.tank.rest.mvc.rest.cloud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class JobLockManagerTest {

    @Test
    @DisplayName("getLock returns same object for same jobId — lock identity is stable")
    void getLock_returnsSameObjectForSameId() {
        Object lock1 = JobLockManager.getLock("job-stable");
        Object lock2 = JobLockManager.getLock("job-stable");
        assertSame(lock1, lock2, "Same jobId must always return the same lock object");
    }

    @Test
    @DisplayName("getLock returns different objects for different jobIds")
    void getLock_returnsDifferentObjectsForDifferentIds() {
        Object lockA = JobLockManager.getLock("job-a");
        Object lockB = JobLockManager.getLock("job-b");
        assertNotSame(lockA, lockB);
    }

    @Test
    @DisplayName("getLock is idempotent across many calls")
    void getLock_idempotentAcrossManyCalls() {
        Object first = JobLockManager.getLock("job-idem");
        for (int i = 0; i < 1000; i++) {
            assertSame(first, JobLockManager.getLock("job-idem"));
        }
    }

    @RepeatedTest(3)
    @DisplayName("Concurrent getLock for same jobId always returns same object — no split-lock")
    void concurrentGetLock_sameJobId_noSplitLock() throws InterruptedException {
        String jobId = "job-concurrent-" + System.nanoTime();
        int numThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);
        Object[] locks = new Object[numThreads];
        AtomicInteger errors = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    locks[idx] = JobLockManager.getLock(jobId);
                } catch (Exception e) {
                    errors.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        assertTrue(doneLatch.await(5, TimeUnit.SECONDS));
        executor.shutdown();

        assertEquals(0, errors.get());
        // All threads must have gotten the exact same lock object
        for (int i = 1; i < numThreads; i++) {
            assertSame(locks[0], locks[i],
                    "Thread " + i + " got a different lock object — split-lock detected");
        }
    }
}
