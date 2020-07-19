package query.definition;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds set of property values of query result
 * @author Serhii Pylypenko
 *
 */

class Tuple {
	
	private final List<Object> properties;
	
	Tuple(final int dimension){
		properties=new ArrayList<>(dimension);
	}
	
	void add(final Object value) {
		properties.add(value);
	}
	
	@Override public int hashCode() {
		return properties.hashCode();
	}
	
	@Override public boolean equals(final Object o) {
		return (o instanceof Tuple)?properties.equals(((Tuple)o).properties):false;
	}

}
