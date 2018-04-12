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

package org.owasp.proxy.util;

import org.owasp.proxy.util.WindowsProxy.WinInet.INTERNET_PER_CONN_OPTION;
import org.owasp.proxy.util.WindowsProxy.WinInet.INTERNET_PER_CONN_OPTION_LIST;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.LongByReference;

public class WindowsProxy {

    public static class ProxySettings {
        public int flags = 0;
        public String proxyServer = null;
        public String proxyBypass = null;
        public String autoConfigUrl = null;
        public int autoDiscoveryFlags = 0;

        private int set(int flags, int mask, boolean value) {
            if (value)
                return flags | mask;
            return flags & ~mask;
        }

        public boolean isAutoProxy() {
            return (flags & WinInet.PROXY_TYPE_AUTO_PROXY_URL) > 0;
        }

        public void setAutoProxy(boolean value) {
            flags = set(flags, WinInet.PROXY_TYPE_AUTO_PROXY_URL, value);
        }

        public boolean isAutoDetect() {
            return (flags & WinInet.PROXY_TYPE_AUTO_DETECT) > 0;
        }

        public void setAutoDetect(boolean value) {
            flags = set(flags, WinInet.PROXY_TYPE_AUTO_DETECT, value);
        }

        public boolean isDirect() {
            return (flags & WinInet.PROXY_TYPE_DIRECT) > 0;
        }

        public void setDirect(boolean value) {
            flags = set(flags, WinInet.PROXY_TYPE_DIRECT, value);
        }

        public boolean isProxy() {
            return (flags & WinInet.PROXY_TYPE_PROXY) > 0;
        }

        public void setProxy(boolean value) {
            flags = set(flags, WinInet.PROXY_TYPE_PROXY, value);
        }

        public String toString() {
            return getClass().getSimpleName() + " (flags=" + flags
                    + " (autoProxy=" + isAutoProxy() + ", autoDetect="
                    + isAutoDetect() + ", direct=" + isDirect() + ", proxy="
                    + isProxy() + "), proxyServer=" + proxyServer
                    + ", proxyBypass=" + proxyBypass + ", autoConfigUrl="
                    + autoConfigUrl + ", autoDiscoveryFlags="
                    + autoDiscoveryFlags + ")";
        }
    }

    private static Error available = null;

    private static WinInet wininet = null;
    private static Kernel32 kernel32 = null;
    private static CLibrary clibrary = null;

    static {
        try {
            wininet = (WinInet) Native.loadLibrary("wininet", WinInet.class);
            kernel32 = (Kernel32) Native
                    .loadLibrary("kernel32", Kernel32.class);
            clibrary = (CLibrary) Native.loadLibrary("msvcrt", CLibrary.class);
        } catch (Error e) {
            available = e;
        }
    }

