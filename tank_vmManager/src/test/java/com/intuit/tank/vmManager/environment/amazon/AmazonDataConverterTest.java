package com.intuit.tank.vmManager.environment.amazon;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceState;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;
import software.amazon.awssdk.services.ec2.model.InstanceStateName;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.ec2.model.PlatformValues;
import software.amazon.awssdk.services.ec2.model.Reservation;

/**
 * The class <code>AmazonDataConverterTest</code> contains tests for the class <code>{@link AmazonDataConverter}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 6:30 PM
 */
public class AmazonDataConverterTest {
    /**
     * Run the AmazonDataConverter() constructor test.
     * 
     * @generatedBy CodePro at 12/16/14 6:30 PM
     */
    @Test
    public void testAmazonDataConverter_1()
            throws Exception {
        AmazonDataConverter result = new AmazonDataConverter();
        assertNotNull(result);
    }

    /**
     * Run the List<VMInformation> processReservation(Reservation,VMRegion) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 6:30 PM
     */
    @Test
    public void testProcessReservation_1()
            throws Exception {
        Reservation reservation = Reservation.builder().build();
        VMRegion region = VMRegion.ASIA_1;

        List<VMInformation> result = AmazonDataConverter.processReservation(reservation.requesterId(), reservation.instances(), region);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<VMInformation> processReservation(Reservation,VMRegion) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 6:30 PM
     */
    @Test
    public void testProcessReservation_2()
            throws Exception {
        Reservation reservation = Reservation.builder().build();
        VMRegion region = VMRegion.ASIA_1;

        List<VMInformation> result = AmazonDataConverter.processReservation(reservation.requesterId(), reservation.instances(), region);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<VMInformation> processStateChange(List<InstanceStateChange>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 6:30 PM
     */
    @Test
    public void testProcessStateChange_1()
            throws Exception {
        List<InstanceStateChange> changes = new LinkedList();

        List<VMInformation> result = AmazonDataConverter.processStateChange(changes);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the List<VMInformation> processStateChange(List<InstanceStateChange>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 6:30 PM
     */
    @Test
    public void testProcessStateChange_2()
            throws Exception {
        List<InstanceStateChange> changes = new LinkedList();

        List<VMInformation> result = AmazonDataConverter.processStateChange(changes);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testProcessStateChange_mapsIdAndCurrentState() {
        List<InstanceStateChange> changes = List.of(
                InstanceStateChange.builder().instanceId("i-1")
                        .currentState(InstanceState.builder().name(InstanceStateName.PENDING).build()).build(),
                InstanceStateChange.builder().instanceId("i-2")
                        .currentState(InstanceState.builder().name(InstanceStateName.RUNNING).build()).build());

        List<VMInformation> result = AmazonDataConverter.processStateChange(changes);

        assertEquals(List.of("i-1", "i-2"), result.stream().map(VMInformation::getInstanceId).toList());
        assertEquals(List.of("pending", "running"), result.stream().map(VMInformation::getState).toList());
    }

    @Test
    public void testProcessStateChange_runtimeExceptionIsRethrown() {
        List<InstanceStateChange> changes = List.of(InstanceStateChange.builder().instanceId("i-1").build());

        // no current state
        assertThrows(NullPointerException.class, () -> AmazonDataConverter.processStateChange(changes));
    }

    @Test
    public void testProcessStateChange_checkedExceptionIsWrapped() {
        Exception failure = new Exception("unreadable change");
        InstanceStateChange change = mock(InstanceStateChange.class);
        when(change.instanceId()).thenAnswer(invocation -> {
            throw failure;
        });

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> AmazonDataConverter.processStateChange(List.of(change)));
        assertSame(failure, thrown.getCause());
    }

    @Test
    public void testInstanceToVmInformation_copiesInstanceFields() {
        Instance instance = Instance.builder()
                .imageId("ami-1")
                .instanceId("i-1")
                .keyName("tank-key")
                .platform(PlatformValues.WINDOWS)
                .privateDnsName("ip-10-0-0-5")
                .publicDnsName("ec2-54-0-0-1")
                .privateIpAddress("10.0.0.5")
                .publicIpAddress("54.0.0.1")
                .state(InstanceState.builder().name(InstanceStateName.RUNNING).build())
                .instanceType(InstanceType.M7_G_XLARGE)
                .build();

        VMInformation info = AmazonDataConverter.instanceToVmInformation("r-1", instance, VMRegion.US_WEST_2);

        assertEquals(VMProvider.Amazon, info.getProvider());
        assertEquals("r-1", info.getRequestId());
        assertEquals("ami-1", info.getImageId());
        assertEquals("i-1", info.getInstanceId());
        assertEquals("tank-key", info.getKeyName());
        assertEquals(VMRegion.US_WEST_2, info.getRegion());
        assertEquals("Windows", info.getPlatform());
        assertEquals("ip-10-0-0-5", info.getPrivateDNS());
        assertEquals("ec2-54-0-0-1", info.getPublicDNS());
        assertEquals("10.0.0.5", info.getPrivateIp());
        assertEquals("54.0.0.1", info.getPublicIp());
        assertEquals("running", info.getState());
        assertEquals("m7g.xlarge", info.getSize());
    }

    @Test
    public void testProcessReservation_failureIsWrapped() {
        // no state on the instance
        List<Instance> instances = List.of(Instance.builder().instanceId("i-1").build());

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> AmazonDataConverter.processReservation("r-1", instances, VMRegion.US_WEST_2));
        assertInstanceOf(NullPointerException.class, thrown.getCause());
    }
}
