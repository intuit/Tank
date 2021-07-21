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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import com.intuit.tank.util.ScriptFilterType;

@Entity
@Audited
@Table(name = "script_filter",
        indexes = { @Index(name = "IDX_CREATOR", columnList = ColumnPreferences.PROPERTY_CREATOR)})
public class ScriptFilter extends OwnableEntity implements Comparable<ScriptFilter> {

    private static final long serialVersionUID = 1L;
    @Column(name = "name", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "persist")
    private boolean persist = true;

    @Column(name = "all_conditions_must_pass")
    private boolean allConditionsMustPass;

    @Column(name = "filter_type")
    private ScriptFilterType filterType;

    @Column(name = "external_script_id")
    private Integer externalScriptId;

    @Column(name = "product_name", length = 255)
    @Size(max = 255)
    private String productName;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "filter_id"),
            inverseJoinColumns = @JoinColumn(name = "action_id"))
    private Set<ScriptFilterAction> actions = new HashSet<ScriptFilterAction>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "filter_id"),
            inverseJoinColumns = @JoinColumn(name = "condition_id"))
    private Set<ScriptFilterCondition> conditions = new HashSet<ScriptFilterCondition>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ScriptFilterAction> getActions() {
        return actions;
    }

    public void setActions(Set<ScriptFilterAction> actions) {
        this.actions = actions;
    }

    public void addAction(ScriptFilterAction action) {
        this.actions.add(action);
    }

    public Set<ScriptFilterCondition> getConditions() {
        return conditions;
    }

    public void setConditions(Set<ScriptFilterCondition> conditions) {
        this.conditions = conditions;
    }

    public void addCondition(ScriptFilterCondition condition) {
        this.conditions.add(condition);
    }

    public boolean getPersist() {
        return persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public boolean getAllConditionsMustPass() {
        return allConditionsMustPass;
    }

    public void setAllConditionsMustPass(boolean allConditionsMustPass) {
        this.allConditionsMustPass = allConditionsMustPass;
    }

    public ScriptFilterType getFilterType() {
        if (this.filterType == null) {
            return ScriptFilterType.INTERNAL;
        } else {
            return this.filterType;
        }
    }

    public void setFilterType(ScriptFilterType filterType) {
        this.filterType = filterType;
    }

    public Integer getExternalScriptId() {
        return externalScriptId;
    }

    public void setExternalScriptId(Integer externalScriptId) {
        this.externalScriptId = externalScriptId;
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
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", name).append("persist", this.persist)
                .append("allConditionsMustPass", this.allConditionsMustPass)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScriptFilter)) {
            return false;
        }
        ScriptFilter o = (ScriptFilter) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(39, 41).append(getId()).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ScriptFilter o) {
        return name.compareToIgnoreCase(o.getName());
    }
}
