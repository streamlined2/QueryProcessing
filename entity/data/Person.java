package entity.data;

import entity.definition.Entity;

public class Person extends Entity {
	
	public enum Sex {FEMALE,MALE};

	private final String firstname;
	public String firstname() { return firstname;}

	private final String lastname;
	public String lastname() { return lastname;}

	private final int age;
	public int age() { return age;}

	private Sex sex;
	public Sex sex() { return sex;}
	
	private Location location;
	public Location location() { return location;}
	
	public Person(final String firstname,final String lastname,final int age,final Sex sex,final Location location) {
		this.firstname=firstname;
		this.lastname=lastname;
		this.age=age;
		this.sex=sex;
		this.location=location;
	}	

}
