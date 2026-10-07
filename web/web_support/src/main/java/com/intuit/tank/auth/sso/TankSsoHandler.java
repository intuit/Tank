package com.intuit.tank.auth.sso;

import com.intuit.tank.admin.UserCreate;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.models.Token;
import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.springframework.web.util.UriComponentsBuilder;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
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

    static final String STATE_SESSION_ATTRIBUTE = TankSsoHandler.class.getName() + ".state";
    static final String NONCE_SESSION_ATTRIBUTE = TankSsoHandler.class.getName() + ".nonce";
    static final String RETURN_PATH_SESSION_ATTRIBUTE = TankSsoHandler.class.getName() + ".returnPath";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Builds the IdP authorization URL. A fresh random {@code state} (CSRF protection for the callback)
     * and {@code nonce} (binds the ID token to this login) are stored in the session, and any return path
     * left by an earlier, abandoned login is cleared.
     */
    public String GetOnLoadAuthorizationRequest(HttpSession session) {
        OidcSsoConfig oidcSsoConfig = _tankConfig.getOidcSsoConfig();

        if (Objects.requireNonNull(oidcSsoConfig).getConfiguration() == null ) {
            throw new IllegalArgumentException("Missing OIDC SSO Config");
        }

        String state = randomValue();
        String nonce = randomValue();
        session.setAttribute(STATE_SESSION_ATTRIBUTE, state);
        session.setAttribute(NONCE_SESSION_ATTRIBUTE, nonce);
        // a return path belongs to one login; a new login, from either UI, starts without one
        session.removeAttribute(RETURN_PATH_SESSION_ATTRIBUTE);

        URI uri = UriComponentsBuilder
                .fromHttpUrl(oidcSsoConfig.getAuthorizationUrl())
                .queryParam(OidcConstants.CLIENT_ID_KEY, oidcSsoConfig.getClientId())
                .queryParam(OidcConstants.RESPONSE_TYPE_KEY, OidcConstants.RESPONSE_TYPE_VALUE)
                .queryParam(OidcConstants.REDIRECT_URL_KEY, oidcSsoConfig.getRedirectUrl())
                .queryParam(OidcConstants.SCOPE_KEY, OidcConstants.SCOPE_VALUE)
                .queryParam(OidcConstants.STATE_KEY, state)
                .queryParam(OidcConstants.NONCE_KEY, nonce)
                .build()
                .toUri();

        return oidcSsoConfig.getAuthorizationUrl() + "?" + uri.getQuery();
    }

    /**
     * Completes the login from the IdP callback. The {@code state} must match the value issued for this
     * session, and the ID token must carry the issued nonce, be addressed to this client and be unexpired.
     * The state and nonce are single use.
     */
    public void HandleSsoAuthorization(String authorizationCode, String state, HttpSession session) throws IOException {
        if (authorizationCode == null || authorizationCode.isEmpty() || authorizationCode.isBlank()) {
            throw new IllegalArgumentException("Missing Authorization Code");
        }
        if (session == null) {
            throw new IllegalArgumentException("No login in progress for this session");
        }
        String expectedState = (String) session.getAttribute(STATE_SESSION_ATTRIBUTE);
        String expectedNonce = (String) session.getAttribute(NONCE_SESSION_ATTRIBUTE);
        session.removeAttribute(STATE_SESSION_ATTRIBUTE);
        session.removeAttribute(NONCE_SESSION_ATTRIBUTE);
        if (!constantTimeEquals(expectedState, state)) {
            throw new IllegalArgumentException("Invalid SSO state parameter");
        }

        Token token = _tankOidcAuthorization.GetAccessToken(authorizationCode);
        UserInfo userInfo = _tankOidcAuthorization.DecodeIdToken(token);

        if (userInfo == null || userInfo.getEmail() == null) {
            throw new IllegalArgumentException("Missing User Information");
        }
        validateClaims(userInfo, expectedNonce);

        User user = _userDao.findByEmail(userInfo.getEmail());

        if (user == null) {
            user = _userCreate.CreateUser(userInfo);
        }

        _tankSecurityContext.ssoSecurityContext(user);
    }

    /**
     * Remembers where to send the browser once the SSO login started for this session completes.
     *
     * @param returnPath an already validated path within this application, or null to use the configured
     *                   redirect URL
     */
    public void setReturnPath(HttpSession session, String returnPath) {
        if (returnPath == null) {
            session.removeAttribute(RETURN_PATH_SESSION_ATTRIBUTE);
        } else {
            session.setAttribute(RETURN_PATH_SESSION_ATTRIBUTE, returnPath);
        }
    }

    /**
     * @return the return path set for this session's SSO login, or null; it is single use
     */
    public String consumeReturnPath(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object returnPath = session.getAttribute(RETURN_PATH_SESSION_ATTRIBUTE);
        session.removeAttribute(RETURN_PATH_SESSION_ATTRIBUTE);
        return returnPath instanceof String ? (String) returnPath : null;
    }

    /*
     * The ID token comes straight from the token endpoint over TLS using the client secret, so per
     * OIDC Core 3.1.3.7 TLS server validation stands in for signature validation; the claims that bind
     * the token to this client and this login are still checked here.
     */
    private void validateClaims(UserInfo userInfo, String expectedNonce) {
        if (!constantTimeEquals(expectedNonce, userInfo.getNonce())) {
            throw new IllegalArgumentException("ID token nonce does not match");
        }
        String clientId = _tankConfig.getOidcSsoConfig().getClientId();
        if (clientId != null && !clientId.equals(userInfo.getAudience())) {
            throw new IllegalArgumentException("ID token audience does not match the client id");
        }
        if (userInfo.getExpirationTimeUtc() <= Instant.now().getEpochSecond() - CLOCK_SKEW_SECONDS) {
            throw new IllegalArgumentException("ID token has expired");
        }
    }

    private static final long CLOCK_SKEW_SECONDS = 60;

    private static String randomValue() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static boolean constantTimeEquals(String expected, String actual) {
        return expected != null && actual != null
                && MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), actual.getBytes(StandardCharsets.UTF_8));
    }
}
