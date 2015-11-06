package com.intuit.tank.http;

import javax.xml.bind.annotation.XmlEnum;

/**
 * 
 * @author denisa
 *
 */
@XmlEnum(String.class)
public enum AuthScheme {
    
    Basic("BASIC"), 
    Digest("DIGEST"),
    NTLM("NTLM");

    private String representation;

    private AuthScheme(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

    public static AuthScheme getScheme(String value) {
        AuthScheme ret = AuthScheme.Basic;
        try {
            ret = AuthScheme.valueOf(value);
        } catch (Exception e) {
        }
        return ret;
    }

}
