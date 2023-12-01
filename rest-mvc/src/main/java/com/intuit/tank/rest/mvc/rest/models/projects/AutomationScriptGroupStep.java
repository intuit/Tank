/*
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.projects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlRootElement(name = "automationScriptGroupStep", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationScriptGroupStep", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "scriptId",
        "name",
        "loop",
        "position"
})
public class AutomationScriptGroupStep implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "scriptId", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private Integer scriptId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String name;

    @XmlElement(name = "loop", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int loop = 1;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer position;

    /**
     * @param scriptId
     * @param loop
     * @param position
     */
    public AutomationScriptGroupStep(Integer scriptId, String name, int loop, Integer position) {
        this.scriptId = scriptId;
        this.name = name;
        this.loop = loop;
        this.position = position;
    }

    protected AutomationScriptGroupStep() {
    }

    /**
     * @return the scriptId
     */
    public Integer getScriptId() { return scriptId; }

    /**
     * @return the script name
     */
    public String getName() { return name; }

    /**
     * @return the loop
     */
    public int getLoop() {
        return loop;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("scriptId", scriptId).append("loop", loop).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AutomationScriptGroupStep)) {
            return false;
        }
        AutomationScriptGroupStep o = (AutomationScriptGroupStep) obj;
        return new EqualsBuilder().append(o.scriptId, scriptId).append(o.name, name).append(o.loop, loop)
                .append(o.position, position).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(27, 45).append(getScriptId()).append(getName()).append(getLoop())
                .append(getPosition()).toHashCode();
    }
}
