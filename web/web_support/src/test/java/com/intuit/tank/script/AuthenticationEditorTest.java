package com.intuit.tank.script;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.util.Messages;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.intuit.tank.util.ButtonLabel.ADD_LABEL;
import static com.intuit.tank.util.ButtonLabel.EDIT_LABEL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationEditorTest {

    @InjectMocks
    private AuthenticationEditor authEditor;

    @Mock
    private ScriptEditor scriptEditor;

    @Mock
    private Messages messages;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        // Manually call init since @PostConstruct isn't called in Mockito
        authEditor.init();
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testConstructor() {
        assertNotNull(authEditor);
    }

    @Test
    public void testGetAuthSchemes_NotEmpty() {
        List<String> schemes = authEditor.getAuthSchemes();
        assertNotNull(schemes);
        assertFalse(schemes.isEmpty());
        assertTrue(schemes.contains(AuthScheme.Basic.name()));
    }

    @Test
    public void testGetSetButtonLabel() {
        authEditor.setButtonLabel("Save");
        assertEquals("Save", authEditor.getButtonLabel());
    }

    @Test
    public void testGetSetUserName() {
        authEditor.setUserName("testuser");
        assertEquals("testuser", authEditor.getUserName());
    }

    @Test
    public void testGetSetPassword() {
        authEditor.setPassword("secret");
        assertEquals("secret", authEditor.getPassword());
    }

    @Test
    public void testGetSetRealm() {
        authEditor.setRealm("myRealm");
        assertEquals("myRealm", authEditor.getRealm());
    }

    @Test
    public void testGetSetScheme() {
        authEditor.setScheme(AuthScheme.NTLM.name());
        assertEquals(AuthScheme.NTLM.name(), authEditor.getScheme());
    }

    @Test
    public void testGetSetHost() {
        authEditor.setHost("example.com");
        assertEquals("example.com", authEditor.getHost());
    }

    @Test
    public void testGetSetPort() {
        authEditor.setPort("8080");
        assertEquals("8080", authEditor.getPort());
    }

    @Test
    public void testInsertAuthentication_ResetsToAddMode() {
        authEditor.setButtonLabel(EDIT_LABEL);
        authEditor.setUserName("someuser");

        authEditor.insertAuthentication();

        assertEquals(ADD_LABEL, authEditor.getButtonLabel());
        assertNull(authEditor.getUserName());
    }

    @Test
    public void testEditAuthentication_SetsEditMode() {
        ScriptStep step = new ScriptStep();
        Set<RequestData> data = new HashSet<>();

        RequestData userRd = createRequestData(ScriptConstants.AUTH_USER_NAME, "alice");
        RequestData passRd = createRequestData(ScriptConstants.AUTH_PASSWORD, "pass123");
        RequestData realmRd = createRequestData(ScriptConstants.AUTH_REALM, "myRealm");
        RequestData schemeRd = createRequestData(ScriptConstants.AUTH_SCHEME, AuthScheme.Basic.name());
        RequestData hostRd = createRequestData(ScriptConstants.AUTH_HOST, "api.example.com");
        RequestData portRd = createRequestData(ScriptConstants.AUTH_PORT, "443");

        data.add(userRd);
        data.add(passRd);
        data.add(realmRd);
        data.add(schemeRd);
        data.add(hostRd);
        data.add(portRd);
        step.setData(data);

        authEditor.editAuthentication(step);

        assertEquals(EDIT_LABEL, authEditor.getButtonLabel());
        assertEquals("alice", authEditor.getUserName());
        assertEquals("pass123", authEditor.getPassword());
        assertEquals("myRealm", authEditor.getRealm());
        assertEquals("api.example.com", authEditor.getHost());
        assertEquals("443", authEditor.getPort());
    }

    @Test
    public void testAddToScript_WhenMissingCredentials_ShowsErrors() {
        authEditor.insertAuthentication();
        // userName and password are null after insertAuthentication

        authEditor.addToScript();

        verify(messages, atLeast(2)).error(anyString());
    }

    @Test
    public void testAddToScript_InInsertMode_WithValidData_CallsInsert() {
        authEditor.insertAuthentication();
        authEditor.setUserName("user");
        authEditor.setPassword("pass");
        authEditor.setScheme(AuthScheme.Basic.name());

        authEditor.addToScript();

        verify(scriptEditor).insert(any(ScriptStep.class));
    }

    @Test
    public void testDone_WhenInEditMode_UpdatesStep() {
        ScriptStep step = new ScriptStep();
        Set<RequestData> data = new HashSet<>();
        step.setData(data);

        authEditor.editAuthentication(step);
        authEditor.setScheme(AuthScheme.Basic.name());
        authEditor.setHost("localhost");

        authEditor.done();

        assertFalse(step.getData().isEmpty());
    }

    private RequestData createRequestData(String key, String value) {
        RequestData rd = new RequestData();
        rd.setKey(key);
        rd.setValue(value);
        return rd;
    }
}
