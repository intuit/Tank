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

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "script_group")
public class ScriptGroup extends BaseEntity implements Comparable<ScriptGroup> {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false, length = 255)
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "loop_count")
    private int loop = 1;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "script_group_id", referencedColumnName = "id")
    @OrderColumn(name = "position")
    @AuditMappedBy(mappedBy = "scriptGroup", positionMappedBy = "position")
    private List<ScriptGroupStep> steps = new ArrayList<ScriptGroupStep>();

    @ManyToOne
    @JoinColumn(name = "test_plan_id", updatable = false, insertable = false)
    private TestPlan testPlan;

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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the scripts
     */
    public List<ScriptGroupStep> getScriptGroupSteps() {
        return steps;
    }

    /**
     * @param scripts
     *            the scripts to set
     */
    public void setScriptGroupSteps(List<ScriptGroupStep> scripts) {
        this.steps = scripts;
    }

    /**
     * @param step
     *            the scripts to set
     */
    public void addScriptGroupStep(ScriptGroupStep step) {
        step.setScriptGroup(this);
        this.steps.add(step);
    }

    /**
     * @param step
     *            the scripts to remove
     */
    public void removeScriptGroupStep(ScriptGroupStep step) {
        step.setScriptGroup(null);
        this.steps.remove(step);
    }

    /**
     * @return the workload
     */
    public TestPlan getTestPlan() {
        return testPlan;
    }

    /**
     * @param w
     */
    public void setParent(TestPlan w) {
        setTestPlan(w);
    }

    /**
     * @param testPlan
     *            the testPlan to set
     */
    protected void setTestPlan(TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", name)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScriptGroup)) {
            return false;
        }
        ScriptGroup o = (ScriptGroup) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(@Nonnull ScriptGroup group) {
        return new Builder();
    }

    public static class Builder extends ScriptGroupBuilderBase<Builder> {

        private Builder() {
            super(new ScriptGroup());
        }

        private Builder(@Nonnull ScriptGroup group) {
            super(group);
        }

        public ScriptGroup build() {
            return getInstance();
        }
    }

    static class ScriptGroupBuilderBase<GeneratorT extends ScriptGroupBuilderBase<GeneratorT>> {
        private ScriptGroup instance;

        protected ScriptGroupBuilderBase(ScriptGroup aInstance) {
            instance = aInstance;
        }

        protected ScriptGroup getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT name(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT loop(int aValue) {
            instance.setLoop(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT testPlan(TestPlan aValue) {
            instance.setTestPlan(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addScriptGroupStep(ScriptGroupStep aValue) {
            instance.addScriptGroupStep(aValue);

            return (GeneratorT) this;
        }
    }

    @Override
    public int compareTo(ScriptGroup o) {
        if (o == null) {
            return 1;
        }
        return new CompareToBuilder().append(position, o.position).toComparison();
    }

}
