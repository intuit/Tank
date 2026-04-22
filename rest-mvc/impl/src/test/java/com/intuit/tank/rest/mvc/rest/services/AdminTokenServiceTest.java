package com.intuit.tank.rest.mvc.rest.services;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminTokenServiceTest {

    @Test
    void isValidAdminToken_returnsFalseForNullToken() {
        try (MockedConstruction<TankConfig> tcMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> daoMock = Mockito.mockConstruction(UserDao.class)) {

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertFalse(service.isValidAdminToken(null));
        }
    }

    @Test
    void isValidAdminToken_returnsFalseForEmptyToken() {
        try (MockedConstruction<TankConfig> tcMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> daoMock = Mockito.mockConstruction(UserDao.class)) {

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertFalse(service.isValidAdminToken(""));
            assertFalse(service.isValidAdminToken("   "));
        }
    }

    @Test
    void isValidAdminToken_returnsFalseWhenNoUserFoundForToken() {
        try (MockedConstruction<TankConfig> tcMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> daoMock = Mockito.mockConstruction(UserDao.class,
                (mock, ctx) -> when(mock.findByApiToken("invalid-token")).thenReturn(null))) {

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertFalse(service.isValidAdminToken("invalid-token"));
        }
    }

    @Test
    void isValidAdminToken_returnsFalseWhenUserFoundButSSMFails() {
        User user = new User();
        user.setName("admin");

        try (MockedConstruction<TankConfig> tcMock = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> daoMock = Mockito.mockConstruction(UserDao.class,
                (mock, ctx) -> when(mock.findByApiToken("some-token")).thenReturn(user))) {

            AdminTokenService service = new AdminTokenService();
            service.init();

            // SSM client will fail in test environment (no AWS credentials)
            // This exercises the user-found but SSM-fails path
            assertFalse(service.isValidAdminToken("some-token"));
        }
    }
}
