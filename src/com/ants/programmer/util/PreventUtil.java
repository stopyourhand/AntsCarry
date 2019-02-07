package com.ants.programmer.util;

public class PreventUtil {

	// 防止JS注入，将JS代码替换成“蚂蚁置物”;
	public static String Prevent(String str) {
		if (str == null || str.equals("")) {
			return "这人很懒，什么都没有留下！";
		}
		if (str.contains("<script>") && str.contains("</script>")) {
			//String before = str.substring(0, str.indexOf("<script>"));
			String behind[] = str.split("<");
			int i=str.indexOf("<");
			String newStr="";
			if(i==0) {
				for(int index=0;index<behind.length;index++) {
					newStr+=behind[index];
				}
				return "&lt;"+newStr;
			}
			else {
				newStr=behind[0]+"&lt;";
				for(int index=1;index<behind.length;index++) {
					newStr+=behind[index];
				}
				return newStr;
			}
		}

		return str.toString();
	}
}
