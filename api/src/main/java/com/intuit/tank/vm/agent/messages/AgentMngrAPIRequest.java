package com.intuit.tank.vm.agent.messages;

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.VMRegion;

public class AgentMngrAPIRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    private int numberVirtualUsers = 0;
    private Map<VMRegion, Integer> userMap = new HashMap<VMRegion, Integer>();
    private String jobId;

    public AgentMngrAPIRequest(@Nonnull String jobId, @Nonnull UserRequest... users) {
        for (UserRequest ur : users) {
            if (ur.getRegion() != null) {
                userMap.put(ur.getRegion(), ur.getNumUsers());
            }
            numberVirtualUsers += ur.getNumUsers();
        }
        this.jobId = jobId;
    }

    public int getNumberVirtualUsers(VMRegion region) {
        Integer ret = region != null ? userMap.get(region) : numberVirtualUsers;
        return ret != null ? ret : 0;
    }

    public int totalNumberVirtualUsers() {
        return numberVirtualUsers;
    }

    public String getJobId() {
        return jobId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static class UserRequest implements Serializable {

        private static final long serialVersionUID = 1L;
        private VMRegion region;
        private int numUsers;

        /**
         * @param region
         * @param numUsers
         */
        public UserRequest(@Nullable VMRegion region, int numUsers) {
            this.region = region;
            this.numUsers = numUsers;
        }

        /**
         * @return the region
         */
        public VMRegion getRegion() {
            return region;
        }

        /**
         * @return the numUsers
         */
        public int getNumUsers() {
            return numUsers;
        }

        /**
         * @param region
         *            the region to set
         */
        public void setRegion(VMRegion region) {
            this.region = region;
        }

        /**
         * @param numUsers
         *            the numUsers to set
         */
        public void setNumUsers(int numUsers) {
            this.numUsers = numUsers;
        }

    }
}
