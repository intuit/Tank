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

public class JobVmCalculator {

    /**
     * 
     * @param numberOfUsers
     * @param numUsersPerAgent
     * @return
     */
    public static int getMachinesForAgent(int numberOfUsers, int numUsersPerAgent) {
        if (numberOfUsers == 0 || numUsersPerAgent == 0) {
            return 0;
        }
        return (int) Math.ceil((double) numberOfUsers / (double) numUsersPerAgent);
    }

    /**
     * attempts to find the optimum number of users per machine to spread the load among the machines evenly
     * 
     * @param numberOfUsers
     * @param numberOfMachines
     * @return
     */
    public static int getOptimalUsersPerAgent(int numberOfUsers, int numberOfMachines) {
        if (numberOfUsers == 0 || numberOfMachines == 0) {
            return 0;
        }
        return (int) Math.ceil((double) numberOfUsers / (double) numberOfMachines);
    }

}
