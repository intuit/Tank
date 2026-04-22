package com.intuit.tank.rest.mvc.rest.cloud;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.enterprise.inject.Instance;

import static org.mockito.Mockito.*;

class JobEventListenerTest {

    @InjectMocks
    private JobEventListener listener;

    @Mock
    private Instance<JobEventSender> controllerSource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void observerJobKillRequest_abortedEvent_callsKillJob() {
        JobEventSender sender = mock(JobEventSender.class);
        when(controllerSource.get()).thenReturn(sender);

        JobEvent event = new JobEvent("42", "", JobLifecycleEvent.JOB_ABORTED);
        listener.observerJobKillRequest(event);

        verify(sender).killJob("42", false);
    }

    @Test
    void observerJobKillRequest_finishedEvent_clearsContext() {
        JobEvent event = new JobEvent("42", "", JobLifecycleEvent.JOB_FINISHED);
        listener.observerJobKillRequest(event);
        // Should not throw, just clears ThreadContext
    }

    @Test
    void observerJobKillRequest_killedEvent_clearsContext() {
        JobEvent event = new JobEvent("42", "", JobLifecycleEvent.JOB_KILLED);
        listener.observerJobKillRequest(event);
    }

    @Test
    void observerJobKillRequest_otherEvent_doesNothing() {
        JobEvent event = new JobEvent("42", "", JobLifecycleEvent.AGENT_LAUNCHED);
        listener.observerJobKillRequest(event);
        // Should not call killJob or clear context
        verifyNoInteractions(controllerSource);
    }
}
