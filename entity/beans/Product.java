package entity.beans;

import java.math.BigDecimal;
import java.sql.Date;

import entity.definition.Entity;

public class Product extends Entity {
	
	public enum Status { OUT_OF_STOCK, IN_STOCK, RUNNING_LOW };
	
	private final String name;
	public String name() { return name;}
	
	private final BigDecimal price;
	public BigDecimal price() { return price;}
	
	private final Status status;
	public Status status() { return status;}
	
	private final Date createdAt;
	public Date createdAt() { return createdAt;}
	
	public Product(final String name,final BigDecimal price,final Status status,final Date createdAt) {
		this.name=name;
		this.price=price;
		this.status=status;
		this.createdAt=createdAt;
	}
	
}
