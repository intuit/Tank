/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script.models;

import jakarta.xml.bind.annotation.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Builder(setterPrefix = "with")
@NoArgsConstructor  // makes JAXB happy, will never be invoked
@AllArgsConstructor
@XmlRootElement(name = "script", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScriptTO", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "id",
        "created",
        "modified",
        "creator",
        "name",
        "runtime",
        "productName",
        "comments",
        "steps"
})
public class ScriptTO {

    @XmlElement(name = "id", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Integer id;

    @XmlElement(name = "created", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date created;

    @XmlElement(name = "modified", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date modified;

    @XmlElement(name = "creator", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String creator;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String name;

    @XmlElement(name = "runtime", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private int runtime;

    @XmlElement(name = "productName", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String productName;

    @XmlElement(name = "comments", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String comments;

    @Singular(ignoreNullCollections = true)
    @XmlElementWrapper(name = "steps", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "step", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<ScriptStepTO> steps;

    /**
     * {@inheritDoc}
     */
    public Integer getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    public Date getCreated() {
        return created;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * {@inheritDoc}
     */
    public Date getModified() {
        return modified;
    }

    /**
     * {@inheritDoc}
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * {@inheritDoc}
     */
    public String getCreator() {
        return creator;
    }

    /**
     * {@inheritDoc}
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * {@inheritDoc}
     */
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    /**
     * {@inheritDoc}
     */
    public String getProductName() {
        return productName;
    }

    /**
     * {@inheritDoc}
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * {@inheritDoc}
     */
    public String getComments() {
        return comments;
    }

    /**
     * {@inheritDoc}
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * {@inheritDoc}
     */
    public List<ScriptStepTO> getSteps() {
        return steps;
    }

    /**
     * {@inheritDoc}
     */
    public void setSteps(List<ScriptStepTO> steps) {
        this.steps = steps;
    }

}
