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
import jakarta.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "automationScriptGroup", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AutomationScriptGroup", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "name",
        "loop",
        "position"
})
public class AutomationScriptGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String name;

    @XmlElement(name = "loop", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int loop = 1;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer position;

    @XmlElement(name = "scripts", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<AutomationScriptGroupStep> scripts = new ArrayList<AutomationScriptGroupStep>();

    /**
     * @param name
     * @param loop
     * @param position
     */
    public AutomationScriptGroup(String name, int loop, Integer position, List<AutomationScriptGroupStep> scripts) {
        this.name = name;
        this.loop = loop;
        this.position = position;
        this.scripts = scripts;
    }

    protected AutomationScriptGroup() {
    }

    /**
     * @return the name
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
     * @return the scripts
     */
    public List<AutomationScriptGroupStep> getScripts() {
        return scripts;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("loop", loop).toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AutomationScriptGroup)) {
            return false;
        }
        AutomationScriptGroup o = (AutomationScriptGroup) obj;
        return new EqualsBuilder().append(o.name, name).append(o.loop, loop).append(o.position, position)
                .append("scripts", scripts).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(27, 45).append(getName()).append(getLoop()).append(getPosition()).toHashCode();
    }

}
