package com.intuit.tank.auth.sso;

import com.google.gson.Gson;
import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.auth.sso.models.Token;
import com.intuit.tank.http.WebHttpClient;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * TankOidcAuthorization
 *
 * @author Shawn Park
 */
public class TankOidcAuthorization {
    private static final Logger LOG = LogManager.getLogger(TankOidcAuthorization.class);

    private Gson _gson = new Gson();

    @Inject
    private WebHttpClient _webHttpClient;

    @Inject
    private TankConfig _tankConfig;

    public Token GetAccessToken(String authorizationCode) throws IOException {
        if (authorizationCode == null || authorizationCode.isEmpty() || authorizationCode.isBlank()) {
            LOG.warn("GetAccessToken: Mission Authorization Code");
            throw new IllegalArgumentException("Missing Authorization Code");
        }

        OidcSsoConfig oidcSsoConfig = _tankConfig.getOidcSsoConfig();

        if (Objects.requireNonNull(oidcSsoConfig).getConfiguration() == null) {
            LOG.warn("GetAccessToken: OIDC Config Null");
            throw new IllegalArgumentException("OIDC Config Not Properly Configured");
        }

        URIBuilder builder = new URIBuilder()
                .setScheme(OidcConstants.HTTPS_SCHEME)
                .setHost(oidcSsoConfig.getIssuer())
                .setPath(oidcSsoConfig.getTokenEndpoint());

        var authenticationUrl = builder.toString();

        Map<Object, Object> oidcRequestParameters = getOidcRequestParameters(oidcSsoConfig, authorizationCode);

        LOG.info("Request Access Token from Authorization server");
        HttpResponse<String> httpPostResponse = _webHttpClient.Post(authenticationUrl, oidcRequestParameters);

        return _gson.fromJson(httpPostResponse.body(), Token.class);
    }

    public UserInfo DecodeIdToken(Token token) {
        if (token == null || token.getIdToken() == null) {
            LOG.warn("DecodeIdToken: Missing Token Information");
            throw new IllegalArgumentException("DecodeIdToken: Missing Token Information");
        }

        var truncatedEncodedIdTokenString = token.getIdToken().substring(token.getIdToken().indexOf('.')+1, token.getIdToken().lastIndexOf('.'));

        byte[] decodedIdTokenBuffer = Base64.getDecoder().decode(truncatedEncodedIdTokenString);
        String decodedUserInfoString = new String(decodedIdTokenBuffer);

        UserInfo userInfo = _gson.fromJson(decodedUserInfoString, UserInfo.class);
        LOG.info("Decoding ID Token to UserInfo for " + userInfo.getUsername());
        return userInfo;
    }

    private Map<Object, Object> getOidcRequestParameters(OidcSsoConfig oidcSsoConfig, String authorizationCode) {
        if(Objects.requireNonNull(oidcSsoConfig).getConfiguration() == null) {
            throw new IllegalArgumentException("Missing OIDC SSO Config");
        }

        Map<Object, Object> requestParameters = new HashMap<>();

        requestParameters.put(OidcConstants.CLIENT_ID_KEY, oidcSsoConfig.getClientId());
        requestParameters.put(OidcConstants.CLIENT_SECRET_KEY, oidcSsoConfig.getClientSecret());
        requestParameters.put(OidcConstants.CONTENT_TYPE_KEY, OidcConstants.CONTENT_TYPE_VALUE);
        requestParameters.put(OidcConstants.GRANT_TYPE_KEY, OidcConstants.GRANT_TYPE_VALUE);
        requestParameters.put(OidcConstants.GRANT_CODE_KEY, authorizationCode);
        requestParameters.put(OidcConstants.REDIRECT_URL_KEY, oidcSsoConfig.getRedirectUrl());

        return requestParameters;
    }
}
