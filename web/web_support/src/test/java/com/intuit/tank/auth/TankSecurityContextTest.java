package com.intuit.tank.auth;

import com.intuit.tank.project.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.CallerPrincipal;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TankSecurityContextTest {
    @Mock
    TankIdentityStore _tankIdentityStoreMock;
    @Mock
    CredentialValidationResult _credentialValidationResultMock;
    @Mock
    HttpServletRequest _httpServletRequestMock;
    @Mock
    HttpServletResponse _httpServletResponseMock;
    @Mock
    AuthenticationParameters _authenticationParametersMock;
    @Mock
    Credential _credentialMock;
    @Mock
    CallerPrincipal _callerPrincipalMock;

    @InjectMocks
    private TankSecurityContext _sut;

    private AutoCloseable closeable;

    private User _userStub;

    @BeforeEach
    public void SetUp() {
        closeable = MockitoAnnotations.openMocks(this);

        _userStub = new User();
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    //region authenticate

    @Test
    public void Authenticate_Given_Existing_User_Returns_Success() {
        // Arrange
        when(_authenticationParametersMock.getCredential()).thenReturn(_credentialMock);
        when(_credentialValidationResultMock.getCallerPrincipal()).thenReturn(_callerPrincipalMock);
        when(_credentialValidationResultMock.getCallerGroups()).thenReturn(null);
        when(_tankIdentityStoreMock.validate(_credentialMock)).thenReturn(_credentialValidationResultMock);

        // Act
        AuthenticationStatus response = _sut.authenticate(_httpServletRequestMock, _httpServletResponseMock, _authenticationParametersMock);

        // Assert
        assertEquals(AuthenticationStatus.SUCCESS, response);
    }

    @Test
    public void Authenticate_Given_Failed_Auth_Returns_Failure() {
        // Arrange
        when(_authenticationParametersMock.getCredential()).thenReturn(_credentialMock);
        when(_tankIdentityStoreMock.validate(_credentialMock)).thenReturn(CredentialValidationResult.INVALID_RESULT);

        // Act
        AuthenticationStatus response = _sut.authenticate(_httpServletRequestMock, _httpServletResponseMock, _authenticationParametersMock);

        // Assert
        assertEquals(AuthenticationStatus.SEND_FAILURE, response);
    }

    @Test
    public void Authenticate_Given_Null_Result_Returns_Failure() {
        // Arrange
        when(_authenticationParametersMock.getCredential()).thenReturn(_credentialMock);
        when(_tankIdentityStoreMock.validate(_credentialMock)).thenReturn(null);

        // Act
        AuthenticationStatus response = _sut.authenticate(_httpServletRequestMock, _httpServletResponseMock, _authenticationParametersMock);

        // Assert
        assertEquals(AuthenticationStatus.SEND_FAILURE, response);
    }

    @Test
    public void Authenticate_Given_Exception_Throws_Up() {
        when(_authenticationParametersMock.getCredential()).thenReturn(_credentialMock);
        doThrow(NullPointerException.class).when(_tankIdentityStoreMock).validate(_credentialMock);

        assertThrows(NullPointerException.class, () -> {
            _sut.authenticate(_httpServletRequestMock, _httpServletResponseMock, _authenticationParametersMock);
        });
    }

    //endregion

    //region ssoSecurityContext

    @Test
    public void SsoSecurityContext_Given_Existing_User_Returns_Success() {
        // Arrange
        when(_tankIdentityStoreMock.validateSSOUser(_userStub)).thenReturn(_credentialValidationResultMock);

        // Act
        AuthenticationStatus response = _sut.ssoSecurityContext(_userStub);

        // Assert
        assertEquals(AuthenticationStatus.SUCCESS, response);
    }

    @Test
    public void SsoSecurityContext_Given_Failed_SSO_Returns_Failure() {
        // Arrange
        when(_tankIdentityStoreMock.validateSSOUser(_userStub)).thenReturn(CredentialValidationResult.INVALID_RESULT);

        // Act
        AuthenticationStatus response = _sut.ssoSecurityContext(_userStub);

        // Assert
        assertEquals(AuthenticationStatus.SEND_FAILURE, response);
    }

    @Test
    public void SsoSecurityContext_Given_Null_Result_Returns_Failure() {
        // Arrange
        when(_tankIdentityStoreMock.validateSSOUser(_userStub)).thenReturn(null);

        // Act
        AuthenticationStatus response = _sut.ssoSecurityContext(_userStub);

        // Assert
        assertEquals(AuthenticationStatus.SEND_FAILURE, response);
    }

    @Test
    public void SsoSecurityContext_Given_Exception_Throws_Up() {
        doThrow(NullPointerException.class).when(_tankIdentityStoreMock).validateSSOUser(_userStub);

        assertThrows(NullPointerException.class, () -> {
            _sut.ssoSecurityContext(_userStub);
        });
    }

    //endregion
}
