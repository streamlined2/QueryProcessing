package entity.beans;

import entity.definition.Entity;

public class Country extends Entity {
	
	private final String name;
	public String name() { return name;}
	
	private Double area;
	public Double area() { return area;}
	
	private final Integer population;
	public Integer population() { return population;}
	
	public Country(final String name,final Double area,final Integer population) {
		this.name=name;
		this.area=area;
		this.population=population;
	}

}
