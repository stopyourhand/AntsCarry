package com.ants.programmer.bean;

/**
 * 客户端发送给服务器d段消息实体
 * 
 * @author lenovo
 *
 */

public class ContentVoBean {
	private String tomobile;
	private String msg;
	private Integer type;
	private String mobile;
	private String time;
	private String username;

	public String getTomobile() {
		return tomobile;
	}

	public void setTomobile(String tomobile) {
		this.tomobile = tomobile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTo() {
		return tomobile;
	}

	public void setTo(String to) {
		this.tomobile = to;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
