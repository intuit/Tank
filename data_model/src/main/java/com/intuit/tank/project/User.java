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
import java.util.UUID;

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
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

/**
 * 
 * Project top level Object in data model. All objects are eventually traversable from their project.
 * 
 * @author dangleton
 * 
 */
@Entity
@Audited
@Table(name = "user",
        indexes = { @Index(name = "IDX_USER_NAME", columnList = User.PROPERTY_NAME),
                    @Index(name = "IDX_USER_EMAIL", columnList = User.PROPERTY_EMAIL),
                    @Index(name = "IDX_USER_TOKEN", columnList = "token")})

public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_GROUPS = "groups";
    public static final String PROPERTY_EMAIL = "email";
    public static final String PROPERTY_TOKEN = "apiToken";

    @Column(name = "name", unique = true, nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "email", nullable = false)
    @NotNull
    @Size(max = 255)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "token")
    private String apiToken;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @BatchSize(size=10)
    private Set<Group> groups = new HashSet<Group>();

    public User() {
    }

    /**
     * @param groups
     *            the groups to set
     */
    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

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
     * @return the apiToken
     */
    public String getApiToken() {
        return apiToken;
    }

    /**
     * @return apiToken
     */
    public void generateApiToken() {
        this.apiToken = UUID.randomUUID().toString();
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the groups
     */
    public Set<Group> getGroups() {
        return groups;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", name)
                .toString();
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User o = (User) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(User u) {
        return new Builder(u);
    }
    
    /**
     * Fluent Builder for User Builder
     * 
     * @author Vaishakh
     * 
     */
    public static class Builder extends UserBase<Builder> {

        private Builder() {
            super(new User());
        }

        private Builder(User p) {
            super(p);
        }

        public User build() {
            return getInstance();
        }
    }

    private static class UserBase<GeneratorT extends UserBase<GeneratorT>> {
        private User instance;

        protected UserBase(User aInstance) {
            instance = aInstance;
        }

        protected User getInstance() {
            return instance;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT email(String aValue) {
            instance.setEmail(aValue);

            return (GeneratorT) this;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT name(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT password(String aValue) {
            instance.setPassword(aValue);

            return (GeneratorT) this;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT groups(Set<Group> aValue) {
            instance.setGroups(aValue);

            return (GeneratorT) this;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT generateApiToken() {
            instance.generateApiToken();

            return (GeneratorT) this;
        }
    }

}
