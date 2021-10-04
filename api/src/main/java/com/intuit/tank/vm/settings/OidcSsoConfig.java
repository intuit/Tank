package com.intuit.tank.vm.settings;

import org.apache.commons.configuration.HierarchicalConfiguration;

/**
 * OidcSsoConfig
 *
 * @author Shawn Park
 */
public class OidcSsoConfig {
    private static final String KEY_AUTHENTICATION_URL = "authentication-url";
    private static final String KEY_CLIENT_SECRET = "client-secret";
    private static final String KEY_CLIENT_ID = "client-id";
    private static final String KEY_REDIRECT_URL = "redirect-url";

    private final HierarchicalConfiguration _config;

    public OidcSsoConfig(HierarchicalConfiguration config) {
        this._config = config;
    }

    /**
     * @return Authentication URL
     */
    public String getAuthenticationUrl() { return _config.getString(KEY_AUTHENTICATION_URL); }

    /**
     * @return Client Secret
     */
    public String getClientSecret() { return _config.getString(KEY_CLIENT_SECRET); }

    /**
     * @return Client ID
     */
    public String getClientId() { return _config.getString(KEY_CLIENT_ID); }

    /**
     * @return Redirect URL
     */
    public String getRedirectUrl() { return _config.getString(KEY_REDIRECT_URL); }
}
