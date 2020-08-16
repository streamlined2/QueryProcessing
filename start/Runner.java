package start;
import java.util.function.Function;

import entity.data.Person;
import entity.definition.Entity;
import entity.definition.Source;
import query.definition.QualifiedProperty;
import query.definition.Query;
import query.definition.Entry;

public class Runner {

	public static void main(final String... args) {
		final Query q1=new Query(new Source<>(
				new Person("John","Kurtiss",20,Person.Sex.MALE),
				new Person("Janet","Small",30,Person.Sex.FEMALE),
				new Person("Jack","Freeman",40,Person.Sex.MALE)));
		q1.select(
				new QualifiedProperty<Person>((Entry<Person>) q1.getEntry(1),Person::firstname));
		System.out.println(q1);
	}

}
