/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script.models;

import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "scriptSteps", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScriptStepContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "steps",
        "startIndex",
        "numRequsted",
        "numReturned",
        "numRemaining"
})
public class ScriptStepContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElementWrapper(name = "steps", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "step", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<ScriptStepTO> steps = new ArrayList<ScriptStepTO>();

    @XmlElement(name = "startIndex", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int startIndex;

    @XmlElement(name = "numRequsted", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int numRequsted;

    @XmlElement(name = "numReturned", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int numReturned;

    @XmlElement(name = "numRemaining", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int numRemaining;

    private ScriptStepContainer() {

    }

    /**
     * create a descriptor from the script object
     * 
     * @param script
     */
    public ScriptStepContainer(List<ScriptStepTO> steps) {
        this.steps = steps;
    }

    /**
     * @return the steps
     */
    public List<ScriptStepTO> getSteps() {
        return steps;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @return the numRequsted
     */
    public int getNumRequsted() {
        return numRequsted;
    }

    /**
     * @return the numReturned
     */
    public int getNumReturned() {
        return numReturned;
    }

    /**
     * @return the numRemaining
     */
    public int getNumRemaining() {
        return numRemaining;
    }

    /**
     * Gets a builder for ScriptStepContainers
     * 
     * @return
     */
    public static ScriptStepContainerBuilder builder() {
        return new ScriptStepContainerBuilder();
    }

    /**
     * Fluent builder for ScriptStepContainers
     */
    public static class ScriptStepContainerBuilder extends ScriptStepContainerBuilderBase<ScriptStepContainerBuilder> {

        public ScriptStepContainerBuilder() {
            super(new ScriptStepContainer());
        }

        public ScriptStepContainer build() {
            return getInstance();
        }
    }

    static class ScriptStepContainerBuilderBase<GeneratorT extends ScriptStepContainerBuilderBase<GeneratorT>> {
        private ScriptStepContainer instance;

        public ScriptStepContainerBuilderBase() {
        }

        protected ScriptStepContainerBuilderBase(ScriptStepContainer aInstance) {
            instance = aInstance;
        }

        protected ScriptStepContainer getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withSteps(List<ScriptStepTO> aValue) {
            instance.steps = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withStartIndex(int aValue) {
            instance.startIndex = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withNumRequsted(int aValue) {
            instance.numRequsted = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withNumReturned(int aValue) {
            instance.numReturned = aValue;
            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT withNumRemaining(int aValue) {
            instance.numRemaining = aValue < 0 ? 0 : aValue;
            return (GeneratorT) this;
        }
    }

}
