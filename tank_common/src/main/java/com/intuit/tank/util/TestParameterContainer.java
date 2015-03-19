package com.intuit.tank.util;

/*
 * #%L
 * Common
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

public final class TestParameterContainer implements Serializable {
    private static final long serialVersionUID = 1L;
    private long rampTime;
    private long simulationTime;

    /**
     * private constructor
     */
    private TestParameterContainer() {

    }

    /**
     * Get an instance of a builder for this class.
     * 
     * @return
     */
    public static final Builder builder() {
        return new Builder();
    }

    /**
     * @return the rampTime
     */
    public long getRampTime() {
        return rampTime;
    }

    /**
     * @return the simulationTime
     */
    public long getSimulationTime() {
        return simulationTime;
    }

    /**
     * 
     * TestParameterContainer Builder
     * 
     * @author dangleton
     * 
     */
    public static class Builder {
        TestParameterContainer container;

        private Builder() {
            container = new TestParameterContainer();
        }

        public Builder withSimulationTime(long aValue) {
            container.simulationTime = aValue;
            return this;
        }

        public Builder withRampTime(long aValue) {
            container.rampTime = aValue;
            return this;
        }

        public TestParameterContainer build() {
            return container;
        }
    }

}
