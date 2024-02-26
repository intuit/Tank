package com.intuit.tank.vm.vmManager.models;

/*
 * #%L
 * Cloud Rest API
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
import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement(name = "UserDetail", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserDetail", namespace = Namespace.NAMESPACE_V1)
public class UserDetail implements Serializable, Comparable<UserDetail> {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "script", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String script;

    @XmlElement(name = "users", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Integer users;

    @XmlElement(name = "created", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Date createTime;

    /**
     * @{frameworkUseOnly
     */
    public UserDetail() {

    }

    public UserDetail(String script, Integer users) {
        super();
        this.script = script;
        this.users = users;
        this.createTime = new Date();
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @return the users
     */
    public Integer getUsers() {
        return users;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public int compareTo(UserDetail o) {
        return script.compareTo(o.script);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
