package query.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Holds set of property values of query result
 * @author Serhii Pylypenko
 *
 */

public class Tuple {
	
	private final List<Object> values;
	
	public Tuple(final int dimension){
		values=new ArrayList<>(dimension);
	}
	
	public void set(final int index,final Object value) {
		values.set(index, value);
	}
	
	@Override public int hashCode() {
		return values.hashCode();
	}
	
	@Override public boolean equals(final Object o) {
		return (o instanceof Tuple)?values.equals(((Tuple)o).values):false;
	}
	
	@Override
	public String toString() {
		StringJoiner joiner=new StringJoiner(",","[","]");
		values.forEach(x->joiner.add(x.toString()));
		return joiner.toString();
	}

}
