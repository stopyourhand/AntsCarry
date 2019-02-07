package com.ants.programmer.bean;

//商品信息Bean类
public class ProductBean {
	private String Id;
	private String Mobile;
	private String Name;
	private Double Price;
	private String Introduce;
	private String Ways;
	private String Bargin;
	private int ApcId;
	private int ApcChildId;
	private String FileName;
	private int Status;

	public void setPrice(Double price) {
		Price = price;
	}

	public int getAP_STATUS() {
		return Status;
	}

	public void setAP_STATUS(int aP_STATUS) {
		Status = aP_STATUS;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String mobile) {
		Mobile = mobile;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public double getPrice() {
		return Price;
	}

	public void setPrice(double price) {
		Price = price;
	}

	public String getIntroduce() {
		return Introduce;
	}

	public void setIntroduce(String introduce) {
		Introduce = introduce;
	}

	public String getWays() {
		return Ways;
	}

	public void setWays(String ways) {
		Ways = ways;
	}

	public String getBargin() {
		return Bargin;
	}

	public void setBargin(String bargin) {
		Bargin = bargin;
	}

	public int getApcId() {
		return ApcId;
	}

	public void setApcId(int apcId) {
		ApcId = apcId;
	}

	public int getApcChildId() {
		return ApcChildId;
	}

	public void setApcChildId(int apcChildId) {
		ApcChildId = apcChildId;
	}

	public String getFileName() {
		return FileName;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public ProductBean(String Id, String Mobile, String Name, double Price, String Introduce, String Ways, String Bargin,
			int ApcId, int ApcChildId, String FileName, int Status) {
		this.Id = Id;
		this.Mobile = Mobile;
		this.Name = Name;
		this.Price = Price;
		this.Introduce = Introduce;
		this.Ways = Ways;
		this.Bargin = Bargin;
		this.ApcId = ApcId;
		this.ApcChildId = ApcChildId;
		this.FileName = FileName;
		this.Status = Status;
	}

}
