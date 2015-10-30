/**
 * 
 */
package com.intuit.tank.http;

import java.io.Serializable;

/**
 * @author denisa
 *
 */
public class TankCookie implements Serializable {
    private static final long serialVersionUID = 1L;

    private String domain;
    private String name;
    private String value;
    private String path;

    public static final Builder builder() {
        return new Builder();
    }

    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    public static final class Builder {
        private TankCookie instance;

        private Builder() {
            this.instance = new TankCookie();
        }

        public TankCookie build() {
            return instance;
        }

        public Builder withDomain(String aValue) {
            instance.domain = aValue;
            return this;
        }

        public Builder withName(String aValue) {
            instance.name = aValue;
            return this;
        }

        public Builder withPath(String aValue) {
            instance.path = aValue;
            return this;
        }

        public Builder withValue(String aValue) {
            instance.value = aValue;
            return this;
        }

    }

}
