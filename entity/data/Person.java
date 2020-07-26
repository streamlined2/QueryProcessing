package entity.data;

import entity.definition.Entity;

public class Person extends Entity {
	
	private final String firstname;
	private final String lastname;
	private final int age;
	public enum Sex {FEMALE,MALE};
	private Sex sex;
	
	public Person(final String firstname,final String lastname,final int age,final Sex sex) {
		this.firstname=firstname;
		this.lastname=lastname;
		this.age=age;
		this.sex=sex;
	}
	
	public String firstname() { return firstname;}
	public String lastname() { return lastname;}
	public int age() { return age;}
	public Sex sex() { return sex;}

}
