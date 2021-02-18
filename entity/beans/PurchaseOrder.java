package entity.beans;

import java.time.LocalDateTime;

import entity.definition.Entity;

public class PurchaseOrder extends Entity {
	
	private final int userId;
	public int userId() { return userId;}
	
	private final String status;
	public String status() { return status;}
	
	private final LocalDateTime createdAt;
	public LocalDateTime createdAt() { return createdAt;}
	
	public PurchaseOrder(final int userId,final String status,final LocalDateTime createdAt) {
		this.createdAt=createdAt;
		this.status=status;
		this.userId=userId;
	}

}
