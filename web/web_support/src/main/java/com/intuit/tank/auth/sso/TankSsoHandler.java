package com.intuit.tank.auth.sso;

import com.intuit.tank.admin.UserCreate;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.models.Token;
import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;

import javax.inject.Inject;
import java.io.IOException;

/**
 * TankSsoHandler
 *
 * @author Shawn Park
 */
public class TankSsoHandler {
    @Inject
    private TankOidcAuthorization _tankOidcAuthorization;

    @Inject
    private TankSecurityContext _tankSecurityContext;

    @Inject
    private UserDao _userDao;

    @Inject
    private UserCreate _userCreate;

    public void HandleSsoAuthorization(String authorizationCode) throws IOException {
        if (authorizationCode == null || authorizationCode.isEmpty() || authorizationCode.isBlank()) {
            return;
        }

        Token token = _tankOidcAuthorization.GetAccessToken(authorizationCode);
        UserInfo userInfo = _tankOidcAuthorization.DecodeIdToken(token.getIdToken());

        User user = _userDao.findByEmail(userInfo.getEmail());

        if (user == null) {
            user = _userCreate.CreateUser(userInfo);
        }

        _tankSecurityContext.ssoSecurityContext(user);
    }
}
