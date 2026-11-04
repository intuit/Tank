package com.intuit.tank.vmManager.environment;

import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateInstanceTest {

    private final VMTracker vmTracker = mock(VMTracker.class);

    @Test
    public void run_amazon_createsInstancesAndPersistsEach() {
        VMInstanceRequest request = request(VMProvider.Amazon);
        VMInformation first = vm("i-1");
        VMInformation second = vm("i-2");

        try (MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class,
                     (mock, ctx) -> when(mock.create(request)).thenReturn(List.of(first, second)));
             MockedConstruction<VMImageDao> dao = mockConstruction(VMImageDao.class)) {
            new CreateInstance(request, vmTracker).run();

            assertEquals(1, amazon.constructed().size());
            verify(dao.constructed().get(0)).addImageFromInfo("9", first, VMRegion.US_WEST_2);
            verify(dao.constructed().get(0)).addImageFromInfo("9", second, VMRegion.US_WEST_2);
        }

        ArgumentCaptor<CloudVmStatus> captor = ArgumentCaptor.forClass(CloudVmStatus.class);
        verify(vmTracker, times(2)).setStatus(captor.capture());
        CloudVmStatus status = captor.getAllValues().get(0);
        assertEquals("i-1", status.getInstanceId());
        assertEquals("9", status.getJobId());
        assertEquals(VMStatus.starting, status.getVmStatus());
        assertEquals(JobStatus.Starting, status.getJobStatus());
    }

    @Test
    public void run_pharos_hasNoEnvironment() {
        CreateInstance createInstance = new CreateInstance(request(VMProvider.Pharos), vmTracker);

        // no Pharos implementation exists, so there is nothing to create instances with
        assertThrows(NullPointerException.class, createInstance::run);
        verifyNoInteractions(vmTracker);
    }

    @Test
    public void run_missingProvider_hasNoEnvironment() {
        CreateInstance createInstance = new CreateInstance(request(null), vmTracker);

        assertThrows(NullPointerException.class, createInstance::run);
        verifyNoInteractions(vmTracker);
    }

    private static VMInstanceRequest request(VMProvider provider) {
        VMInstanceRequest request = new VMInstanceRequest();
        request.setProvider(provider);
        request.setJobId("9");
        request.setRegion(VMRegion.US_WEST_2);
        return request;
    }

    private static VMInformation vm(String instanceId) {
        VMInformation info = new VMInformation();
        info.setInstanceId(instanceId);
        return info;
    }
}
