package com.intuit.tank.vm.settings;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class InstanceTag implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    /**
     * @param name
     * @param value
     */
    public InstanceTag(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * tests to see if this tag has a name and a value and they are less than
     * the max size
     * 
     * @return
     */
    public boolean isValid() {
        return StringUtils.isNotBlank(name) && StringUtils.isNotBlank(value) && name.length() < 127 && value.length() < 255;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InstanceTag)) {
            return false;
        }
        InstanceTag other = (InstanceTag) o;
        return new EqualsBuilder().append(this.name, other.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).toHashCode();
    }

    @Override
    public String toString() {
        return name + " = " + value;
    }

}
