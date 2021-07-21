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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;

@Entity
@Audited
@Table(name = "script_filter_action")
public class ScriptFilterAction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "filter_action", length = 255)
    @Enumerated(EnumType.STRING)
    private ScriptFilterActionType action;

    @Column(name = "filter_scope", length = 255)
    private String scope;

    @Column(name = "filter_key", length = 255)
    private String key;

    @Column(name = "filter_value", length = 255)
    private String value;

    // @ManyToOne
    // @JoinColumn(name = "filter_id", updatable = false, insertable = false)
    // private ScriptFilter filter;

    /**
     * @return the action
     */
    public ScriptFilterActionType getAction() {
        return action;
    }

    /**
     * @param action
     *            the action to set
     */
    public void setAction(ScriptFilterActionType action) {
        this.action = action;
    }

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
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
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

    // /**
    // * @return the filter
    // */
    // public ScriptFilter getScriptFilter() {
    // return filter;
    // }
    //
    // /**
    // * @param filter the filter to set
    // */
    // public void setScriptFilter(ScriptFilter filter) {
    // this.filter = filter;
    // }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("action", action).append("scope", scope)
                .append("key", key).append("value", value)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScriptFilterAction)) {
            return false;
        }
        ScriptFilterAction o = (ScriptFilterAction) obj;
        if (getId() == 0) {
            return new EqualsBuilder().append(action, o.action).append(scope, o.scope).append(key, o.key)
                    .append(value, o.value).isEquals();
        }
        return new EqualsBuilder().append(o.getId(), getId())
                // .append(o.getAction(), getAction()).append(o.getScope(), getScope()).append(o.getKey(),
                // getKey()).append(o.getValue(), getValue())
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(21, 57).append(getAction()).append(getScope()).append(getKey()).append(getValue())
                .toHashCode();
    }

}
