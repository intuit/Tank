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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "script_group_step")
public class ScriptGroupStep extends BaseEntity {

    private static final long serialVersionUID = 1L;
    public static final String PROPERTY_SCRIPT = "script";

    @Column(name = "loop_count")
    private int loop = 1;

    @ManyToOne
    @JoinColumn(name = "script_group_id", updatable = false, insertable = false)
    private ScriptGroup scriptGroup;

    @OneToOne
    @JoinColumn(name = "script_id")
    private Script script;

    @Column(name = "position", insertable = false, updatable = false)
    private Integer position;

    /**
     * @return the position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * @return the loop
     */
    public int getLoop() {
        return loop;
    }

    /**
     * @param loop
     *            the loop to set
     */
    public void setLoop(int loop) {
        this.loop = loop;
    }

    /**
     * @return the scriptGroup
     */
    public ScriptGroup getScriptGroup() {
        return scriptGroup;
    }

    /**
     * @param scriptGroup
     *            the scriptGroup to set
     */
    protected void setScriptGroup(ScriptGroup scriptGroup) {
        this.scriptGroup = scriptGroup;
    }

    /**
     * @param scriptGroup
     *            the scriptGroup to set
     */
    public void setParent(ScriptGroup scriptGroup) {
        this.scriptGroup = scriptGroup;
    }

    /**
     * @return the script
     */
    public Script getScript() {
        return script;
    }

    /**
     * @param script
     *            the script to set
     */
    public void setScript(Script script) {
        this.script = script;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("loop", loop)
                .toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScriptGroupStep)) {
            return false;
        }
        ScriptGroupStep o = (ScriptGroupStep) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

    public static ScriptGroupStepBuilder builder() {
        return new ScriptGroupStepBuilder();
    }

    public static ScriptGroupStepBuilder builderFrom(ScriptGroupStep step) {
        return new ScriptGroupStepBuilder(step);
    }

    public static class ScriptGroupStepBuilder extends ScriptGroupStepBuilderBase<ScriptGroupStepBuilder> {

        private ScriptGroupStepBuilder() {
            super(new ScriptGroupStep());
        }

        private ScriptGroupStepBuilder(ScriptGroupStep step) {
            super(step);
        }

        public ScriptGroupStep build() {
            return getInstance();
        }
    }

    static class ScriptGroupStepBuilderBase<GeneratorT extends ScriptGroupStepBuilderBase<GeneratorT>> {
        private ScriptGroupStep instance;

        protected ScriptGroupStepBuilderBase(ScriptGroupStep aInstance) {
            instance = aInstance;
        }

        protected ScriptGroupStep getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT loop(int aValue) {
            instance.setLoop(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT scriptGroup(ScriptGroup aValue) {
            instance.setScriptGroup(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT script(Script aValue) {
            instance.setScript(aValue);

            return (GeneratorT) this;
        }
    }

}
