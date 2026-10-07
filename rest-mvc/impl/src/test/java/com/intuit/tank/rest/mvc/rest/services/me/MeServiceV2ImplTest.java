/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.me;

import com.intuit.tank.dao.PreferencesDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Preferences;
import com.intuit.tank.project.User;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceUnauthorizedException;
import com.intuit.tank.rest.mvc.rest.models.AccountUpdate;
import com.intuit.tank.rest.mvc.rest.models.ColumnPreference;
import com.intuit.tank.rest.mvc.rest.models.ColumnPreferenceUpdate;
import com.intuit.tank.rest.mvc.rest.models.CurrentUser;
import com.intuit.tank.rest.mvc.rest.models.TablePreferences;
import com.intuit.tank.rest.mvc.rest.security.TankPrincipal;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridge;
import com.intuit.tank.rest.mvc.rest.security.WebSessionBridgeProvider;
import com.intuit.tank.rest.mvc.rest.util.TableColumnDefaults;
import com.intuit.tank.vm.common.PasswordEncoder;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.settings.SecurityConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.intuit.tank.rest.mvc.rest.security.SecurityTestSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeServiceV2ImplTest {

    @InjectMocks
    private MeServiceV2Impl service;

    @Mock
    private WebSessionBridgeProvider bridgeProvider;

    @Mock
    private WebSessionBridge bridge;

    private AutoCloseable mocks;
    private MockedConstruction<TankConfig> tankConfigs;
    private MockedConstruction<UserDao> userDaos;
    private User bob;
    /** what the last UserDao saved */
    private final AtomicReference<User> saved = new AtomicReference<>();

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        when(bridgeProvider.get()).thenReturn(bridge);
        useConfig(true, Map.of(AccessRight.CREATE_PROJECT, List.of("projectors")));

        SecurityConfig securityConfig = mock(SecurityConfig.class);
        when(securityConfig.getRestrictionMap()).thenReturn(Map.of(AccessRight.CREATE_PROJECT.name(), List.of("projectors")));
        tankConfigs = Mockito.mockConstruction(TankConfig.class,
                (mock, context) -> when(mock.getSecurityConfig()).thenReturn(securityConfig));

        bob = User.builder().name("bob").email("bob@example.com")
                .password(PasswordEncoder.encodePassword("old-password")).lastLoginTs(Instant.ofEpochSecond(1000)).build();
        userDaos = Mockito.mockConstruction(UserDao.class, (mock, context) -> {
            when(mock.findByUserName("bob")).thenReturn(bob);
            when(mock.saveOrUpdate(any())).thenAnswer(i -> {
                saved.set(i.getArgument(0));
                return i.getArgument(0);
            });
        });
        actAs(user("bob", "projectors"));
    }

    @AfterEach
    void tearDown() throws Exception {
        userDaos.close();
        tankConfigs.close();
        mocks.close();
        reset();
    }

    @Test
    void getCurrentUser_describesRightsAndAccount() {
        CurrentUser me = service.getCurrentUser();
        assertEquals("bob", me.name());
        assertEquals("bob@example.com", me.email());
        assertEquals(List.of("projectors"), me.groups());
        assertFalse(me.admin());
        assertTrue(me.rights().get(AccessRight.CREATE_PROJECT.name()));
        assertFalse(me.rights().get(AccessRight.DELETE_PROJECT.name()));
        assertEquals(AccessRight.values().length, me.rights().size());
        assertFalse(me.hasApiToken());
        assertEquals(1000_000L, me.lastLoginTs().getTime());
    }

    @Test
    void getCurrentUser_adminHoldsEveryRight() {
        actAs(user("bob", "admin"));
        CurrentUser me = service.getCurrentUser();
        assertTrue(me.admin());
        assertTrue(me.rights().values().stream().allMatch(Boolean::booleanValue));
    }

    @Test
    void getCurrentUser_requiresUser() {
        reset();
        useConfig(false, Map.of());
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.getCurrentUser());
        actAs(TankPrincipal.agent());
        assertThrows(GenericServiceForbiddenAccessException.class, () -> service.getCurrentUser());
    }

    @Test
    void getCurrentUser_deletedUserIsUnauthorized() {
        actAs(user("ghost"));
        assertThrows(GenericServiceUnauthorizedException.class, () -> service.getCurrentUser());
    }

    @Test
    void updateAccount_changesEmail() {
        CurrentUser me = service.updateAccount(new AccountUpdate("  new@example.com ", null, null));
        assertEquals("new@example.com", me.email());
        assertSame(bob, saved.get());
    }

    @Test
    void updateAccount_rejectsInvalidEmail() {
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateAccount(new AccountUpdate(" ", null, null)));
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateAccount(new AccountUpdate("nope", null, null)));
        assertThrows(GenericServiceBadRequestException.class, () -> service.updateAccount(null));
        assertNull(saved.get());
    }

    @Test
    void updateAccount_changesPasswordWithCurrentPassword() {
        service.updateAccount(new AccountUpdate(null, "old-password", "new-password"));
        assertTrue(PasswordEncoder.validatePassword("new-password", bob.getPassword()));
    }

    @Test
    void updateAccount_passwordChangeNeedsCorrectCurrentPassword() {
        String before = bob.getPassword();
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.updateAccount(new AccountUpdate(null, "wrong-password", "new-password")));
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.updateAccount(new AccountUpdate(null, null, "new-password")));
        assertEquals(before, bob.getPassword());
        assertNull(saved.get());
    }

    @Test
    void updateAccount_rejectsShortPassword() {
        assertThrows(GenericServiceBadRequestException.class,
                () -> service.updateAccount(new AccountUpdate(null, "old-password", "short")));
        assertNull(saved.get());
    }

    @Test
    void createApiToken_replacesTokenAndReturnsIt() {
        bob.generateApiToken();
        String old = bob.getApiToken();
        String token = service.createApiToken().apiToken();
        assertNotNull(token);
        assertNotEquals(old, token);
        assertEquals(token, saved.get().getApiToken());
        assertTrue(saved.get().isTokenDisplayed());
    }

    @Test
    void deleteApiToken_removesToken() {
        bob.generateApiToken();
        service.deleteApiToken();
        assertNull(saved.get().getApiToken());
    }

    @Test
    void deleteApiToken_noTokenIsNoOp() {
        service.deleteApiToken();
        assertNull(saved.get());
    }

    @Test
    void getPreferences_createsDefaultsForNewUser() {
        try (MockedConstruction<PreferencesDao> daos = Mockito.mockConstruction(PreferencesDao.class,
                (mock, context) -> when(mock.saveOrUpdate(any())).thenAnswer(i -> i.getArgument(0)))) {
            TablePreferences prefs = service.getPreferences();
            assertEquals(List.of("projects", "scripts", "scriptSteps", "datafiles", "jobs"), List.copyOf(prefs.tables().keySet()));
            assertEquals(TableColumnDefaults.JOBS_COL_PREFS.size(), prefs.tables().get("jobs").size());
            verify(daos.constructed().get(0)).saveOrUpdate(any());
        }
    }

    @Test
    void updateTablePreferences_appliesChanges() {
        Preferences stored = TableColumnDefaults.ensureDefaults(null, "bob").preferences();
        try (MockedConstruction<PreferencesDao> daos = Mockito.mockConstruction(PreferencesDao.class, (mock, context) -> {
            when(mock.getForOwner("bob")).thenReturn(stored);
            when(mock.saveOrUpdate(any())).thenAnswer(i -> i.getArgument(0));
        })) {
            TablePreferences prefs = service.updateTablePreferences("projects",
                    List.of(new ColumnPreferenceUpdate("nameColumn", 300, false),
                            new ColumnPreferenceUpdate("idColumn", null, true)));
            ColumnPreference name = column(prefs, "projects", "nameColumn");
            assertEquals(300, name.size());
            assertFalse(name.visible());
            assertTrue(column(prefs, "projects", "idColumn").visible());
            assertEquals(75, column(prefs, "projects", "idColumn").size());
            verify(bridge).preferencesChanged(stored);
        }
    }

    @Test
    void updateTablePreferences_validatesBeforeChangingAnything() {
        Preferences stored = TableColumnDefaults.ensureDefaults(null, "bob").preferences();
        try (MockedConstruction<PreferencesDao> daos = Mockito.mockConstruction(PreferencesDao.class,
                (mock, context) -> when(mock.getForOwner("bob")).thenReturn(stored))) {
            assertThrows(GenericServiceResourceNotFoundException.class,
                    () -> service.updateTablePreferences("nope", List.of()));
            assertThrows(GenericServiceBadRequestException.class, () -> service.updateTablePreferences("projects",
                    List.of(new ColumnPreferenceUpdate("nameColumn", 300, null), new ColumnPreferenceUpdate("bogus", 1, null))));
            assertThrows(GenericServiceBadRequestException.class, () -> service.updateTablePreferences("projects",
                    List.of(new ColumnPreferenceUpdate("actionsColumn", null, false))));
            assertThrows(GenericServiceBadRequestException.class, () -> service.updateTablePreferences("projects",
                    List.of(new ColumnPreferenceUpdate("nameColumn", 0, null))));
            assertThrows(GenericServiceBadRequestException.class, () -> service.updateTablePreferences("projects", null));
            assertEquals(250, stored.getProjectTableColumns().get(2).getSize());
            daos.constructed().forEach(dao -> verify(dao, never()).saveOrUpdate(any()));
            verifyNoInteractions(bridge);
        }
    }

    @Test
    void resetPreferences_deletesAndNotifiesWebUi() {
        Preferences stored = new Preferences();
        try (MockedConstruction<PreferencesDao> daos = Mockito.mockConstruction(PreferencesDao.class,
                (mock, context) -> when(mock.getForOwner("bob")).thenReturn(stored))) {
            service.resetPreferences();
            verify(daos.constructed().get(0)).delete(stored);
            verify(bridge).preferencesChanged(stored);
        }
    }

    @Test
    void resetPreferences_nothingSaved() {
        try (MockedConstruction<PreferencesDao> daos = Mockito.mockConstruction(PreferencesDao.class)) {
            service.resetPreferences();
            verify(daos.constructed().get(0), never()).delete(any(Preferences.class));
            verifyNoInteractions(bridge);
        }
    }

    private static ColumnPreference column(TablePreferences prefs, String table, String colName) {
        return prefs.tables().get(table).stream().filter(c -> c.colName().equals(colName)).findFirst().orElseThrow();
    }
}
