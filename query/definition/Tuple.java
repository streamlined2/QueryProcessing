package query.definition;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Holds set of property values of query result
 * @author Serhii Pylypenko
 *
 */

public class Tuple {
	
	private final Object[] values;
	
	public Tuple(final int dimension){
		values=new Object[dimension];
	}
	
	public void set(final int index,final Object value) {
		values[index]=value;
	}
	
	@Override public int hashCode() {
		return values.hashCode();
	}
	
	@Override public boolean equals(final Object o) {
		return (o instanceof Tuple tuple)?values.equals(tuple.values):false;
	}
	
	@Override
	public String toString() {
		StringJoiner joiner=new StringJoiner(",","[","]");
		Arrays.asList(values).forEach(x->joiner.add(x.toString()));
		return joiner.toString();
	}

}
