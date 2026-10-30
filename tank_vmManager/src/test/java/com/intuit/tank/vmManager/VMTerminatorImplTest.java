package com.intuit.tank.vmManager;

import com.intuit.tank.vm.vmManager.VMChannel;
import com.intuit.tank.vm.vmManager.VMTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VMTerminatorImplTest {

    @Mock
    private VMQueue queue;
    @Mock
    private VMChannel channel;
    @Mock
    private VMTracker vmTracker;
    @InjectMocks
    private VMTerminatorImpl terminator;

    @Test
    public void terminate_queuesDelayedTermination() {
        when(vmTracker.isDevMode()).thenReturn(false);

        terminator.terminate("i-1");

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(queue).execute(captor.capture());
        verifyNoInteractions(channel);

        // the task waits 30s before terminating; an interrupt cuts the wait short
        Thread.currentThread().interrupt();
        try {
            captor.getValue().run();
        } finally {
            Thread.interrupted();
        }
        verify(channel).terminateInstances(List.of("i-1"));
    }

    @Test
    public void terminate_devMode_doesNothing() {
        when(vmTracker.isDevMode()).thenReturn(true);

        terminator.terminate("i-1");

        verify(queue, never()).execute(any());
        verifyNoInteractions(channel);
    }
}
