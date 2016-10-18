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

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * EntityVersion
 * 
 * @author dangleton
 * 
 */
@Embeddable
public class EntityVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "object_id")
    private int objectId;

    @Column(name = "version_id")
    private int versionId;

    @Column(name = "object_class")
    private String objectClass;

    public EntityVersion() {

    }

    /**
     * @param objectId
     * @param versionId
     * @param objectClass
     */
    public EntityVersion(int objectId, int versionId, @Nonnull Class<? extends BaseEntity> objectClass) {
        this.objectId = objectId;
        this.versionId = versionId;
        this.objectClass = objectClass.getCanonicalName();
    }

    /**
     * @return the objectId
     */
    public int getObjectId() {
        return objectId;
    }

    /**
     * @return the versionId
     */
    public int getVersionId() {
        return versionId;
    }

    /**
     * @return the objectClass
     */
    public String getObjectClass() {
        return objectClass;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("object class", getObjectClass()).append("objectId", getObjectId())
                .append("version", getVersionId())
                .toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EntityVersion)) {
            return false;
        }
        EntityVersion o = (EntityVersion) obj;
        return new EqualsBuilder().append(o.getObjectId(), getObjectId()).append(o.getVersionId(), getVersionId())
                .append(o.getObjectClass(), getObjectClass()).isEquals();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(35, 21).append(getObjectId()).append(getVersionId()).append(getObjectClass())
                .toHashCode();
    }

}
