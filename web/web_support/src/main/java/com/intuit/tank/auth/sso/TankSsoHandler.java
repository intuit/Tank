package com.intuit.tank.auth.sso;

import com.intuit.tank.admin.UserCreate;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.models.Token;
import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

/**
 * TankSsoHandler
 *
 * @author Shawn Park
 */
@Named("ssoHandler")
@RequestScoped
public class TankSsoHandler {
    @Inject
    private TankOidcAuthorization _tankOidcAuthorization;

    @Inject
    private TankSecurityContext _tankSecurityContext;

    @Inject
    private UserDao _userDao;

    @Inject
    private UserCreate _userCreate;

    @Inject
    private TankConfig _tankConfig;

    public String GetOnLoadAuthorizationRequest() {
        OidcSsoConfig oidcSsoConfig = _tankConfig.getOidcSsoConfig();

        if (Objects.requireNonNull(oidcSsoConfig).getConfiguration() == null ) {
            throw new IllegalArgumentException("Missing OIDC SSO Config");
        }

        URI uri = UriBuilder
                .fromUri(oidcSsoConfig.getAuthorizationUrl())
                .queryParam(OidcConstants.CLIENT_ID_KEY, oidcSsoConfig.getClientId())
                .queryParam(OidcConstants.RESPONSE_TYPE_KEY, OidcConstants.RESPONSE_TYPE_VALUE)
                .queryParam(OidcConstants.REDIRECT_URL_KEY, oidcSsoConfig.getRedirectUrl())
                .queryParam(OidcConstants.SCOPE_KEY, OidcConstants.SCOPE_VALUE)
                .queryParam(OidcConstants.STATE_KEY, OidcConstants.STATE_VALUE).build();

        return oidcSsoConfig.getAuthorizationUrl() + "?" + uri.getQuery();
    }

    public void HandleSsoAuthorization(String authorizationCode) throws IOException {
        if (authorizationCode == null || authorizationCode.isEmpty() || authorizationCode.isBlank()) {
            throw new IllegalArgumentException("Missing Authorization Code");
        }

        Token token = _tankOidcAuthorization.GetAccessToken(authorizationCode);
        UserInfo userInfo = _tankOidcAuthorization.DecodeIdToken(token);

        if (userInfo == null || userInfo.getEmail() == null) {
            throw new IllegalArgumentException("Missing User Information");
        }

        User user = _userDao.findByEmail(userInfo.getEmail());

        if (user == null) {
            user = _userCreate.CreateUser(userInfo);
        }

        _tankSecurityContext.ssoSecurityContext(user);
    }
}
