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

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "test_plan")
public class TestPlan extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false, length = 255)
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "user_percentage")
    private int userPercentage = 100;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "test_plan_id", referencedColumnName = "id")
    @OrderColumn(name = "position")
    @AuditMappedBy(mappedBy = "testPlan", positionMappedBy = "position")
    private List<ScriptGroup> scriptGroups = new ArrayList<ScriptGroup>();

    @ManyToOne
    @JoinColumn(name = "workload_id", updatable = false, insertable = false)
    private Workload workload;

    @Column(name = "position", insertable = false, updatable = false)
    private Integer position;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * 
     * @param scriptGroups
     */
    public void setScriptGroups(List<ScriptGroup> scriptGroups) {
        this.scriptGroups = scriptGroups;
    }

    /**
     * 
     * @return
     */
    public List<ScriptGroup> getScriptGroups() {
        return this.scriptGroups;
    }

    /**
     * 
     * @param scriptGroup
     */
    public void addScriptGroup(ScriptGroup scriptGroup) {
        addScriptGroupAt(scriptGroup, -1);
    }

    /**
     * 
     * @return
     */
    public int getUserPercentage() {
        return userPercentage;
    }

    /**
     * 
     * @param userPercentage
     */
    public void setUserPercentage(int userPercentage) {
        this.userPercentage = userPercentage;
    }

    /**
     * 
     * @param scriptGroup
     * @param index
     */
    public void addScriptGroupAt(ScriptGroup scriptGroup, int index) {

        scriptGroup.setTestPlan(this);
        if (index >= 0 && index < scriptGroups.size()) {
            this.scriptGroups.add(index, scriptGroup);
        } else {
            this.scriptGroups.add(scriptGroup);
        }
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
     * @return the workload
     */
    public Workload getWorkload() {
        return workload;
    }

    /**
     * @param w
     */
    public void setParent(Workload w) {
        setWorkload(w);
    }

    /**
     * @param workload
     *            the workload to set
     */
    protected void setWorkload(Workload workload) {
        this.workload = workload;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", name)
                .toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestPlan)) {
            return false;
        }
        TestPlan o = (TestPlan) obj;
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

    public static Builder builderFrom(@Nonnull TestPlan plan) {
        return new Builder(plan);
    }

    public static class Builder extends ScriptGroupBuilderBase<Builder> {

        private Builder() {
            super(new TestPlan());
        }

        private Builder(@Nonnull TestPlan group) {
            super(group);
        }

        public TestPlan build() {
            return getInstance();
        }
    }

    static class ScriptGroupBuilderBase<GeneratorT extends ScriptGroupBuilderBase<GeneratorT>> {
        private TestPlan instance;

        protected ScriptGroupBuilderBase(TestPlan aInstance) {
            instance = aInstance;
        }

        protected TestPlan getInstance() {
            return instance;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT name(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT usersPercentage(int aValue) {
            instance.setUserPercentage(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT workload(Workload aValue) {
            instance.setWorkload(aValue);

            return (GeneratorT) this;
        }
    }

}
