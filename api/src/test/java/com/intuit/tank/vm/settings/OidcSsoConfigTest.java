package com.intuit.tank.vm.settings;

import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OidcSsoConfigTest {

    HierarchicalConfiguration config = new HierarchicalConfiguration();
    OidcSsoConfig oidc = null;

    @BeforeEach
    public void init() {
        oidc = new OidcSsoConfig(config);
    }

    @Test
    public void testBuild() {
        assertNotNull(oidc);
    }

    @Test
    public void testOIDC() {
        assertNull(oidc.getAuthorizationEndpoint());
        assertNull(oidc.getAuthorizationUrl());
        assertNull(oidc.getClientId());
        assertNull(oidc.getClientSecret());
        assertEquals(config, oidc.getConfiguration());
        assertNull(oidc.getIssuer());
        assertNull(oidc.getRedirectUrl());
        assertNull(oidc.getTokenEndpoint());
    }
}
