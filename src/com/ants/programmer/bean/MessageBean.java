package com.ants.programmer.bean;

import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

/**
 * 服务端发送给客户端消息实体
 * 
 * @author lenovo
 *
 */
public class MessageBean {
	private String alert; //

	private List<String> names;

	private String sendMsg;

	private String from;

	private String date;

	private JSONObject json;

	public JSONObject getJson() {
		return json;
	}

	public void setJson(JSONObject json) {
		this.json = json;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSendMsg() {
		return sendMsg;
	}

	public void setSendMsg(String sendMsg) {
		this.sendMsg = sendMsg;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public MessageBean() {
		super();
	}

}
