package com.ants.programmer.bean;

public class FeedBackBean {
	private String Text;
	private String Satisfaction;
	private String UserName;
	private String Mobile;
	private String Time;
	
	
	public FeedBackBean(String text,String satis,String username,String mobile,String time) {
		this.Text=text;
		this.Satisfaction=satis;
		this.UserName=username;
		this.Mobile=mobile;
		this.Time=time;
	}
	
	public String getText() {
		return Text;
	}
	public void setText(String text) {
		Text = text;
	}
	public String getSatisfaction() {
		return Satisfaction;
	}
	public void setSatisfaction(String satisfaction) {
		Satisfaction = satisfaction;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getMobile() {
		return Mobile;
	}
	public void setMobile(String mobile) {
		Mobile = mobile;
	}
	public String getTime() {
		return Time;
	}
	public void setTime(String time) {
		Time = time;
	}
	
	
}
