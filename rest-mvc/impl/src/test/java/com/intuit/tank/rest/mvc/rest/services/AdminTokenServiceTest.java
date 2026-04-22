package com.intuit.tank.rest.mvc.rest.services;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.SsmClientBuilder;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminTokenServiceTest {

    @Test
    void isValidAdminToken_returnsFalseForNullToken() {
        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> ignoredDao = Mockito.mockConstruction(UserDao.class)) {

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertFalse(service.isValidAdminToken(null));
        }
    }

    @Test
    void isValidAdminToken_returnsFalseForEmptyToken() {
        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> ignoredDao = Mockito.mockConstruction(UserDao.class)) {

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertFalse(service.isValidAdminToken(""));
            assertFalse(service.isValidAdminToken("   "));
        }
    }

    @Test
    void isValidAdminToken_returnsFalseWhenNoUserFoundForToken() {
        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> ignoredDao = Mockito.mockConstruction(UserDao.class,
                (mock, ctx) -> when(mock.findByApiToken("invalid-token")).thenReturn(null))) {

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertFalse(service.isValidAdminToken("invalid-token"));
        }
    }

    @Test
    void isValidAdminToken_returnsFalseWhenSSMFails() {
        User user = new User();
        user.setName("admin");

        SsmClient mockSsmClient = mock(SsmClient.class);
        when(mockSsmClient.getParameter(any(GetParameterRequest.class)))
                .thenThrow(new RuntimeException("SSM connection refused"));

        SsmClientBuilder mockBuilder = mock(SsmClientBuilder.class);
        when(mockBuilder.build()).thenReturn(mockSsmClient);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> ignoredDao = Mockito.mockConstruction(UserDao.class,
                (mock, ctx) -> when(mock.findByApiToken("some-token")).thenReturn(user));
             MockedStatic<SsmClient> ssmStatic = Mockito.mockStatic(SsmClient.class)) {

            ssmStatic.when(SsmClient::builder).thenReturn(mockBuilder);

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertFalse(service.isValidAdminToken("some-token"));
        }
    }

    @Test
    void isValidAdminToken_returnsFalseWhenTokensDoNotMatch() {
        User user = new User();
        user.setName("admin");

        Parameter param = Parameter.builder().value("different-token").build();
        GetParameterResponse response = GetParameterResponse.builder().parameter(param).build();

        SsmClient mockSsmClient = mock(SsmClient.class);
        when(mockSsmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        SsmClientBuilder mockBuilder = mock(SsmClientBuilder.class);
        when(mockBuilder.build()).thenReturn(mockSsmClient);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> ignoredDao = Mockito.mockConstruction(UserDao.class,
                (mock, ctx) -> when(mock.findByApiToken("my-token")).thenReturn(user));
             MockedStatic<SsmClient> ssmStatic = Mockito.mockStatic(SsmClient.class)) {

            ssmStatic.when(SsmClient::builder).thenReturn(mockBuilder);

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertFalse(service.isValidAdminToken("my-token"));
        }
    }

    @Test
    void isValidAdminToken_returnsTrueWhenTokensMatch() {
        User user = new User();
        user.setName("admin");

        Parameter param = Parameter.builder().value("matching-token").build();
        GetParameterResponse response = GetParameterResponse.builder().parameter(param).build();

        SsmClient mockSsmClient = mock(SsmClient.class);
        when(mockSsmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        SsmClientBuilder mockBuilder = mock(SsmClientBuilder.class);
        when(mockBuilder.build()).thenReturn(mockSsmClient);

        try (MockedConstruction<TankConfig> ignored = Mockito.mockConstruction(TankConfig.class,
                (mock, ctx) -> when(mock.getInstanceName()).thenReturn("test"));
             MockedConstruction<UserDao> ignoredDao = Mockito.mockConstruction(UserDao.class,
                (mock, ctx) -> {
                    when(mock.findByApiToken("matching-token")).thenReturn(user);
                    when(mock.saveOrUpdate(any(User.class))).thenReturn(user);
                });
             MockedStatic<SsmClient> ssmStatic = Mockito.mockStatic(SsmClient.class)) {

            ssmStatic.when(SsmClient::builder).thenReturn(mockBuilder);

            AdminTokenService service = new AdminTokenService();
            service.init();

            assertTrue(service.isValidAdminToken("matching-token"));
        }
    }
}
