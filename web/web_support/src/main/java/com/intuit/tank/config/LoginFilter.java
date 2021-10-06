package com.intuit.tank.config;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.exceptions.SegmentNotFoundException;
import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.auth.sso.TankSsoHandler;
import com.intuit.tank.vm.settings.OidcSsoConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * LoginFilter
 * 
 * @author Kevin McGoldrick
 * 
 */
public class LoginFilter extends HttpFilter {
	private static final Logger LOG = LogManager.getLogger(LoginFilter.class);
	private static final String AUTHCODEKEY = "code";

    @Inject
    private TankSecurityContext _securityContext;

	@Inject
	private TankSsoHandler _tankSsoHandler;

	@Inject
	private TankConfig _tankConfig;

	private String AUTHENTICATION_URL;
	private String CLIENT_SECRET_VALUE;
	private String CLIENT_ID_VALUE;
	private String REDIRECT_URL_VALUE;

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String authorizationCode = request.getParameter(AUTHCODEKEY);
		if (authorizationCode != null) {
			try {
				_tankSsoHandler.HandleSsoAuthorization(authorizationCode);
			} catch(IllegalArgumentException e) {
				LOG.error("Failed SSO due to missing argument", e);
				InvalidateAndRedirect(request, response);
			}
			return;
		}

		if (_securityContext.getCallerPrincipal() == null) {
			OidcSsoConfig _oidcSsoConfig = _tankConfig.getOidcSsoConfig();

			if (_oidcSsoConfig != null ||
				_oidcSsoConfig.getAuthenticationUrl() != null ||
				_oidcSsoConfig.getClientSecret() != null ||
				_oidcSsoConfig.getClientId() != null ||
				_oidcSsoConfig.getRedirectUrl() != null) {

				AUTHENTICATION_URL = _oidcSsoConfig.getAuthenticationUrl();
				CLIENT_SECRET_VALUE = _oidcSsoConfig.getClientSecret();
				CLIENT_ID_VALUE = _oidcSsoConfig.getClientId();
				REDIRECT_URL_VALUE = _oidcSsoConfig.getRedirectUrl();

				// Temp
				URIBuilder builder = new URIBuilder()
						.setScheme("https")
						.setHost("federatesys.intuit.com/as/token.oauth2")
						.addParameter("client_id", CLIENT_ID_VALUE)
						.addParameter("response_type", "code")
						.addParameter("redirect_uri", REDIRECT_URL_VALUE)
						.addParameter("scope", "openid+email+profile+groups")
						.addParameter("state", "af0ifjsldkj");

				var authenticationUrl = builder.toString();

				response.sendRedirect(authenticationUrl);
			}

			LOG.warn("Failed to access " + (request).getRequestURI() + ", lack of permissions");
			InvalidateAndRedirect(request, response);
			return;
		}

		try {
			AWSXRay.getCurrentSegment().setUser(_securityContext.getCallerPrincipal().getName());
		} catch (SegmentNotFoundException snfe ) { //Ignore
		} catch (Exception e) {
			LOG.error("Failed to set User on current segment : " + e.getMessage(), e);
		}
        chain.doFilter(request, response);
	}

	@Override
	public void destroy() {}

	private void InvalidateAndRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.getSession().invalidate();
		String contextPath = request.getContextPath();
		response.sendRedirect(contextPath);
	}
}
