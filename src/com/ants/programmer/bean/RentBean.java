package com.ants.programmer.bean;

public class RentBean {
	private String Id;
	private int Stock;
	private int Status;
	
	public RentBean(String id,int stock,int status) {
		this.Id=id;
		this.Stock=stock;
		this.Status=status;
	}
	
	
	
	
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public int getStock() {
		return Stock;
	}
	public void setStock(int stock) {
		Stock = stock;
	}
	public int getStatus() {
		return Status;
	}
	public void setStatus(int status) {
		Status = status;
	}
	
	
}
