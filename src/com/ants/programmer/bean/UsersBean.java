package com.ants.programmer.bean;

//个人用户信息Bean类
public class UsersBean {
	private String UserId;
	private String UserName;
	private String Password;
	private String Sex;
	private String Birthday;
	private String IdentityCode;
	private String Email;
	private String Phone;
	private String Address;
	private String WechatHidden;
	private String QQHidden;

	public UsersBean(String userId, String userName, String password, String sex, String birthday, String identityCode,
			String email, String phone, String address, String wechathidden, String qqhidden) {
		this.UserId = userId;
		this.UserName = userName;
		this.Password = password;
		this.Sex = sex;
		this.Birthday = birthday;
		this.IdentityCode = identityCode;
		this.Email = email;
		this.Phone = phone;
		this.Address = address;
		this.WechatHidden = wechathidden;
		this.QQHidden = qqhidden;
	}

	public String getWechatHidden() {
		return WechatHidden;
	}

	public void setWechatHidden(String wechatHidden) {
		WechatHidden = wechatHidden;
	}

	public String getQQHidden() {
		return QQHidden;
	}

	public void setQQHidden(String qQHidden) {
		QQHidden = qQHidden;
	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getSex() {
		return Sex;
	}

	public void setSex(String sex) {
		Sex = sex;
	}

	public String getBirthday() {
		return Birthday;
	}

	public void setBirthday(String birthday) {
		Birthday = birthday;
	}

	public String getIdentityCode() {
		return IdentityCode;
	}

	public void setIdentityCode(String identityCode) {
		IdentityCode = identityCode;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPhone() {
		return Phone;
	}

	public void setPhone(String phone) {
		Phone = phone;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

}
