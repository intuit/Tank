package com.intuit.tank.auth;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;
import com.intuit.tank.service.InitializeEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@ApplicationScoped
public class TankIdentityStore implements IdentityStore {
    private static final Logger LOG = LogManager.getLogger(TankIdentityStore.class);

    @Inject
    private InitializeEnvironment initializeEnvironment;

    @Inject
    @Authenticated
    private Event<User> loginEventSrc;

    @PostConstruct
    public void init() {
        initializeEnvironment.ping();
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {

        UsernamePasswordCredential login = (UsernamePasswordCredential) credential;

        LOG.info("Attempting to login " + login.getCaller());
        User user = new UserDao().authenticate(login.getCaller(), login.getPasswordAsString());
        if (user != null) {
            List<String> userRoles = new ArrayList<>();
            for (Group g : user.getGroups()) {
                userRoles.add(g.getName());
            }
            loginEventSrc.fire(user);
            LOG.info("Successfully logged in " + login.getCaller());
            return new CredentialValidationResult(login.getCaller(), new HashSet<>(userRoles));
        }
        LOG.warn("Failed to login " + login.getCaller());
        return CredentialValidationResult.INVALID_RESULT;
    }

    public CredentialValidationResult validateSSOUser(User user) {
        try {
            List<String> userRoles = new ArrayList<>();
            for (Group g : user.getGroups()) {
                userRoles.add(g.getName());
            }
            loginEventSrc.fire(user);

            return new CredentialValidationResult(user.getName(), new HashSet<>(userRoles));
        } catch(Exception e) {
            LOG.warn("Failed to login " + user.getName());
            return CredentialValidationResult.INVALID_RESULT;
        }
    }
}