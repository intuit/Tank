package com.intuit.tank.auth.sso;

import com.intuit.tank.admin.UserCreate;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.models.Token;
import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TankSsoHandler
 *
 * @author Shawn Park
 */
public class TankSsoHandlerTest {

    @Mock
    private TankConfig _tankConfigMock;
    @Mock
    private OidcSsoConfig _oidcSsoConfigMock;
    @Mock
    private HierarchicalConfiguration<ImmutableNode> _hierarchicalConfigurationMock;
    @Mock
    private TankOidcAuthorization _tankOidcAuthorizationMock;
    @Mock
    private TankSecurityContext _tankSecurityContextMock;
    @Mock
    private UserDao _userDaoMock;
    @Mock
    private UserCreate _userCreateMock;
    @Mock
    private HttpSession _sessionMock;

    private final String AUTHORIZATION_CODE_STUB = "testAuthorizationCode";
    private final String STATE_STUB = "testState";
    private final String NONCE_STUB = "testNonce";
    private final String CLIENT_ID_STUB = "client-id";
    private Token TOKEN_STUB;
    private UserInfo USERINFO_STUB;
    private User USER_STUB;

    @InjectMocks
    private TankSsoHandler _sut;

    private AutoCloseable closeable;

    @BeforeEach
    public void SetUp() {
        closeable = MockitoAnnotations.openMocks(this);

        TOKEN_STUB = new Token();
        USERINFO_STUB = new UserInfo();
        USER_STUB = new User();

        TOKEN_STUB.setIdToken("testIdToken");
        TOKEN_STUB.setAccessToken("testAccessToken");
        USERINFO_STUB.setEmail("user@intuit.com");
        USERINFO_STUB.setNonce(NONCE_STUB);
        USERINFO_STUB.setAudience(CLIENT_ID_STUB);
        USERINFO_STUB.setExpirationTimeUtc(Instant.now().plusSeconds(300).getEpochSecond());

        when(_sessionMock.getAttribute(TankSsoHandler.STATE_SESSION_ATTRIBUTE)).thenReturn(STATE_STUB);
        when(_sessionMock.getAttribute(TankSsoHandler.NONCE_SESSION_ATTRIBUTE)).thenReturn(NONCE_STUB);
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getClientId()).thenReturn(CLIENT_ID_STUB);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    //region GetOnLoadAuthorizationRequest Happy Path

    @Test
    public void GetOnLoadAuthorizationRequest_Given_AuthorizationCode_Call_To_Get_AccessToken() {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(_hierarchicalConfigurationMock);
        when(_oidcSsoConfigMock.getAuthorizationUrl()).thenReturn("https://www.authorization-url.com/auth");
        when(_oidcSsoConfigMock.getClientId()).thenReturn("client-id");
        when(_oidcSsoConfigMock.getRedirectUrl()).thenReturn("https://www.redirect-url.com");

        // Act
        var authorizationRequestString = _sut.GetOnLoadAuthorizationRequest(_sessionMock);

        // Assert
        assertNotNull(authorizationRequestString);
        assertFalse(authorizationRequestString.isEmpty());
        assertFalse(authorizationRequestString.isBlank());
    }

    //endregion

    //region GetOnLoadAuthorizationRequest Negative Case

