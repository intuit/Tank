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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.vmManager.JobRequestImpl.Builder;
import com.intuit.tank.vm.vmManager.JobRequestImpl.JobRequestBuilderBase;

/**
 * 
 * JobInstance
 * 
 * @author dangleton
 * 
 */
public final class JobRequestImpl implements Serializable, JobRequest {

    private static final long serialVersionUID = 1L;

    private String id;

    private IncrementStrategy incrementStrategy = IncrementStrategy.increasing;
    private String location;
    private TerminationPolicy terminationPolicy = TerminationPolicy.time;
    private long rampTime;
    private int baselineVirtualUsers;
    private long simulationTime;
    private boolean useEips;
    private int userIntervalIncrement;
    private String reportingMode = TankConstants.RESULTS_NONE;
    private String loggingProfile = LoggingProfile.STANDARD.name();
    private String stopBehavior = StopBehavior.END_OF_SCRIPT_GROUP.name();
    private JobQueueStatus status = JobQueueStatus.Created;
    private Set<? extends RegionRequest> regions = new HashSet<RegionRequest>();
    private Set<? extends Notification> notifications = new HashSet<Notification>();
    private Set<Integer> dataFileIds = new HashSet<Integer>();
    private String vmInstanceType;
    private int numUsersPerAgent;

    private String scriptsXmlUrl;

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 
     */
    private JobRequestImpl() {
        super();
    }

    public String getLoggingProfile() {
        return loggingProfile;
    }

    /**
     * @return the useEips
     */
    public boolean isUseEips() {
        return useEips;
    }

