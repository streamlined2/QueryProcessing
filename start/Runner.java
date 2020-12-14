package start;
import entity.data.Person;
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
		
		DataSource data=new DataSource(
				new EntitySource<Person>(
					new Person("John","Kurtiss",20,Person.Sex.MALE),
					new Person("Janet","Small",30,Person.Sex.FEMALE),
					new Person("Jack","Freeman",40,Person.Sex.MALE))
		);
		
		Query q1=new Query();
		Entry<Person> persons=q1.addEntry(Person.class);

		persons.select(Person::firstname,Person::lastname,Person::age,Person::sex);
		System.out.println(q1);
		
		QueryProcessor processor=new BasicQueryProcessor(q1);
		QueryResult rst=processor.fetch(data);
		System.out.printf("Query #1: \n%s\n",rst);
		
		
	}

}
