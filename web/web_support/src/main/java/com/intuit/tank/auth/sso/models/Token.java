package com.intuit.tank.auth.sso.models;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("id_token")
    private String idToken;
}
