package com.ants.programmer.util;

import java.io.IOException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

public class Sendmessage {

	// 给对应的手机号码，发送对应的消息类
	public static void send(String mobile, String message) throws HttpException, IOException {
		System.out.println("发送的人的手机号码是:" + mobile);
		System.out.println("发送的人消息是:" + message);
		HttpClient client = new HttpClient();
		PostMethod post = new PostMethod("http://gbk.sms.webchinese.cn");// 中国网建url
		post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=gbk");// 在头文件中设置转码
		NameValuePair[] data = { new NameValuePair("Uid", "晨边啊啊"), // 用户名
				new NameValuePair("Key", "d41d8cd98f00b204e980"), // 密码匙
				new NameValuePair("smsMob", mobile), // 发送对应的手机号码

				new NameValuePair("smsText", message)// 发送的消息
		};
		post.setRequestBody(data);
		client.executeMethod(post);
		Header[] headers = post.getResponseHeaders();
		int statusCode = post.getStatusCode();
		System.out.println("statusCode:" + statusCode);
		for (Header h : headers) {
			System.out.println(h.toString());
		}
		String result = new String(post.getResponseBodyAsString().getBytes("gbk"));
		System.out.println(result);
		post.releaseConnection();
	}

}
