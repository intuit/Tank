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
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
@Table(name = "user_group",
        indexes = { @Index(name = "IDX_GROUP_NAME", columnList = Group.PROPERTY_NAME)})
public class Group extends BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String PROPERTY_NAME = "name";

    @Column(name = "name", unique = true, nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;

    public Group() {
    }

    /**
     * @param name
     */
    public Group(String name) {
        this.name = name;
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
     * @inheritDoc
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Group)) {
            return false;
        }
        Group o = (Group) obj;
        return new EqualsBuilder().append(o.getName(), getName()).isEquals();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getName()).toHashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(Group g) {
        return new Builder(g);
    }
    
    /**
     * Fluent Builder for Group Builder
     * 
     * @author Vaishakh
     * 
     */
    public static class Builder extends GroupBase<Builder> {

        private Builder() {
            super(new Group());
        }

        private Builder(Group g) {
            super(g);
        }

        public Group build() {
            return getInstance();
        }
    }

    private static class GroupBase<GeneratorT extends GroupBase<GeneratorT>> {
        private Group instance;

        protected GroupBase(Group aInstance) {
            instance = aInstance;
        }

        protected Group getInstance() {
            return instance;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT name(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }
    }
}
