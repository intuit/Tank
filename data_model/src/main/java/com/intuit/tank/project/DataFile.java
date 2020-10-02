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

@Entity
@Audited
@Table(name = "data_file",
        indexes = { @Index(name = "IDX_CREATOR", columnList = ColumnPreferences.PROPERTY_CREATOR),
                    @Index(name = "IDX_PATH", columnList = "path")})
public class DataFile extends OwnableEntity implements Comparable<DataFile> {

    private static final long serialVersionUID = 1L;

    // path relative from datafiles directory in S3
    @Column(name = "path", nullable = false)
    @NotNull
    @Size(max = 255)
    private String path;

    @Column(name = "comments", length = 1024)
    @Size(max = 1024)
    private String comments;

    @Column(name = "file_name")
    private String fileName;

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *            the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @inheritDoc
     */
    @Override
    public String toString() {
        return path;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DataFile)) {
            return false;
        }
        DataFile o = (DataFile) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getId()).toHashCode();
    }

    /**
     * @inheritDoc
     */
    @Override
    public int compareTo(DataFile o) {
        return path.compareTo(o.path);
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(DataFile df) {
        return new Builder(df);
    }
    
    /**
     * Fluent Builder for DataFile Builder
     * 
     * @author Vaishakh
     * 
     */
    public static class Builder extends DataFileBase<Builder> {

        private Builder() {
            super(new DataFile());
        }

        private Builder(DataFile df) {
            super(df);
        }

        public DataFile build() {
            return getInstance();
        }
    }

    private static class DataFileBase<GeneratorT extends DataFileBase<GeneratorT>> {
        private DataFile instance;

        protected DataFileBase(DataFile aInstance) {
            instance = aInstance;
        }

        protected DataFile getInstance() {
            return instance;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT comments(String aValue) {
            instance.setComments(aValue);

            return (GeneratorT) this;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT fileName(String aValue) {
            instance.setFileName(aValue);

            return (GeneratorT) this;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT path(String aValue) {
            instance.setPath(aValue);

            return (GeneratorT) this;
        }
        
        @SuppressWarnings("unchecked")
        public GeneratorT creator(String aValue) {
            instance.setCreator(aValue);

            return (GeneratorT) this;
        }
        
    }

}
