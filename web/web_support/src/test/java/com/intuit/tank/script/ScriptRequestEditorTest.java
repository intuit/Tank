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

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptRequestEditor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@EnableAutoWeld
@ActivateScopes(ConversationScoped.class)
public class ScriptRequestEditorTest {

    @InjectMocks
    private ScriptRequestEditor fixture;
    @Mock
    private ScriptEditor scriptEditor;
    @Mock
    private RequestHeaderEditor requestHeaderEditor;
    @Mock
    private ResponseHeaderEditor responseHeaderEditor;
    @Mock
    private RequestCookiesEditor requestCookiesEditor;
    @Mock
    private ResponseCookiesEditor responseCookiesEditor;
    @Mock
    private ResponseContentEditor responseContentEditor;
    @Mock
    private PostDataEditor postDataEditor;
    @Mock
    private QueryStringEditor queryStringEditor;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    public void testScriptRequestEditor() {
        ScriptRequestEditor result = new ScriptRequestEditor();
        assertNotNull(result);
    }

    @Test
    public void testInsertRequest() {
        fixture.insertRequest();
        assertFalse(fixture.editMode);
    }

    @Test
    public void testEditRequest() {
        fixture.editRequest(ScriptStep.builder().name("ScriptStep").onFail("goto-TEST").build());
        assertEquals(ScriptConstants.REQUEST, fixture.getStep().getType());
        assertTrue(fixture.editMode);
    }

    @Test
    public void testGetGotoGroup() {
        fixture.setStep(ScriptStep.builder().name("ScriptStep").onFail("goto").build());
        assertEquals("", fixture.getGotoGroup());
        fixture.setStep(ScriptStep.builder().name("ScriptStep").onFail("goto-TEST").build());
        assertEquals("TEST", fixture.getGotoGroup());
    }

    @Test
    public void testAddToScript() {
        fixture.setStep(ScriptStep.builder().name("ScriptStep").build());
        fixture.addToScript(); // insert()
        fixture.editMode = true;
        fixture.addToScript(); // done()
    }

    @Test
    public void testGetRequestFormats() {
        RepresentationEntity[] result = fixture.getRequestFormats();
        assertNotNull(result);
        assertEquals(5, result.length);
    }

    @Test
    public void testResponseFormats() {
        RepresentationEntity[] result = fixture.getResponseFormats();
        assertNotNull(result);
        assertEquals(3, result.length);
    }
}