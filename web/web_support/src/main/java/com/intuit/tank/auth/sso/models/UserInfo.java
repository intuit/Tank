package com.intuit.tank.auth.sso.models;

import com.google.gson.annotations.SerializedName;
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

    @SerializedName("sub")
    private String subject;

    @SerializedName("iss")
    private String issuer;

    @SerializedName("groups")
    private List<String> groups;

    @SerializedName("preferred_username")
    private String username;

    @SerializedName("given_name")
    private String firstName;

    @SerializedName("aud")
    private String audience;

    @SerializedName("acr")
    private String authenticationContext;

    @SerializedName("auth_time")
    private long authenticationTimeUtc;

    @SerializedName("name")
    private String fullName;

    @SerializedName("exp")
    private long expirationTimeUtc;

    @SerializedName("iat")
    private long tokenIssueTimeUtc;

    @SerializedName("family_name")
    private String lastName;

    @SerializedName("jti")
    private String tokenIdentifier;

    @SerializedName("email")
    private String email;
}
