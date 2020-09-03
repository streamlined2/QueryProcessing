package query.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Holds set of property values of query result
 * @author Serhii Pylypenko
 *
 */

class Tuple {
	
	private final List<Object> values;
	
	Tuple(final int dimension){
		values=new ArrayList<>(dimension);
	}
	
	void add(final Object value) {
		values.add(value);
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
