package com.intuit.tank.admin;

import com.intuit.tank.auth.sso.models.UserInfo;
import com.intuit.tank.dao.GroupDao;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import com.intuit.tank.vm.common.PasswordEncoder;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.Set;

/**
 * UserCreate
 *
 * @author Shawn Park
 */
public class UserCreate {
    private static final Logger LOG = LogManager.getLogger(UserCreate.class);
    private static final String TEMPSSOPASSWORD = "sso-password";

    private User _user;

    @Inject
    private UserDao _userDao;

    public User CreateUser(UserInfo userInfo) {
        try {
            Set<String> defaultGroupsStringSet = new TankConfig().getSecurityConfig().getDefaultGroups();

            _user = User.builder()
                    .name(userInfo.getUsername())
                    .password(PasswordEncoder.encodePassword(TEMPSSOPASSWORD))
                    .email(userInfo.getEmail())
                    .build();


            _userDao.saveOrUpdate(_user);

            for (String g : defaultGroupsStringSet) {
                _user.addGroup(new GroupDao().getOrCreateGroup(g));
            }

            _userDao.saveOrUpdate(_user);

            return _user;
        } catch (Exception e) {
            LOG.error("Failed to create user for: " + userInfo, e);
            throw e;
        }
    }
}
