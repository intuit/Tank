package com.intuit.tank.rest.mvc.rest.cloud;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Shared per-job lock manager used by both JobEventSender (CDI @RequestScoped)
 * and JobServiceV2Impl (Spring @Service) to prevent start/delete races.
 *
 * The lock map is static so it's shared across all instances regardless of
 * injection framework. Entries are never evicted — this is intentional.
 * Tank runs O(hundreds) of jobs, so the map stays small. Evicting locks
 * would create a split-lock race where two threads synchronize on different
 * objects for the same jobId.
 */
public final class JobLockManager {

    private static final ConcurrentHashMap<String, Object> JOB_LOCKS = new ConcurrentHashMap<>();

    private JobLockManager() {}

    /**
     * Get or create a stable lock object for the given job ID.
     * Callers should synchronize on the returned object.
     *
     * The same object is always returned for the same jobId, guaranteeing
     * mutual exclusion across all callers (startJob, deleteJob, etc.).
     */
    public static Object getLock(String jobId) {
        return JOB_LOCKS.computeIfAbsent(jobId, k -> new Object());
    }
}
