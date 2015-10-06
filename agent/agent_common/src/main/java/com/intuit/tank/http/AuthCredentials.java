/**
 * 
 */
package com.intuit.tank.http;

import java.io.Serializable;

import com.intuit.tank.http.AuthScheme;

/**
 * @author denisa
 *
 */
public class AuthCredentials implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userName;
    private String password;
    private AuthScheme scheme;
    private String host;
    private String portString;
    private String realm;
    
    public static final Builder builder() {
        return new Builder();
    }
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @return the scheme
     */
    public AuthScheme getScheme() {
        return scheme;
    }
    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }
    /**
     * @return the portString
     */
    public String getPortString() {
        return portString;
    }
    /**
     * @return the realm
     */
    public String getRealm() {
        return realm;
    }

    public static final class Builder {
        private AuthCredentials instance;

        private  Builder() {
            this.instance = new AuthCredentials();
        }
        public AuthCredentials build() {
            return instance;
        }
        
        public Builder withUserName(String aValue) {
            instance.userName = aValue;
            return this;
        }
        
        public Builder withPassword(String aValue) {
            instance.password = aValue;
            return this;
        }
        
        public Builder withHost(String aValue) {
            instance.host = aValue;
            return this;
        }
        
        public Builder withPortString(String aValue) {
            instance.portString = aValue;
            return this;
        }
        
        public Builder withRealm(String aValue) {
            instance.realm = aValue;
            return this;
        }
        
        public Builder withScheme(AuthScheme aValue) {
            instance.scheme = aValue;
            return this;
        }
        
    }

}
