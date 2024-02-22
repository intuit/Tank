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

import java.sql.Blob;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Audited
@Table(name = "serialized_script_step")
public class SerializedScriptStep extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "serialized_data", nullable = false)
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @NotAudited
    private Blob serialzedData;

    @Transient
    private byte[] bytes;

    /**
     * Constructor
     */
    public SerializedScriptStep() {
    }

    /**
     * @param serialzedData
     *          new constructor for a new script
     */
    public SerializedScriptStep(byte[] serialzedData) {
        this.bytes = serialzedData;

    }

    /**
     * @param serialzedData
     *            the serialzedData to set
     */
    public void setSerialzedData(Blob serialzedData) {
        this.serialzedData = serialzedData;
    }

    /**
     * @return the serialzedData
     */
    public Blob getSerialzedBlob() {
        return serialzedData;
    }

    /**
     * @param bytes
     *          the bytes stored before Blob creation
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * @return the bytes
     */
    public byte[] getBytes() {
        return bytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId())
                .toString();
    }

    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public boolean equals(Object obj) {
    // if (!(obj instanceof SerializedScriptStep)) {
    // return false;
    // }
    // SerializedScriptStep o = (SerializedScriptStep) obj;
    // return new EqualsBuilder().append(o.getId(), getId()).append(getSerialzedBlob(), rhs).isEquals();
    // }
    //
    // /**
    // * {@inheritDoc}
    // */
    // @Override
    // public int hashCode() {
    // return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    // }

}
