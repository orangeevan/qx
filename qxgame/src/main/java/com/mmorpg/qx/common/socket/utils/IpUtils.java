package com.mmorpg.qx.common.socket.utils;

import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取IP地址的简单工具类
 * 
 * @author wangke
 */
public class IpUtils {

	/**
	 * 获取会话的IP地址
	 * 
	 * @param address
	 * @return
	 */
	public static String getIp(String address) {
		if (address == null) {
			return "UNKNOWN";
		}
		return substringBetween(address, "/", ":");
	}

	public static String getIp(SocketAddress remoteAddress) {
		return getIp(remoteAddress.toString());
	}

	/**
	 * 判断是否为合法IP
	 * 
	 * @return true or false
	 */
	public static boolean isIpv4(String ipAddress) {
		String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
				+ "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	private static String substringBetween(String str, String open, String close) {
		if (str == null || open == null || close == null) {
			return null;
		}
		int start = str.indexOf(open);
		if (start != -1) {
			int end = str.indexOf(close, start + open.length());
			if (end != -1) {
				return str.substring(start + open.length(), end);
			}
		}
		return null;
	}

}