    public static ProxySettings getProxySettings() {
        if (available != null)
            throw new RuntimeException("Unable to initialise JNA libraries",
                    available);

        INTERNET_PER_CONN_OPTION opt = new INTERNET_PER_CONN_OPTION();
        INTERNET_PER_CONN_OPTION[] options = (INTERNET_PER_CONN_OPTION[]) opt
                .toArray(5);

        options[0].dwOption = WinInet.INTERNET_PER_CONN_FLAGS;
        options[1].dwOption = WinInet.INTERNET_PER_CONN_PROXY_SERVER;
        options[2].dwOption = WinInet.INTERNET_PER_CONN_PROXY_BYPASS;
        options[3].dwOption = WinInet.INTERNET_PER_CONN_AUTOCONFIG_URL;
        options[4].dwOption = WinInet.INTERNET_PER_CONN_AUTODISCOVERY_FLAGS;
        for (INTERNET_PER_CONN_OPTION option : options) option.write();

        INTERNET_PER_CONN_OPTION_LIST list = new INTERNET_PER_CONN_OPTION_LIST();
        list.dwOptionCount = options.length;
        list.dwOptionError = 0;
        list.pOptions = options[0].getPointer();
        list.dwSize = list.size();
        list.write();
        LongByReference size = new LongByReference(list.size());

        boolean result = wininet.InternetQueryOptionA(null,
                WinInet.INTERNET_OPTION_PER_CONNECTION_OPTION, list
                        .getPointer(), size);

        if (!result) {
            System.out.println("Error: " + kernel32.GetLastError());
            System.out.println("Option error: " + list.dwOptionError);
            return null;
        } else {
            ProxySettings settings = new ProxySettings();
            list.read();
            for (int i = 0; i < options.length; i++) {
                switch (options[i].dwOption) {
                case WinInet.INTERNET_PER_CONN_FLAGS:
                    settings.flags = getInt(options[i]);
                    break;
                case WinInet.INTERNET_PER_CONN_PROXY_SERVER:
                    settings.proxyServer = getString(options[i]);
                    break;
                case WinInet.INTERNET_PER_CONN_PROXY_BYPASS:
                    settings.proxyBypass = getString(options[i]);
                    break;
                case WinInet.INTERNET_PER_CONN_AUTOCONFIG_URL:
                    settings.autoConfigUrl = getString(options[i]);
                    break;
                case WinInet.INTERNET_PER_CONN_AUTODISCOVERY_FLAGS:
                    settings.autoDiscoveryFlags = getInt(options[i]);
                    break;
                }
            }
            return settings;
        }
    }

    private static String getString(INTERNET_PER_CONN_OPTION option) {
        option.value.setType(Pointer.class);
        option.value.read();
        if (option.value.pszValue == null)
            return null;
        String str = option.value.pszValue.getString(0);
        clibrary.free(option.value.pszValue);
        return str;
    }

    private static int getInt(INTERNET_PER_CONN_OPTION option) {
        option.value.setType(int.class);
        option.value.read();
        return option.value.dwValue;
    }

    public static void setProxySettings(ProxySettings settings) {
        if (available != null)
            throw new RuntimeException("Unable to initialise JNA libraries",
                    available);

        INTERNET_PER_CONN_OPTION opt = new INTERNET_PER_CONN_OPTION();
        INTERNET_PER_CONN_OPTION[] options = (INTERNET_PER_CONN_OPTION[]) opt
                .toArray(5);

        options[0].dwOption = WinInet.INTERNET_PER_CONN_FLAGS;
        options[0].value.setType(int.class);
        options[0].value.dwValue = settings.flags;

        options[1].dwOption = WinInet.INTERNET_PER_CONN_PROXY_SERVER;
        options[1].value.setType(String.class);
        options[1].value.strValue = settings.proxyServer;

        options[2].dwOption = WinInet.INTERNET_PER_CONN_PROXY_BYPASS;
        options[2].value.setType(String.class);
        options[2].value.strValue = settings.proxyBypass;

        options[3].dwOption = WinInet.INTERNET_PER_CONN_AUTOCONFIG_URL;
        options[3].value.setType(String.class);
        options[3].value.strValue = settings.autoConfigUrl;

        options[4].dwOption = WinInet.INTERNET_PER_CONN_AUTODISCOVERY_FLAGS;
        options[4].value.setType(int.class);
        options[4].value.dwValue = settings.autoDiscoveryFlags;

        for (int i = 0; i < options.length; i++)
            options[i].write();

        INTERNET_PER_CONN_OPTION_LIST list = new INTERNET_PER_CONN_OPTION_LIST();
        list.dwOptionCount = options.length;
        list.dwOptionError = 0;
        list.pOptions = options[0].getPointer();
        list.dwSize = list.size();
        list.write();

        if (!wininet.InternetSetOptionA(null,
                WinInet.INTERNET_OPTION_PER_CONNECTION_OPTION, list
                        .getPointer(), list.size()))
            throw new RuntimeException(
                    "Error invoking InternetSetOptionA, code "
                            + kernel32.GetLastError());
    }

