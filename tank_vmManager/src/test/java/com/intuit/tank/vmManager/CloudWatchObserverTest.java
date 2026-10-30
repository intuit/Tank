package com.intuit.tank.vmManager;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.RecipientType;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.vmManager.Recipient;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vmManager.environment.amazon.CloudwatchInstance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for CloudWatchObserver. Watches are added/removed on a background thread, and Mockito
 * construction mocks are thread-local, so the CloudwatchInstance construction mock is opened on
 * that thread (from the vmTracker answer) and the test waits on a latch for the AWS calls.
 */
@ExtendWith(MockitoExtension.class)
public class CloudWatchObserverTest {

    @Mock
    private JobInstanceDao dao;
    @Mock
    private JobNotificationDao notificationDao;
    @Mock
    private VMTracker vmTracker;
    @InjectMocks
    private CloudWatchObserver observer;

    @Mock
    private JobInstance job;

    /** watch calls recorded per region: region -> calls made on that region's CloudwatchInstance */
    private final Map<VMRegion, List<String>> calls = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp() {
        lenient().when(job.getId()).thenReturn(42);
    }

    @Test
    public void agentStarted_withCpuNotification_addsWatchPerRegionForEmailRecipients() throws Exception {
        JobNotification cpuNotification = notification(JobLifecycleEvent.AGENT_EXCESSIVE_CPU);
        stubJobWithNotifications(cpuNotification);
        CountDownLatch done = stubStatuses(2, Set.of(
                status("i-1", VMRegion.US_WEST_2), status("i-2", VMRegion.US_WEST_2), status("i-3", VMRegion.US_EAST)));

        observer.observe(new JobEvent("42", "", JobLifecycleEvent.AGENT_STARTED));

        assertTrue(done.await(5, TimeUnit.SECONDS), "watches were not added");
        assertEquals(List.of("add [i-1, i-2] perf@example.com 42"), calls.get(VMRegion.US_WEST_2));
        assertEquals(List.of("add [i-3] perf@example.com 42"), calls.get(VMRegion.US_EAST));
    }

    @Test
    public void jobFinished_withCpuNotification_removesWatchPerRegion() throws Exception {
        JobNotification cpuNotification = notification(JobLifecycleEvent.AGENT_EXCESSIVE_CPU);
        stubJobWithNotifications(cpuNotification);
        CountDownLatch done = stubStatuses(1, Set.of(status("i-1", VMRegion.US_WEST_2)));

        observer.observe(new JobEvent("42", "", JobLifecycleEvent.JOB_FINISHED));

        assertTrue(done.await(5, TimeUnit.SECONDS), "watches were not removed");
        assertEquals(List.of("remove perf@example.com 42"), calls.get(VMRegion.US_WEST_2));
    }

    @Test
    public void agentStarted_withoutCpuNotification_addsNoWatches() {
        JobNotification other = notification(JobLifecycleEvent.JOB_STARTED);
        JobNotification noEvents = mock(JobNotification.class);
        when(noEvents.getLifecycleEvents()).thenReturn(null);
        // the second version points at a deleted notification
        stubJobWithNotifications(other, noEvents, null);

        observer.observe(new JobEvent("42", "", JobLifecycleEvent.AGENT_STARTED));

        verifyNoInteractions(vmTracker);
    }

    @Test
    public void jobFinished_withoutCpuNotification_removesNoWatches() {
        stubJobWithNotifications((JobNotification) null);

        observer.observe(new JobEvent("42", "", JobLifecycleEvent.JOB_FINISHED));

        verifyNoInteractions(vmTracker);
    }

    @Test
    public void otherEvents_areIgnored() {
        observer.observe(new JobEvent("42", "", JobLifecycleEvent.LOAD_STARTED));

        verifyNoInteractions(dao, notificationDao, vmTracker);
    }

    // ============ helpers ============

    private JobNotification notification(JobLifecycleEvent event) {
        JobNotification notification = mock(JobNotification.class);
        lenient().when(notification.getLifecycleEvents()).thenReturn(List.of(event));
        Recipient email = mock(Recipient.class);
        lenient().when(email.getType()).thenReturn(RecipientType.email);
        lenient().when(email.getAddress()).thenReturn("perf@example.com");
        // a recipient without a type is skipped
        Recipient untyped = mock(Recipient.class);
        Set<Recipient> recipients = new LinkedHashSet<>(List.of(email, untyped));
        lenient().doReturn(recipients).when(notification).getRecipients();
        return notification;
    }

    private void stubJobWithNotifications(JobNotification... notifications) {
        Set<EntityVersion> versions = new LinkedHashSet<>();
        for (int i = 0; i < notifications.length; i++) {
            EntityVersion version = mock(EntityVersion.class);
            when(version.getObjectId()).thenReturn(100 + i);
            when(notificationDao.findById(100 + i)).thenReturn(notifications[i]);
            versions.add(version);
        }
        when(job.getNotificationVersions()).thenReturn(versions);
        when(dao.findById(42)).thenReturn(job);
    }

    /**
     * Stubs the job's VM statuses and opens the CloudwatchInstance construction mock on the
     * observer's background thread. Returns a latch released after {@code expectedRegions} watch calls.
     */
    private CountDownLatch stubStatuses(int expectedRegions, Set<CloudVmStatus> statuses) {
        CountDownLatch done = new CountDownLatch(expectedRegions);
        CloudVmStatusContainer container = new CloudVmStatusContainer();
        container.getStatuses().addAll(statuses);
        when(vmTracker.getVmStatusForJob("42")).thenAnswer(inv -> {
            // opened on the observer thread; it ends with that thread
            MockedConstruction<CloudwatchInstance> ignored = mockConstruction(CloudwatchInstance.class, (cw, ctx) -> {
                VMRegion region = (VMRegion) ctx.arguments().get(0);
                lenient().doAnswer(call -> {
                    Collection<String> ids = call.getArgument(0);
                    record(region, "add " + ids.stream().sorted().toList() + " " + call.getArgument(1) + " "
                            + call.getArgument(2));
                    done.countDown();
                    return null;
                }).when(cw).addWatch(anyCollection(), anyString(), anyString());
                lenient().doAnswer(call -> {
                    record(region, "remove " + call.getArgument(0) + " " + call.getArgument(1));
                    done.countDown();
                    return null;
                }).when(cw).removeWatch(anyString(), anyString());
            });
            return container;
        });
        return done;
    }

    private void record(VMRegion region, String call) {
        calls.computeIfAbsent(region, r -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(call);
    }

    private static CloudVmStatus status(String instanceId, VMRegion region) {
        return new CloudVmStatus(instanceId, "42", "sg-test", JobStatus.Running, VMImageType.AGENT, region,
                VMStatus.running, new ValidationStatus(), 1, 1, null, null);
    }
}