    @Test
    public void GetOnLoadAuthorizationRequest_Given_Null_Config_Throws_IllegalArgumentException() {
        // Arrange
        when(_tankConfigMock.getOidcSsoConfig()).thenReturn(_oidcSsoConfigMock);
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(null);

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.GetOnLoadAuthorizationRequest(_sessionMock);
        });
    }

    //endregion

    //region HandleSsoAuthorization Happy Path

    @Test
    public void HandleSsoAuthorization_Given_AuthorizationCode_Call_To_Get_AccessToken() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock);

        // Assert
        verify(_tankOidcAuthorizationMock, times(1)).GetAccessToken(any(String.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_AccessToken_Call_To_Decode_To_UserInfo() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock);

        // Assert
        verify(_tankOidcAuthorizationMock, times(1)).DecodeIdToken(any(Token.class));
    }

    @Test
    public void HandleSsoAuthorization_Any_SSO_User_Call_To_Check_If_User_Exists() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock);

        // Assert
        verify(_userDaoMock, times(1)).findByEmail(any(String.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_New_User_Call_To_Create_New_User() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(null);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock);

        // Assert
        verify(_userCreateMock, times(1)).CreateUser(any(UserInfo.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_Existing_User_Does_Not_Call_To_Create_New_User() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(USER_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock);

        // Assert
        verify(_userCreateMock, times(0)).CreateUser(any(UserInfo.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_Any_User_Call_To_Populate_Security_Context() throws IOException {
        // Arrange
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);
        when(_userDaoMock.findByEmail(any(String.class))).thenReturn(USER_STUB);

        // Act
        _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock);

        // Assert
        verify(_tankSecurityContextMock, times(1)).ssoSecurityContext(any(User.class));
    }

    //endregion

    //region HandleSsoAuthorization Negative Case

    @Test
    public void HandleSsoAuthorization_Given_Invalid_AuthorizationCode_Throws_Exception() throws IllegalArgumentException {
        // Arrange + Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.HandleSsoAuthorization(null, STATE_STUB, _sessionMock);
        });
    }

    @Test
    public void HandleSsoAuthorization_Given_Invalid_OidcConfig_Throws_Exception() throws IllegalArgumentException {
        // Arrange + Act + Assert
        assertThrows(IllegalArgumentException.class, () -> {
            _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock);
        });
    }

    //endregion
    //region State, nonce and claim validation

    @Test
    public void GetOnLoadAuthorizationRequest_Stores_Random_State_And_Nonce_In_Session() {
        when(_oidcSsoConfigMock.getConfiguration()).thenReturn(_hierarchicalConfigurationMock);
        when(_oidcSsoConfigMock.getAuthorizationUrl()).thenReturn("https://www.authorization-url.com/auth");
        when(_oidcSsoConfigMock.getRedirectUrl()).thenReturn("https://www.redirect-url.com");

        String first = _sut.GetOnLoadAuthorizationRequest(_sessionMock);
        String second = _sut.GetOnLoadAuthorizationRequest(_sessionMock);

        ArgumentCaptor<Object> states = ArgumentCaptor.forClass(Object.class);
        verify(_sessionMock, times(2)).setAttribute(eq(TankSsoHandler.STATE_SESSION_ATTRIBUTE), states.capture());
        verify(_sessionMock, times(2)).setAttribute(eq(TankSsoHandler.NONCE_SESSION_ATTRIBUTE), any());
        assertNotEquals(states.getAllValues().get(0), states.getAllValues().get(1));
        assertTrue(first.contains("state=" + states.getAllValues().get(0)));
        assertTrue(first.contains("nonce="));
        assertNotEquals(first, second);
    }

    @Test
    public void HandleSsoAuthorization_Given_Wrong_State_Throws_Without_Token_Exchange() throws IOException {
        assertThrows(IllegalArgumentException.class,
                () -> _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, "forged-state", _sessionMock));
        verify(_tankOidcAuthorizationMock, never()).GetAccessToken(any(String.class));
        verify(_sessionMock).removeAttribute(TankSsoHandler.STATE_SESSION_ATTRIBUTE);
    }

    @Test
    public void HandleSsoAuthorization_Given_No_Session_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, null));
    }

    @Test
    public void HandleSsoAuthorization_Given_Wrong_Nonce_Throws() throws IOException {
        USERINFO_STUB.setNonce("other-nonce");
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        assertThrows(IllegalArgumentException.class,
                () -> _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock));
        verify(_tankSecurityContextMock, never()).ssoSecurityContext(any(User.class));
    }

    @Test
    public void HandleSsoAuthorization_Given_Wrong_Audience_Throws() throws IOException {
        USERINFO_STUB.setAudience("another-client");
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        assertThrows(IllegalArgumentException.class,
                () -> _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock));
    }

    @Test
    public void HandleSsoAuthorization_Given_Expired_Token_Throws() throws IOException {
        USERINFO_STUB.setExpirationTimeUtc(Instant.now().minusSeconds(3600).getEpochSecond());
        when(_tankOidcAuthorizationMock.GetAccessToken(any(String.class))).thenReturn(TOKEN_STUB);
        when(_tankOidcAuthorizationMock.DecodeIdToken(any(Token.class))).thenReturn(USERINFO_STUB);

        assertThrows(IllegalArgumentException.class,
                () -> _sut.HandleSsoAuthorization(AUTHORIZATION_CODE_STUB, STATE_STUB, _sessionMock));
    }

    //endregion
}
