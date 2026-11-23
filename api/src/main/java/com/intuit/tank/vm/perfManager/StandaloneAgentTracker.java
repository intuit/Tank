package com.intuit.tank.vm.perfManager;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import jakarta.annotation.Nullable;
import jakarta.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;

/**
 * 
 * StandaloneAgentTracker
 * 
 * @author dangleton
 * 
 */
@ApplicationScoped
public class StandaloneAgentTracker {

    private Map<String, AgentAvailability> availabilityMap = new ConcurrentHashMap<String, AgentAvailability>();

    /** The token each agent reported its availability with, which the controller's requests to it must carry */
    private final Map<String, String> commandTokens = new ConcurrentHashMap<>();

    /**
     * 
     * @param availability
     */
    public void addAvailability(AgentAvailability availability) {
        addAvailability(availability, null);
    }

    /**
     * @param availability
     * @param commandToken the bearer token the agent reported with, or null if it sent none
     */
    public void addAvailability(AgentAvailability availability, @Nullable String commandToken) {
        cleanOutdatedAvailability();
        availabilityMap.put(availability.getInstanceId(), availability);
        if (StringUtils.isNotBlank(commandToken)) {
            commandTokens.put(availability.getInstanceId(), commandToken);
        } else {
            commandTokens.remove(availability.getInstanceId());
        }
    }

    /**
     * @return the token the agent reported its availability with, or null if it sent none
     */
    @Nullable
    public String getCommandToken(String instanceId) {
        return commandTokens.get(instanceId);
    }

    /**
     * 
     * @param instanceId
     * @return
     */
    public AgentAvailability getAvailabilityForAgent(String instanceId) {
        cleanOutdatedAvailability();
        return availabilityMap.get(instanceId);
    }

    /**
     * 
     * @return
     */
    public Collection<AgentAvailability> getAvailabilities() {
        cleanOutdatedAvailability();
        return new ArrayList<AgentAvailability>(availabilityMap.values());
    }

    /**
     * gets a list of AgentAvailability to run the specified number of users.
     * 
     * @param numUsers
     *            the number of users requested
     * @return the list of agents.
     */
    @Nullable
    public List<AgentAvailability> getAgents(int numUsers) {
        cleanOutdatedAvailability();
        List<AgentAvailability> ret = new ArrayList<AgentAvailability>();
        for (AgentAvailability a : availabilityMap.values()) {
            if (isValid(a) && isAvailable(a)) {
                numUsers -= a.getCapacity();
                ret.add(a);
                if (numUsers <= 0) {
                    break;
                }
            }
        }
        if (numUsers <= 0) {
            for (AgentAvailability a : ret) {
                a.setAvailabilityStatus(AgentAvailabilityStatus.DELEGATING);
            }
        } else {
            ret = null;
        }
        return ret;
    }

    private void cleanOutdatedAvailability() {
        List<String> toClean = availabilityMap.values().stream().filter(a -> !isValid(a)).map(AgentAvailability::getInstanceId).collect(Collectors.toList());
        for (String s : toClean) {
            availabilityMap.remove(s);
            commandTokens.remove(s);
        }
    }

    private boolean isAvailable(AgentAvailability a) {
        return a.getAvailabilityStatus() == AgentAvailabilityStatus.AVAILABLE;
    }

    private boolean isValid(AgentAvailability a) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, -10);
        return a.getTimestamp().after(c.getTime());
    }

}
