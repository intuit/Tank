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

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

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
     *
     * @param totalAgents
     * @param regionRequests
     *
     * Uses proportional rounding to closely align with provided region percentage split (region with higher left over decimal gets the extra agent)
     *
     * @return returns mapping of number of machines needed to support the given number of agents and user percentage to each region request
     */
    public static Map<RegionRequest, Integer> getMachinesForAgentByUserPercentage(int totalAgents, Set<? extends RegionRequest> regionRequests) {
        Map<RegionRequest, Integer> agentAllocation = new HashMap<>();

        if (regionRequests == null || regionRequests.isEmpty()) {
            return agentAllocation;
        }

        if (totalAgents == 0) {
            for (RegionRequest regionRequest : regionRequests) {
                agentAllocation.put(regionRequest, 0);
                return agentAllocation;
            }
        }

        PriorityQueue<Map.Entry<RegionRequest, Double>> remainingFractions = // save the remaining fractions after rounding down in PQ to get the highest fraction first
                new PriorityQueue<>((a,b) -> Double.compare(b.getValue() - a.getValue().intValue(), a.getValue() - b.getValue().intValue()));

        int allocatedAgents = 0;

        for (RegionRequest regionRequest : regionRequests) {
            double percentage = (Integer.parseInt(regionRequest.getPercentage()) / 100.0);
            int agentsForRegion = (int) Math.floor(totalAgents * percentage);
            agentAllocation.put(regionRequest, agentsForRegion);
            allocatedAgents += agentsForRegion;
            remainingFractions.add(Map.entry(regionRequest, totalAgents * percentage));
        }

        int remainingAgents = totalAgents - allocatedAgents;
        while (remainingAgents > 0) {
            Map.Entry<RegionRequest, Double> entry = remainingFractions.poll();
            RegionRequest regionRequest = entry.getKey();
            agentAllocation.put(regionRequest, agentAllocation.get(regionRequest) + 1);
            remainingAgents--;
        }

        return agentAllocation;
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
