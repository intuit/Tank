package com.intuit.tank.rest.mvc;

import com.intuit.tank.dao.UserDao;
import com.intuit.tank.project.User;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private UserDao userDao;

    private static final Logger LOG = LogManager.getLogger(AuthTokenFilter.class);

    private final AuthTokenUserDetailService userDetailService;

    @Inject
    public AuthTokenFilter(AuthTokenUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if(validateToken(token)) {
                    String username = getUsernameFromToken(token);
                    if(username != null) {
                        UserDetails userDetails = userDetailService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (UsernameNotFoundException unfe) {
                    LOG.error("User not found via auth token", unfe);
            } catch (Exception e) {
                LOG.error("Error validating token", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean validateToken(String token) {
        User user = userDao.findByApiToken(token);
        return user != null;
    }

    private String getUsernameFromToken(String token){
        User user = userDao.findByApiToken(token);
        if(user != null){
            return user.getName();
        }

        return null;
    }

}
