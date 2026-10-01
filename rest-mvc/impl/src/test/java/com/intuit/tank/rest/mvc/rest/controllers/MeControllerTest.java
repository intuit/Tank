/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.controllers;

import com.intuit.tank.rest.mvc.rest.models.AccountUpdate;
import com.intuit.tank.rest.mvc.rest.models.ApiTokenResponse;
import com.intuit.tank.rest.mvc.rest.models.ColumnPreferenceUpdate;
import com.intuit.tank.rest.mvc.rest.models.CurrentUser;
import com.intuit.tank.rest.mvc.rest.models.TablePreferences;
import com.intuit.tank.rest.mvc.rest.models.UiOptions;
import com.intuit.tank.rest.mvc.rest.services.config.ConfigServiceV2;
import com.intuit.tank.rest.mvc.rest.services.me.MeServiceV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeControllerTest {

    @InjectMocks
    private MeController meController;

    @InjectMocks
    private ConfigController configController;

    @InjectMocks
    private UserController userController;

    @Mock
    private MeServiceV2 meService;

    @Mock
    private ConfigServiceV2 configService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void currentUserAndAccount() {
        CurrentUser me = new CurrentUser("bob", "e", List.of(), false, Map.of(), false, null);
        AccountUpdate update = new AccountUpdate("e", null, null);
        when(meService.getCurrentUser()).thenReturn(me);
        when(meService.updateAccount(update)).thenReturn(me);
        assertSame(me, meController.getCurrentUser().getBody());
        assertSame(me, meController.updateAccount(update).getBody());
    }

    @Test
    void apiToken() {
        when(meService.createApiToken()).thenReturn(new ApiTokenResponse("t"));
        assertEquals("t", meController.createApiToken().getBody().apiToken());
        assertEquals(HttpStatus.NO_CONTENT, meController.deleteApiToken().getStatusCode());
        verify(meService).deleteApiToken();
    }

    @Test
    void preferences() {
        TablePreferences prefs = new TablePreferences(Map.of());
        List<ColumnPreferenceUpdate> columns = List.of(new ColumnPreferenceUpdate("nameColumn", 1, true));
        when(meService.getPreferences()).thenReturn(prefs);
        when(meService.updateTablePreferences("jobs", columns)).thenReturn(prefs);
        assertSame(prefs, meController.getPreferences().getBody());
        assertSame(prefs, meController.updateTablePreferences("jobs", columns).getBody());
        assertEquals(HttpStatus.NO_CONTENT, meController.resetPreferences().getStatusCode());
        verify(meService).resetPreferences();
    }

    @Test
    void optionsAndUserNames() {
        UiOptions options = mock(UiOptions.class);
        when(configService.getOptions()).thenReturn(options);
        when(configService.getUserNames()).thenReturn(List.of("bob"));
        assertSame(options, configController.getOptions().getBody());
        assertEquals(List.of("bob"), userController.getUserNames().getBody());
    }
}
