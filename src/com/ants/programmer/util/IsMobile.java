package com.ants.programmer.util;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

public class IsMobile {
	
	public static boolean isMobile(String mobile)
	{
		Pattern s = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		
		Matcher m = s.matcher(mobile);
		
		return  m.matches();
	}
	
	public static void main(String[] args) {
		System.out.println(isMobile("554616616"));
	}
}