    /**
     * @return the stopBehavior
     */
    public String getStopBehavior() {
        return stopBehavior;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @inheritDoc
     */
    @Override
    public IncrementStrategy getIncrementStrategy() {
        return incrementStrategy;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getLocation() {
        return location;
    }

    /**
     * @inheritDoc
     */
    @Override
    public TerminationPolicy getTerminationPolicy() {
        return terminationPolicy;
    }

    /**
     * @inheritDoc
     */
    @Override
    public long getRampTime() {
        return rampTime;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getBaselineVirtualUsers() {
        return baselineVirtualUsers;
    }

    /**
     * @inheritDoc
     */
    @Override
    public long getSimulationTime() {
        return simulationTime;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getUserIntervalIncrement() {
        return userIntervalIncrement > 0 ? userIntervalIncrement : 1;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String getReportingMode() {
        return reportingMode;
    }

    /**
     * @return the scriptsXml
     */
    public String getScriptsXmlUrl() {
        return scriptsXmlUrl;
    }

    /**
     * @return the vmInstanceType
     */
    public String getVmInstanceType() {
        return vmInstanceType;
    }

    /**
     * @param vmInstanceType
     *            the vmInstanceType to set
     */
    public void setVmInstanceType(String vmInstanceType) {
        this.vmInstanceType = vmInstanceType;
    }

    /**
     * @return the numUsersPerAgent
     */
    public int getNumUsersPerAgent() {
        return numUsersPerAgent;
    }

    /**
     * @param numUsersPerAgent
     *            the numUsersPerAgent to set
     */
    public void setNumUsersPerAgent(int numUsersPerAgent) {
        this.numUsersPerAgent = numUsersPerAgent;
    }

    /**
     * @inheritDoc
     */
    @Override
    public int getTotalVirtualUsers() {
        return regions.stream().mapToInt(region -> JobUtil.parseUserString(region.getUsers())).sum();
    }

    /**
     * @inheritDoc
     */
    @Override
    public JobQueueStatus getStatus() {
        return status;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<? extends RegionRequest> getRegions() {
        return regions;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<? extends Notification> getNotifications() {
        return notifications;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Set<Integer> getDataFileIds() {
        return dataFileIds;
    }

    /**
     * @inheritDoc private String id;
     * 
     *              private IncrementStrategy incrementStrategy = IncrementStrategy.increasing; private Location
     *              location = Location.san_diego; private TerminationPolicy terminationPolicy = TerminationPolicy.time;
     *              private int rampTime; private int baselineVirtualUsers; private int simulationTime; private int
     *              userIntervalIncrement; private ReportingMode reportingMode = ReportingMode.None; private
     *              JobQueueStatus status = JobQueueStatus.Created; private Set<? extends RegionRequest> regions = new
     *              HashSet<RegionRequest>(); private Set<? extends Notification> notifications = new
     *              HashSet<Notification>(); private Set<Integer> dataFileIds = new HashSet<Integer>();
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("location", location)
                .append("terminationPolicy", terminationPolicy)
                .append("rampTime", rampTime)
                .append("loggingProfile", LoggingProfile.fromString(loggingProfile).getDisplayName())
                .append("stopBehavior", StopBehavior.fromString(stopBehavior).getDisplay())
                .append("simulationTime", simulationTime)
                .append("useEips", useEips)
                .append("baselineVirtualUsers", baselineVirtualUsers)
                .append("userIntervalIncrement", userIntervalIncrement)
                .append("reportingMode", reportingMode)
                .append("regions", ToStringBuilder.reflectionToString(regions))
                .toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobRequestImpl)) {
            return false;
        }
        JobRequest o = (JobRequest) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

    /**
     * Fluent Builder for JobRequests
     * 
     * @author dangleton
     * 
     */
    public static class Builder extends JobRequestBuilderBase<Builder> {

        public Builder() {
            super(new JobRequestImpl());
        }

        public JobRequest build() {
            return getInstance();
        }

    }

    /**
     * 
     * JobRequestImpl JobRequestBuilderBase
     * 
     * @author dangleton
     * 
     * @param <GeneratorT>
     */
    static class JobRequestBuilderBase<GeneratorT extends JobRequestBuilderBase<GeneratorT>> {
        private JobRequestImpl instance;

        protected JobRequestBuilderBase(JobRequestImpl aInstance) {
            instance = aInstance;
        }

        protected JobRequest getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withId(String aValue) {
            instance.id = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withStopBehavior(String aValue) {
            instance.stopBehavior = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withVmInstanceType(String aValue) {
            instance.vmInstanceType = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withnumUsersPerAgent(int aValue) {
            instance.numUsersPerAgent = aValue;
            return (GeneratorT) this;
        }
        @SuppressWarnings("unchecked")
        public GeneratorT withUseEips(boolean aValue) {
            instance.useEips = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withLoggingProfile(String aValue) {
            instance.loggingProfile = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withIncrementStrategy(IncrementStrategy aValue) {
            instance.incrementStrategy = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withLocation(String aValue) {
            instance.location = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withTerminationPolicy(TerminationPolicy aValue) {
            instance.terminationPolicy = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withRampTime(long aValue) {
            instance.rampTime = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withBaselineVirtualUsers(int aValue) {
            instance.baselineVirtualUsers = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withSimulationTime(long aValue) {
            instance.simulationTime = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withUserIntervalIncrement(int aValue) {
            instance.userIntervalIncrement = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withReportingMode(String aValue) {
            instance.reportingMode = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withStatus(JobQueueStatus aValue) {
            instance.status = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withRegions(Set<? extends RegionRequest> aValue) {
            instance.regions = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedRegion(RegionRequest aValue) {
            if (instance.getRegions() == null) {
                instance.regions = new HashSet<RegionRequest>();
            }
            ((HashSet<RegionRequest>) instance.getRegions()).add(aValue);
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withNofitications(Set<? extends Notification> aValue) {
            instance.notifications = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedNofitication(Notification aValue) {
            if (instance.getNotifications() == null) {
                instance.notifications = new HashSet<Notification>();
            }
            ((HashSet<Notification>) instance.getNotifications()).add(aValue);
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withDataFileIds(Set<Integer> aValue) {
            instance.dataFileIds = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withAddedDataFileId(Integer aValue) {
            if (instance.getDataFileIds() == null) {
                instance.dataFileIds = new HashSet<Integer>();
            }
            ((HashSet<Integer>) instance.getDataFileIds()).add(aValue);
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withScriptXmlUrl(String aValue) {
            instance.scriptsXmlUrl = aValue;
            return (GeneratorT) this;
        }
    }

}