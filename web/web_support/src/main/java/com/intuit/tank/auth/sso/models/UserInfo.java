package com.intuit.tank.auth.sso.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;
import java.util.List;

/**
 * UserInfo
 *
 * @author Shawn Park
 */
@Data
@ToString
public class UserInfo implements Serializable {

    @JsonProperty("sub")
    private String subject;

    @JsonProperty("iss")
    private String issuer;

    @JsonProperty("groups")
    private List<String> groups;

    @JsonProperty("preferred_username")
    private String username;

    @JsonProperty("given_name")
    private String firstName;

    @JsonProperty("aud")
    private String audience;

    @JsonProperty("acr")
    private String authenticationContext;

    @JsonProperty("auth_time")
    private long authenticationTimeUtc;

    @JsonProperty("name")
    private String fullName;

    @JsonProperty("exp")
    private long expirationTimeUtc;

    @JsonProperty("iat")
    private long tokenIssueTimeUtc;

    @JsonProperty("family_name")
    private String lastName;

    @JsonProperty("jti")
    private String tokenIdentifier;

    @JsonProperty("email")
    private String email;

    @JsonProperty("nonce")
    private String nonce;
}
