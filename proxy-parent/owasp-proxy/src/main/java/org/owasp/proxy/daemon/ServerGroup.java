/*
 * This file is part of the OWASP Proxy, a free intercepting proxy library.
 * Copyright (C) 2008-2010 Rogan Dawes <rogan@dawes.za.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to:
 * The Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.owasp.proxy.daemon;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ServerGroup {

    private static List<InetAddress> addrs = null;

    static {
        addrs = new ArrayList<InetAddress>();
        try {
            Enumeration<NetworkInterface> ni = NetworkInterface
                    .getNetworkInterfaces();
            while (ni.hasMoreElements()) {
                Enumeration<InetAddress> ia = ni.nextElement()
                        .getInetAddresses();
                while (ia.hasMoreElements())
                    addrs.add(ia.nextElement());
            }
        } catch (SocketException se) {
            se.printStackTrace();
        }
    }

    private List<InetSocketAddress> servers = new ArrayList<InetSocketAddress>();

    public synchronized void addServer(InetSocketAddress listen) {
        servers.add(listen);
    }

    public synchronized void removeServer(InetSocketAddress listen) {
        servers.remove(listen);
    }

    private static boolean isLocalAddress(InetAddress target) {
        return addrs.contains(target);
    }

    public synchronized boolean wouldAccept(InetSocketAddress target) {
        if (target == null)
            return false;
        for (InetSocketAddress listen : servers) {
            if (listen.getPort() == target.getPort()) { // maybe
                if (listen.getAddress().equals(target.getAddress()))
                    return true;
                else if (listen.getAddress().isAnyLocalAddress()
                        && isLocalAddress(target.getAddress()))
                    return true;
            }
        }
        return false;
    }

}
