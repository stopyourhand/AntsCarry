package com.ants.programmer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ShopIdUtil {
	
	public static String getShopIdByUUID() {
		SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
		String time=format.format(new Date());
		int hashCode=UUID.randomUUID().toString().hashCode();
		if(hashCode<0) {
			hashCode=-hashCode;
			
		}
		return time+String.format("%011d", hashCode);
	}

}
