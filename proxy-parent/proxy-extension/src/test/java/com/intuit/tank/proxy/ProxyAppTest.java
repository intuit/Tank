package com.intuit.tank.proxy;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class ProxyAppTest {

    @Test
    void testProxyApp() {
        ProxyApp proxyApp = new ProxyApp();
        assertNotNull(proxyApp);
    }

}
