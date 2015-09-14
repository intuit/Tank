package com.intuit.tank.http;

/**
 * 
 * @author denisa
 *
 */
public enum AuthScheme {

    NTLM("NTLM"), BASIC("BASIC"), DIGEST("DIGEST"), ALL(null);

    private String representation;

    private AuthScheme(String representation) {
        this.representation = representation;
    }

    public String getRepresentation() {
        return representation;
    }

}
