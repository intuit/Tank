/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * ColumnPreferences
 * 
 * @author dangleton
 * 
 */
@Entity
@Table(name = "table_column",
        indexes = { @Index(name = "IDX_CREATOR", columnList = ColumnPreferences.PROPERTY_CREATOR)})
public class ColumnPreferences extends OwnableEntity implements Serializable {

    public static enum Visibility {
        VISIBLE, HIDDEN
    }

    public static enum Hidability {
        HIDABLE, NON_HIDABLE
    }

    private static final long serialVersionUID = 1L;

    @Size(max = 256, message = "Column name cannot be more than 256 characters long.")
    @NotNull(message = "Column Name must be specified.")
    @Column(name = "colname", nullable = false)
    private String colName;

    @Size(max = 256, message = "Display name cannot be more than 256 characters long.")
    @NotNull(message = "Display Name must be specified.")
    @Column(name = "displayname")
    private String displayName;

    @Column(name = "size", nullable = false)
    private int size;

    @Column(name = "visible", nullable = false)
    private boolean visible = true;

    @Column(name = "hideable", nullable = false)
    private boolean hideable = true;

    /**
     * @{frameworkUseOnly
     */
    public ColumnPreferences() {
    }

    /**
     * @param colName
     * @param size
     * @param visible
     * @param hidable
     */
    public ColumnPreferences(String colName, String displayName, int size, Visibility visible, Hidability hidable) {
        this.colName = colName;
        this.displayName = displayName;
        this.size = size;
        this.visible = visible == Visibility.VISIBLE;
        this.hideable = hidable == Hidability.HIDABLE;
    }

    /**
     * @return the hideable
     */
    public boolean isHideable() {
        return hideable;
    }

    /**
     * @return the colName
     */
    public String getColName() {
        return colName;
    }

    /**
     * @return
     */
    public String getDisplayName() {
        return displayName != null ? displayName : colName;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @param visible
     *            the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ColumnPreferences)) {
            return false;
        }
        ColumnPreferences o = (ColumnPreferences) obj;
        return new EqualsBuilder().append(colName, o.colName).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(colName).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("col-name", colName).append("label", displayName).append("size", size)
                .append("visible", visible)
                .toString();
    }

}
