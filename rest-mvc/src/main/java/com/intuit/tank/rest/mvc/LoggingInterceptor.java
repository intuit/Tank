package com.intuit.tank.rest.mvc;

import com.intuit.tank.logging.LoggingConfig;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LoggingConfig.setupThreadContext();
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadContext.clearAll(); // clear the context when done
    }
}

