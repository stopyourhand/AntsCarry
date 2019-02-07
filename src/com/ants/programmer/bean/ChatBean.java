package com.ants.programmer.bean;

//聊天室Bean类
public class ChatBean {
	private String toMobile;
	private String Mobile;
	private String userName;
	private String Message;
	private String Time;
	private String id;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getToMobile() {
		return toMobile;
	}

	public void setToMobile(String toMobile) {
		this.toMobile = toMobile;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String mobile) {
		Mobile = mobile;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public ChatBean(String Mobile, String toMobile, String userName, String Message, String Time,String id) {
		this.toMobile = toMobile;
		this.Mobile = Mobile;
		this.userName = userName;
		this.Message = Message;
		this.Time = Time;
		this.id = id;
	}

}
