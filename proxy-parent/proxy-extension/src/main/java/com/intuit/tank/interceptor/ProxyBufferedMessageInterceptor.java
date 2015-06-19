package com.intuit.tank.interceptor;

import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.server.BufferedMessageInterceptor;

public class ProxyBufferedMessageInterceptor extends BufferedMessageInterceptor {
	@Override
	public Action directResponse(RequestHeader request,
			MutableResponseHeader response) {
		return Action.BUFFER;
	}

	@Override
	public Action directRequest(MutableRequestHeader request) {
		return Action.BUFFER;
	}
};
