package com.intuit.tank.selector;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class TankProxySelector extends ProxySelector {
	
	private static Logger logger = Logger.getLogger("org.owasp.proxy");
	
	private java.net.Proxy upstream;
	
	public TankProxySelector(java.net.Proxy upstream) {
		this.upstream = upstream;
	}

	@Override
	public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
		logger.info("Proxy connection to " + uri + " via " + sa
				+ " failed! " + ioe.getLocalizedMessage());
	}

	@Override
	public List<java.net.Proxy> select(URI uri) {
		return Collections.singletonList(upstream);
	}
};
