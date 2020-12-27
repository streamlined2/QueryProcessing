package entity.beans;

import java.time.Year;

import entity.definition.Entity;

public class Location extends Entity {
	
	public record Coordinate(Double latitude,Double longitude) { };

	private String name;
	public String name() { return name;}
	
	private Country country;
	public Country country() { return country;}
	
	private Integer population;
	public Integer population() { return population;}
	
	private Double area;
	public Double area() { return area;}
	
	private Year founded;
	public Year founded() { return founded;}
	
	private Coordinate coordinate;
	public Coordinate coordinate() { return coordinate;}
	
	public Location(final String name,final Country country,final Integer population,final Double area,final Year founded,final Coordinate coordinate) {
		this.name=name;
		this.country=country;
		this.population=population;
		this.area=area;
		this.founded=founded;
		this.coordinate=coordinate;
	}
	
}
