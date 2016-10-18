/**
 * 
 */
package com.intuit.tank.api.model.v1.agent;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author denisa
 *
 */
@XmlRootElement(name = "tankHttpClientDefinition", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TankHttpClientDefinition", namespace = Namespace.NAMESPACE_V1)
public class TankHttpClientDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String name;

    @XmlElement(name = "className", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String className;

    /**
     * @param name
     * @param className
     */
    public TankHttpClientDefinition(String name, String className) {
        this.name = name;
        this.className = className;
    }
    /**
     * @frameworkuseonly
     */
    protected TankHttpClientDefinition() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    @Override
    public String toString() {
        return name + " (" + className + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
