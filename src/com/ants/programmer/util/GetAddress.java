package com.ants.programmer.util;

import java.net.InetAddress;
import java.net.UnknownHostException;


//获取本地的ip地址
public class GetAddress {
	public static String address()
	{
		String ip=null;
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}
}
