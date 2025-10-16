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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.Audited;

import java.util.concurrent.ConcurrentHashMap;

@Entity
@Audited
@Table(name = "external_script",
        indexes = { @Index(name = "IDX_CREATOR", columnList = ColumnPreferences.PROPERTY_CREATOR)})
public class ExternalScript extends OwnableEntity implements Comparable<ExternalScript> {

    private static final long serialVersionUID = 1L;

    // Cache ScriptEngine instances to avoid expensive recreation
    // Thread-safe: ConcurrentHashMap + ScriptEngine reuse is safe in Nashorn 
    private static final ConcurrentHashMap<String, ScriptEngine> ENGINE_CACHE = new ConcurrentHashMap<>();
    private static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();

    @Column(name = "name", length = 255, nullable = false)
    @NotNull
    @Size(max = 255)
    private String name;

    @Column(name = "script")
    @Lob
    private String script;

    @Column(name = "product_name", length = 255)
    @Size(max = 255)
    private String productName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * @param script
     *            the script to set
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName
     *            the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets a cached ScriptEngine for this script's file extension.
     *
     * Thread Safety: ConcurrentHashMap.computeIfAbsent is thread-safe.
     * ScriptEngine reuse is safe for Nashorn (read operations are thread-safe,
     * write operations use isolated ScriptContext in ScriptRunner).
     * 
     * @return cached ScriptEngine instance for this script's extension
     * @throws IllegalStateException if no ScriptEngine is available for the extension
     */
    public ScriptEngine getEngine() {
        String extension = FilenameUtils.getExtension(name);
        if (extension == null || extension.isEmpty()) {
            extension = "js"; // Default to JavaScript
        }
        
        return ENGINE_CACHE.computeIfAbsent(extension, ext -> {
            ScriptEngine engine = SCRIPT_ENGINE_MANAGER.getEngineByExtension(ext);
            if (engine == null) {
                throw new IllegalStateException(
                    String.format("No ScriptEngine found for extension: %s (script: %s)", ext, name)
                );
            }
            return engine;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", getId()).append("name", name).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ExternalScript)) {
            return false;
        }
        ExternalScript o = (ExternalScript) obj;
        return new EqualsBuilder().append(o.getId(), getId()).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(39, 41).append(getId()).toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ExternalScript o) {
        return name.compareToIgnoreCase(o.getName());
    }
}