    public static void main(String[] args) {
        ProxySettings current = getProxySettings();
        System.out.println(current + "\n\n");
        ProxySettings replacements = new ProxySettings();
        replacements.proxyServer = "socks=localhost:1081";
        replacements.setProxy(true);
        setProxySettings(replacements);
        System.out.println(getProxySettings() + "\n\n");
        setProxySettings(current);
        System.out.println(getProxySettings());
    }

    public interface WinInet extends com.sun.jna.Library {

        /**
         * PER_CONN_FLAGS
         */

        /**
         * Specifies that some connections may be made directly to the server, bypassing the proxy
         */
        public static int PROXY_TYPE_DIRECT = 0x00000001; // direct to net
        /**
         * Specifies that some connections may go via a proxy
         */
        public static int PROXY_TYPE_PROXY = 0x00000002; // via named proxy
        /**
         * Not sure exactly what this one does. Maybe that a .pac file is active?
         */
        public static int PROXY_TYPE_AUTO_PROXY_URL = 0x00000004; // autoproxy
        // URL
        /**
         * Specifies that the browser will auto detect the proxy, according to the MS auto-detect methodology
         */
        public static int PROXY_TYPE_AUTO_DETECT = 0x00000008; // use autoproxy
        // detection

        //
        // Options used in INTERNET_PER_CONN_OPTON struct
        //
        public static int INTERNET_PER_CONN_FLAGS = 1;
        public static int INTERNET_PER_CONN_PROXY_SERVER = 2;
        public static int INTERNET_PER_CONN_PROXY_BYPASS = 3;
        public static int INTERNET_PER_CONN_AUTOCONFIG_URL = 4;
        public static int INTERNET_PER_CONN_AUTODISCOVERY_FLAGS = 5;

        //
        // PER_CONN_AUTODISCOVERY_FLAGS
        //
        // user changed this setting
        public static int AUTO_PROXY_FLAG_USER_SET = 0x00000001;
        // force detection even when its not needed
        public static int AUTO_PROXY_FLAG_ALWAYS_DETECT = 0x00000002;
        // detection has been run
        public static int AUTO_PROXY_FLAG_DETECTION_RUN = 0x00000004;
        // migration has just been done
        public static int AUTO_PROXY_FLAG_MIGRATED = 0x00000008;
        // don't cache result of host=proxy name
        public static int AUTO_PROXY_FLAG_DONT_CACHE_PROXY_RESULT = 0x00000010;
        // don't initalize and run unless URL expired
        public static int AUTO_PROXY_FLAG_CACHE_INIT_RUN = 0x00000020;
        // if we're on a LAN & Modem, with only one IP, bad?!?
        public static int AUTO_PROXY_FLAG_DETECTION_SUSPECT = 0x00000040;

        public static int INTERNET_OPTION_PER_CONNECTION_OPTION = 75;

        boolean InternetQueryOptionA(Pointer unused, int dwOption,
                Pointer lpBuffer, LongByReference size);

        boolean InternetSetOptionA(Pointer unused, int dwOption,
                Pointer buffer, int bufferLength);

        boolean InternetQueryOptionW(Pointer unused, int dwOption,
                Pointer lpBuffer, LongByReference size);

        boolean InternetSetOptionW(Pointer unused, int dwOption,
                Pointer buffer, int bufferLength);

        public static class INTERNET_PER_CONN_OPTION extends Structure {

            public int dwOption;

            public static class FILETIME extends Structure {
                public int dwLowDateTime;
                public int dwHighDateTime;
            }

            public static class Value extends Union {
                public int dwValue;
                public Pointer pszValue;
                public FILETIME ftValue;
                public String strValue;
            }

            public Value value;
        }

        public static class INTERNET_PER_CONN_OPTION_LIST extends Structure {
            public int dwSize;
            public Pointer pszConnection;
            public int dwOptionCount;
            public int dwOptionError;
            public Pointer pOptions;
        }
    }

    public interface CLibrary extends Library {

        void free(Pointer p);

    }

    public interface Kernel32 extends Library {

        int GetLastError();

    }

}
