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
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "script_filter_group",
        indexes = { @Index(name = "IDX_CREATOR", columnList = ColumnPreferences.PROPERTY_CREATOR),
                    @Index(name = "IDX_PRODUCT_NAME", columnList = "product_name")})
public class ScriptFilterGroup extends OwnableEntity implements Comparable<ScriptFilterGroup> {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_PRODUCT_NAME = "productName";
    public static final String PROPERTY_FILTERS = "filters";

    @Column(name = "name", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "product_name", length = 255)
    @Size(max = 255)
    private String productName;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "filter_id"),
            inverseJoinColumns = @JoinColumn(name = "filter_group_id"))
    private Set<ScriptFilter> filters = new HashSet<ScriptFilter>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<ScriptFilter> getFilters() {
        return filters;
    }

    public void setFilters(Set<ScriptFilter> filters) {
        this.filters = filters;
    }

    public void addFilter(ScriptFilter filter) {
        this.filters.add(filter);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", this.name)
                .append("productName", this.productName)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScriptFilterGroup)) {
            return false;
        }
        ScriptFilterGroup o = (ScriptFilterGroup) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(57, 23).append(getId()).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ScriptFilterGroup other) {
        return name.compareToIgnoreCase(other.getName());
    }
}
