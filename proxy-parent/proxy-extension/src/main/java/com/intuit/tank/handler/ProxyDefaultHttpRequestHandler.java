package com.intuit.tank.handler;

import java.net.ProxySelector;

import org.owasp.proxy.http.client.HttpClient;
import org.owasp.proxy.http.server.DefaultHttpRequestHandler;

public class ProxyDefaultHttpRequestHandler extends DefaultHttpRequestHandler {
	private ProxySelector ps;
	
	public ProxyDefaultHttpRequestHandler(ProxySelector ps) {
		this.ps = ps;
	}
	
	@Override
	protected HttpClient createClient() {
		HttpClient client = super.createClient();
		client.setProxySelector(ps);
		client.setSoTimeout(20000);
		return client;
	}
};
