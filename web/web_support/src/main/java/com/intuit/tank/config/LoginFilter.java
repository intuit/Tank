package com.intuit.tank.config;

import java.io.IOException;
import java.util.Objects;

import jakarta.inject.Inject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.exceptions.SegmentNotFoundException;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.OidcConstants;
import com.intuit.tank.auth.sso.TankSsoHandler;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebFilter(urlPatterns = {"/login.jsf", "/admin/*", "/projects/*", "/scripts/*", "/filters/*", "/agents/*", "/datafiles/*", "/tools/*"})
public class LoginFilter extends HttpFilter {
	private static final Logger LOG = LogManager.getLogger(LoginFilter.class);

    @Inject
    private TankSecurityContext _securityContext;

	@Inject
	private TankSsoHandler _tankSsoHandler;

	@Inject
	private TankConfig _tankConfig;

	private boolean firstOnloadFlag = true;

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		OidcSsoConfig oidcSsoConfig = _tankConfig.getOidcSsoConfig();

		String authorizationCode = request.getParameter(OidcConstants.AUTH_CODE_PARAMETER_KEY);

		// Handle Redirect From Authorization Server
		if (authorizationCode != null) {
			try {
				_tankSsoHandler.HandleSsoAuthorization(authorizationCode);
			} catch(IllegalArgumentException e) {
				LOG.error("Failed SSO due to missing argument", e);
				InvalidateAndRedirect(request, response);
			} catch(Exception e) {
				LOG.error("Failed SSO due to unhandled exception", e);
				InvalidateAndRedirect(request, response);
			}

			if (Objects.requireNonNull(oidcSsoConfig).getConfiguration() != null ) {
				String REDIRECT_URL_VALUE = oidcSsoConfig.getRedirectUrl();
				response.sendRedirect(REDIRECT_URL_VALUE);
			}

			return;
		}

		if (_securityContext.getCallerPrincipal() == null) {
			if (firstOnloadFlag) {
				firstOnloadFlag = false;
				LOG.warn("Failed to access " + (request).getRequestURI() + ", lack of permissions");
				InvalidateAndRedirect(request, response);
				return;
			}
		} else {
			try {
				AWSXRay.getCurrentSegment().setUser(_securityContext.getCallerPrincipal().getName());
			} catch (SegmentNotFoundException ignored) {
			} catch (Exception e) {
				LOG.error("Failed to set User on current segment : " + e.getMessage(), e);
			}
		}
        chain.doFilter(request, response);
	}

	private void InvalidateAndRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().invalidate();
		String contextPath = request.getContextPath() + "/login.jsf";
		response.sendRedirect(contextPath);
	}
}
