package entity.beans;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

import entity.definition.Entity;
import entity.definition.EntityInspector;

public class Product extends Entity {
	
	public enum Status { OUT_OF_STOCK, IN_STOCK, RUNNING_LOW };
	
	private final String name;
	public String name() { return name;}
	
	private final BigDecimal price;
	public BigDecimal price() { return price;}
	
	private final Status status;
	public Status status() { return status;}
	
	private final LocalDateTime createdAt;
	public LocalDateTime createdAt() { return createdAt;}
	
	//arguments should be ordered alphabetically to comply with EntityInspector.getSerializableFields
	public Product(final LocalDateTime createdAt,final String name,final BigDecimal price,final Status status) {
		this.name=name;
		this.price=price;
		this.status=status;
		this.createdAt=createdAt;
	}
	
}
