package com.ants.programmer.bean;

//正在交易Bean类
public class TransactionBean {
	private String Id;
	private String Mobile;
	private String Name;
	private Double Price;
	private String Introduce;
	private String Ways;
	private String Bargin;
	private double ParentId;
	private double ChildId;
	private String FileName;
	private int Status;

	public double getParentId() {
		return ParentId;
	}

	public void setParentId(double parentId) {
		ParentId = parentId;
	}

	public double getChildId() {
		return ChildId;
	}

	public TransactionBean(String id, String mobile, String name, double price, String introduce, String ways,
			String bargin, double parentid, double childid, String filename, int status) {
		this.Id = id;
		this.Mobile = mobile;
		this.Name = name;
		this.Price = price;
		this.Introduce = introduce;
		this.Ways = ways;
		this.Bargin = bargin;
		this.ParentId = parentid;
		this.ChildId = childid;
		this.FileName = filename;
		this.Status = status;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String mobile) {
		Mobile = mobile;
	}

	public void setChildId(double childId) {
		ChildId = childId;
	}

	public void setPrice(Double price) {
		Price = price;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
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

}
