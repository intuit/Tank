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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import com.intuit.tank.vm.api.enumerated.RecipientType;
import com.intuit.tank.vm.vmManager.Recipient;

/**
 * Recipient
 * 
 * @author dangleton
 * 
 */
@Entity
@Audited
@Table(name = "recipient")
public class JobRecipient extends BaseEntity implements Recipient {

    private static final long serialVersionUID = 1L;

    @Column(name = "address")
    private String address;

    @Column(name = "recipient_type")
    @Enumerated(EnumType.STRING)
    private RecipientType type = RecipientType.email;

    /**
     * 
     */
    public JobRecipient() {
        super();
    }

    /**
     * @param address
     * @param type
     */
    public JobRecipient(String address, RecipientType type) {
        super();
        this.address = address;
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAddress() {
        return address;
    }

    /**
     * {@inheritDoc}
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecipientType getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    public void setType(RecipientType type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("type", type).append("address", address)
                .toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobRecipient)) {
            return false;
        }
        JobRecipient o = (JobRecipient) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

}
