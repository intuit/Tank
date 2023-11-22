package com.intuit.tank.conversation;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.annotation.Nonnull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlType(name = "header", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class Header {

    private static final String UTF_8 = "UTF-8";
    @XmlElement(name = "key", namespace = Namespace.NAMESPACE_V1)
    private String key;
    @XmlElement(name = "value", namespace = Namespace.NAMESPACE_V1)
    private byte[] valueBytes;

    /**
     * @param key
     * @param value
     */
    public Header(@Nonnull String key, @Nonnull String value) {
        this.key = key;
        if (value == null) {
            value = "";
        }
        this.valueBytes = value.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Default Constructor
     * 
     * Framework use only
     */
    protected Header() {
        this("", "");
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @XmlTransient
    public String getValue() {
        String value = "";
        value = new String(valueBytes, StandardCharsets.UTF_8);
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return key + " : " + getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 25).
                append(key).append(valueBytes).toHashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Header o = (Header) obj;

        return new EqualsBuilder().append(o.getKey(), getKey())
                .append(o.getValue(), getValue()).isEquals();
    }
}
