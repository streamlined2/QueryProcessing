package start;
import entity.data.Person;
import entity.definition.Source;
import query.definition.Query;

public class Runner {

	public static void main(String[] args) {
		Query q1=new Query(new Source<>(
				new Person("John","Kurtiss",20,Person.Sex.MALE),
				new Person("Janet","Small",30,Person.Sex.FEMALE),
				new Person("Jack","Freeman",40,Person.Sex.MALE)));
		System.out.println(q1);
	}

}
