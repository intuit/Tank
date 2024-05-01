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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(setterPrefix = "with")
@XmlRootElement(name = "scriptSteps", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScriptStepContainer", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "steps",
        "startIndex",
        "numRequsted",
        "numReturned",
        "numRemaining"
})
public class ScriptStepContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Singular(ignoreNullCollections = true)
    @XmlElementWrapper(name = "steps", namespace = Namespace.NAMESPACE_V1)
    @XmlElement(name = "step", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private List<ScriptStepTO> steps;

    @XmlElement(name = "startIndex", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int startIndex;

    @XmlElement(name = "numRequsted", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int numRequsted;

    @XmlElement(name = "numReturned", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int numReturned;

    @XmlElement(name = "numRemaining", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int numRemaining;

    /**
     * @return the steps
     */
    public List<ScriptStepTO> getSteps() {
        return steps;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @return the numRequsted
     */
    public int getNumRequsted() {
        return numRequsted;
    }

    /**
     * @return the numReturned
     */
    public int getNumReturned() {
        return numReturned;
    }

    /**
     * @return the numRemaining
     */
    public int getNumRemaining() {
        return numRemaining;
    }
}