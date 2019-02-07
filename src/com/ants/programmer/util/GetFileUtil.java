package com.ants.programmer.util;

public class GetFileUtil {

	// 获得图片的名字
	public static String getFileName(String header) {
		String[] temArr1 = header.split(";");
		String[] temArr2 = temArr1[2].split("=");

		String fileName = temArr2[1].substring(temArr2[1].lastIndexOf("\\") + 1).replaceAll("\"", "");
		return fileName;
	}
}
