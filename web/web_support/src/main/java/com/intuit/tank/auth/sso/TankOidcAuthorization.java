package com.intuit.tank.auth.sso;

import com.google.gson.Gson;
import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.auth.sso.models.Token;
import com.intuit.tank.http.WebHttpClient;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * TankOidcAuthorization
 *
 * @author Shawn Park
 */
public class TankOidcAuthorization {
    private static final Logger LOG = LogManager.getLogger(TankOidcAuthorization.class);

    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRET_KEY = "client_secret";
    private static final String CONTENT_TYPE_KEY = "content-type";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String GRANT_TYPE_KEY = "grant_type";
    private static final String GRANT_TYPE_VALUE = "authorization_code";
    private static final String GRANT_CODE_KEY = "code";
    private static final String REDIRECT_URL_KEY = "redirect_uri";

    private String AUTHENTICATION_URL;
    private String CLIENT_SECRET_VALUE;
    private String CLIENT_ID_VALUE;
    private String REDIRECT_URL_VALUE;
    private Gson _gson = new Gson();

    @Inject
    private WebHttpClient _webHttpClient;

    @Inject
    private TankConfig _tankConfig;

    public Token GetAccessToken(String authorizationCode) throws IOException {
        OidcSsoConfig _oidcSsoConfig = _tankConfig.getOidcSsoConfig();

        AUTHENTICATION_URL = _oidcSsoConfig.getAuthenticationUrl();
        CLIENT_SECRET_VALUE = _oidcSsoConfig.getClientSecret();
        CLIENT_ID_VALUE = _oidcSsoConfig.getClientId();
        REDIRECT_URL_VALUE = _oidcSsoConfig.getRedirectUrl();

        Map<Object, Object> oidcRequestParameters = getOidcRequestParameters(authorizationCode);

        HttpResponse<String> httpPostResponse = _webHttpClient.Post(AUTHENTICATION_URL, oidcRequestParameters);
        return _gson.fromJson(httpPostResponse.body(), Token.class);
    }

    public UserInfo DecodeIdToken(String idToken) {
        try {
            var truncatedEncodedIdTokenString = idToken.substring(idToken.indexOf('.')+1, idToken.lastIndexOf('.'));

            byte[] decodedIdTokenBuffer = Base64.getDecoder().decode(truncatedEncodedIdTokenString);
            String decodedUserInfoString = new String(decodedIdTokenBuffer);

            return _gson.fromJson(decodedUserInfoString, UserInfo.class);
        } catch(Exception e) {
            LOG.error("Error decoding ID token", e);
            return null;
        }
    }

    private Map<Object, Object> getOidcRequestParameters(String authorizationCode) {
        Map<Object, Object> requestParameters = new HashMap<>();

        requestParameters.put(CLIENT_ID_KEY, CLIENT_ID_VALUE);
        requestParameters.put(CLIENT_SECRET_KEY, CLIENT_SECRET_VALUE);
        requestParameters.put(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);
        requestParameters.put(GRANT_TYPE_KEY, GRANT_TYPE_VALUE);
        requestParameters.put(GRANT_CODE_KEY, authorizationCode);
        requestParameters.put(REDIRECT_URL_KEY, REDIRECT_URL_VALUE);

        return requestParameters;
    }
}
