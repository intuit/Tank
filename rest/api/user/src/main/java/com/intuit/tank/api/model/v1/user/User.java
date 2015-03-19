/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.api.model.v1.user;

/*
 * #%L
 * User Rest API
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
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * ProjectTO
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "user", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserTO", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "name",
        "email",
        "groups"
})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String name;

    @XmlElement(name = "email", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String email;

    @XmlElementWrapper(name = "groups", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "group", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<String> groups = new HashSet<String>();

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
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the groups
     */
    public Set<String> getGroups() {
        return groups;
    }

    /**
     * @param groups
     *            the groups to set
     */
    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }

    /**
     * @param groups
     *            the groups to set
     */
    public void addGroup(String group) {
        this.groups.add(group);
    }

}
