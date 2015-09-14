package com.intuit.tank.vm.vmManager;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.VMInformation;

/**
 * The class <code>VMInformationCpTest</code> contains tests for the class <code>{@link VMInformation}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class VMInformationCpTest {
    /**
     * Run the VMInformation() constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testVMInformation_1()
            throws Exception {

        VMInformation result = new VMInformation();

        assertNotNull(result);
        assertEquals(null, result.getPlatform());
        assertEquals(null, result.getInstanceId());
        assertEquals(null, result.getState());
        assertEquals(null, result.getSize());
        assertEquals(null, result.getRegion());
        assertEquals(null, result.getProvider());
        assertEquals(null, result.getLaunchTime());
        assertEquals(null, result.getImageId());
        assertEquals(null, result.getKeyName());
        assertEquals(null, result.getRequestId());
        assertEquals(null, result.getPrivateDNS());
        assertEquals(null, result.getPublicDNS());
    }

    /**
     * Run the String getImageId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetImageId_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getImageId();

        assertEquals(null, result);
    }

    /**
     * Run the String getInstanceId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetInstanceId_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getInstanceId();

        assertEquals(null, result);
    }

    /**
     * Run the String getKeyName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetKeyName_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getKeyName();

        assertEquals(null, result);
    }

    /**
     * Run the Calendar getLaunchTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetLaunchTime_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        Calendar result = fixture.getLaunchTime();

        assertEquals(null, result);
    }

    /**
     * Run the String getPlatform() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetPlatform_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getPlatform();

        assertEquals(null, result);
    }

    /**
     * Run the String getPrivateDNS() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetPrivateDNS_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getPrivateDNS();

        assertEquals(null, result);
    }

    /**
     * Run the VMProvider getProvider() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetProvider_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        VMProvider result = fixture.getProvider();

        assertEquals(null, result);
    }

    /**
     * Run the String getPublicDNS() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetPublicDNS_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getPublicDNS();

        assertEquals(null, result);
    }

    /**
     * Run the VMRegion getRegion() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRegion_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        VMRegion result = fixture.getRegion();

        assertEquals(null, result);
    }

    /**
     * Run the String getRequestId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetRequestId_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getRequestId();

        assertEquals(null, result);
    }

    /**
     * Run the String getSize() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetSize_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getSize();

        assertEquals(null, result);
    }

    /**
     * Run the String getState() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetState_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.getState();

        assertEquals(null, result);
    }

    /**
     * Run the void setImageId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetImageId_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String data = "";

        fixture.setImageId(data);

    }

    /**
     * Run the void setInstanceId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetInstanceId_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String data = "";

        fixture.setInstanceId(data);

    }

    /**
     * Run the void setKeyName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetKeyName_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String data = "";

        fixture.setKeyName(data);

    }

    /**
     * Run the void setLaunchTime(Calendar) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetLaunchTime_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        Calendar data = new GregorianCalendar();

        fixture.setLaunchTime(data);

    }

    /**
     * Run the void setPlatform(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetPlatform_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String data = "";

        fixture.setPlatform(data);

    }

    /**
     * Run the void setPrivateDNS(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetPrivateDNS_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String data = "";

        fixture.setPrivateDNS(data);

    }

    /**
     * Run the void setProvider(VMProvider) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetProvider_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        VMProvider data = VMProvider.Amazon;

        fixture.setProvider(data);

    }

    /**
     * Run the void setPublicDNS(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetPublicDNS_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String data = "";

        fixture.setPublicDNS(data);

    }

    /**
     * Run the void setRegion(VMRegion) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetRegion_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        VMRegion region = VMRegion.ASIA_1;

        fixture.setRegion(region);

    }

    /**
     * Run the void setRequestId(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetRequestId_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String data = "";

        fixture.setRequestId(data);

    }

    /**
     * Run the void setSize(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetSize_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String size = "";

        fixture.setSize(size);

    }

    /**
     * Run the void setState(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetState_1()
            throws Exception {
        VMInformation fixture = new VMInformation();
        String data = "";

        fixture.setState(data);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        VMInformation fixture = new VMInformation();

        String result = fixture.toString();

        assertNotNull(result);
    }
}