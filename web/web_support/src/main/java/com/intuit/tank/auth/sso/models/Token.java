package com.intuit.tank.auth.sso.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;

/**
 * Token
 *
 * @author Shawn Park
 *
 */
@Data
@ToString
public class Token implements Serializable {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("id_token")
    private String idToken;
}
