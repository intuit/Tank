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

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;
import software.amazon.awssdk.services.ec2.model.InstanceStateChange;
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
}