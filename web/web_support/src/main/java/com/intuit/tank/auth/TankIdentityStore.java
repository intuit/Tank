package com.intuit.tank.auth;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.Group;
import com.intuit.tank.project.User;
import com.intuit.tank.service.InitializeEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
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
            AWSXRay.getCurrentSegment().setUser(user.getName());
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
            AWSXRay.getCurrentSegment().setUser(user.getName());

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