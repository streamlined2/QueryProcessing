package entity.beans;

import entity.definition.Entity;

public class OrderItem extends Entity {
	
	private final int quantity;
	public int quantity() { return quantity;}
	
	private final Product product;
	public Product product() { return product;}
	
	public OrderItem(final int quantity,final Product product) {
		this.product=product;
		this.quantity=quantity;
	}

}
