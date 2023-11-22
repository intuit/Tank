package com.intuit.tank.conversation;

import java.util.Date;

import javax.annotation.Nonnull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlType(name = "cookie", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class Cookie {

    @XmlElement(name = "key", required = true, namespace = Namespace.NAMESPACE_V1)
    private String key;
    @XmlElement(name = "value", required = true, namespace = Namespace.NAMESPACE_V1)
    private String value;
    @XmlElement(name = "expires", namespace = Namespace.NAMESPACE_V1)
    private Date expires;
    @XmlElement(name = "max-age", namespace = Namespace.NAMESPACE_V1)
    private String maxAge;
    @XmlElement(name = "path", namespace = Namespace.NAMESPACE_V1)
    private String path;
    @XmlElement(name = "domain", namespace = Namespace.NAMESPACE_V1)
    private String domain;
    @XmlElement(name = "securedOnly", namespace = Namespace.NAMESPACE_V1)
    private boolean securedOnly;
    @XmlElement(name = "httpOnly", namespace = Namespace.NAMESPACE_V1)
    private boolean httpOnly;

    /**
     * Default Constructor
     * 
     */
    public Cookie() {

    }

    public Cookie(@Nonnull String key, @Nonnull String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * @return the maxAge
     */
    public String getMaxAge() {
        return maxAge;
    }

    /**
     * @param maxAge
     *            the maxAge to set
     */
    public void setMaxAge(String maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the expires
     */
    public Date getExpires() {
        return expires;
    }

    /**
     * @param expires
     *            the expires to set
     */
    public void setExpires(Date expires) {
        this.expires = expires;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * @param domain
     *            the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * @return the securedOnly
     */
    public boolean isSecuredOnly() {
        return securedOnly;
    }

    /**
     * @param securedOnly
     *            the securedOnly to set
     */
    public void setSecuredOnly(boolean securedOnly) {
        this.securedOnly = securedOnly;
    }

    /**
     * @return the httpOnly
     */
    public boolean isHttpOnly() {
        return httpOnly;
    }

    /**
     * @param httpOnly
     *            the httpOnly to set
     */
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 25).
                append(key).append(value).append(path).append(expires).append(domain).append(securedOnly)
                .append(httpOnly).toHashCode();
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
        Cookie o = (Cookie) obj;

        return new EqualsBuilder().append(o.getDomain(), getDomain()).append(o.getExpires(), getExpires())
                .append(o.getKey(), getKey())
                .append(o.getValue(), getValue()).append(o.isSecuredOnly(), isSecuredOnly())
                .append(o.isHttpOnly(), isHttpOnly()).isEquals();
    }

}
