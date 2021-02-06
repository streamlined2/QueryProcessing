package query.definition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.StringJoiner;

import math.Numeric;

/**
 * Holds aggregated values for GROUP BY queries, e.g. SUM,AVG,COUNT
 * 
 * @author Serhii Pylypenko
 *
 */

public class AggregatedData implements Iterable<Numeric> {

	private final Numeric[] values;
	
	public AggregatedData(final int dimension) {
		this.values=new Numeric[dimension];
	}
	
	public Numeric getValue(final int index) {
		return values[index];
	}
	
	public void setValue(final int index,final Numeric value) {
		values[index]=value;
	}
	
	public int getDimension() {
		return values.length;
	}
	
	@Override
	public String toString() {
		final StringJoiner joiner=new StringJoiner(",","[","]");
		forEach(x->joiner.add(x.toString()));
		return joiner.toString();
	}

	@Override
	public Iterator<Numeric> iterator() {
		return Arrays.asList(values).iterator();
	}
	
}
