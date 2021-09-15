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
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

/**
 * 
 * ScriptFilterCondition represents a condition that the filter must match
 * 
 * @author dangleton
 * 
 */
@Entity
@Audited
@Table(name = "script_filter_condition")
public class ScriptFilterCondition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "filter_scope", length = 255)
    private String scope;

    @Column(name = "filter_condition", length = 255)
    private String condition;

    @Column(name = "filter_value", length = 255)
    private String value;

    // @ManyToOne
    // @JoinColumn(name = "filter_id", updatable = false, insertable = false)
    // private ScriptFilter filter;

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope
     *            the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition
     *            the condition to set
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    //
    // /**
    // * @return the filter
    // */
    // public ScriptFilter getScriptFilter() {
    // return filter;
    // }
    //
    // /**
    // * @param filter
    // * the filter to set
    // */
    // public void setScriptFilter(ScriptFilter filter) {
    // this.filter = filter;
    // }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("scope", this.scope)
                .append("condition", this.condition)
                .append("value", this.value)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScriptFilterCondition)) {
            return false;
        }
        ScriptFilterCondition o = (ScriptFilterCondition) obj;
        if (getId() == 0) {
            return new EqualsBuilder().append(condition, o.condition).append(scope, o.scope).append(value, o.value)
                    .isEquals();
        }
        return new EqualsBuilder().append(o.getId(), getId())
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(33, 19).append(getId())
                // .append(getCondition()).append(getScope()).append(getValue())
                .toHashCode();
    }
}
