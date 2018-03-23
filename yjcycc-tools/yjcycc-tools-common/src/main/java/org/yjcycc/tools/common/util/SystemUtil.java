package org.yjcycc.tools.common.util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class SystemUtil {
	private static Logger logger = Logger.getLogger(SystemUtil.class);

	public static void main(String[] args) throws SocketException {
//		Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
//		InetAddress ip = null;
//		while (allNetInterfaces.hasMoreElements()) {
//			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
//			System.out.println(netInterface.getName());
//			Enumeration addresses = netInterface.getInetAddresses();
//			while (addresses.hasMoreElements()) {
//				ip = (InetAddress) addresses.nextElement();
//				if (ip != null && ip instanceof Inet4Address) {
//					System.out.println("本机的IP = " + ip.getHostAddress());
//				}
//			}
//		}
		
		
		//
		System.out.println(getHostAddress());
		
		System.out.println(Integer.MAX_VALUE);
		int days = Integer.MAX_VALUE / 60  /60 /24 ;
		System.out.println(days);
	}
	
	
	
	/**
	 * @description 获取本地主机地址
	 * @return
	 */
	public static String getHostAddress() {
		String hostAddress = "";
		try {
			hostAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage());
			hostAddress = null;
		}
		return hostAddress;
	}
	
	/**
	 * 获取进程pid
	 * @return
	 */
	public static int getPid() {
		Integer pid = null;
		try {
			pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
		} catch (Exception e) {
			logger.error(e.getMessage());
			pid = null;
		}
		return pid;
	}
	
	/*** 判断是否为合法IP 
	 * @return is ip 
	 **/
	public static boolean isIp(String ipAddress) {
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

}
