package com.kanven.netty.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;

public class IPUtil {

	private final static String JMX_HOST_NAME = "java.rmi.server.hostname";

	public static String getIP() throws SocketException {
		String host = System.getenv(JMX_HOST_NAME);
		if (StringUtils.isNotBlank(host)) {
			String[] items = host.split("\\.");
			if (items != null && items.length == 4) {
				boolean flag = true;
				for (String item : items) {
					try {
						int v = Integer.parseInt(item);
						if (v < 0 && v > 255) {
							flag = false;
							break;
						}
					} catch (Exception e) {
						flag = false;
						break;
					}
				}
				if (flag) {
					return host;
				}
			}
		}
		return getHostIP();
	}

	public static String getHostIP() throws SocketException {
		String ip = "";
		Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
		while (enumeration.hasMoreElements()) {
			NetworkInterface networkInterface = enumeration.nextElement();
			Enumeration<InetAddress> inetAddresss = networkInterface.getInetAddresses();
			while (inetAddresss.hasMoreElements()) {
				InetAddress inetAddress = inetAddresss.nextElement();
				if (inetAddress.isSiteLocalAddress() && !inetAddress.isLoopbackAddress()
						&& inetAddress.getHostAddress().indexOf(":") == -1) {
					return inetAddress.getHostAddress();
				}
			}
		}
		return ip;
	}
	
}
