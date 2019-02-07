package com.ants.programmer.bean;

//购物车Bean类
public class ShopBean {
	private String Id;
	private String SellerMobile;
	private String Time;

	public ShopBean(String id, String sellermobile, String time) {
		this.Id = id;
		this.SellerMobile = sellermobile;
		this.Time = time;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getSellerMobile() {
		return SellerMobile;
	}

	public void setSellerMobile(String sellerMobile) {
		SellerMobile = sellerMobile;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

}
