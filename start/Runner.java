package start;
import java.time.Year;

import entity.beans.Country;
import entity.beans.Location;
import entity.beans.Person;
import entity.definition.DataSource;
import entity.definition.EntitySource;
import query.definition.Query;
import query.definition.QueryResult;
import query.exceptions.QueryException;
import query.processor.QueryProcessor;
import query.processor.BasicQueryProcessor;
import query.definition.Entry;

public class Runner {

	@SuppressWarnings("unchecked")
	public static void main(final String... args) throws QueryException {
		
		//define source data
		final EntitySource<Country> countries=new EntitySource<>(
				new Country("USA",9833520D,328239523),
				new Country("UK",242495D,67886004),
				new Country("France",640679D,67153000));
		final EntitySource<Location> locations=new EntitySource<>(
				new Location("Washington",countries.one(Country::name,"USA"),705_749,184_661D,Year.of(1791),new Location.Coordinate(38.9101D, -77.0147D)),
				new Location("New York City",countries.one(Country::name,"USA"),8_175_133,1_212.60D,Year.of(1624),new Location.Coordinate(40.71274D, -74.005974D)),
				new Location("London",countries.one(Country::name,"UK"),8_961_989,1_572D,Year.of(47),new Location.Coordinate(51.507222D, -0.1275D)),
				new Location("Glasgow",countries.one(Country::name,"UK"),633_120,175D,Year.of(580),new Location.Coordinate(55.860916D, -4.251433D)),
				new Location("Paris",countries.one(Country::name,"France"),2_148_271,105.4D,Year.of(-52),new Location.Coordinate(48.856613D, 2.352222D)),
				new Location("Bordeaux",countries.one(Country::name,"France"),254_436,49.36D,Year.of(-60),new Location.Coordinate(44.84D, -0.58D)));
		final EntitySource<Person> persons=new EntitySource<Person>(
				new Person("John","Kurtiss",20,Person.Sex.MALE,locations.one(Location::name,"Washington")),
				new Person("Janet","Small",30,Person.Sex.FEMALE,locations.one(Location::name,"New York City")),
				new Person("David","Cole",35,Person.Sex.MALE,locations.one(Location::name,"Glasgow")),
				new Person("Juliet","Freeman",22,Person.Sex.FEMALE,locations.one(Location::name,"London")),
				new Person("Maurice","Lacroix",25,Person.Sex.MALE,locations.one(Location::name,"Paris")),
				new Person("Genevieve","Odila",15,Person.Sex.FEMALE,locations.one(Location::name,"Bordeaux")));
		final DataSource data=new DataSource(
				countries,locations,persons
		);
		
		//define query and its components
		final Query q1=new Query();
		//define data entries for each entity bean
		final Entry<Person> personEntry=q1.addEntry(Person.class);
		final Entry<Location> locationEntry=q1.addEntry(Location.class);
		final Entry<Country> countryEntry=q1.addEntry(Country.class);
		//define select properties
		countryEntry.select(Country::name);
		locationEntry.select(Location::name);
		personEntry.select(Person::lastname,Person::firstname,Person::age,Person::sex);
		locationEntry.select(Location::founded);
		countryEntry.select(Country::population);
		//define sort by properties
		countryEntry.sortBy(Country::name);
		locationEntry.sortBy(Location::name);
		personEntry.sortBy(Person::lastname,Person::firstname);
		//define filter conditions
		//countryEntry.where((Country c)->c.name().equals("USA"));
		//locationEntry.where((Location loc)->loc.name().startsWith("W"));
		personEntry.where((Person p)->p.firstname().startsWith("J"));

		//System.out.println(q1);
		
		QueryProcessor processor=new BasicQueryProcessor(q1);
		QueryResult rst=processor.fetch(data);
		System.out.printf("Query #1: \n%s\n",rst);
		
		
	}

}
