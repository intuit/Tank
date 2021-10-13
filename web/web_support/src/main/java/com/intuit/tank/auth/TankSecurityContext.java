package com.intuit.tank.auth;

import com.intuit.tank.project.User;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.Set;

@Named
@SessionScoped
public class TankSecurityContext implements SecurityContext, Serializable {
    private static final long serialVersionUID = 1L;

    private Principal principal;
    private Set<String> userRoles = Collections.emptySet();

    @Inject
    TankIdentityStore tankIdentityStore;

    @Override
    public Principal getCallerPrincipal() {
        return principal;
    }

    @Override
    public <T extends Principal> Set<T> getPrincipalsByType(Class<T> aClass) {
        return null;
    }

    @Override
    public boolean isCallerInRole(String role) {
        return userRoles.contains(role);
    }

    @Override
    public boolean hasAccessToWebResource(String resource, String... methods) {
        return false;
    }

    @Override
    public AuthenticationStatus authenticate(HttpServletRequest httpServletRequest,
                                             HttpServletResponse httpServletResponse,
                                             AuthenticationParameters authenticationParameters) {
        CredentialValidationResult credentialValidationResult
                = tankIdentityStore.validate(authenticationParameters.getCredential());
        if (credentialValidationResult != null &&
                credentialValidationResult != CredentialValidationResult.INVALID_RESULT) {
            principal = credentialValidationResult.getCallerPrincipal();
            userRoles = credentialValidationResult.getCallerGroups();
            return AuthenticationStatus.SUCCESS;
        }
        return AuthenticationStatus.SEND_FAILURE;
    }

    public AuthenticationStatus ssoSecurityContext(User user) {
        CredentialValidationResult credentialValidationResult = tankIdentityStore.validateSSOUser(user);

        if (credentialValidationResult != null &&
                credentialValidationResult != CredentialValidationResult.INVALID_RESULT) {
            principal = credentialValidationResult.getCallerPrincipal();
            userRoles = credentialValidationResult.getCallerGroups();
            return AuthenticationStatus.SUCCESS;
        }

        return AuthenticationStatus.SEND_FAILURE;
    }
}