package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "script")
public class Script extends OwnableEntity implements Comparable<Script> {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false, length = 255)
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "runtime")
    private int runtime;

    @Column(name = "product_name", length = 255)
    @Size(max = 255)
    private String productName;

    @Column(name = "comments", length = 1024)
    @Size(max = 1024)
    private String comments;

    @Transient
    private List<ScriptStep> steps = new ArrayList<ScriptStep>();

    // @OneToOne(fetch = FetchType.EAGER)
    // @JoinColumn(name = "serial_step_id")
    // private SerializedScriptStep serializedScriptStep;

    @Column(name = "serial_step_id")
    private Integer serializedScriptStepId;

    // public void serialize() {
    // ObjectOutputStream s = null;
    // try {
    // // if (steps.size() > 0) {
    // ByteArrayOutputStream bos = new ByteArrayOutputStream();
    // s = new ObjectOutputStream(bos);
    // s.writeObject(steps);
    // serializedScriptStep = new SerializedScriptStep(bos.toByteArray());
    // // }
    // } catch (IOException e) {
    // throw new RecoverableException(e);
    // } finally {
    // IOUtils.closeQuietly(s);
    // }
    // }
    //
    // @PostLoad
    // protected void deserialize() {
    // steps = deserializeBlob(serializedScriptStep);
    // }

    @SuppressWarnings("unchecked")
    public static List<ScriptStep> deserializeBlob(SerializedScriptStep serializedScriptStep) {
        List<ScriptStep> result = null;
        ObjectInputStream s = null;
        try {
            if (serializedScriptStep != null && serializedScriptStep.getSerialzedBlob() != null) {
                s = new ObjectInputStream(serializedScriptStep.getSerialzedBlob().getBinaryStream());
                result = (List<ScriptStep>) s.readObject();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(s);
        }
        return result;
    }

    // /**
    // * @return the serializedScriptStep
    // */
    // public SerializedScriptStep getSerializedScriptStep() {
    // boolean load = serializedScriptStep == null;
    //
    // return serializedScriptStep;
    // }
    /**
     * @param serializedSteps
     */
    public void setSerializedSteps(SerializedScriptStep serializedSteps) {
        steps = deserializeBlob(serializedSteps);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the serializedScriptStepId
     */
    public Integer getSerializedScriptStepId() {
        return serializedScriptStepId;
    }

    /**
     * @param serializedScriptStepId
     *            the serializedScriptStepId to set
     */
    public void setSerializedScriptStepId(Integer serializedScriptStepId) {
        this.serializedScriptStepId = serializedScriptStepId;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the runtime
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * @param runtime
     *            the runtime to set
     */
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @return the steps
     */
    public List<ScriptStep> getScriptSteps() {
        return steps;
    }

    /**
     * @return the steps
     */
    public List<ScriptStep> getSteps() {
        return steps;
    }

    /**
     * @return the steps
     */
    public void setScriptSteps(List<ScriptStep> steps) {
        this.steps = steps;
    }

    /**
     * @return the steps
     */
    public void setSteps(List<ScriptStep> steps) {
        this.steps = steps;
    }

    /**
     * 
     * @param step
     */
    public void addStep(ScriptStep step) {
        // step.setScript(this);
        this.steps.add(step);
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Script)) {
            return false;
        }
        Script o = (Script) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(Script script) {
        return new Builder(script);
    }

    public static class Builder extends ScriptBuilderBase<Builder> {

        private Builder() {
            super(new Script());
        }

        private Builder(Script script) {
            super(script);
        }

        public Script build() {
            return getInstance();
        }
    }

    static class ScriptBuilderBase<GeneratorT extends ScriptBuilderBase<GeneratorT>> {
        private Script instance;

        protected ScriptBuilderBase(Script aInstance) {
            instance = aInstance;
        }

        protected Script getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT name(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT runtime(int aValue) {
            instance.setRuntime(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT productName(String aValue) {
            instance.setProductName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT comments(String aValue) {
            instance.setComments(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT steps(List<ScriptStep> aValue) {
            instance.setScriptSteps(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addStep(ScriptStep aValue) {
            if (instance.getScriptSteps() == null) {
                instance.setScriptSteps(new ArrayList<ScriptStep>());
            }

            ((ArrayList<ScriptStep>) instance.getScriptSteps()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT creator(String aValue) {
            instance.setCreator(aValue);

            return (GeneratorT) this;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public int compareTo(Script o) {
        if (name != null) {
            return name.compareToIgnoreCase(o.name);
        } else if (o.name != null) {
            return o.name.compareToIgnoreCase(name);
        }
        return 0;
    }

}
